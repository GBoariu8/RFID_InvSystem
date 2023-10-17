package com.example.rfid_inventorysystem.UI.Controllers;

import com.example.rfid_inventorysystem.Hardware.TagsHandling.TagsHandling;
import com.example.rfid_inventorysystem.Hardware.TagsHandling.TagsHandlingImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class TabsController {
    private TagsHandling tagsHandling;
    @FXML
    private TextField productNameField;

    @FXML
    private TextField supplierField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField responsibleField;

    public void initialize(){
        tagsHandling = new TagsHandlingImpl();
    }

    public void Hndl_ReadTag(ActionEvent actionEvent) {
        tagsHandling.readTag();
    }

    public void Hndl_AddItem(ActionEvent actionEvent) {
        String productName = productNameField.getText();
        String supplier = supplierField.getText();
        if(!priceField.getText().isEmpty()){int price = Integer.parseInt(priceField.getText());}
        else{int price = 0;}
        String responsible = responsibleField.getText();

        // Implement your logic to read the tag and save these attributes

        // Show a pop-up indicating successful saving
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Item");
        alert.setHeaderText(null);
        alert.setContentText("The tag has been successfully saved with the given attributes.");
        alert.showAndWait();
    }
}
