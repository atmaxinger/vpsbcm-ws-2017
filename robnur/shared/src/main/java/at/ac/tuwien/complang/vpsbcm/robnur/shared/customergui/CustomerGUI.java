package at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.ConfigService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.DeliveryStorageService;
import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.OrderService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class CustomerGUI extends Application {
    final static Logger logger = Logger.getLogger(CustomerGUI.class);

    public static ConfigService configService;
    public static OrderService orderService;
    public static String address;
    public static DeliveryStorageService deliveryStorageService;

    public void execute(String[] args) {
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

        primaryStage.setTitle("Blumenshop");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
