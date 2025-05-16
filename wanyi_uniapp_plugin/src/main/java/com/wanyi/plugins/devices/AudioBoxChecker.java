package com.wanyi.plugins.devices;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.wanyi.plugins.enums.DeviceStatus;
import com.wanyi.plugins.model.Device;

/**
 * 扬声器检测
 */

public class AudioBoxChecker extends AbstractBootDeviceChecker{
    public static final String TAG = "AudioBoxChecker";

    private final String DISPLAY_NAME = "扬声器";

    private static AudioBoxChecker INSTANCE;
    public static AudioBoxChecker getInstance() {
        if (INSTANCE == null){
            synchronized (AudioBoxChecker.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AudioBoxChecker();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Device checkDeviceStatus(Context context) {
        Device device = new Device("AUDIO_BOX", "USB", DISPLAY_NAME);
        device.setStatus(DeviceStatus.DISCONNECTED);
        device.setFaultReason("设备未连接");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            return device;
        }

        // 切换到扬声器模式
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);

        // 播放短音频进行测试
        MediaPlayer mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        if (mediaPlayer == null) {
            return device;
        }

        try {
            mediaPlayer.start();
            Thread.sleep(1000); // 播放 1 秒
            boolean isPlaying = mediaPlayer.isPlaying();
            mediaPlayer.stop();
            mediaPlayer.release();
            if (isPlaying){
                device.setStatus(DeviceStatus.NORMAL);
                device.setFaultReason("");
                return device;
            }
        } catch (Exception e) {
            Log.e(TAG, "检查设备失败: " + device.getId(), e);
        }
        return device;
    }
}
