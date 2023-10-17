package com.example.rfid_inventorysystem.Data.Access;

public interface DatabaseAccess {
    void disconnect();
    void insertData(String username, String password);
    void printData();
    void showDBLoginDialog();
}
