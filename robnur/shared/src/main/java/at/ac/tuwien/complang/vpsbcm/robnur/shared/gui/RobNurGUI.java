package at.ac.tuwien.complang.vpsbcm.robnur.shared.gui;

import at.ac.tuwien.complang.vpsbcm.robnur.shared.services.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class RobNurGUI extends Application {

    public static CompostService compostService;
    public static ConfigService configService;
    public static GreenhouseService greenhouseService;
    public static MarketService marketService;
    public static PackingService packingService;
    public static StorageService storageService;
    public static ResearchService researchService;
    public static TransactionService transactionService;

    public void execute(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TabPane root = null;

        URL resource = getClass().getResource("main.fxml");

        try {
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
