package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.resouces.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.Transaction;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.TransactionService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class StorageController {

    private StorageService storageService = RobNurGUI.storageService;
    private ConfigService configService = RobNurGUI.configService;
    private TransactionService transactionService = RobNurGUI.transactionService;

    public TableView<SeedsTableDataModel> tvSeeds;
    public TableColumn<SeedsTableDataModel, String> tcSeedType;
    public TableColumn<SeedsTableDataModel, String> tcSeedCount;
    public TableColumn<SeedsTableDataModel, Button> tcSeedBuy;

    public TableView<ResourceTableDataModel> tvResources;
    public TableColumn<ResourceTableDataModel, String> tcResourcesArt;
    public TableColumn<ResourceTableDataModel, String> tcResourcesAmountStatus;
    public TableColumn<ResourceTableDataModel, Button> tcResourcesBuy;

    private abstract class Buyable {
        abstract void buyAction(int amount);
    }

    private abstract class SeedsTableDataModel extends Buyable {
        String type;
        boolean isFlower=true;
        int fertilizer;
        int soil;
        int water;
        int amount;
    }

    private abstract class ResourceTableDataModel extends Buyable {
        String resource;
        String amount;
        boolean canBuy = true;

        abstract void buyAction(int amount);
    }

    public synchronized void showAndWaitForBuyDialog(Buyable buyable, String name) {
        Dialog<Integer> dialog = new Dialog<>();

        dialog.setTitle("Resource Kaufen");
        dialog.setHeaderText(String.format("Wie viel %s wollen Sie kaufen?", name));

        ButtonType buyButtonType = new ButtonType("Kaufen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, ButtonType.CANCEL);


        TextField tf = new TextField("1");
        tf.lengthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = tf.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9' )) {
                        // if it's not number then just setText to previous one
                        tf.setText(tf.getText().substring(0,tf.getText().length()-1));
                    }
                }
            }
        });

        dialog.getDialogPane().setContent(tf);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buyButtonType) {
                return Integer.parseInt(tf.getText());
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(amount -> buyable.buyAction(amount));
    }

    public StorageController() {
    }

    @FXML
    public void initialize() {
        initSeedsTableView();
        initResourcesTableView();
    }

    private synchronized int getCountOfFlowerSeed(List<Plant> allSeeds, FlowerType type) {
        int count=0;

        for(Plant p : allSeeds) {
            if(p instanceof FlowerPlant) {
                if(((FlowerPlant) p).getCultivationInformation().getFlowerType() == type) {
                    count++;
                }
            }
        }

        return count;
    }

    private synchronized int getCountOfVeggiSeeds(List<Plant> allSeeds, VegetableType type) {
        int count=0;

        for(Plant p : allSeeds) {
            if(p instanceof VegetablePlant) {
                if(((VegetablePlant) p).getCultivationInformation().getVegetableType() == type) {
                    count++;
                }
            }
        }

        return count;
    }


    private synchronized void initSeedsData() {
        List<Plant> allSeeds = new LinkedList<>();
        allSeeds.addAll(storageService.readAllVegetableSeeds());
        allSeeds.addAll(storageService.readAllFlowerSeeds());

        ObservableList<SeedsTableDataModel> obs = tvSeeds.getItems();
        obs.clear();

        List<FlowerPlantCultivationInformation> fcpis = configService.readAllFlowerPlantCultivationInformation(null);
        for(FlowerPlantCultivationInformation fcpi : fcpis) {
            SeedsTableDataModel dataModel = new SeedsTableDataModel() {
                @Override
                void buyAction(int amount) {
                    Transaction t = transactionService.beginTransaction(-1);
                    List<FlowerPlant> flowerSeeds = new LinkedList<>();
                    for(int i=0;i<amount; i++) {
                        FlowerPlant fp = new FlowerPlant();
                        fp.setCultivationInformation(fcpi);
                        fp.setGrowth(Plant.STATUS_PLANTED);

                        flowerSeeds.add(fp);
                    }
                    storageService.putFlowerSeeds(flowerSeeds, t);
                    t.commit();
                }
            };
            dataModel.amount =  getCountOfFlowerSeed(allSeeds, fcpi.getFlowerType());
            dataModel.type = fcpi.getFlowerType().toString();
            dataModel.fertilizer = fcpi.getFertilizerAmount();
            dataModel.soil = fcpi.getSoilAmount();
            dataModel.water = fcpi.getWaterAmount();
            dataModel.isFlower = true;

            obs.add(dataModel);
        }

        List<VegetablePlantCultivationInformation> vpis = configService.readAllVegetablePlantCultivationInformation(null);
        for(VegetablePlantCultivationInformation vpci : vpis) {
            SeedsTableDataModel dataModel = new SeedsTableDataModel() {
                @Override
                void buyAction(int amount) {
                    Transaction t = transactionService.beginTransaction(-1);
                    List<VegetablePlant> vegetableSeeds = new LinkedList<>();
                    for(int i=0; i<amount; i++) {
                        VegetablePlant vp = new VegetablePlant();
                        vp.setCultivationInformation(vpci);
                        vp.setGrowth(Plant.STATUS_PLANTED);

                        vegetableSeeds.add(vp);
                    }

                    storageService.putVegetableSeeds(vegetableSeeds, t);
                    t.commit();
                }
            };
            dataModel.isFlower = false;
            dataModel.amount =  getCountOfVeggiSeeds(allSeeds, vpci.getVegetableType());
            dataModel.type = vpci.getVegetableType().toString();
            dataModel.fertilizer = vpci.getFertilizerAmount();
            dataModel.soil = vpci.getSoilAmount();
            dataModel.water = vpci.getWaterAmount();

            obs.add(dataModel);
        }
    }

    private synchronized void initSeedsTableView() {
        initSeedsData();

        storageService.onFlowerSeedChanged(data -> {
            for (SeedsTableDataModel dm : tvSeeds.getItems()) {
                if(dm.isFlower) {
                    dm.amount=0;
                }
            }

            for(FlowerPlant plant : data) {
                for (SeedsTableDataModel dm : tvSeeds.getItems()) {
                    if(dm.type.equals(plant.getTypeName())) {
                        dm.amount+=1;
                    }
                }
            }

            tvSeeds.refresh();
        });
        storageService.onVegetableSeedsChanged(data -> {
            for (SeedsTableDataModel dm : tvSeeds.getItems()) {
                if(!dm.isFlower) {
                    dm.amount=0;
                }
            }
            for(VegetablePlant plant : data) {
                for (SeedsTableDataModel dm : tvSeeds.getItems()) {
                    if(dm.type.equals(plant.getTypeName())) {
                        dm.amount+=1;
                    }
                }
            }

            tvSeeds.refresh();
        });

        tcSeedType.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().type));
        tcSeedCount.setCellValueFactory(param -> new ReadOnlyStringWrapper("" + param.getValue().amount));

        tcSeedBuy.setCellValueFactory(param -> {
            Button btnBuy = new Button("Kaufen");
            btnBuy.setOnAction(event -> showAndWaitForBuyDialog(param.getValue(), param.getValue().type));

            return new ReadOnlyObjectWrapper<>(btnBuy);
        });
    }


    private synchronized void initResourcesData() {
        ObservableList<ResourceTableDataModel> obs = tvResources.getItems();
        obs.clear();

        ResourceTableDataModel soil = new ResourceTableDataModel() {
            @Override
            void buyAction(int amount) {
                Transaction t = transactionService.beginTransaction(-1);
                List<SoilPackage> soilPackages = new LinkedList<>();
                for(int i=0; i< amount; i++) {
                    SoilPackage sp = new SoilPackage();
                    soilPackages.add(sp);
                }

                storageService.putSoilPackages(soilPackages, t);
                t.commit();
            }
        };
        soil.resource = "Erde";
        List<SoilPackage> soilPackages = storageService.readAllSoilPackage();
        if(soilPackages != null) {
            soil.amount = "" + soilPackages.size();

            // count liters
            int liters=0;
            for(SoilPackage sp : soilPackages) {
                liters += sp.getAmount();
            }
            soil.amount += String.format(" (%d Liter)", liters);
        } else {
            soil.amount = "0";
        }


        ResourceTableDataModel flowerFertilizer = new ResourceTableDataModel() {
            @Override
            void buyAction(int amount) {
                Transaction t = transactionService.beginTransaction(-1);

                List<FlowerFertilizer> flowerFertilizers = new LinkedList<>();
                for(int i=0; i<amount; i++) {
                    FlowerFertilizer ff = new FlowerFertilizer();
                    flowerFertilizers.add(ff);
                }

                storageService.putFlowerFertilizers(flowerFertilizers, t);
                t.commit();
            }
        };
        flowerFertilizer.resource = "Blumen Dünger";
        List tmp = storageService.readAllFlowerFertilizer();
        if(tmp != null) {
            flowerFertilizer.amount = "" + tmp.size();
        } else {
            flowerFertilizer.amount = "0";
        }


        ResourceTableDataModel vegFert = new ResourceTableDataModel() {
            @Override
            void buyAction(int amount) {
                Transaction t = transactionService.beginTransaction(-1);

                List<VegetableFertilizer> vegetableFertilizers = new LinkedList<>();
                for(int i=0; i<amount; i++) {
                    VegetableFertilizer vf = new VegetableFertilizer();
                    vegetableFertilizers.add(vf);
                }

                storageService.putVegetableFertilizers(vegetableFertilizers, t);
                t.commit();
            }
        };
        vegFert.resource = "Gemüse Dünger";
        tmp = storageService.readAllVegetableFertilizer();
        if(tmp != null) {
            vegFert.amount = "" + tmp.size();
        } else {
            vegFert.amount = "0";
        }

        ResourceTableDataModel flowerPesticide = new ResourceTableDataModel() {
            @Override
            void buyAction(int amount) {
                Transaction t = transactionService.beginTransaction(-1);

                List<FlowerPesticide> flowerPesticides = new LinkedList<>();
                for(int i=0; i<amount; i++) {
                    FlowerPesticide ff = new FlowerPesticide();
                    flowerPesticides.add(ff);
                }

                storageService.putFlowerPesticides(flowerPesticides, t);
                t.commit();
            }
        };
        flowerPesticide.resource = "Blumen Schutzmittel";
        tmp = storageService.readAllFlowerPesticides(null);
        if(tmp != null) {
            flowerPesticide.amount = "" + tmp.size();
        } else {
            flowerPesticide.amount = "0";
        }

        ResourceTableDataModel vegetablePesticide = new ResourceTableDataModel() {
            @Override
            void buyAction(int amount) {
                Transaction t = transactionService.beginTransaction(-1);

                List<VegetablePesticide> vegetablePesticides = new LinkedList<>();
                for(int i=0; i<amount; i++) {
                    VegetablePesticide pesticide = new VegetablePesticide();
                    vegetablePesticides.add(pesticide);
                }

                storageService.putVegetablePesticides(vegetablePesticides, t);
                t.commit();
            }
        };
        vegetablePesticide.resource = "Gemüse Schutzmittel";
        tmp = storageService.readAllVegetablePesticides(null);
        if(tmp != null) {
            vegetablePesticide.amount = "" + tmp.size();
        } else {
            vegetablePesticide.amount = "0";
        }


        obs.addAll(soil, flowerFertilizer, vegFert, flowerPesticide, vegetablePesticide, waterModel);
    }


    ResourceTableDataModel waterModel;

    private void initResourcesTableView() {
        waterModel = new ResourceTableDataModel() {
            @Override
            void buyAction(int amount) {
            }
        };
        waterModel.resource = "Wasser";
        waterModel.amount = "frei";
        waterModel.canBuy = false;

        initResourcesData();

        storageService.onFlowerFertilizerChanged(data -> initResourcesData());
        storageService.onSoilPackagesChanged(data -> initResourcesData());
        storageService.onVegetableFertilizerChanged(data -> initResourcesData());
        storageService.onWaterRobotChanged(data -> {
            if(data == null) {
                waterModel.amount = "frei";
            }
            else {
                waterModel.amount = "Robot " + data;
            }
            tvResources.refresh();
        });
        storageService.onFlowerPesticidesChanged(data -> initResourcesData());
        storageService.onVegetablePesticidesChanged(data -> initResourcesData());

        tcResourcesArt.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().resource));
        tcResourcesAmountStatus.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().amount));
        tcResourcesBuy.setCellValueFactory(param -> {
            if(!param.getValue().canBuy) {
                return null;
            }

            Button btnBuy = new Button("Kaufen");
            btnBuy.setOnAction(event -> showAndWaitForBuyDialog(param.getValue(), param.getValue().resource));

            return new ReadOnlyObjectWrapper<>(btnBuy);
        });
    }
}
