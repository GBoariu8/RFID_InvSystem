module com.example.rfid_inventorysystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rfid_inventorysystem to javafx.fxml;
    exports com.example.rfid_inventorysystem;
}