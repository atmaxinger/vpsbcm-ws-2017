package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Plant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlant;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.GreenhouseService;
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
    public TableColumn<Plant, String> tcGrowth;
    public TableColumn<Plant, String> tcRemainingHarvets;
    public TableColumn<Plant, String> tcPlantId;
    public TableColumn<Plant, String> tcPlantedBy;
    public TableColumn<Plant, String> tcInfestation;
    public TableColumn<Plant, String> tcFosterRobots;

    private GreenhouseService greenhouseService = RobNurGUI.greenhouseService;


    private synchronized void updateData(List<Plant> data) {
        ObservableList<Plant> plants = greenHouse.getItems();
        plants.clear();
        plants.addAll(data);
    }

    @FXML
    public void initialize() {
        greenhouseService.onGreenhouseChanged(data -> updateData(data));

        tcIndex.setSortable(false);
        tcIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue())+1));

        tcPlantId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));

        tcPlant.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTypeName()));

        tcGrowth.setCellValueFactory(param -> {
            String s = "";

            int growth = param.getValue().getGrowth();
            if(growth == Plant.STATUS_PLANTED) {
                s = "Angepflanzt";
            } else if(growth == Plant.STATUS_LIMP) {
                s = "Welk";
            }
            else if(growth >= 100) {
                s = "Erntebereit";
            }
            else {
                s = String.format("%d", growth);
            }

            return new ReadOnlyStringWrapper(s);
        });

        tcPlantedBy.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getPlantRobot()));

        tcRemainingHarvets.setCellValueFactory(param -> {
            String rem = "-";

            if(param.getValue() instanceof VegetablePlant) {
                rem = "" + ((VegetablePlant) param.getValue()).getCultivationInformation().getRemainingNumberOfHarvests();
            }

            return new ReadOnlyStringWrapper(rem);
        });

        tcInfestation.setCellValueFactory(column -> {
            String s = String.format("%d%%", Math.round(column.getValue().getInfestation() * 100));
            return new ReadOnlyStringWrapper(s);
        });

        tcFosterRobots.setCellValueFactory(column -> {
            StringBuilder s = new StringBuilder();
            List<String> robots = column.getValue().getFosterRobots();

            for(int i=0; i<robots.size(); i++) {
                s.append(robots.get(i));
                if(i < robots.size() - 1) {
                    s.append(", ");
                }
            }

            return new ReadOnlyStringWrapper(s.toString());
        });


        List<Plant> data = new LinkedList<>();
        data.addAll(greenhouseService.readAllFlowerPlants());
        data.addAll(greenhouseService.readAllVegetablePlants());
        updateData(data);
    }
}
