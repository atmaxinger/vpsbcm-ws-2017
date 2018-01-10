package at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.gui.EndProductInformationDialog;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Bouquet;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.EndProduct;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetableBasket;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.DeliveryStorageService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class VegetableDeliveryController {
    public TableView<VegetableBasket> tableView;
    public TableColumn<VegetableBasket, Integer> tcIndex;
    public TableColumn<VegetableBasket, String> tcVegetableBasketId;
    public TableColumn<VegetableBasket, String> tcPackRobot;
    public TableColumn<VegetableBasket, String> tcDeliveryRobot;
    public TableColumn<EndProduct, Button> tcActions;

    private DeliveryStorageService deliveryStorageService = CustomerGUI.deliveryStorageService;

    private void initData(List<VegetableBasket> list) {
        ObservableList<VegetableBasket> obs = tableView.getItems();
        obs.clear();
        obs.addAll(list);
        tableView.refresh();
    }

    @FXML
    public void initialize() {
        initData(deliveryStorageService.readAllVegetableBaskets());
        deliveryStorageService.onVegetableBasketsChanged(this::initData);

        tcIndex.setSortable(false);
        tcIndex.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(column.getTableView().getItems().indexOf(column.getValue())+1));

        tcVegetableBasketId.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getId()));
        tcPackRobot.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getPackingRobotIdsAsString()));
        tcDeliveryRobot.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getDeliveryRobotId()));
        tcActions.setCellValueFactory(p -> {
            Button btn = new Button();
            btn.setText("Info");
            btn.setOnAction(event -> {
                EndProductInformationDialog dialog = new EndProductInformationDialog();
                dialog.show(p.getValue());
            });

            return new ReadOnlyObjectWrapper<>(btn);
        });
    }
}
