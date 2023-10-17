package com.example.rfid_inventorysystem.Hardware.Settings;

import com.ad.comm.usb.SioUsb2;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.rcp.RcpMM;
import com.example.rfid_inventorysystem.Hardware.HardwareBase.ADSio;

public class RFIDSettingsImpl implements RFIDSettings{
    SioUsb2 sio = ADSio.getInstance().getSio();
    @Override
    public void GetPwrLvl() {
        sio.send(new ProtocolPacket(65535, RcpMM.RCP_MM_GET_TX_PWR, RcpBase.RCP_MSG_CMD, null).ToArray());
    }
    @Override
    public void SetPwrLvl(int PwrLvl) {
        byte[] powerLvlBytes = new byte[] {(byte) PwrLvl };
        sio.send(new ProtocolPacket(65535, RcpMM.RCP_MM_SET_TX_PWR, RcpBase.RCP_MSG_CMD, powerLvlBytes).ToArray());
    }
    @Override
    public void GetRegion() {
        sio.send(new ProtocolPacket(65535, RcpMM.RCP_MM_GET_REGION, RcpBase.RCP_MSG_CMD, null).ToArray());
    }
    @Override
    public void SetRegion(byte Region) {
        byte[] regionBytes = new byte[] { Region };
        sio.send(new ProtocolPacket(65535, RcpMM.RCP_MM_SET_REGION, RcpBase.RCP_MSG_CMD, regionBytes).ToArray());
    }
}
