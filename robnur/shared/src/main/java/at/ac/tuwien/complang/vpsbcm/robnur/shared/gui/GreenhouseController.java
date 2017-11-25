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

        tcPlant.setCellValueFactory(param ->{
            String type="unknown";
            Plant p = param.getValue();

            if(p instanceof VegetablePlant) {
                type = ((VegetablePlant) p).getCultivationInformation().getVegetableType().toString();
            } else if(p instanceof FlowerPlant) {
                type = ((FlowerPlant) p).getCultivationInformation().getFlowerType().toString();
            }

            return new ReadOnlyStringWrapper(type);
        });

        tcGrowth.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getGrowth()));

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