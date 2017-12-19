package at.ac.tuwien.complang.vpsbcm.robnur.shared.robots;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PackRobot extends Robot {

    private final int TRANSACTION_TIMEOUT = -1;
    private PackingService packingService;
    private MarketService marketService;
    private ResearchService researchService;
    private OrderService orderService;
    private TransactionService transactionService;

    public PackRobot(String id, PackingService packingService, MarketService marketService, ResearchService researchService, OrderService orderService, TransactionService transactionService) {
        this.setId(id);

        this.packingService = packingService;
        this.marketService = marketService;
        this.researchService = researchService;
        this.orderService = orderService;
        this.transactionService = transactionService;

        tryCreateVegetableBasket();
        tryCreateBouquet();
    }


    private void tryPutFlowersIntoResearch() {
        if (researchService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        Transaction transaction = transactionService.beginTransaction(TRANSACTION_TIMEOUT);
        List<Flower> flowers = packingService.readAllFlowers(transaction);

        if (flowers == null) {
            transaction.rollback();
            return;
        }

        int put = 0;
        for (int i = 0; i < Math.min(flowers.size(), 10); i++) {
            Flower flower = packingService.getFlower(flowers.get(i).getId(), transaction);
            if (flower == null) {
                transaction.rollback();

                tryPutFlowersIntoResearch();
                return;
            }

            flower.addPutResearchRobot(getId());
            researchService.putFlower(flower, transaction);

            logger.info(String.format("PackRobot %s: forward flower (id = %s) to research department", this.getId(), flower.getId()));

            put++;
        }

        if (!transaction.commit()) {
            transaction.rollback();
            tryPutFlowersIntoResearch();
        }

        // If we have packed 10 into research, chances are good there are more...
        if (put == 10) {
            tryPutFlowersIntoResearch();
        }
    }

    public synchronized void tryCreateBouquet() {
        if (researchService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        tryFulfilBouquetOrder();

        // check if there are already enough bouquets in the market
        if (marketService.getAmountOfBouquets() >= 5) {
            tryPutFlowersIntoResearch();
            return;
        }

        Transaction transaction = transactionService.beginTransaction(TRANSACTION_TIMEOUT);

        List<Flower> flowers = packingService.readAllFlowers(transaction);
        List<Flower> flowersForBouquet = new ArrayList<>();

        if (getNumberOfDistinctFlowerTypes(flowers) >= 3) {

            if (flowers.size() >= 5) {
                // try to create bouquet with 5 flowers (at leas 3 different types)

                flowersForBouquet.add(flowers.get(0));
                flowersForBouquet.add(flowers.get(1));
                flowersForBouquet.add(flowers.get(2));
                switch (getNumberOfDistinctFlowerTypes(flowersForBouquet)) {
                    case 1:
                        flowersForBouquet.add(getDistinctFlowerForBouquet(flowersForBouquet, flowers));
                        flowersForBouquet.add(getDistinctFlowerForBouquet(flowersForBouquet, flowers));
                        break;
                    case 2:
                        flowersForBouquet.add(flowers.get(3));
                        switch (getNumberOfDistinctFlowerTypes(flowersForBouquet)) {
                            case 2:
                                flowersForBouquet.add(getDistinctFlowerForBouquet(flowersForBouquet, flowers));
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
            } else if (flowers.size() >= 4) {
                // try to create bouquet with 4 flowers (at leas 3 different types)

                flowersForBouquet.add(flowers.get(0));
                flowersForBouquet.add(flowers.get(1));
                switch (getNumberOfDistinctFlowerTypes(flowersForBouquet)) {
                    case 1:
                        flowersForBouquet.add(getDistinctFlowerForBouquet(flowersForBouquet, flowers));
                        flowersForBouquet.add(getDistinctFlowerForBouquet(flowersForBouquet, flowers));
                        break;
                    case 2:
                        flowersForBouquet.add(flowers.get(2));
                        switch (getNumberOfDistinctFlowerTypes(flowersForBouquet)) {
                            case 2:
                                flowersForBouquet.add(getDistinctFlowerForBouquet(flowersForBouquet, flowers));
                                break;
                            case 3:
                                flowersForBouquet.add(flowers.get(3));
                                break;
                        }
                        break;
                }
            } else {
                // not enough flowers
                logger.info(String.format("PackRobot %s: not enough flowers to create bouquet", this.getId()));
                transaction.rollback();
                tryCreateBouquet();
                return;
            }

            /* remove flowers from packing-queue */
            for (Flower f : flowersForBouquet) {
                Flower flower = packingService.getFlower(f.getId(), transaction);
                if (flower == null) {
                    transaction.rollback();
                    tryCreateBouquet();
                    return;
                }
                logger.info(String.format("PackRobot %s: remove flower(%s) from packing-queue", this.getId(), f.getId()));
            }

            transaction.commit();

            waitPackingTime();

            Bouquet bouquet = new Bouquet();
            bouquet.setFlowers(flowersForBouquet);
            bouquet.setPackingRobotId(getId());

            marketService.putBouquet(bouquet, null);
            logger.info(String.format("PackRobot %s: created bouquet(%s) and put it into basket", this.getId(), bouquet.getId()));

            tryCreateBouquet();    // try to create another bouquet

            return;
        } else {
            // not enough flowers of distinct types
            transaction.commit();
            return;
        }
    }

    private void tryPutVegetablesIntoResearch() {
        if (researchService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        Transaction transaction = transactionService.beginTransaction(TRANSACTION_TIMEOUT);
        List<Vegetable> vegetables = packingService.readAllVegetables(transaction);

        if (vegetables == null) {
            transaction.rollback();
            return;
        }

        int put = 0;
        for (int i = 0; i < Math.min(vegetables.size(), 10); i++) {
            Vegetable vegetable = packingService.getVegetable(vegetables.get(i).getId(), transaction);
            if (vegetable == null) {
                transaction.rollback();

                tryPutVegetablesIntoResearch();
                return;
            }

            vegetable.addPutResearchRobot(getId());
            researchService.putVegetable(vegetable, transaction);

            logger.info(String.format("PackRobot %s: forward vegetable(%s) to research department.", this.getId(), vegetable.getId()));

            put++;
        }

        transaction.commit();

        // If we have packed 10 into research, chances are good there are more...
        if (put == 10) {
            tryPutVegetablesIntoResearch();
        }
    }

    public synchronized void tryCreateVegetableBasket() {
        if (researchService.isExit()) {
            logger.info("you can quit me now...");
            return;
        }

        tryFulfilVegetableBasketOrder();

        if (marketService.getAmountOfVegetableBaskets() >= 3) {
            tryPutVegetablesIntoResearch();
            return;
        }

        Transaction transaction = transactionService.beginTransaction(TRANSACTION_TIMEOUT);
        List<Vegetable> vegetables = packingService.readAllVegetables(transaction);

        if (vegetables.size() <= 5) {
            // not enough vegetables available
            logger.info(String.format("PackRobot %s: not enough vegetable to create vegetable basket", this.getId()));
            transaction.commit();
            return;
        }

        List<VegetableType> checkedVegetableTypes = new ArrayList();    // list of VegetableType to avoid checking a VegetableType twice

        for (Vegetable v : vegetables) {

            VegetableType vegetableType = v.getParentVegetablePlant().getCultivationInformation().getVegetableType();

            if (!checkedVegetableTypes.contains(vegetableType)) {

                List<Vegetable> vegetablesOfSameType = getVegetablesOfSameType(vegetableType, vegetables);

                if (vegetablesOfSameType.size() >= 5) {

                    // create vegetable basket

                    VegetableBasket vegetableBasket = new VegetableBasket();
                    vegetableBasket.setPackingRobotId(getId());
                    List<Vegetable> vegetablesForVegetableBasket = new ArrayList<>();

                    for (int i = 0; i < 5; i++) {
                        logger.info(String.format("PackRobot %s: remove vegetable(%s) from packing-queue", this.getId(), v.getId()));
                        Vegetable vegetable = packingService.getVegetable(vegetablesOfSameType.get(i).getId(), transaction);
                        if (vegetable == null) {
                            transaction.rollback();

                            tryCreateVegetableBasket();
                            return;
                        }
                        vegetablesForVegetableBasket.add(vegetable);
                    }

                    vegetableBasket.setVegetables(vegetablesForVegetableBasket);

                    transaction.commit();

                    waitPackingTime();

                    marketService.putVegetableBasket(vegetableBasket, null);
                    logger.info(String.format("PackRobot %s: created vegetable basket(%s) and put it into basket", this.getId(), vegetableBasket.getId()));

                    tryCreateVegetableBasket(); // try to create another vegetable basket

                    return;
                }

                checkedVegetableTypes.add(vegetableType);
            }
        }

        transaction.commit();
    }

    private void tryFulfilVegetableBasketOrder() {

        Transaction transaction = transactionService.beginTransaction(-1);
        List<Order<VegetableType,Vegetable>> orders = new ArrayList<>();

        Order<VegetableType,Vegetable> currentOrder = orderService.getNextVegetableBasketOrder(Order.OrderStatus.PLACED,transaction);

        while(currentOrder != null) {

            for (VegetableType type : VegetableType.values()) {

                while (currentOrder.getMissingItems().get(type) != null && currentOrder.getMissingItems().get(type) > 0) {

                    Vegetable vegetable = packingService.getVegetableByType(type, transaction);

                    // check if there is an appropriate vegetable
                    if (vegetable == null) {
                        break;
                    }

                    // add vegetable to order
                    currentOrder.setAlreadyAcquiredItem(vegetable, VegetableType.valueOf(vegetable.getParentVegetablePlant().getTypeName()));
                    logger.info(String.format("PackRobot %s: added vegetable (%s) to order (%s)",getId(),vegetable.getId(),currentOrder.getId()));
                }
            }

            orders.add(currentOrder);
            currentOrder = orderService.getNextVegetableBasketOrder(Order.OrderStatus.PLACED,transaction);
        }

        for (Order o:orders) {
            orderService.placeOrderForVegetableBasket(o,transaction);
            if(o.getOrderStatus() == Order.OrderStatus.PACKED){
                VegetableBasket vegetableBasket = new VegetableBasket();
                vegetableBasket.setVegetables(o.getAlreadyAcquiredItems());
                vegetableBasket.setDeliveryRobotId(getId());

                orderService.deliverVegetableBasket(vegetableBasket,o.getAddress());
                logger.info(String.format("PackRobot %s: delivered vegetable basket (%s)",getId(),vegetableBasket.getId()));
            }
        }

        transaction.commit();
    }

    private void tryFulfilBouquetOrder() {

        Transaction transaction = transactionService.beginTransaction(-1);
        List<Order<FlowerType,Flower>> orders = new ArrayList<>();

        Order<FlowerType,Flower> currentOrder = orderService.getNextBouquetOrder(Order.OrderStatus.PLACED,transaction);

        while(currentOrder != null) {

            for (FlowerType type : FlowerType.values()) {

                while (currentOrder.getMissingItems().get(type) != null && currentOrder.getMissingItems().get(type) > 0) {

                    Flower flower = packingService.getFlowerByType(type, transaction);

                    // check if there is an appropriate flower
                    if (flower == null) {
                        break;
                    }

                    // add flower to order
                    currentOrder.setAlreadyAcquiredItem(flower, FlowerType.valueOf(flower.getParentFlowerPlant().getTypeName()));
                    logger.info(String.format("PackRobot %s: added flower (%s) to order (%s)",getId(),flower.getId(),currentOrder.getId()));
                }
            }

            orders.add(currentOrder);
            currentOrder = orderService.getNextBouquetOrder(Order.OrderStatus.PLACED,transaction);
        }


        for (Order o:orders) {
            orderService.placeOrderForBouquet(o,transaction);
            if(o.getOrderStatus() == Order.OrderStatus.PACKED){
                Bouquet bouquet = new Bouquet();
                bouquet.setFlowers(o.getAlreadyAcquiredItems());
                bouquet.setDeliveryRobotId(getId());

                orderService.deliverBouquet(bouquet,o.getAddress());

                logger.info(String.format("PackRobot %s: delivered bouquet (%s)",getId(),bouquet.getId()));
            }
        }

        transaction.commit();
    }

    List<Vegetable> getVegetablesOfSameType(VegetableType vegetableType, List<Vegetable> vegetables) {

        List<Vegetable> vegetablesOfSameType = new ArrayList<>();

        for (Vegetable v : vegetables) {
            if (v.getParentVegetablePlant().getCultivationInformation().getVegetableType().equals(vegetableType)) {
                vegetablesOfSameType.add(v);
            }
        }

        return vegetablesOfSameType;
    }

    private Flower getDistinctFlowerForBouquet(List<Flower> flowersForBouquet, List<Flower> flowers) {

        List<FlowerType> distinctFlowerTypes = getDistinctFlowerTypes(flowersForBouquet);

        for (Flower f : flowers) {
            if (!distinctFlowerTypes.contains(f.getParentFlowerPlant().getCultivationInformation().getFlowerType())) {
                return f;
            }
        }

        return null;
    }

    private List<FlowerType> getDistinctFlowerTypes(List<Flower> flowers) {
        List<FlowerType> types = new ArrayList<>();
        for (Flower f : flowers) {
            if (!types.contains(f.getParentFlowerPlant().getCultivationInformation().getFlowerType())) {
                types.add(f.getParentFlowerPlant().getCultivationInformation().getFlowerType());
            }
        }

        return types;
    }

    private int getNumberOfDistinctFlowerTypes(List<Flower> flowers) {
        return getDistinctFlowerTypes(flowers).size();
    }

    private void waitPackingTime() {
        int waitTime = ThreadLocalRandom.current().nextInt(1000, 3000 + 1);

        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            logger.trace("EXCEPTION", e);
        }
    }
}
