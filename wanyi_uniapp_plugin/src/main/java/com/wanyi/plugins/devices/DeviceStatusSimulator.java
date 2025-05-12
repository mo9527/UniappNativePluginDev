package com.wanyi.plugins.devices;

import com.wanyi.plugins.enums.DeviceStatus;
import com.wanyi.plugins.model.Device;

import java.util.Random;

public class DeviceStatusSimulator {
    private static final Random random = new Random();
    private static final String[] FAULT_REASONS = {
            "连接断开",
            "硬件故障",
            "驱动异常",
            "电源不足"
    };

    public static void simulateStatus(Device device) {
        // 模拟状态：20% 概率故障，10% 概率断开，70% 概率正常
        int chance = random.nextInt(100);
        if (chance < 20) {
            device.setStatus(DeviceStatus.FAULT);
            device.setFaultReason(FAULT_REASONS[random.nextInt(FAULT_REASONS.length)]);
        } else if (chance < 30) {
            device.setStatus(DeviceStatus.DISCONNECTED);
            device.setFaultReason("设备未连接");
        } else {
            device.setStatus(DeviceStatus.NORMAL);
            device.setFaultReason("");
        }
    }
}
