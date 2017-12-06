package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.CompostService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class CompostController {

    public TableView<FlowerPlant> tvFlowerPlants;
    public TableColumn<FlowerPlant, String> tcFlowerPlantsId;
    public TableColumn<FlowerPlant, String> tcFlowerPlantsType;
    public TableColumn<FlowerPlant, String> tcFlowerPlantsPlantRobot;
    public TableColumn<FlowerPlant, String> tcFlowerPlantsCompostRobot;

    public TableView<Flower> tvFlowers;
    public TableColumn<Flower, String> tcFlowersId;
    public TableColumn<Flower, String> tcFlowersType;
    public TableColumn<Flower, String> tcFlowersParent;
    public TableColumn<Flower, String> tcFlowersPlantRobot;
    public TableColumn<Flower, String> tcFlowersHarvestRobot;
    public TableColumn<Flower, String> tcFlowersCompostRobot;

    public TableView<VegetablePlant> tvVegetablePlants;
    public TableColumn<VegetablePlant, String> tcVegetablePlantsId;
    public TableColumn<VegetablePlant, String> tcVegetablePlantsType;
    public TableColumn<VegetablePlant, String> tcVegetablePlantsPlantRobot;
    public TableColumn<VegetablePlant, String> tcVegetablePlantsCompostRobot;

    public TableView<Vegetable> tvVegetables;
    public TableColumn<Vegetable, String> tcVegetablesId;
    public TableColumn<Vegetable, String> tcVegetablesType;
    public TableColumn<Vegetable, String> tcVegetablesParent;
    public TableColumn<Vegetable, String> tcVegetablesPlantRobot;
    public TableColumn<Vegetable, String> tcVegetablesHarvestRobot;
    public TableColumn<Vegetable, String> tcVegetablesCompostRobot;


    private CompostService compostService = RobNurGUI.compostService;

    @FXML
    public void initialize() {
        initFlowerPlantsTable();
        initFlowersTable();
        initVegetablePlantsTable();
        initVegetablesTable();
    }

    private void setFlowerPlantsTableData(List<FlowerPlant> plants) {
        ObservableList<FlowerPlant> obs = tvFlowerPlants.getItems();
        obs.clear();
        obs.addAll(plants);
    }
    private void initFlowerPlantsTable() {
        setFlowerPlantsTableData(compostService.readAllFlowerPlants());
        compostService.onFlowerPlantsChanged(this::setFlowerPlantsTableData);

        tcFlowerPlantsId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcFlowerPlantsType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getTypeName()));
        tcFlowerPlantsPlantRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getPlantRobot()));
        tcFlowerPlantsCompostRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getCompostRobot()));
    }

    private void setFlowersTableData(List<Flower> flowers) {
        ObservableList<Flower> obs = tvFlowers.getItems();
        obs.clear();
        obs.addAll(flowers);
    }
    private void initFlowersTable() {
        setFlowersTableData(compostService.readAllFlowers());
        compostService.onFlowersChanged(this::setFlowersTableData);

        tcFlowersId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcFlowersType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getTypeName()));
        tcFlowersParent.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getId()));
        tcFlowersPlantRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getPlantRobot()));
        tcFlowersHarvestRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getHarvestRobot()));
        tcFlowersCompostRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getCompostRobot()));
    }


    private void setVegetablePlantsTableData(List<VegetablePlant> plants) {
        ObservableList<VegetablePlant> obs = tvVegetablePlants.getItems();
        obs.clear();
        obs.addAll(plants);
    }
    private void initVegetablePlantsTable() {
        setVegetablePlantsTableData(compostService.readAllVegetablePlants());
        compostService.onVegetablePlantsChanged(this::setVegetablePlantsTableData);

        tcVegetablePlantsId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcVegetablePlantsType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getTypeName()));
        tcVegetablePlantsPlantRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getPlantRobot()));
        tcVegetablePlantsCompostRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getCompostRobot()));
    }

    private void setVegetablesTableData(List<Vegetable> Vegetables) {
        ObservableList<Vegetable> obs = tvVegetables.getItems();
        obs.clear();
        obs.addAll(Vegetables);
    }
    private void initVegetablesTable() {
        setVegetablesTableData(compostService.readAllVegetables());
        compostService.onVegetablesChanged(this::setVegetablesTableData);

        tcVegetablesId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcVegetablesType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getTypeName()));
        tcVegetablesParent.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getId()));
        tcVegetablesPlantRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getPlantRobot()));
        tcVegetablesHarvestRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getHarvestRobot()));
        tcVegetablesCompostRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getCompostRobot()));
    }
}
