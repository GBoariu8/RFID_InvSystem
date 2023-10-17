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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

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
        databaseAccess = new DatabaseAccessImpl();
    }

    public void Hndl_Connect(ActionEvent actionEvent) {
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
    public void Hndl_Exit(ActionEvent actionEvent) {
        Platform.exit();
    }
    public void Hndl_DBConnect(ActionEvent actionEvent){databaseAccess.showDBLoginDialog();}
    public void Hndl_DBDisconnect(ActionEvent actionEvent){databaseAccess.disconnect();}

    public void Hndl_GetPwrLvl(ActionEvent actionEvent) {
        rfidSettings.GetPwrLvl();
    }
    public void Hndl_SetPwrLvl5(ActionEvent actionEvent) {
        rfidSettings.SetPwrLvl( 5);
    }
    public void Hndl_SetPwrLvl10(ActionEvent actionEvent) {
        rfidSettings.SetPwrLvl( 10);
    }
    public void Hndl_SetPwrLvl15(ActionEvent actionEvent) {
        rfidSettings.SetPwrLvl(15);
    }
    public void Hndl_SetPwrLvl20(ActionEvent actionEvent) {
        rfidSettings.SetPwrLvl(20);
    }
    public void Hndl_SetPwrLvl25(ActionEvent actionEvent) {
        rfidSettings.SetPwrLvl(25);
    }
    public void Hndl_SetPwrLvl30(ActionEvent actionEvent) {
        rfidSettings.SetPwrLvl(30);
    }

    public void Hndl_GetRegion(ActionEvent actionEvent) {rfidSettings.GetRegion();}
    public void Hndl_SetRegionUS(ActionEvent actionEvent) {rfidSettings.SetRegion(MACROS.REGION_US);}
    public void Hndl_SetRegionChina800(ActionEvent actionEvent) {rfidSettings.SetRegion(MACROS.REGION_CHINA_800M);}
    public void Hndl_SetRegionChina900(ActionEvent actionEvent) {rfidSettings.SetRegion(MACROS.REGION_CHINA_900M);}
    public void Hndl_SetRegionEurope(ActionEvent actionEvent) {rfidSettings.SetRegion(MACROS.REGION_EUROPE);}
    public void Hndl_SetRegionKorea(ActionEvent actionEvent) {rfidSettings.SetRegion(MACROS.REGION_KOREA);}
}
