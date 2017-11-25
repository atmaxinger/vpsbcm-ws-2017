package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.RobNurGUI;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.StorageController;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.GreenhouseServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.StorageServiceImpl;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.TransactionServiceImpl;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.ContainerFullException;
import org.mozartspaces.core.*;
import org.mozartspaces.core.aspects.ContainerIPoint;

import java.net.URISyntaxException;
import java.util.List;

public class SpaceServer {

    static void testTrans(Capi capi)  {
        try {
            ContainerReference cref1 = capi.createContainer("asdf", capi.getCore().getConfig().getSpaceUri(), 1, null, null);
            ContainerReference cref2 = capi.createContainer("xxx", capi.getCore().getConfig().getSpaceUri(), 1, null, null);

            capi.write(cref1, new Entry("a"));
            capi.write(cref2, new Entry("begone"));


            TransactionReference t = capi.createTransaction(1000, capi.getCore().getConfig().getSpaceUri());

            List<String> jkl = capi.read(cref2, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.RequestTimeout.INFINITE, null);

            try {
                capi.take(cref2, AnyCoordinator.newSelector(1), MzsConstants.RequestTimeout.DEFAULT, t);
                capi.write(new Entry("shouldnotseeme"), cref1, MzsConstants.RequestTimeout.DEFAULT, t);
                capi.commitTransaction(t);
            } catch (MzsTimeoutException ex) {
                System.out.println("TTRANSACTION EXC");
            }
            catch (ContainerFullException ex) {
                System.out.println("CONTAINER FULL EXCEPTION 1");
            }



            List<String> asdf = capi.read(cref1, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.TransactionTimeout.INFINITE, null);
            jkl = capi.read(cref2, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.TransactionTimeout.INFINITE, null);

            System.out.println();
            System.out.println();
            System.out.println("HI");
            System.out.println(
            );
            System.out.println();




        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);

        System.out.println("URL: " + core.getConfig().getSpaceUri());

        //testTrans(capi);

        ConfigService configService = new ConfigService();

        try {



            ContainerReference waterContainer = CapiUtil.lookupOrCreateContainer("waterContainer", core.getConfig().getSpaceUri(), null,null, capi);
            WaterAspect was = new WaterAspect(capi);
            capi.addContainerAspect(was, waterContainer, ContainerIPoint.POST_TAKE);

            StorageService storageService = new StorageServiceImpl(core.getConfig().getSpaceUri());
            GreenhouseService greenhouseService = new GreenhouseServiceImpl(core.getConfig().getSpaceUri());
            TranscationService transactionService = new TransactionServiceImpl(core.getConfig().getSpaceUri());

            storageService.putFlowerFertilizer(new FlowerFertilizer());
            storageService.putFlowerFertilizer(new FlowerFertilizer());
            storageService.putFlowerFertilizer(new FlowerFertilizer());
            storageService.putFlowerFertilizer(new FlowerFertilizer());
            storageService.getFlowerFertilizer(2);

            SoilPackage soilPackage1 = new SoilPackage();
            soilPackage1.setAmount(50);
            SoilPackage soilPackage2 = new SoilPackage();
            soilPackage2.setAmount(10);
            SoilPackage soilPackage3 = new SoilPackage();
            soilPackage3.setAmount(40);
            SoilPackage soilPackage4 = new SoilPackage();
            soilPackage4.setAmount(30);

            storageService.putSoilPackage(soilPackage1);
            storageService.putSoilPackage(soilPackage2);
            storageService.putSoilPackage(soilPackage3);
            storageService.putSoilPackage(soilPackage4);

            storageService.getSoil(30);
            storageService.getSoil(200);

            Water water = new Water();
            water.setAmount(250);
            storageService.putWater(water);

            storageService.getWater(600);





            //--------------------------------------------
            //--------------------------------------------

            Transaction t = transactionService.beginTransaction(-1);
            VegetablePlant vp = new VegetablePlant();
            vp.setCultivationInformation(configService.getVegetablePlantCultivationInformation().get(0));
            vp.setGrowth(10);
            greenhouseService.plant(vp, t);
            t.commit();

            t = transactionService.beginTransaction(-1);

            FlowerPlant fp = new FlowerPlant();
            fp.setCultivationInformation(configService.getFlowerPlantCultivationInformation().get(0));
            fp.setGrowth(100);
            greenhouseService.plant(fp, t);
            t.commit();

            t = transactionService.beginTransaction(-1);

            vp = new VegetablePlant();
            vp.setCultivationInformation(configService.getVegetablePlantCultivationInformation().get(0));
            vp.setGrowth(100);
            greenhouseService.plant(vp, t);
            t.commit();

            t = transactionService.beginTransaction(-1);

            vp = new VegetablePlant();
            vp.setCultivationInformation(configService.getVegetablePlantCultivationInformation().get(0));
            vp.setGrowth(101);
            greenhouseService.plant(vp, t);

            t.commit();

            t = transactionService.beginTransaction(-1);

            List<VegetablePlant> vegs1 = greenhouseService.readAllVegetablePlants();
            List<Vegetable> vegs = greenhouseService.harvestVegetablePlant(t);

            t.commit();

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
