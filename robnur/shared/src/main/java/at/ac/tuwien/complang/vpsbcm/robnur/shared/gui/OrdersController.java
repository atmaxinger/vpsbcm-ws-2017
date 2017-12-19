package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Vegetable;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableType;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class OrdersController {
    public TableView<Order<VegetableType, Vegetable>> tvVegetableOrders;
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesId;
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesAddress;
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesActions;


    private OrderService orderService = RobNurGUI.orderService;

    @FXML
    public void initialize() {
        initVegetables();
    }

    private void updateVegetablesData(List<Order<VegetableType, Vegetable>> orders) {
        ObservableList<Order<VegetableType, Vegetable>> obs = tvVegetableOrders.getItems();
        obs.clear();
        obs.addAll(orders);

        tvVegetableOrders.refresh();
    }

    private void initVegetables() {
        updateVegetablesData(orderService.readAllOrdersForVegetables(null));
        orderService.onVegetableOrdersChanged(this::updateVegetablesData);

        tcVegetablesId.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getId()));
        tcVegetablesAddress.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getAddress()));
    }
}
