package com.example.rfid_inventorysystem.UI.Controllers;

import com.example.rfid_inventorysystem.Service.FeedbackService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class FeedbackTextAreaController {
    @FXML
    private TextArea feedbackTextArea;

    @FXML
    public void initialize() {
        FeedbackService.getInstance().setFeedbackTextArea(feedbackTextArea);
    }
}
