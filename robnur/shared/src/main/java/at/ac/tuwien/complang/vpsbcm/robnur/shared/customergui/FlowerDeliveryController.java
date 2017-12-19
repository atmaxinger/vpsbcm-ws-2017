package at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.DeliveryStorageService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class FlowerDeliveryController {
    public TableView<Bouquet> tableView;
    public TableColumn<Bouquet, Integer> tcIndex;
    public TableColumn<Bouquet, String> tcBouqetId;
    public TableColumn<Bouquet, String> tcPackRobot;
    public TableColumn<Bouquet, String> tcDeliveryRobot;

    private DeliveryStorageService deliveryStorageService = CustomerGUI.deliveryStorageService;

    private void initData(List<Bouquet> bouquetList) {
        ObservableList<Bouquet> obs = tableView.getItems();
        obs.clear();
        obs.addAll(bouquetList);
        tableView.refresh();
    }

    @FXML
    public void initialize() {
        initData(deliveryStorageService.readAllBouqets());
        deliveryStorageService.onBouqetsChanged(this::initData);

        tcIndex.setSortable(false);
        tcIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue())+1));

        tcBouqetId.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getId()));
        tcBouqetId.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getPackingRobotId()));
        tcBouqetId.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getDeliveryRobotId()));
    }
}
