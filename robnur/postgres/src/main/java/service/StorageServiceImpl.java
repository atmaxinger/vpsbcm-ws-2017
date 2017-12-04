package service;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.FlowerFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.SoilPackage;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.VegetableFertilizer;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.Water;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;

import java.util.ArrayList;
import java.util.List;

public class StorageServiceImpl extends StorageService {

    private static final String STORAGE_FLOWER_SEED_TABLE = "sfs";
    private static final String STORAGE_VEGETABLE_SEED_TABLE = "svs";
    private static final String STORAGE_SOIL_TABLE = "ss";
    private static final String STORAGE_FLOWER_FERTILIZER_TABLE = "sff";
    private static final String STORAGE_VEGETABLE_FERTILIZER_TABLE = "svf";
    private static final String STORAGE_WATER_TABLE = "svf";

    @Override
    protected List<FlowerPlant> getSeeds(FlowerType type, Transaction transaction) {
        return ServiceUtil.getItemsByParameter("'cultivationInformation'->'flowerType'",type.name(),STORAGE_FLOWER_SEED_TABLE,FlowerPlant.class,transaction);
    }

    @Override
    protected List<VegetablePlant> getSeeds(VegetableType type, Transaction transaction) {
        return ServiceUtil.getItemsByParameter("'cultivationInformation'->'vegetableType'",type.name(),STORAGE_VEGETABLE_SEED_TABLE,VegetablePlant.class,transaction);

    }

    @Override
    public void putSeed(FlowerPlant plant, Transaction transaction) {
        ServiceUtil.writeItem(plant,STORAGE_FLOWER_SEED_TABLE,transaction);
    }

    @Override
    public void putSeed(VegetablePlant plant, Transaction transaction) {
        ServiceUtil.writeItem(plant,STORAGE_VEGETABLE_SEED_TABLE,transaction);
    }

    @Override
    public void putFlowerSeeds(List<FlowerPlant> plants, Transaction transaction) {
        for (FlowerPlant fp:plants) {
            putSeed(fp,transaction);
        }
    }

    @Override
    public void putVegetableSeeds(List<VegetablePlant> plants, Transaction transaction) {
        for (VegetablePlant vp:plants) {
            putSeed(vp,transaction);
        }
    }

    @Override
    public List<FlowerPlant> readAllFlowerSeeds(Transaction transaction) {
        return ServiceUtil.readAllItems(STORAGE_FLOWER_SEED_TABLE,FlowerPlant.class,transaction);
    }

    @Override
    public List<VegetablePlant> readAllVegetableSeeds(Transaction transaction) {
        return ServiceUtil.readAllItems(STORAGE_VEGETABLE_SEED_TABLE,VegetablePlant.class,transaction);
    }

    @Override
    protected List<SoilPackage> getAllSoilPackages(Transaction transaction) {
        List<SoilPackage> soilPackages = ServiceUtil.readAllItems(STORAGE_SOIL_TABLE,SoilPackage.class,transaction);
        for (SoilPackage sp:soilPackages) {
            ServiceUtil.deleteItemById(sp.getId(),STORAGE_SOIL_TABLE,transaction);
        }
        return soilPackages;
    }

    @Override
    public void putSoilPackage(SoilPackage soilPackage, Transaction transaction) {
        ServiceUtil.writeItem(soilPackage,STORAGE_SOIL_TABLE,transaction);
    }

    @Override
    public void putSoilPackages(List<SoilPackage> soilPackages, Transaction transaction) {
        for (SoilPackage sp:soilPackages) {
            putSoilPackage(sp,transaction);
        }
    }

    @Override
    public List<SoilPackage> readAllSoilPackage(Transaction transaction) {
        return ServiceUtil.readAllItems(STORAGE_SOIL_TABLE,SoilPackage.class,transaction);
    }

    // TODO: handling the amount should be in the abstract class
    @Override
    public List<FlowerFertilizer> getFlowerFertilizer(int amount, Transaction transaction) {
        List<FlowerFertilizer> flowerFertilizers = ServiceUtil.readAllItems(STORAGE_FLOWER_FERTILIZER_TABLE,FlowerFertilizer.class,transaction);

        if(flowerFertilizers.size()<amount){
            return null;
        }

        List<FlowerFertilizer> result = new ArrayList<>();

        for (int i = 0; i<amount; i++){
            FlowerFertilizer flowerFertilizer = flowerFertilizers.get(i);
            result.add(flowerFertilizer);
            ServiceUtil.deleteItemById(flowerFertilizer.getId(),STORAGE_FLOWER_FERTILIZER_TABLE,transaction);
        }
        return result;
    }

    @Override
    public void putFlowerFertilizer(FlowerFertilizer flowerFertilizer) {
        ServiceUtil.writeItem(flowerFertilizer,STORAGE_FLOWER_FERTILIZER_TABLE);
    }

    @Override
    public void putFlowerFertilizers(List<FlowerFertilizer> flowerFertilizers) {
        for (FlowerFertilizer ff:flowerFertilizers) {
            putFlowerFertilizer(ff);
        }
    }

    @Override
    public List<FlowerFertilizer> readAllFlowerFertilizer(Transaction transaction) {
        return ServiceUtil.readAllItems(STORAGE_FLOWER_FERTILIZER_TABLE,FlowerFertilizer.class);
    }

    @Override
    public List<VegetableFertilizer> getVegetableFertilizer(int amount, Transaction transaction) {
        List<VegetableFertilizer> vegetableFertilizers = ServiceUtil.readAllItems(STORAGE_VEGETABLE_FERTILIZER_TABLE,VegetableFertilizer.class,transaction);

        if(vegetableFertilizers.size()<amount){
            return null;
        }

        List<VegetableFertilizer> result = new ArrayList<>();

        for (int i = 0; i<amount; i++){
            VegetableFertilizer vegetableFertilizer = vegetableFertilizers.get(i);
            result.add(vegetableFertilizer);
            ServiceUtil.deleteItemById(vegetableFertilizer.getId(),STORAGE_FLOWER_FERTILIZER_TABLE,transaction);
        }
        return result;
    }

    @Override
    public void putVegetableFertilizer(VegetableFertilizer vegetableFertilizer) {
        ServiceUtil.writeItem(vegetableFertilizer,STORAGE_VEGETABLE_FERTILIZER_TABLE);
    }

    @Override
    public void putVegetableFertilizers(List<VegetableFertilizer> vegetableFertilizers) {
        for (VegetableFertilizer vf:vegetableFertilizers) {
            putVegetableFertilizer(vf);
        }
    }

    @Override
    public List<VegetableFertilizer> readAllVegetableFertilizer(Transaction transaction) {
        return ServiceUtil.readAllItems(STORAGE_VEGETABLE_FERTILIZER_TABLE,VegetableFertilizer.class,transaction);
    }

    @Override
    public Water accessTap() {
        // TODO: !!!!!! NOT TRANSACTIONAL SECURE !!!!!!
        // TODO: !!!!!! BUSY WAITING !!!!!!
        while (true){
            List<Water> waterList = ServiceUtil.readAllItems(STORAGE_WATER_TABLE,Water.class);
            if(waterList.size() >= 1){
                ServiceUtil.deleteItemById(waterList.get(0).getId(),STORAGE_WATER_TABLE);
                return waterList.get(0);
            }
        }
    }

    @Override
    public void putWater(Water water) {
        ServiceUtil.writeItem(water,STORAGE_WATER_TABLE);
    }
}
