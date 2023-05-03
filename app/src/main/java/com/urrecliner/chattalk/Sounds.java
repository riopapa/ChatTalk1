package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.beepRawIds;
import static com.urrecliner.chattalk.Vars.audioReady;
import static com.urrecliner.chattalk.Vars.isPhoneBusy;
import static com.urrecliner.chattalk.Vars.mAudioManager;
import static com.urrecliner.chattalk.Vars.mContext;
import static com.urrecliner.chattalk.Vars.mFocusGain;
import static com.urrecliner.chattalk.NotificationListener.notificationBar;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.urrecliner.chattalk.Sub.AudioMgr;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

class Sounds {
    public static boolean isTalking = false;
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
                notificationBar.hideStop();
                isTalking = false;
                new Timer().schedule(new TimerTask() {
                    public void run () {
                        beepOnce(Vars.soundType.POST.ordinal());
                        mAudioManager.abandonAudioFocusRequest(mFocusGain);
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
                beepOnce(Vars.soundType.ONLY.ordinal());
                return;
            } else {
                beepOnce(Vars.soundType.PRE.ordinal());
            }
        }
        audioReady = new AudioMgr().isActive();
        if (audioReady) {
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
                        new Utils().logE("Sound", "TTS Error:" + e);
                    }
                }
            }, 150);
        }
    }
    public void speakBuyStock(String text) {

        if (isSilent()) {
            audioReady = true;
            return;
        }
        beepOnce(Vars.soundType.STOCK.ordinal());

        audioReady = new AudioMgr().isActive();
        if (audioReady) {
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
                        new Utils().logE("Sound", "TTS Error:" + e);
                    }
                }
            }, 150);
        }
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


    boolean isSilent() {
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }
}