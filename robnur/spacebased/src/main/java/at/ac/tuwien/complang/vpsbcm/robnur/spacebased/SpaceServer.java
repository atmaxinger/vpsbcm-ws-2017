package at.ac.tuwien.complang.vpsbcm.robnur.spacebased;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services.StorageServiceImpl;
import org.mozartspaces.core.*;
import org.mozartspaces.core.aspects.ContainerIPoint;

import java.net.URISyntaxException;

public class SpaceServer {

    public static void main(String[] args) {
        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);



        try {
            ContainerReference waterContainer = CapiUtil.lookupOrCreateContainer("waterContainer", core.getConfig().getSpaceUri(), null,null, capi);
            WaterAspect was = new WaterAspect(capi);
            capi.addContainerAspect(was, waterContainer, ContainerIPoint.POST_TAKE);

            StorageService storageService = new StorageServiceImpl(core.getConfig().getSpaceUri());

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

        } catch (MzsCoreException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
