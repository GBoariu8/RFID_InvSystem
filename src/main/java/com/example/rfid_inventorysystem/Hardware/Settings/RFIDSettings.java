package com.example.rfid_inventorysystem.Hardware.Settings;

public interface RFIDSettings {
    void GetPwrLvl();
    void SetPwrLvl(int PwrLevel);
    void GetRegion();
    void SetRegion(byte Region);
}
