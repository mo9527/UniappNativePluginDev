package com.wanyi.plugins.model;

import com.wanyi.plugins.enums.DeviceStatus;

public class Device {
    private String id;
    private String displayName;
    private String serialPort;
    private DeviceStatus status;
    private String faultReason;

    public Device(String id, String serialPort, String displayName) {
        this.id = id;
        this.serialPort = serialPort;
        this.status = DeviceStatus.DISCONNECTED;
        this.faultReason = "";
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public String getSerialPort() { return serialPort; }
    public DeviceStatus getStatus() { return status; }
    public String getFaultReason() { return faultReason; }
    public void setStatus(DeviceStatus status) { this.status = status; }
    public void setFaultReason(String faultReason) { this.faultReason = faultReason; }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}




