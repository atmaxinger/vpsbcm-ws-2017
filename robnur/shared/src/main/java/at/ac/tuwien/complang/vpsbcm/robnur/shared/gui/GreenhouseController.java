package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.StorageService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.LinkedList;
import java.util.List;

public class GreenhouseController {
    public TableView<Plant> greenHouse;
    public TableColumn<Plant, Integer> tcIndex;
    public TableColumn<Plant, String> tcPlant;
    public TableColumn<Plant, Integer> tcGrowth;
    public TableColumn<Plant, String> tcRemainingHarvets;
    public TableColumn<Plant, String> tcPlantId;
    public TableColumn<Plant, String> tcPlantedBy;

    private GreenhouseService greenhouseService = RobNurGUI.greenhouseService;


    void updateData(List<Plant> data) {
        ObservableList<Plant> plants = greenHouse.getItems();
        plants.clear();
        plants.addAll(data);
    }

    @FXML
    public void initialize() {
        greenhouseService.onGreenhouseChanged(data -> updateData(data));

        tcIndex.setSortable(false);
        tcIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(greenHouse.getItems().indexOf(column.getValue())+1));

        tcPlantId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));

        tcPlant.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTypeName()));

        tcGrowth.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getGrowth()));

        tcPlantedBy.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getPlantRobot()));

        tcRemainingHarvets.setCellValueFactory(param -> {
            String rem = "-";

            if(param.getValue() instanceof VegetablePlant) {
                rem = "" + ((VegetablePlant) param.getValue()).getCultivationInformation().getRemainingNumberOfHarvests();
            }

            return new ReadOnlyStringWrapper(rem);
        });


        List<Plant> data = new LinkedList<>();
        data.addAll(greenhouseService.readAllFlowerPlants());
        data.addAll(greenhouseService.readAllVegetablePlants());
        updateData(data);
    }
}
