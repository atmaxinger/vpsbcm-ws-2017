package at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.Order;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class NewOrderController {

    String address;

    public NewOrderController(String address) {
        this.address = address;
    }

    List<VegetablePlantCultivationInformation> vpci = new LinkedList<>();
    List<FlowerPlantCultivationInformation> fpci = new LinkedList<>();

    private class TableData {
        Enum e;
        int price = 0;
        int count = 0;
    }

    private TableView<TableData> tableView = new TableView<>();
    private TableColumn<TableData, Enum> tcType = new TableColumn<>();
    private TableColumn<TableData, Number> tcCount = new TableColumn<>();
    private TableColumn<TableData, String> tcSinglePrice = new TableColumn<>();
    private TableColumn<TableData, HBox> tcActions = new TableColumn<>();


    private int getPrice(VegetableType type) {
        int p = -1;

        for(VegetablePlantCultivationInformation c : vpci) {
            if(c.getVegetableType() == type) {
                return c.getPrice();
            }
        }

        return p;
    }

    private int getPrice(FlowerType type) {
        int p = -1;

        for(FlowerPlantCultivationInformation c : fpci) {
            if(c.getFlowerType() == type) {
                return c.getPrice();
            }
        }

        return p;
    }


    private void initialize() {
        tcType.setText("Art");
        tcType.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(column.getValue().e));

        tcCount.setText("Anzahl");
        tcCount.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().count));

        tcCount.setEditable(true);

        tcSinglePrice.setText("Einzelpreis");
        tcSinglePrice.setCellValueFactory(p -> new ReadOnlyStringWrapper(String.format("%.02f €", ((float)p.getValue().price)/100.0f)));

        tcActions.setCellValueFactory(p -> {
            HBox box = new HBox();

            Button btnPlus = new Button();
            btnPlus.setText("+");
            btnPlus.setOnAction(event -> {
                p.getValue().count++;
                tableView.refresh();
            });

            Button btnMinus = new Button();
            btnMinus.setText("-");
            btnMinus.setOnAction(event -> {
                p.getValue().count--;
                p.getValue().count = Math.max(0, p.getValue().count);
                tableView.refresh();
            });

            box.getChildren().addAll(btnPlus, btnMinus);

            return new ReadOnlyObjectWrapper<>(box);
        });

        tableView.getColumns().addAll(tcType, tcCount, tcSinglePrice, tcActions);
    }

    public void showVegetableOrderNew(List<VegetablePlantCultivationInformation> vpci) {
        this.vpci = vpci;

        initialize();

        ObservableList<TableData> obs = tableView.getItems();

        for(VegetableType type : VegetableType.values()) {
            TableData td = new TableData();
            td.e = type;
            td.price = getPrice(type);
            obs.add(td);
        }
        tableView.refresh();

        Dialog<Order<VegetableType, Vegetable>> dialog = new Dialog<>();

        ButtonType buyButtonType = new ButtonType("Kaufen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, ButtonType.CANCEL);

        dialog.setTitle("Gemüse Bestellen");

        dialog.getDialogPane().setContent(tableView);


        dialog.setResizable(true);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buyButtonType) {
                Order<VegetableType, Vegetable> vt = new Order<>();
                vt.setAddress(address);
                List<TableData> tds = tableView.getItems();
                for(TableData td : tds) {
                    VegetableType t = (VegetableType)td.e;
                    int count = td.count;

                    vt.setPlantAmount(t, count);
                }

                return vt;
            }
            return null;
        });

        Optional<Order<VegetableType, Vegetable>> result = dialog.showAndWait();
        result.ifPresent(order -> CustomerGUI.orderService.placeOrderForVegetableBasket(order, null));
    }

    public void showFlowerOrderNew(List<FlowerPlantCultivationInformation> fpci) {
        this.fpci = fpci;

        initialize();

        ObservableList<TableData> obs = tableView.getItems();

        for(FlowerType type : FlowerType.values()) {
            TableData td = new TableData();
            td.e = type;
            td.count = 0;
            td.price = getPrice(type);
            obs.add(td);
        }
        tableView.refresh();

        Dialog<Order<FlowerType, Flower>> dialog = new Dialog<>();

        ButtonType buyButtonType = new ButtonType("Kaufen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getButtonTypes().addAll(buyButtonType, ButtonType.CANCEL);

        dialog.setTitle("Blumen Bestellen");

        dialog.getDialogPane().setContent(tableView);


        dialog.setResizable(true);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buyButtonType) {
                Order<FlowerType, Flower> vt = new Order<>();
                vt.setAddress(address);
                List<TableData> tds = tableView.getItems();
                for(TableData td : tds) {
                    FlowerType t = (FlowerType) td.e;
                    int count = td.count;

                    vt.setPlantAmount(t, count);
                }

                return vt;
            }
            return null;
        });

        Optional<Order<FlowerType, Flower>> result = dialog.showAndWait();
        result.ifPresent(order -> CustomerGUI.orderService.placeOrderForBouquet(order, null));
    }
}

