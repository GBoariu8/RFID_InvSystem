package com.example.rfid_inventorysystem.Data.Access;

import java.sql.ResultSet;
import java.util.Map;

public interface DatabaseAccess {
    boolean DBIsConnected();
    void DBDisconnect();
    void DBConnect(String url, String username, String password);
    ResultSet DBGetAllItems();
    ResultSet DBGetTagByEPC(String EPC);
    ResultSet DBGetTagByID(String id);
    boolean DBDeleteRecord(Integer id);
    boolean DBUpdateRecord(int id, Map<String, String> updatedData);
    void DBUpdateDate(String EPC);
    boolean DBUpdatePickUp(int id);
    boolean DBIsEPCinDB(String epc);
    boolean DBAddItem(Map<String, String> itemData);
    void DBExportToExcel();
}
