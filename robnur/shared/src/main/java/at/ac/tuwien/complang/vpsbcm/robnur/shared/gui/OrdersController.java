package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui.NewOrderController;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import sun.security.krb5.Config;

import java.util.List;

public class OrdersController {
    public TableView<Order<VegetableType, Vegetable>> tvVegetableOrders;
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesId;
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesAddress;
    public TableColumn<Order<VegetableType, Vegetable>, HBox> tcVegetablesActions;
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesStatus;
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesPackRobots;
    public TableColumn<Order<VegetableType, Vegetable>, String> tcVegetablesDeliveryRobot;

    public TableView<Order<FlowerType, Flower>> tvFlowerOrders;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersId;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersAddress;
    public TableColumn<Order<FlowerType, Flower>, HBox> tcFlowersActions;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersStatus;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersPackRobots;
    public TableColumn<Order<FlowerType, Flower>, String> tcFlowersDeliveryRobot;


    private OrderService orderService = RobNurGUI.orderService;
    private ConfigService configService = RobNurGUI.configService;

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

        tcVegetablesPackRobots.setCellValueFactory(p -> new ReadOnlyStringWrapper(formatList(p.getValue().getPackRobotIds())));
        tcVegetablesDeliveryRobot.setCellValueFactory(p -> new ReadOnlyStringWrapper(""));

        tcVegetablesActions.setCellValueFactory(p -> {
            HBox hBox = new HBox();

            Button btnShowReserved = new Button();
            btnShowReserved.setText("Reservierte");
            btnShowReserved.setOnAction(event -> {
                EndProductInformationDialog dialog = new EndProductInformationDialog();
                VegetableBasket tmp = new VegetableBasket();
                tmp.setVegetables(p.getValue().getAlreadyAcquiredItems());
                dialog.show(tmp);
            });

            Button btnShowInfo = new Button();
            btnShowInfo.setText("Noch fehlende");
            btnShowInfo.setOnAction(event -> {
                NewOrderController noc = new NewOrderController("");
                noc.showVegetableOrder(configService.readAllVegetablePlantCultivationInformation(null), p.getValue());
            });

            hBox.getChildren().addAll(btnShowReserved, btnShowInfo);

            return new ReadOnlyObjectWrapper<>(hBox);
        });
    }

    private void initFlowers() {
        updateFlowersData(orderService.readAllOrdersForFlowers(null));
        orderService.onFlowerOrdersChanged(this::updateFlowersData);

        tcFlowersId.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getId()));
        tcFlowersAddress.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getAddress()));
        tcFlowersStatus.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getOrderStatus().name()));

        tcFlowersPackRobots.setCellValueFactory(p -> new ReadOnlyStringWrapper(formatList(p.getValue().getPackRobotIds())));
        tcFlowersDeliveryRobot.setCellValueFactory(p -> new ReadOnlyStringWrapper(""));

        tcFlowersActions.setCellValueFactory(p -> {
            HBox hBox = new HBox();

            Button btnShowReserved = new Button();
            btnShowReserved.setText("Reservierte");
            btnShowReserved.setOnAction(event -> {
                EndProductInformationDialog dialog = new EndProductInformationDialog();
                Bouquet tmp = new Bouquet();
                tmp.setFlowers(p.getValue().getAlreadyAcquiredItems());
                dialog.show(tmp);
            });

            Button btnShowInfo = new Button();
            btnShowInfo.setText("Noch fehlende");
            btnShowInfo.setOnAction(event -> {
                NewOrderController noc = new NewOrderController("");
                noc.showFlowerOrder(configService.readAllFlowerPlantCultivationInformation(null), p.getValue());
            });


            hBox.getChildren().addAll(btnShowReserved, btnShowInfo);

            return new ReadOnlyObjectWrapper<>(hBox);
        });
    }
}
