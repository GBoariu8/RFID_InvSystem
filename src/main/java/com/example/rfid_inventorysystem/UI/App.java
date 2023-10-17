package com.example.rfid_inventorysystem.UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/rfid_inventorysystem/rfid_gui.fxml")));
        stage.setTitle("RFID Inventory System");

        Scene scene = new Scene(root, 1000, 650);

        stage.setScene(scene);

        // Set the minimum size of the window
        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
