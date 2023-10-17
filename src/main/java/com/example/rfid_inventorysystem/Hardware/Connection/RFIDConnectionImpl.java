package com.example.rfid_inventorysystem.Hardware.Connection;

import com.ad.comm.usb.SioUsb2;
import com.example.rfid_inventorysystem.Hardware.HardwareBase.ADSio;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import java.util.ArrayList;
import java.util.List;

public class RFIDConnectionImpl implements RFIDConnection{
    private boolean isConnected = false;
    SioUsb2 sio = ADSio.getInstance().getSio();
    @Override
    public boolean connect() {
        List<UsbDevice> rfidReader = this.findDevices();
        try {
            if(sio.connect(rfidReader.get(0))){
                System.out.println("OK");
                isConnected = true;
                return true;
            }
        } catch (Exception e){
            System.out.println("Error while connecting: " + e.getMessage());
            return false;
        }
        return false;
    }
    @Override
    public void disconnect() {
        sio.disConnect();
        isConnected = false;
    }
    @Override
    public boolean isConnected() {
        return isConnected;
    }
    private List<UsbDevice> findDevices(){
        List<UsbDevice> devices = new ArrayList<>();
        try {
            for (Object object : UsbHostManager.getUsbServices().getRootUsbHub().getAttachedUsbDevices()) {
                UsbDevice device = (UsbDevice) object;
                UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
                if (desc.idVendor() == 0x04d8 && desc.idProduct() == 0x033f) {
                    devices.add(device);
                }
            }
        } catch (UsbException e) {
            throw new RuntimeException(e);
        }
        return devices;
    }
}
