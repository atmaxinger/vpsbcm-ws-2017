package at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.FlowerPlantCultivationInformation;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.VegetablePlantCultivationInformation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.LinkedList;
import java.util.List;

public class OrdersController {
    public Button btnOrderFlowers;
    public Button btnOrderVegetables;


    List<FlowerPlantCultivationInformation> fpcis = new LinkedList<>();
    List<VegetablePlantCultivationInformation> vpcis = new LinkedList<>();

    @FXML
    public void initialize() {
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
