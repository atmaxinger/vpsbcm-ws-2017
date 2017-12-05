package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ResearchService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ResearchController {

    public TableView<Vegetable> tvVegetables;
    public TableColumn<Vegetable, String> tcVegetablesId;
    public TableColumn<Vegetable, String> tcVegetablesPlantId;
    public TableColumn<Vegetable, String> tcVegetablesType;
    public TableView<Flower> tvFlowers;
    public TableColumn<Flower, String> tcFlowersId;
    public TableColumn<Flower, String> tcFlowersPlantId;
    public TableColumn<Flower, String> tcFlowersType;

    private ResearchService researchService = RobNurGUI.researchService;

    @FXML
    public void initialize() {
        initializeVegeTable();
        initializeFlowersTable();
    }

    private void updateVegetableData(List<Vegetable> vegetableList) {
        ObservableList<Vegetable> obs = tvVegetables.getItems();
        obs.clear();
        obs.addAll(vegetableList);
    }

    private void updateFlowerData(List<Flower> flowers) {
        ObservableList<Flower> obs = tvFlowers.getItems();
        obs.clear();
        obs.addAll(flowers);
    }

    // wow - such creative - much thought - very wow
    private void initializeVegeTable() {
        tcVegetablesId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcVegetablesPlantId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getId()));
        tcVegetablesType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getTypeName()));

        updateVegetableData(researchService.readAllVegetables(null));

        researchService.onVegetablesChanged(this::updateVegetableData);
    }

    private void initializeFlowersTable() {
        tcFlowersId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcFlowersPlantId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getId()));
        tcFlowersType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getTypeName()));

        updateFlowerData(researchService.readAllFlowers(null));

        researchService.onFlowersChanged(this::updateFlowerData);
    }
}
