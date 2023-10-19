package com.example.rfid_inventorysystem.Data.Access;

import java.sql.ResultSet;
import java.util.Map;

public interface DatabaseAccess {
    boolean isConnected();
    void DBDisconnect();
    void DBConnect(String url, String username, String password);
    ResultSet getAllItems();
    ResultSet getTagByEPC(String EPC);
    String DBGetEPCByPName(String productName);
    boolean DBDeleteRecord(Integer id);
    boolean DBUpdateRecord(int id, Map<String, String> updatedData);
    boolean DBUpdateDate(String EPC);
    boolean DBUpdatePickUp(int id);
    boolean DBIsEPCinDB(String epc);
    boolean DBAddItem(Map<String, String> itemData);
    boolean DBExportToExcel();
}
