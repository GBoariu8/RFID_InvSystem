package com.example.rfid_inventorysystem.Hardware.HardwareBase;

import com.ad.rcp.OnProtocolListener;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.rcp.RcpMM;
import com.example.rfid_inventorysystem.Data.Access.DatabaseAccess;
import com.example.rfid_inventorysystem.Data.Access.DatabaseAccessImpl;
import com.example.rfid_inventorysystem.Hardware.Parameters.MACROS;
import com.example.rfid_inventorysystem.Hardware.TagsHandling.TagsHandling;
import com.example.rfid_inventorysystem.Hardware.TagsHandling.TagsHandlingImpl;
import com.example.rfid_inventorysystem.Service.FeedbackService;

import java.util.Map;

public class ADRcp {
    private static final ADRcp INSTANCE = new ADRcp();
    private static final RcpBase rcp = new RcpBase();
    private String currentEPC = "";
    private DatabaseAccess databaseAccess = DatabaseAccessImpl.getInstance();

    private ADRcp() {}

    public static ADRcp getInstance() {
        return INSTANCE;
    }

    public RcpBase getRcp() {
        rcp.setOnProtocolListener(this.OnProtocolListener());
        return rcp;
    }

    public String getCurrentEPC() {
        return currentEPC;
    }
    private OnProtocolListener OnProtocolListener() {
        return (object, protocolEventArg) -> {
            String response;
            ProtocolPacket protocolPacket = protocolEventArg.getProtocolPacket();
            switch (protocolPacket.Code) {
                case RcpMM.RCP_MM_READ_C_DT:
                case RcpMM.RCP_MM_SET_ACCESS_EPC_MATCH:
                case RcpMM.RCP_MM_WRITE_C_DT:
                    break;
                case RcpMM.RCP_MM_READ_C_UII:{
                    if (protocolPacket.Type == RcpBase.RCP_MSG_NOTI){
                        int pcepclen = GetCodelen(protocolPacket.Payload[1]);
                        currentEPC = ProtocolPacket.ByteArrayToHexString(protocolPacket.Payload, 3, pcepclen - 2);
                        response = "EPC is: " + currentEPC;
                        FeedbackService.getInstance().updateFeedback(response);
                    }
                    break;
                }
                case RcpMM.RCP_MM_GET_TX_PWR:{
                    response = "The current TX Power Level is: " + protocolPacket.Payload[0] + "dBm";
                    FeedbackService.getInstance().updateFeedback(response);
                    break;
                }
                case RcpMM.RCP_MM_SET_TX_PWR:{
                    response = "The TX power level has been updated.";
                    FeedbackService.getInstance().updateFeedback(response);
                    break;
                }
                case RcpMM.RCP_MM_GET_REGION:{
                    String region = "";
                    switch (protocolPacket.Payload[0]) {
                        case MACROS.REGION_CHINA_900M -> region = " CHINA - 900M";
                        case MACROS.REGION_CHINA_800M -> region = " CHINA - 800M";
                        case MACROS.REGION_US -> region = " USA";
                        case MACROS.REGION_EUROPE -> region = " EUROPE";
                        case MACROS.REGION_KOREA -> region = " KOREA";
                        default -> {}
                    }
                    response = "The current region is:" + region;
                    FeedbackService.getInstance().updateFeedback(response);
                    break;
                }
                case RcpMM.RCP_MM_SET_REGION:{
                    response = "The region has been updated";
                    FeedbackService.getInstance().updateFeedback(response);
                    break;
                }
                default:
                    break;
            }
        };
    }
    private static int GetCodelen(byte iData) {
        return (((iData >> 3) + 1) * 2);
    }

}
