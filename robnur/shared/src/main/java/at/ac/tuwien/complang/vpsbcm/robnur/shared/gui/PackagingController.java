package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class PackagingController {


    private PackingService packingService = RobNurGUI.packingService;

    public TableView<Vegetable> tvVegetables;
    public TableColumn<Vegetable, Integer> tcVegetablesIndex;
    public TableColumn<Vegetable, String> tcVegetablesId;
    public TableColumn<Vegetable, String> tcVegetablesType;
    public TableColumn<Vegetable, String> tcVegetablesParent;
    public TableColumn<Vegetable, String> tcVegetablesPlantedBy;
    public TableColumn<Vegetable, String> tcVegetablesHarvestedBy;

    public TableView<Flower> tvFlowers;
    public TableColumn<Flower, Integer> tcFlowersIndex;
    public TableColumn<Flower, String> tcFlowersId;
    public TableColumn<Flower, String> tcFlowersType;
    public TableColumn<Flower, String> tcFlowerParent;
    public TableColumn<Flower, String> tcFlowersPlantedBy;
    public TableColumn<Flower, String> tcFlowersHarvestedBy;

    @FXML
    public void initialize() {
        initVegsTable();
        initFlowersTable();
    }


    private void initVegsTable() {
        packingService.onVegetablesChanged(this::initVegsData);

        tcVegetablesIndex.setSortable(false);
        tcVegetablesIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue())+1));

        tcVegetablesId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcVegetablesType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentVegetablePlant().getCultivationInformation().getVegetableType().toString()));
        tcVegetablesParent.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentVegetablePlant().getId()));

        tcVegetablesPlantedBy.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentVegetablePlant().getPlantRobot()));
        tcVegetablesHarvestedBy.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getHarvestRobot()));

        initVegsData(packingService.readAllVegetables(null));
    }

    private void initFlowersTable() {
        packingService.onFlowersChanged(this::initFlowerData);

        tcFlowersIndex.setSortable(false);
        tcFlowersIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue())+1));

        tcFlowersId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcFlowersType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentFlowerPlant().getCultivationInformation().getFlowerType().toString()));
        tcFlowerParent.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentFlowerPlant().getId()));

        tcFlowersPlantedBy.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentFlowerPlant().getPlantRobot()));
        tcFlowersHarvestedBy.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getHarvestRobot()));

        initFlowerData(packingService.readAllFlowers(null));
    }


    private synchronized void initVegsData(List<Vegetable> vegs) {
        ObservableList<Vegetable> obs = tvVegetables.getItems();
        obs.clear();
        obs.addAll(vegs);
    }

    private synchronized void initFlowerData(List<Flower> flowers) {
        ObservableList<Flower> obs = tvFlowers.getItems();
        obs.clear();
        obs.addAll(flowers);
    }
}
