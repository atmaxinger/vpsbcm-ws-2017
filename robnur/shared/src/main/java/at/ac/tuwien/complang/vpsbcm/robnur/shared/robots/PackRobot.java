package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PackRobot extends Robot {

    private PackingService packingService;
    private MarketService marketService;
    private ResearchService researchService;
    private TransactionService transactionService;

    public PackRobot(PackingService packingService, MarketService marketService, ResearchService researchService, TransactionService transactionService) {
        this.packingService = packingService;
        this.marketService = marketService;
        this.researchService = researchService;
        this.transactionService = transactionService;

        tryCreateVegetableBasket();
        tryCreateBouquet();
    }

    public void tryCreateBouquet(){
        Transaction transaction = transactionService.beginTransaction(1000000);

        List<Flower> flowers = packingService.readAllFlowers(transaction);
        List<Flower> flowersForBouquet = new ArrayList<>();

        if(marketService.getAmountOfBouquets() >= 5){

            // put flowers from packing-queue to the research department
            for (Flower f: flowers) {
                researchService.putFlower(packingService.getFlower(f.getId(),transaction));
            }

            transaction.commit();

            return;
        }

        if(getNumberOfDistinctFlowerTypes(flowers) >= 3){

            if(flowers.size() >= 5){
                flowersForBouquet.add(flowers.get(0));
                flowersForBouquet.add(flowers.get(1));
                flowersForBouquet.add(flowers.get(2));
                switch (getNumberOfDistinctFlowerTypes(flowersForBouquet)){
                    case 1:
                        flowersForBouquet.add(getDistinctFlower(flowersForBouquet));
                        flowersForBouquet.add(getDistinctFlower(flowersForBouquet));
                        break;
                    case 2:
                        flowersForBouquet.add(flowers.get(3));
                        switch (getNumberOfDistinctFlowerTypes(flowersForBouquet)){
                            case 2:
                                flowersForBouquet.add(getDistinctFlower(flowersForBouquet));
                                break;
                            case 3:
                                flowersForBouquet.add(flowers.get(4));
                                break;
                        }
                        break;
                    case 3:
                        flowersForBouquet.add(flowers.get(3));
                        flowersForBouquet.add(flowers.get(4));
                        break;
                }
            }
            else if (flowers.size() >= 4) {
                flowersForBouquet.add(flowers.get(0));
                flowersForBouquet.add(flowers.get(1));
                switch (getNumberOfDistinctFlowerTypes(flowersForBouquet)) {
                    case 1:
                        flowersForBouquet.add(getDistinctFlower(flowersForBouquet));
                        flowersForBouquet.add(getDistinctFlower(flowersForBouquet));
                        break;
                    case 2:
                        flowersForBouquet.add(flowers.get(2));
                        switch (getNumberOfDistinctFlowerTypes(flowersForBouquet)) {
                            case 2:
                                flowersForBouquet.add(getDistinctFlower(flowersForBouquet));
                                break;
                            case 3:
                                flowersForBouquet.add(flowers.get(3));
                                break;
                        }
                        break;
                }
            }else{
                // not enough flowers
                transaction.commit();
                return;
            }

            /* remove flowers from packing-queue */
            for (Flower f:flowersForBouquet) {
                packingService.getFlower(f.getId(),transaction);
            }

            transaction.commit();

            waitPackingTime();

            Bouquet bouquet = new Bouquet();
            bouquet.setFlowers(flowersForBouquet);

            marketService.putBouquet(bouquet);

            tryCreateBouquet();    // try to create another bouquet

            return;
        }
        else {
            // not enough flowers of distinct types
            transaction.commit();
            return;
        }
    }

    public void tryCreateVegetableBasket(){
        Transaction transaction = transactionService.beginTransaction(1000000);

        List<Vegetable> vegetables = packingService.readAllVegetables(transaction);

        if(marketService.getAmountOfVegetableBaskets() >= 3)
        {
            for (Vegetable v:vegetables) {
                researchService.putVegetable(packingService.getVegetable(v.getId(),transaction));
            }

            transaction.commit();
            return;
        }

        if(vegetables.size() <= 5){
            // not enough vegetables available
            transaction.commit();
            return;
        }

        List<VegetableType> checkedVegetableTypes = new ArrayList();    // list of VegetableType to avoid checking a VegetableType twice

        for (Vegetable v:vegetables) {

            VegetableType vegetableType = v.getParentVegetablePlant().getCultivationInformation().getVegetableType();

            if(!checkedVegetableTypes.contains(vegetableType)){

                List<Vegetable> vegetablesOfSameType = getVegetablesOfSameType(vegetableType,vegetables);

                if(vegetablesOfSameType.size() >= 5){

                    // create vegetable basket

                    VegetableBasket vegetableBasket = new VegetableBasket();
                    List<Vegetable> vegetablesForVegetableBasket = new ArrayList<>();

                    for (int i = 0; i < 5; i++){
                        vegetablesForVegetableBasket.add(packingService.getVegetable(vegetablesOfSameType.get(i).getId(),transaction));
                    }

                    transaction.commit();

                    waitPackingTime();

                    marketService.putVegetableBasket(vegetableBasket);

                    tryCreateVegetableBasket(); // try to create another vegetable basket

                    return;
                }

                checkedVegetableTypes.add(vegetableType);
            }
        }

        transaction.commit();
    }

    List<Vegetable> getVegetablesOfSameType(VegetableType vegetableType, List<Vegetable> vegetables){

        List<Vegetable> vegetablesOfSameType = new ArrayList<>();

        for (Vegetable v:vegetables) {
            if (v.getParentVegetablePlant().getCultivationInformation().getVegetableType().equals(vegetableType)){
                vegetablesOfSameType.add(v);
            }
        }

        return vegetablesOfSameType;
    }

    private Flower getDistinctFlower(List<Flower> flowersForBouquet) {

        List<FlowerType> distinctFlowerTypes = getDistinctFlowerTypes(flowersForBouquet);

        for (Flower f:flowersForBouquet) {
            if(!distinctFlowerTypes.contains(f.getParentFlowerPlant().getCultivationInformation().getFlowerType())){
                return f;
            }
        }

        return null;
    }

    private List<FlowerType> getDistinctFlowerTypes(List<Flower> flowers){
        List<FlowerType> types = new ArrayList<>();
        for (Flower f:flowers) {
            if(!types.contains(f.getParentFlowerPlant().getCultivationInformation().getFlowerType())){
                types.add(f.getParentFlowerPlant().getCultivationInformation().getFlowerType());
            }
        }

        return types;
    }

    private int getNumberOfDistinctFlowerTypes(List<Flower> flowers){
        return getDistinctFlowerTypes(flowers).size();
    }

    private void waitPackingTime(){
        int waitTime = ThreadLocalRandom.current().nextInt(1000, 3000 + 1);

        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
