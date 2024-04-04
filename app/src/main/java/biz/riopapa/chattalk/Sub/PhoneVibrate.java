package biz.riopapa.chattalk.Sub;

import static biz.riopapa.chattalk.NotificationListener.vibEffect;
import static biz.riopapa.chattalk.NotificationListener.vibManager;
import static biz.riopapa.chattalk.NotificationListener.vibPattern;
import static biz.riopapa.chattalk.NotificationListener.vibrator;
import static biz.riopapa.chattalk.Vars.mContext;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.VibratorManager;

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
