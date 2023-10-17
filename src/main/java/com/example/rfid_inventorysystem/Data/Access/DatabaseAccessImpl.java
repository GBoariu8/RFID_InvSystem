package com.example.rfid_inventorysystem.Data.Access;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.sql.*;
import java.util.Optional;

public class DatabaseAccessImpl implements DatabaseAccess{
    private Connection connection;

    @Override
    public void disconnect() {
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
                alert.setContentText("Failed to disconnect from the database.");
                alert.showAndWait();

                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void insertData(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "')";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void printData() {
        ResultSet rs;
        String query = "SELECT * FROM " + "users";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            int columnCount = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getMetaData().getColumnName(i) + "\t");
            }
            System.out.println();

            // Print all records
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void showDBLoginDialog(){
        // Create the custom dialog
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
            connectToMySQL(url, user, pass);
        });
    }

    private void connectToMySQL(String url, String username, String password) {
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
                        + "date DATE NOT NULL"
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
}
