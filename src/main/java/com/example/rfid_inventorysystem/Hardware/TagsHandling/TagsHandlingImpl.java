package com.example.rfid_inventorysystem.Hardware.TagsHandling;

import com.ad.comm.usb.SioUsb2;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.rcp.RcpMM;
import com.example.rfid_inventorysystem.Hardware.HardwareBase.ADSio;

public class TagsHandlingImpl implements TagsHandling{
    SioUsb2 sio = ADSio.getInstance().getSio();
    @Override
    public void readTag() {
        byte[] argument = new byte[]{0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x0B};
        sio.send(new ProtocolPacket(65535, RcpMM.RCP_MM_READ_C_UII, RcpBase.RCP_MSG_CMD, null).ToArray());
    }
}
