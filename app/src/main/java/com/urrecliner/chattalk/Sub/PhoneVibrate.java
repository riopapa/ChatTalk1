package com.urrecliner.chattalk.Sub;

import static com.urrecliner.chattalk.NotificationListener.vibPattern;
import static com.urrecliner.chattalk.NotificationListener.vibEffect;
import static com.urrecliner.chattalk.NotificationListener.vibrator;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.NotificationListener.vibManager;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.VibratorManager;

import com.urrecliner.chattalk.NotificationListener;

public class PhoneVibrate {

    public void vib() {
        if (vibManager == null) {
            vibManager =
                    (VibratorManager) mContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibManager.getDefaultVibrator();
            vibEffect = VibrationEffect.createWaveform(vibPattern, -1);
        }
        vibrator.cancel();
        vibrator.vibrate(vibEffect);
    }

}
