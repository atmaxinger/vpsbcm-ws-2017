package at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.DeliveryStorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class CustomerGUI extends Application {
    final static Logger logger = Logger.getLogger(CustomerGUI.class);

    public static ConfigService configService;
    public static OrderService orderService;
    public static String address;
    public static DeliveryStorageService deliveryStorageService;

    private String[] args;

    public void execute(String[] args) {
        this.args = args;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Accordion root = null;

        try {
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        } catch (IOException e) {
            logger.trace("EXCEPTION", e);
        }
        Scene scene = new Scene(root);

        primaryStage.setOnCloseRequest(value -> {
            runDialog();
        });

        primaryStage.setTitle("Blumenshop");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void runDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Beenden");
        alert.setHeaderText("Soll das Programm beendet werden oder nur die GUI neu gestartet werden?");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeQuit = new ButtonType("Programm beenden");
        ButtonType buttonTypeRestart = new ButtonType("GUI neu starten");

        alert.getButtonTypes().setAll(buttonTypeQuit, buttonTypeRestart);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeRestart){
            Stage stage = new Stage();
            start(stage);
        } else if (result.get() == buttonTypeQuit) {
            System.exit(0);
        }
    }
}
