package com.example.rfid_inventorysystem.Hardware.Connection;

public interface RFIDConnection {
    boolean connect();
    void disconnect();
    boolean isConnected();
}
