package com.example.rfid_inventorysystem.Hardware.HardwareBase;

import com.ad.comm.*;
import com.ad.comm.usb.SioUsb2;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.rcp.RcpMM;

public class ADSio {
    private static final ADSio INSTANCE = new ADSio();
    private static final SioBase sio = new SioUsb2();
    RcpBase rcp = ADRcp.getInstance().getRcp();
    private ADSio() {}
    public static ADSio getInstance() {
        return INSTANCE;
    }

    public SioUsb2 getSio() {
        sio.setOnCommListener(this.OnCommListener());
        return (SioUsb2) sio;
    }

    private OnCommListener OnCommListener() {
        return new OnCommListener() {
            @Override
            public void onStatus(Object arg0, StatusEventArg arg1) {
                System.out.println("Object: " + arg0.toString() + " status: " + CommStatus.format(arg1.getStatus()) + "  "
                        + arg1.getMsg());
            }

            @Override
            public void onReceived(Object arg0, ByteEventArg arg1) {
                byte[] receiveBytes = arg1.getData();
                System.out.println("recv data Len: " + receiveBytes.length + " Recv data: "
                        + ProtocolPacket.ByteArrayToHexString(receiveBytes, 0, receiveBytes.length));
                if((receiveBytes[3] == RcpMM.RCP_MM_READ_C_UII) &&
                        (receiveBytes[4] == RcpBase.RCP_MSG_OK))
                {
                    return;
                }
                rcp.dealPacketByte(receiveBytes);
            }
        };
    }
}
