package com.wanyi.plugins.devices;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.wanyi.plugins.enums.DeviceStatus;
import com.wanyi.plugins.model.Device;

public class MicrophoneChecker extends AbstractBootDeviceChecker{
    public static final String TAG = "MicrophoneChecker";

    private final String DISPLAY_NAME = "麦克风";

    private static MicrophoneChecker INSTANCE;
    public static MicrophoneChecker getInstance() {
        if (INSTANCE == null){
            synchronized (MicrophoneChecker.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MicrophoneChecker();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Device checkDeviceStatus(Context context) {
        Context applicationContext = context.getApplicationContext();
        //检查麦克风设备状态
        Device device = new Device("MICROPHONE", "N/A", DISPLAY_NAME);
        AudioRecord audioRecord = null;
        try {
            int bufferSize = AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                device.setStatus(DeviceStatus.FAULT);
                device.setFaultReason("无法获取麦克风设备信息");
                return device;
            }

            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                device.setStatus(DeviceStatus.FAULT);
                device.setFaultReason("请先授权麦克风设备权限");
                return device;
            }
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                device.setStatus(DeviceStatus.FAULT);
                device.setFaultReason("无法获取麦克风设备信息");
                return device;
            }

            audioRecord.startRecording();

            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                // 麦克风被占用或者权限问题
                device.setStatus(DeviceStatus.FAULT);
                device.setFaultReason("请先授权麦克风设备权限");
                return device;
            }

            device.setStatus(DeviceStatus.NORMAL);
            device.setFaultReason("");
            return device;

        } catch (Exception e) {
            Log.e(TAG, "检查设备失败: " + device.getId(), e);
            device.setStatus(DeviceStatus.FAULT);
            device.setFaultReason("未能运行设备检查程序");
            return device;
        } finally {
            if (audioRecord != null) {
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED){
                    audioRecord.stop();
                }else {
                    audioRecord.release();
                }
            }

        }
    }
}
