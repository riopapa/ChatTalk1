package com.urrecliner.chattalk.Sub;

import static com.urrecliner.chattalk.Vars.mContext;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

public class AudioMgr {

    public boolean isActive() {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            return true;
        }

        AudioDeviceInfo[] audioDevices = am.getDevices(AudioManager.GET_DEVICES_INPUTS);
        for(AudioDeviceInfo deviceInfo : audioDevices){
            if(deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO     // 007
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_BLUETOOTH_A2DP    // 008
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES
            ) {
                return true;
            }
        }
        return false;
    }

//    private boolean isNormal() {
//        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
//    }
//    private boolean isBlueTooth() {
//
//        AudioDeviceInfo[] audioDevices = am.getDevices(AudioManager.GET_DEVICES_INPUTS);
//        for(AudioDeviceInfo deviceInfo : audioDevices){
//
//            if(deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO     // 007
//                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_BLUETOOTH_A2DP    // 008
//                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET
//                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES
//            )
//                return true;
//        }
//        return false;
//    }
}
