package at.ac.tuwien.complang.vpsbcm.robnur.spacebased.services;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class StorageServiceImpl extends StorageService {

    private Capi capi;

    private ContainerReference seedContainer;
    private ContainerReference soilContainer;
    private ContainerReference flowerFertilizerContainer;
    private ContainerReference vegetableFertilizerContainer;
    private ContainerReference waterContainer;

    public StorageServiceImpl(URI spaceUri) throws MzsCoreException, URISyntaxException {

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();

        capi = new Capi(core);
        List<FifoCoordinator> coords = null;//Arrays.asList(new FifoCoordinator());

        seedContainer = CapiUtil.lookupOrCreateContainer("seedContainer",spaceUri,coords,null,capi);
        soilContainer = CapiUtil.lookupOrCreateContainer("soilContainer",spaceUri,coords,null,capi);
        flowerFertilizerContainer = CapiUtil.lookupOrCreateContainer("flowerFertilizerContainer",spaceUri,coords,null,capi);
        vegetableFertilizerContainer = CapiUtil.lookupOrCreateContainer("vegetableFertilizerContainer",spaceUri,coords,null,capi);
        waterContainer = CapiUtil.lookupOrCreateContainer("waterContainer",spaceUri,coords,null,capi);
    }

    public Plant getSeed() {
        ArrayList<Plant> plants = null;

        try {
            plants = capi.take(seedContainer);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
        return plants.get(0);
    }

    public void putSeed(Plant plant) {
        try {
            capi.write(seedContainer,new Entry(plant));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Plant> readAllSeeds() {
        List<Plant> plants = null;

        try {
            plants = capi.read(seedContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL),MzsConstants.RequestTimeout.ZERO,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return plants;
    }

    protected List<SoilPackage> getAllSoilPackages() {
        ArrayList<SoilPackage> soilPackages = null;

        try {
            soilPackages = capi.take(soilContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL),MzsConstants.RequestTimeout.INFINITE,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return soilPackages;
    }

    public void putSoilPackage(SoilPackage soilPackage) {
        try {
            capi.write(soilContainer,new Entry(soilPackage));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SoilPackage> readAllSoilPackage() {
        ArrayList<SoilPackage> packages = null;

        try {
            packages = capi.read(soilContainer, AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL), MzsConstants.RequestTimeout.ZERO, null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return packages;
    }

    public List<FlowerFertilizer> getFlowerFertilizer(int amount) {

        ArrayList<FlowerFertilizer> flowerFertilizers = null;

        try {
             flowerFertilizers = capi.take(flowerFertilizerContainer, AnyCoordinator.newSelector(amount),MzsConstants.RequestTimeout.INFINITE,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowerFertilizers;
    }

    public void putFlowerFertilizer(FlowerFertilizer flowerFertilizer) {
        try {
            capi.write(flowerFertilizerContainer,new Entry(flowerFertilizer));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<FlowerFertilizer> readAllFlowerFertilizer() {
        List<FlowerFertilizer> flowerFertilizers = null;

        try {
            flowerFertilizers = capi.read(flowerFertilizerContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL),MzsConstants.RequestTimeout.ZERO,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return flowerFertilizers;
    }

    public List<VegetableFertilizer> getVegetableFertilizer(int amount) {
        ArrayList<VegetableFertilizer> vegetableFertilizers = null;

        try {
            vegetableFertilizers = capi.take(flowerFertilizerContainer, AnyCoordinator.newSelector(amount),MzsConstants.RequestTimeout.INFINITE,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetableFertilizers;
    }

    public void putVegetableFertilizer(VegetableFertilizer vegetableFertilizer) {
        try {
            capi.write(vegetableFertilizerContainer,new Entry(vegetableFertilizer));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<VegetableFertilizer> readAllVegetableFertilizer() {
        List<VegetableFertilizer> vegetableFertilizers = null;

        try {
            vegetableFertilizers = capi.read(vegetableFertilizerContainer,AnyCoordinator.newSelector(AnyCoordinator.AnySelector.COUNT_ALL),MzsConstants.RequestTimeout.ZERO,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return vegetableFertilizers;
    }

    public void putWater(Water water){
        try {
            capi.write(waterContainer,new Entry(water));
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }
    }

    public Water accessTap() {
        ArrayList<Water> waterArrayList = null;

        try {
            waterArrayList = capi.take(waterContainer,AnyCoordinator.newSelector(),MzsConstants.RequestTimeout.INFINITE,null);
        } catch (MzsCoreException e) {
            e.printStackTrace();
        }

        return waterArrayList.get(0);
    }
}
