package com.example.rfid_inventorysystem.UI.Controllers;

import com.example.rfid_inventorysystem.Data.Access.DatabaseAccess;
import com.example.rfid_inventorysystem.Data.Access.DatabaseAccessImpl;
import com.example.rfid_inventorysystem.Hardware.Connection.RFIDConnection;
import com.example.rfid_inventorysystem.Hardware.Connection.RFIDConnectionImpl;
import com.example.rfid_inventorysystem.Hardware.Parameters.MACROS;
import com.example.rfid_inventorysystem.Hardware.Settings.RFIDSettings;
import com.example.rfid_inventorysystem.Hardware.Settings.RFIDSettingsImpl;
import com.example.rfid_inventorysystem.Service.FeedbackService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

public class MenuController {
    private RFIDConnection rfidConnection;
    private RFIDSettings rfidSettings;
    private DatabaseAccess databaseAccess;
    @FXML
    private MenuItem FXML_Connect;
    @FXML private Label FXML_ConnectionStatus;
    public void initialize(){
        rfidConnection = new RFIDConnectionImpl();
        rfidSettings = new RFIDSettingsImpl();
        databaseAccess = DatabaseAccessImpl.getInstance();

    }

    public void Hndl_Connect() {
        if(rfidConnection.isConnected()){
            rfidConnection.disconnect();
            FXML_Connect.setText("Connect");
            FXML_ConnectionStatus.setText("Status: NOT CONNECTED");
            FeedbackService.getInstance().updateFeedback("DISCONNECTED");
        }
        else{
            if(rfidConnection.connect()){
                FXML_Connect.setText("Disconnect");
                FXML_ConnectionStatus.setText("Status: CONNECTED");
                FeedbackService.getInstance().updateFeedback("CONNECTED");
            }
        }
    }
    public void Hndl_Exit() {
        Platform.exit();
    }
    public void Hndl_DBConnect(){
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText("Enter your login credentials.");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        TextField dbUrl = new TextField();
        dbUrl.setPromptText("Database URL");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("Database URL:"), 0, 2);
        grid.add(dbUrl, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            String url = dbUrl.getText();
            String user = usernamePassword.getKey();
            String pass = usernamePassword.getValue();
            databaseAccess.DBConnect(url, user, pass);
        });
    }
    public void Hndl_DBDisconnect(){databaseAccess.DBDisconnect();}
    public void HndlExportDB() {
        databaseAccess.DBExportToExcel();
    }

    public void Hndl_GetPwrLvl() {
        rfidSettings.GetPwrLvl();
    }
    public void Hndl_SetPwrLvl5() {
        rfidSettings.SetPwrLvl( 5);
    }
    public void Hndl_SetPwrLvl10() {
        rfidSettings.SetPwrLvl( 10);
    }
    public void Hndl_SetPwrLvl15() {
        rfidSettings.SetPwrLvl(15);
    }
    public void Hndl_SetPwrLvl20() {
        rfidSettings.SetPwrLvl(20);
    }
    public void Hndl_SetPwrLvl25() {
        rfidSettings.SetPwrLvl(25);
    }
    public void Hndl_SetPwrLvl30() {
        rfidSettings.SetPwrLvl(30);
    }

    public void Hndl_GetRegion() {rfidSettings.GetRegion();}
    public void Hndl_SetRegionUS() {rfidSettings.SetRegion(MACROS.REGION_US);}
    public void Hndl_SetRegionChina800() {rfidSettings.SetRegion(MACROS.REGION_CHINA_800M);}
    public void Hndl_SetRegionChina900() {rfidSettings.SetRegion(MACROS.REGION_CHINA_900M);}
    public void Hndl_SetRegionEurope() {rfidSettings.SetRegion(MACROS.REGION_EUROPE);}
    public void Hndl_SetRegionKorea() {rfidSettings.SetRegion(MACROS.REGION_KOREA);}
}
