package at.ac.tuwien.complang.vpsbcm.robnur.shared.customergui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

public class CustomerGUI extends Application {
    final static Logger logger = Logger.getLogger(CustomerGUI.class);

    public void execute(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = null;

        URL resource = getClass().getResource("main.fxml");

        try {
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        } catch (IOException e) {
            logger.trace("EXCEPTION", e);
        }
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
