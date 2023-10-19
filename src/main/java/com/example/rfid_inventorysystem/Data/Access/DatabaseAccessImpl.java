package com.example.rfid_inventorysystem.Data.Access;

import javafx.scene.control.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DatabaseAccessImpl implements DatabaseAccess{
    private static final DatabaseAccessImpl INSTANCE = new DatabaseAccessImpl();
    public static DatabaseAccessImpl getInstance(){return INSTANCE;}
    private Connection connection;

    public boolean isConnected() {
        try {
            return (connection != null && connection.isValid(2));
        } catch (SQLException e) {
            // Log the exception here, if needed
            return false;
        }
    }
    public void DBConnect(String url, String username, String password){
        try {
            connection = DriverManager.getConnection(url, username, password);
            String databaseName = url.substring(url.lastIndexOf("/") + 1);
            Statement stmt = connection.createStatement();
            String checkTableSQL = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + databaseName + "' AND TABLE_NAME = 'inventory_items'";
            ResultSet rs = stmt.executeQuery(checkTableSQL);
            rs.next();
            int tableExists = rs.getInt(1);
            if (tableExists == 0) {
                String createTableSQL = "CREATE TABLE inventory_items ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "EPC VARCHAR(255) NOT NULL,"
                        + "productName VARCHAR(255) NOT NULL,"
                        + "suppName VARCHAR(255),"
                        + "price INT,"
                        + "responsible VARCHAR(255),"
                        + "permission BOOLEAN DEFAULT FALSE,"
                        + "date DATETIME NOT NULL"
                        + ");";
                stmt.executeUpdate(createTableSQL);
            }

            // Do something with the connection
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Connected to the database successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not connect to the database.");
            alert.showAndWait();
        }
    }
    public void DBDisconnect() {
        if (connection != null) {
            try {
                connection.close();

                // Show a popup indicating successful disconnection
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Database Disconnection");
                alert.setHeaderText(null);
                alert.setContentText("Successfully disconnected from the database.");
                alert.showAndWait();

            } catch (SQLException e) {
                // Show a popup indicating failure
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Disconnection");
                alert.setHeaderText(null);
                alert.setContentText("Failed to DBDisconnect from the database.");
                alert.showAndWait();

                throw new RuntimeException(e);
            }
        }
    }
    public ResultSet getAllItems(){
        String query = "SELECT id, productName, suppName, price, responsible FROM inventory_items";
        Statement statement;

        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Cannot connect to database.");
            alert.showAndWait();
            throw new RuntimeException(e);
        }

        return resultSet;
    }
    public ResultSet getTagByEPC(String EPC) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try{
            String query = "SELECT * FROM inventory_items WHERE EPC = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, EPC);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e){
            throw new RuntimeException("Error fetching item with EPC: " + EPC, e);
        }
        return resultSet;
    }
    public boolean DBDeleteRecord(Integer id) {
        String deleteQuery = "DELETE FROM inventory_items WHERE id = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Item deleted successfully!");
                alert.showAndWait();
                return true;
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("No item found with ID: " + id);
                alert.showAndWait();
                return false;
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not delete the item with ID: " + id);
            alert.showAndWait();
            throw new RuntimeException("Error deleting item with ID: " + id, e);
        }
    }
    public boolean DBUpdateRecord(int id, Map<String, String> updatedData) {
        String query = "UPDATE inventory_items SET productName=?, suppName=?, price=?, responsible=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, updatedData.get("productName"));
            preparedStatement.setString(2, updatedData.get("supplierName"));
            preparedStatement.setInt(3, Integer.parseInt(updatedData.get("price")));
            preparedStatement.setString(4, updatedData.get("responsible"));
            preparedStatement.setInt(5, id);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean DBUpdateDate(String EPC){
        String query = "UPDATE inventory_items SET date=? WHERE EPC=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String date = dtf.format(LocalDateTime.now());
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, EPC);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean DBUpdatePickUp(int id){
        String fetchQuery = "SELECT permission FROM inventory_items WHERE id = ?";
        int currentPermission = -1;

        try (PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery)) {
            fetchStatement.setInt(1, id);
            ResultSet resultSet = fetchStatement.executeQuery();
            if (resultSet.next()) {
                currentPermission = resultSet.getInt("permission");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (currentPermission == -1) {
            return false;
        }

        int newPermission = (currentPermission == 1) ? 0 : 1;

        String updateQuery = "UPDATE inventory_items SET permission = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, newPermission);
            preparedStatement.setInt(2, id);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean DBAddItem(Map<String, String> itemData){
        String query = "INSERT INTO inventory_items (epc, productName, suppName, price, responsible, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, (String) itemData.get("epc"));
            preparedStatement.setString(2, (String) itemData.get("productName"));
            preparedStatement.setString(3, (String) itemData.get("supplierName"));
            preparedStatement.setInt(4, Integer.parseInt(itemData.get("price")));
            preparedStatement.setString(5, (String) itemData.get("responsible"));
            preparedStatement.setString(6, (String) itemData.get("date"));
            int affectedRows = preparedStatement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean DBIsEPCinDB(String epc){
        String query = "SELECT COUNT(*) FROM inventory_items WHERE epc = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, epc);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean DBExportToExcel(){
        boolean result = false;
        String query = "SELECT productName, suppName, price, responsible FROM inventory_items";
        Statement statement;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventory");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Product Name");
        headerRow.createCell(1).setCellValue("Supplier Name");
        headerRow.createCell(2).setCellValue("Price");
        headerRow.createCell(3).setCellValue("Responsible");

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            int rowCount = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(resultSet.getString("productName"));
                row.createCell(1).setCellValue(resultSet.getString("suppName"));
                row.createCell(2).setCellValue(resultSet.getInt("price"));
                row.createCell(3).setCellValue(resultSet.getString("responsible"));
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");
            String date = dtf.format(LocalDateTime.now());

            try (FileOutputStream fileOut = new FileOutputStream("Inventory_" + date + ".xlsx")) {
                workbook.write(fileOut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            workbook.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String DBGetEPCByPName(String productName){
        String query = "SELECT EPC FROM inventory_items WHERE product_name = ?";
        PreparedStatement preparedStatement;
        String EPC = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, productName);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                EPC = rs.getString("EPC");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching EPC for product: " + productName, e);
        }

        return EPC;
    }
}
