module com.example.rfid_inventorysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires adrcplib;
    requires adsiolib;
    requires usb.api;
    requires mysql.connector.j;
    requires java.sql;
    requires usb4java.javax;

    exports com.example.rfid_inventorysystem.Data.Access;
    exports com.example.rfid_inventorysystem.UI;
    exports com.example.rfid_inventorysystem.Hardware.Settings;
    exports com.example.rfid_inventorysystem.Hardware.TagsHandling;
    exports com.example.rfid_inventorysystem.Hardware.Connection;
    exports com.example.rfid_inventorysystem.Hardware.HardwareBase;
    exports com.example.rfid_inventorysystem.Service.Inventory;
    exports com.example.rfid_inventorysystem.UI.Controllers;

    opens com.example.rfid_inventorysystem to javafx.fxml, usb4java.javax;
    opens com.example.rfid_inventorysystem.UI.Controllers;
    exports com.example.rfid_inventorysystem;
}
