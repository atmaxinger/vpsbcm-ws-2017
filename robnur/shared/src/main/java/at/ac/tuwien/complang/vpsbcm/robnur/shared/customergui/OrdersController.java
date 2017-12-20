package at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.LinkedList;
import java.util.List;

public class OrdersController {
    public Button btnOrderFlowers;
    public Button btnOrderVegetables;
    public HBox box;


    List<FlowerPlantCultivationInformation> fpcis = new LinkedList<>();
    List<VegetablePlantCultivationInformation> vpcis = new LinkedList<>();


    private void updateCanPlaceOrder(boolean canPlaceOrder) {
        box.setDisable(!canPlaceOrder);
    }

    @FXML
    public void initialize() {
        updateCanPlaceOrder(CustomerGUI.orderService.canPlaceOrder(CustomerGUI.address));
        CustomerGUI.orderService.onCanPlaceOrderChanged(p -> {
            updateCanPlaceOrder(CustomerGUI.orderService.canPlaceOrder(CustomerGUI.address));
        });

        vpcis = CustomerGUI.configService.readAllVegetablePlantCultivationInformation(null);
        fpcis = CustomerGUI.configService.readAllFlowerPlantCultivationInformation(null);

        btnOrderFlowers.setOnAction(event -> {
            NewOrderController no = new NewOrderController(CustomerGUI.address);
            no.showFlowerOrderNew(fpcis);
        });

        btnOrderVegetables.setOnAction(event -> {
            NewOrderController no = new NewOrderController(CustomerGUI.address);
            no.showVegetableOrderNew(vpcis);
        });
    }
}
