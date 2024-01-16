package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.NotificationListener.notificationService;
import static com.urrecliner.chattalk.NotificationListener.sounds;
import static com.urrecliner.chattalk.Vars.audioReady;
import static com.urrecliner.chattalk.Vars.beepRawIds;
import static com.urrecliner.chattalk.Vars.isPhoneBusy;
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
    public static boolean isTalking = false;
    static TextToSpeech mTTS = null;
    static String TTSId = "";
    AudioManager audioManager = null;
    // 한글, 영문, 숫자만 OK
//                    final String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z.,\\-\\s]";
    final String match = "[^\uAC00-\uD7A3\u3131-\u314E\\da-zA-Z.,\\-]";     // 가~힣 ㄱ~ㅎ
    void init() {

        stopTTS();

        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mFocusGain = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .build();
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
                NotificationBar.hideStop();
                isTalking = false;
                new Timer().schedule(new TimerTask() {
                    public void run () {
                        beepOnce(Vars.soundType.POST.ordinal());
                        audioManager.abandonAudioFocusRequest(mFocusGain);
                    }
                }, 300);
            }

            @Override
            public void onError(String utteranceId) { }
        });

        int result = mTTS.setLanguage(Locale.getDefault());
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
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
                beepOnce(Vars.soundType.INFO.ordinal());
                return;
            } else {
                beepOnce(Vars.soundType.PRE.ordinal());
            }
        }
        if (mTTS == null)
            init();

        if (isActive()) {
            isTalking = true;
            audioManager.requestAudioFocus(mFocusGain);
            new Timer().schedule(new TimerTask() {
                public void run() {
                    String speakText = text.replaceAll(match, " ");
                    int idx = speakText.indexOf("http");
                    if (idx > 0)
                        speakText = speakText.substring(0, idx) + " 링크 있음";
                    try {
                        mTTS.speak(speakText, TextToSpeech.QUEUE_ADD, null, TTSId);
                    } catch (Exception e) {
                        new Utils().logE("Sound", "TTS Error:" + e);
                    }
                }
            }, 300);
        }
    }
    public void speakBuyStock(String text) {

        if (isSilent()) {
            audioReady = true;
            return;
        }
        if (sounds == null)
            sounds = new Sounds();
        beepOnce(Vars.soundType.STOCK.ordinal());

        if (isActive()) {
            audioManager.requestAudioFocus(mFocusGain);
            new Timer().schedule(new TimerTask() {
                public void run() {
                    String speakText = text.replaceAll(match, " ");
                    int idx = speakText.indexOf("http");
                    if (idx > 0)
                        speakText = speakText.substring(0, idx) + " 링크 있음";
                    try {
                        isTalking = true;
                        mTTS.speak(speakText, TextToSpeech.QUEUE_ADD, null, TTSId);
                    } catch (Exception e) {
                        new Utils().logE("Sound", "TTS Error:" + e);
                    }
                }
            }, 150);
        }
    }

    void beepOnce(int soundNbr) {

        final MediaPlayer mPlayer = MediaPlayer.create(mContext, beepRawIds[soundNbr]);
        mPlayer.setVolume(1f, 1f);
        mPlayer.start();
        mPlayer.setOnCompletionListener(mp -> {
            if (mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
        });
    }

    public boolean isActive() {
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            return true;
        }

        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        for(AudioDeviceInfo deviceInfo : audioDevices){
            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO     // 007
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_BLUETOOTH_A2DP    // 008
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES
            )
                return true;
        }
        return false;
    }

    public boolean isSilent() {
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

}