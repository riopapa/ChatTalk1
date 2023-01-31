package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.isPhoneBusy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String phoneState, number;

        try {
            phoneState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        } catch (Exception e) {
            return;
        }
        if (number != null && phoneState != null) {
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                isPhoneBusy = false;
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                isPhoneBusy = true;
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                isPhoneBusy = true;
            }
        }
    }
}