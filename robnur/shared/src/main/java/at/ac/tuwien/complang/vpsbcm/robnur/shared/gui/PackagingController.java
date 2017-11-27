package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.PackingService;
import com.sun.tools.javac.comp.Flow;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class PackagingController {

    private PackingService packingService = RobNurGUI.packingService;

    public TableView<Vegetable> tvVegetables;
    public TableColumn<Vegetable, String> tcVegetablesId;
    public TableColumn<Vegetable, String> tcVegetablesType;
    public TableColumn<Vegetable, String> tcVegetablesParent;

    public TableView<Flower> tvFlowers;
    public TableColumn<Flower, String> tcFlowersId;
    public TableColumn<Flower, String> tcFlowersType;
    public TableColumn<Flower, String> tcFlowerParent;

    @FXML
    public void initialize() {
        initVegsTable();
        initFlowersTable();
    }


    private void initVegsTable() {
        packingService.onVegetablesChanged(this::initVegsData);

        tcVegetablesId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcVegetablesType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentVegetablePlant().getCultivationInformation().getVegetableType().toString()));
        tcVegetablesParent.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentVegetablePlant().getId()));

        initVegsData(packingService.readAllVegetables(null));
    }

    private void initFlowersTable() {
        packingService.onFlowersChanged(this::initFlowerData);

        tcFlowersId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcFlowersType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentFlowerPlant().getCultivationInformation().getFlowerType().toString()));
        tcFlowerParent.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentFlowerPlant().getId()));

        initFlowerData(packingService.readAllFlowers(null));
    }


    private void initVegsData(List<Vegetable> vegs) {
        ObservableList<Vegetable> obs = tvVegetables.getItems();
        obs.clear();
        obs.addAll(vegs);
    }

    private void initFlowerData(List<Flower> flowers) {
        ObservableList<Flower> obs = tvFlowers.getItems();
        obs.clear();
        obs.addAll(flowers);
    }
}
