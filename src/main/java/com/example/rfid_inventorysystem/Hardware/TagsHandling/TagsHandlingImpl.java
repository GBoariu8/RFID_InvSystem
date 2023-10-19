package com.example.rfid_inventorysystem.Hardware.TagsHandling;

import com.ad.comm.usb.SioUsb2;
import com.ad.rcp.ProtocolPacket;
import com.ad.rcp.RcpBase;
import com.ad.rcp.RcpMM;
import com.example.rfid_inventorysystem.Data.Access.DatabaseAccess;
import com.example.rfid_inventorysystem.Data.Access.DatabaseAccessImpl;
import com.example.rfid_inventorysystem.Hardware.HardwareBase.ADRcp;
import com.example.rfid_inventorysystem.Hardware.HardwareBase.ADSio;
import com.example.rfid_inventorysystem.Service.FeedbackService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TagsHandlingImpl implements TagsHandling{
    SioUsb2 sio = ADSio.getInstance().getSio();
    ADRcp adRcp = ADRcp.getInstance();
    String oldEPC = adRcp.getCurrentEPC();
    String newEPC;
    DatabaseAccess databaseAccess = DatabaseAccessImpl.getInstance();
    public String getOldEPC(){
        return oldEPC;
    }
    @Override
    public boolean readNewTag() {
        boolean returnValue = false;
        sio.send(new ProtocolPacket(65535, RcpMM.RCP_MM_READ_C_UII, RcpBase.RCP_MSG_CMD, null).ToArray());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        newEPC = adRcp.getCurrentEPC();
        if(Objects.equals(newEPC, oldEPC)){
            newEPC = "";

        } else {
            oldEPC = newEPC;
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public Map<String, Object> getTagInfo() {
        Map<String, Object> itemInfo = new HashMap<>();
        if(databaseAccess.isConnected()){
            ResultSet resultSet = databaseAccess.getTagByEPC(adRcp.getCurrentEPC());
            if(resultSet != null){
                try {
                    if (resultSet.next()) {
                        itemInfo.put("id", resultSet.getInt("id"));
                        itemInfo.put("EPC", resultSet.getString("EPC"));
                        itemInfo.put("productName", resultSet.getString("productName"));
                        itemInfo.put("suppName", resultSet.getString("suppName"));
                        itemInfo.put("price", resultSet.getInt("price"));
                        itemInfo.put("responsible", resultSet.getString("responsible"));
                        printHashMap(itemInfo);
                    } else {
                        return null;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return itemInfo;
    }

    private void printHashMap(Map<String, Object> myHashMap) {
        StringBuilder hashMapString = new StringBuilder("Item details:\n");

        String[] orderedKeys = {"productName", "suppName", "responsible", "price"};

        for (String key : orderedKeys) {
            Object value = myHashMap.get(key);

            if (value != null) {  // Check if the key exists in the HashMap
                String displayName = switch (key) {
                    case "productName" -> "Prod Name";
                    case "suppName" -> "Supplier";
                    case "price" -> "Price";
                    case "responsible" -> "Responsible";
                    default -> key;
                };

                // Translate database field names to user-friendly names

                hashMapString.append(displayName).append(": ").append(value).append("\n");
            }
        }
        FeedbackService.getInstance().updateFeedback(hashMapString.toString());  // Assuming singleton pattern for FeedbackService
    }
}
