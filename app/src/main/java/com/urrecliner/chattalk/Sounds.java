package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.beepRawIds;
import static com.urrecliner.chattalk.Vars.isPhoneBusy;
import static com.urrecliner.chattalk.Vars.mAudioManager;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.mFocusGain;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

class Sounds {

    private final static String logID = "TTS";
    static private boolean isTalking = false;
    static TextToSpeech mTTS;
    static String TTSId = "";

    void init() {

        stopTTS();

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mFocusGain = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .build();
        readyTTS();
    }

    private void readyTTS() {

        mTTS = null;
        mTTS = new TextToSpeech(mContext, status -> {
            if (status == TextToSpeech.SUCCESS) {
                initializeTTS();
            }
        });
    }

    private void initializeTTS() {

        mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                TTSId = utteranceId;
            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                if (mTTS.isSpeaking())
                    return;
                beepOnce(Vars.soundType.POST.ordinal());
                isTalking = false;
                new Timer().schedule(new TimerTask() {
                    public void run () {
                        mAudioManager.abandonAudioFocusRequest(mFocusGain);
                    }
                }, 300);
            }

            @Override
            public void onError(String utteranceId) { }
        });

        int result = mTTS.setLanguage(Locale.getDefault());
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

//            Toast.makeText(mActivity, "Not supported Language", Toast.LENGTH_SHORT).show();
            beepOnce(Vars.soundType.ERR.ordinal());
        } else {
            mTTS.setPitch(1.2f);
            mTTS.setSpeechRate(1.3f);
        }
    }

    void stopTTS() {
        if (mTTS != null)
            mTTS.stop();
    }

    public void speakAfterBeep(String text) {

        if (isSilent())
            return;
        if (!isTalking) {
            if (isPhoneBusy) {
                beepOnce(Vars.soundType.ONLY.ordinal());
                return;
            } else {
                beepOnce(Vars.soundType.PRE.ordinal());
            }
        }
        if (canTalk()) {
            mAudioManager.requestAudioFocus(mFocusGain);
            new Timer().schedule(new TimerTask() {
                public void run() {
                    // 한글, 영문, 숫자만 OK
//                    final String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z.,\\-\\s]";
                    final String match = "[^\uAC00-\uD7A3\\da-zA-Z.,\\-]";
                    String speakText = text.replaceAll(match, " ");
                    int idx = speakText.indexOf("http");
                    if (idx > 0)
                        speakText = speakText.substring(0, idx) + " 링크 있음";
                    try {
                        isTalking = true;
                        mTTS.speak(speakText, TextToSpeech.QUEUE_ADD, null, TTSId);
                    } catch (Exception e) {
                        new Utils().logE(logID, "TTS Error:" + e);
                    }
                }
            }, 150);
        }
    }
    public void speakBuyStock(String text) {

        if (isSilent())
            return;
//        if (!isTalking) {
//            if (isPhoneBusy) {
//                beepOnce(Vars.soundType.BUY_STOCK.ordinal());
//                return;
//            } else {
                beepOnce(Vars.soundType.STOCK.ordinal());
//            }
//        }
        if (canTalk()) {
            mAudioManager.requestAudioFocus(mFocusGain);
            new Timer().schedule(new TimerTask() {
                public void run() {
                    // 한글, 영문, 숫자만 OK
//                    final String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z.,\\-\\s]";
                    final String match = "[^\uAC00-\uD7A3\\da-zA-Z.,\\-]";
                    String speakText = text.replaceAll(match, " ");
                    int idx = speakText.indexOf("http");
                    if (idx > 0)
                        speakText = speakText.substring(0, idx) + " 링크 있음";
                    try {
                        isTalking = true;
                        mTTS.speak(speakText, TextToSpeech.QUEUE_ADD, null, TTSId);
                    } catch (Exception e) {
                        new Utils().logE(logID, "TTS Error:" + e);
                    }
                }
            }, 150);
        }
    }

    boolean canTalk() {
        return isNormal() || isBlueTooth();
    }

    void beepOnce(int soundNbr) {

        final MediaPlayer mMediaPlayer = MediaPlayer.create(mContext, beepRawIds[soundNbr]);
        mMediaPlayer.setVolume(1f, 1f);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(mp -> {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        });
    }

    private boolean isBlueTooth() {

        AudioDeviceInfo[] audioDevices = mAudioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        for(AudioDeviceInfo deviceInfo : audioDevices){

            if(deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO     // 007
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_BLUETOOTH_A2DP    // 008
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES
            )
                return true;
        }
        return false;
    }

    private boolean isNormal() {
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }
    boolean isSilent() {
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }
}