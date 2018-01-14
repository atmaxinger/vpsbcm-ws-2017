package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ResearchController {

    public TableView<Vegetable> tvVegetables;
    public TableColumn<Vegetable, Integer> tcVegetablesIndex;
    public TableColumn<Vegetable, String> tcVegetablesId;
    public TableColumn<Vegetable, String> tcVegetablesPlantId;
    public TableColumn<Vegetable, String> tcVegetablesType;
    public TableColumn<Vegetable, String> tcVegetablesRobots;
    public TableColumn<Vegetable, String> tcVegetablesPlantRobot;
    public TableColumn<Vegetable, String> tcVegetablesHarvestRobot;

    public TableView<Flower> tvFlowers;
    public TableColumn<Flower, Integer> tcFlowersIndex;
    public TableColumn<Flower, String> tcFlowersId;
    public TableColumn<Flower, String> tcFlowersPlantId;
    public TableColumn<Flower, String> tcFlowersType;
    public TableColumn<Flower, String> tcFlowersRobots;
    public TableColumn<Flower, String> tcFlowersPlantRobot;
    public TableColumn<Flower, String> tcFlowersHarvestRobot;


    private ResearchService researchService = RobNurGUI.researchService;

    private String formatList(List<String> list) {
        String s="";

        for(int i=0; i<list.size(); i++) {
            s += list.get(i);
            if(i < list.size()-1) {
                s += ", ";
            }
        }

        return s;
    }

    @FXML
    public void initialize() {
        initializeVegeTable();
        initializeFlowersTable();
    }

    private synchronized void updateVegetableData(List<Vegetable> vegetableList) {
        Platform.runLater(() -> {
            ObservableList<Vegetable> obs = tvVegetables.getItems();
            obs.clear();
            obs.addAll(vegetableList);
        });
    }

    private synchronized void updateFlowerData(List<Flower> flowers) {
        Platform.runLater(() -> {
            ObservableList<Flower> obs = tvFlowers.getItems();
            obs.clear();
            obs.addAll(flowers);
        });
    }

    // wow - such creative - much thought - very wow
    private void initializeVegeTable() {
        tcVegetablesIndex.setSortable(false);
        tcVegetablesIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue())+1));

        tcVegetablesId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcVegetablesPlantId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getId()));
        tcVegetablesType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getTypeName()));
        tcVegetablesRobots.setCellValueFactory(column -> new ReadOnlyStringWrapper(formatList(column.getValue().getPutResearchRobots())));
        tcVegetablesPlantRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getPlantRobot()));
        tcVegetablesHarvestRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getHarvestRobot()));

        updateVegetableData(researchService.readAllVegetables(null));

        researchService.onVegetablesChanged(this::updateVegetableData);
    }

    private void initializeFlowersTable() {
        tcFlowersIndex.setSortable(false);
        tcFlowersIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue())+1));

        tcFlowersId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcFlowersPlantId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getId()));
        tcFlowersType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getTypeName()));
        tcFlowersRobots.setCellValueFactory(column -> new ReadOnlyStringWrapper(formatList(column.getValue().getPutResearchRobots())));
        tcFlowersPlantRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getPlantRobot()));
        tcFlowersHarvestRobot.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getHarvestRobot()));

        updateFlowerData(researchService.readAllFlowers(null));

        researchService.onFlowersChanged(this::updateFlowerData);
    }
}
