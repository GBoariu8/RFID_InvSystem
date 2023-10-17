package com.example.rfid_inventorysystem.Service;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FeedbackService {
    private static final FeedbackService INSTANCE = new FeedbackService();
    private TextArea feedbackTextArea;

    private FeedbackService() {}

    public static FeedbackService getInstance() {
        return INSTANCE;
    }

    public void setFeedbackTextArea(TextArea feedbackTextArea) {
        this.feedbackTextArea = feedbackTextArea;
    }

    public void updateFeedback(String message) {
        if (feedbackTextArea != null) {
            Platform.runLater(() -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String formattedDateTime = LocalDateTime.now().format(formatter);
                String newMessage = formattedDateTime + " " + message + "\n";
                feedbackTextArea.setText(newMessage + feedbackTextArea.getText());
            });
        }
    }
    public void clearTextArea(){
        if (feedbackTextArea != null) {
            Platform.runLater(() -> {
                feedbackTextArea.setText("");
            });
        }
    }
}
