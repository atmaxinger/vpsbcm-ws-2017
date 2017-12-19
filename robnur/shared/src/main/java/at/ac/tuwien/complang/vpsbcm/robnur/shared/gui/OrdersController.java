package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Flower;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerType;
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
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesStatus;

    public TableView<Order<FlowerType, Flower>> tvFlowerOrders;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersId;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersAddress;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersActions;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersStatus;


    private OrderService orderService = RobNurGUI.orderService;

    @FXML
    public void initialize() {
        initVegetables();
        initFlowers();
    }

    private void updateFlowersData(List<Order<FlowerType, Flower>> orders) {
        ObservableList<Order<FlowerType, Flower>> obs = tvFlowerOrders.getItems();
        obs.clear();
        obs.addAll(orders);

        tvFlowerOrders.refresh();
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
        tcVegetablesStatus.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getOrderStatus().name()));
    }

    private void initFlowers() {
        updateFlowersData(orderService.readAllOrdersForFlowers(null));
        orderService.onFlowerOrdersChanged(this::updateFlowersData);

        tcFlowersId.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getId()));
        tcFlowersAddress.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getAddress()));
        tcFlowersStatus.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getOrderStatus().name()));
    }
}
