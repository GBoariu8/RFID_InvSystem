package com.example.rfid_inventorysystem.UI.Controllers;

import com.ad.comm.usb.SioUsb2;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.rcp.RcpMM;
import com.example.rfid_inventorysystem.Data.Access.DatabaseAccess;
import com.example.rfid_inventorysystem.Data.Access.DatabaseAccessImpl;
import com.example.rfid_inventorysystem.Hardware.HardwareBase.ADRcp;
import com.example.rfid_inventorysystem.Hardware.HardwareBase.ADSio;
import com.example.rfid_inventorysystem.Hardware.TagsHandling.TagsHandling;
import com.example.rfid_inventorysystem.Hardware.TagsHandling.TagsHandlingImpl;
import com.example.rfid_inventorysystem.Service.Inventory.TagModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TabsController {

    private TagsHandling tagsHandling;
    private DatabaseAccess databaseAccess;
    private final ADRcp adRcp = ADRcp.getInstance();
    private final SioUsb2 sio = ADSio.getInstance().getSio();
    private boolean isContinuousReading = false;
    @FXML
    public TextField productNameField;

    @FXML
    public TextField supplierField;

    @FXML
    public TextField priceField;

    @FXML
    public TextField responsibleField;
    @FXML
    public TableView<TagModel> itemsTableView;
    public TableColumn<TagModel, String> productNameColumn;
    public TableColumn<TagModel, String> supplierColumn;
    public TableColumn<TagModel, Integer> priceColumn;
    public TableColumn<TagModel, String> responsibleColumn;

    public TextField searchTextField;
    ObservableList<TagModel> tagModelObservableList = FXCollections.observableArrayList();
    FilteredList<TagModel> filteredData = new FilteredList<>(tagModelObservableList, b->true);

    public void initialize(){
        tagsHandling = new TagsHandlingImpl();
        databaseAccess = DatabaseAccessImpl.getInstance();
        itemsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                getSelectedTableRow(newSelection);
            }
        });
    }

    public void Hndl_ReadTag() throws InterruptedException {
        if(tagsHandling.readNewTag()){
            tagsHandling.getTagInfo();
        }
    }
    public void Hndl_Loop(){
        isContinuousReading = !isContinuousReading;

        if(isContinuousReading){
            new Thread(() -> {
                while (isContinuousReading){
                    if (tagsHandling.readNewTag()){
                        String oldEPC = tagsHandling.getOldEPC();
                        if (databaseAccess.DBIsEPCinDB(oldEPC)){
                            databaseAccess.DBUpdateDate(oldEPC);
                        }
                    }
                }
            }).start();
        }
    }
    public void Hndl_AddItem() {
        tagsHandling.readNewTag();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(adRcp.getCurrentEPC().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No tag scanned.");
            alert.showAndWait();
        }
        else if(!databaseAccess.DBIsEPCinDB(adRcp.getCurrentEPC())){
            Dialog<Map<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add Item");
            dialog.setHeaderText("Add the selected item details.");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField productName = new TextField();
            TextField supplierName = new TextField();
            TextField price = new TextField();
            TextField responsible = new TextField();

            grid.add(new Label("Product Name:"), 0, 0);
            grid.add(productName, 1, 0);
            grid.add(new Label("Supplier Name:"), 0, 1);
            grid.add(supplierName, 1, 1);
            grid.add(new Label("Price:"), 0, 2);
            grid.add(price, 1, 2);
            grid.add(new Label("Responsible:"), 0, 3);
            grid.add(responsible, 1, 3);

            dialog.getDialogPane().setContent(grid);

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    Map<String, String> results = new HashMap<>();
                    results.put("epc", adRcp.getCurrentEPC());
                    results.put("productName", productName.getText());
                    results.put("supplierName", supplierName.getText());
                    results.put("price", price.getText());
                    results.put("responsible", responsible.getText());
                    results.put("date", dtf.format(LocalDateTime.now()));
                    return results;
                }
                return null;
            });

            Optional<Map<String, String>> result = dialog.showAndWait();

            result.ifPresent(addedData -> {
                boolean isAdded = databaseAccess.DBAddItem(addedData);
                if (isAdded) {
                    refreshTable();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Item added successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Could not connect to the database.");
                    alert.showAndWait();
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Tag already linked to an item.");
            alert.showAndWait();
        }
    }
    public void Hndl_DeleteItem() {
        TagModel selectedTagModel = itemsTableView.getSelectionModel().getSelectedItem();

        if (selectedTagModel == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("No item selected for deletion.");
            alert.showAndWait();
            return;
        }

        Integer selectedId = selectedTagModel.getId();
        boolean isDeleted = databaseAccess.DBDeleteRecord(selectedId);

        if (isDeleted) {
            tagModelObservableList.remove(selectedTagModel);
            itemsTableView.refresh();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Item deleted successfully.");
            alert.showAndWait();
        }
    }
    public void Hndl_UpdateItem() {
        TagModel selectedTagModel = itemsTableView.getSelectionModel().getSelectedItem();
        if (selectedTagModel == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No Item selected to be updated.");
            alert.showAndWait();
            return;
        }

        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update Item");
        dialog.setHeaderText("Update the selected item details.");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField productName = new TextField(selectedTagModel.getProductName());
        TextField supplierName = new TextField(selectedTagModel.getSuppName());
        TextField price = new TextField(selectedTagModel.getPrice().toString());
        TextField responsible = new TextField(selectedTagModel.getResponsible());

        grid.add(new Label("Product Name:"), 0, 0);
        grid.add(productName, 1, 0);
        grid.add(new Label("Supplier Name:"), 0, 1);
        grid.add(supplierName, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(price, 1, 2);
        grid.add(new Label("Responsible:"), 0, 3);
        grid.add(responsible, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                Map<String, String> results = new HashMap<>();
                results.put("productName", productName.getText());
                results.put("supplierName", supplierName.getText());
                results.put("price", price.getText());
                results.put("responsible", responsible.getText());
                return results;
            }
            return null;
        });

        Optional<Map<String, String>> result = dialog.showAndWait();

        result.ifPresent(updatedData -> {
            int selectedID = selectedTagModel.getId();
            boolean isUpdated = databaseAccess.DBUpdateRecord(selectedID, updatedData);
            if (isUpdated) {
                selectedTagModel.setProductName(updatedData.get("productName"));
                selectedTagModel.setSuppName(updatedData.get("supplierName"));
                selectedTagModel.setPrice(Integer.parseInt(updatedData.get("price")));
                selectedTagModel.setResponsible(updatedData.get("responsible"));

                itemsTableView.refresh();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Item updated successfully.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Could not connect to the database.");
                alert.showAndWait();
            }
        });
    }
    public void refreshTable() {
        try {
            tagModelObservableList.clear();

            ResultSet selectResult = databaseAccess.getAllItems();
            while (selectResult.next()) {
                Integer id = selectResult.getInt("id");
                String productName = selectResult.getString("productName");
                String suppName = selectResult.getString("suppName");
                Integer price = selectResult.getInt("price");
                String responsible = selectResult.getString("responsible");
                tagModelObservableList.add(new TagModel(id, productName, suppName, responsible, price));
            }
            productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
            supplierColumn.setCellValueFactory(new PropertyValueFactory<>("suppName"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            responsibleColumn.setCellValueFactory(new PropertyValueFactory<>("responsible"));

            itemsTableView.setItems(tagModelObservableList);
            itemsTableView.refresh();

            searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(tagModel -> {
                    if(newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                        return true;
                    }
                    String searchKeyword = newValue.toLowerCase();

                    if(tagModel.getProductName().toLowerCase().contains(searchKeyword)) {
                        return true;
                    } else if ((tagModel.getSuppName().toLowerCase().contains(searchKeyword))) {
                        return true;
                    } else if ((tagModel.getPrice().toString().toLowerCase().contains(searchKeyword))) {
                        return true;
                    } else if ((tagModel.getResponsible().toLowerCase().contains(searchKeyword))) {
                        return true;
                    } else {
                        return false;
                    }
                });

                SortedList<TagModel> sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(itemsTableView.comparatorProperty());
                itemsTableView.setItems(sortedData);
            });
        } catch (SQLException e) {
            Logger.getLogger(TabsController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
    public void Hndl_AllowPickUp() {
        TagModel selectedTagModel = itemsTableView.getSelectionModel().getSelectedItem();
        if (selectedTagModel == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No Item selected to be updated.");
            alert.showAndWait();
            return;
        }

        int selectedID = selectedTagModel.getId();
        boolean isUpdated = databaseAccess.DBUpdatePickUp(selectedID);

        if (isUpdated) {
            refreshTable();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Permission updated successfully.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update permission.");
            alert.showAndWait();
        }
    }
    private void getSelectedTableRow(TagModel selectedItem){
        String selectedEPC = selectedItem.getEPC();
        ResultSet rs = databaseAccess.getTagByEPC(selectedEPC);
        try {
            if (rs.next()) {
                String productName = rs.getString("productName");
                String suppName = rs.getString("suppName");
                Integer price = rs.getInt("price");
                String responsible = rs.getString("responsible");

                // Update the UI with these details, like TextFields
                productNameField.setText(productName);
                supplierField.setText(suppName);
                priceField.setText(price.toString());
                responsibleField.setText(responsible);
            }
        } catch (SQLException e) {
            Logger.getLogger(TabsController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
}
