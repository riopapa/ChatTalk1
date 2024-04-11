package biz.riopapa.chattalk.Sub;

import static biz.riopapa.chattalk.Vars.mContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;

public class VolumeIcon {

    public static Bitmap draw() {
        Bitmap bitmap = Bitmap.createBitmap(256, 128, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        Paint txtPaint = new Paint();
        txtPaint.setTextAlign(Paint.Align.RIGHT);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(24);
        txtPaint.setColor(0xFF333333);
        txtPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        txtPaint.setStrokeWidth(3);
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setTextAlign(Paint.Align.LEFT);
        linePaint.setColor(0xFFDD0000);
        linePaint.setStrokeWidth(12);
        int rVol = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int nVol = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        drawVolume(canvas, "Media", 28, mVol, txtPaint, linePaint);
        drawVolume(canvas, "Ring", 64, rVol, txtPaint, linePaint);
        drawVolume(canvas, "Noti", 100, nVol, txtPaint, linePaint);

        return bitmap;
    }

    static void drawVolume(Canvas canvas, String s, int yPos, int vol, Paint txtPaint, android.graphics.Paint
            lnPaint) {
        final int shift = 84;
        final int scale = 10;
        canvas.drawText(s, shift-4, yPos+8, txtPaint);
        canvas.drawLine(shift, yPos, shift+vol * scale, yPos, lnPaint);
        canvas.drawLine(shift + vol * scale, yPos, shift + 15 * scale, yPos, txtPaint);
    }
}
