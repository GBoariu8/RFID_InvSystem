package com.example.rfid_inventorysystem.UI.Controllers;

import com.example.rfid_inventorysystem.Data.Access.DatabaseAccess;
import com.example.rfid_inventorysystem.Data.Access.DatabaseAccessImpl;
import com.example.rfid_inventorysystem.Service.FeedbackService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FooterController {

    @FXML
    private Label FXML_TimeDataLabel;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private DatabaseAccess databaseAccess;
    @FXML
    public void initialize() {
        databaseAccess = new DatabaseAccessImpl();
        startClock();
    }

    public void Hndl_ClearTextArea(ActionEvent actionEvent) {
        FeedbackService.getInstance().clearTextArea();
    }

    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime currentTime = LocalDateTime.now();
            FXML_TimeDataLabel.setText(currentTime.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }
}
