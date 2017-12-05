package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.EndProduct;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.plants.Harvestable;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.*;

public class EndProductInformationDialog {
    private TableView<Harvestable> tvParts;

    private TableColumn<Harvestable, String> tcPartId;
    private TableColumn<Harvestable, String> tcType;
    private TableColumn<Harvestable, String> tcParentId;
    private TableColumn<Harvestable, String> tcPlantRobotId;
    private TableColumn<Harvestable, String> tcHarvestRobotId;

    private void init() {
        tvParts = new TableView<>();

        tcPartId = new TableColumn<>();
        tcType = new TableColumn<>();
        tcParentId = new TableColumn<>();
        tcPlantRobotId = new TableColumn<>();
        tcHarvestRobotId = new TableColumn<>();

        tcPlantRobotId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getPlantRobot()));
        tcPlantRobotId.setText("Pflanz Roboter");

        tcHarvestRobotId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getHarvestRobot()));
        tcHarvestRobotId.setText("Ernte Roboter");

        tcType.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getTypeName()));
        tcType.setText("Art");

        tcParentId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getParentPlant().getId()));
        tcParentId.setText("Eltern Pflanze");

        tcPartId.setCellValueFactory(column -> new ReadOnlyStringWrapper(column.getValue().getId()));
        tcPartId.setText("Id");

        tvParts.getColumns().addAll(tcPartId, tcType, tcParentId, tcPlantRobotId, tcHarvestRobotId);
    }

    public void show(EndProduct endProduct) {
        init();
        tvParts.getItems().addAll(endProduct.getParts());

        Dialog<Harvestable> dialog = new Dialog<>();

        dialog.setTitle("Produkt Informationen");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);


        dialog.getDialogPane().setContent(tvParts);
        dialog.setResizable(true);
        dialog.showAndWait();
    }
}
