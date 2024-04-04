package biz.riopapa.chattalk.Sub;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;

import androidx.core.content.res.ResourcesCompat;

import biz.riopapa.chattalk.R;
import biz.riopapa.chattalk.Vars;

import org.apache.commons.lang3.StringUtils;

public class LogSpann {

    Typeface font1, font2;
    String dayNow = "x";
    SpannableString ss;
    boolean fontSwitch = true;

    public SpannableString make(String log, Context context) {

        font1 = Typeface.create(ResourcesCompat.getFont(context, R.font.mayplestory), Typeface.NORMAL);
        font2 = Typeface.create(ResourcesCompat.getFont(context, R.font.cookie_run), Typeface.NORMAL);
        int nPos = 0, sLen;

        int [][]colors = new int[2][];

        colors[0] = new int[]{
                context.getColor(R.color.log_head_f0), context.getColor(R.color.log_head_b0),
                context.getColor(R.color.log_line_f0), context.getColor(R.color.log_line_b0),
                context.getColor(R.color.log_line_x0)
        };
        colors[1] = new int[]{
                context.getColor(R.color.log_head_f1), context.getColor(R.color.log_head_b1),
                context.getColor(R.color.log_line_f1), context.getColor(R.color.log_line_b1),
                context.getColor(R.color.log_line_x1)
        };

        int colorIdx = 0;
        int colorFore, colorBack;

        Typeface font = font1;
        String tmp = (log+"\n").replace("\n\n\n","\n\n").trim();
        ss = new SpannableString(tmp);
        String[] msgLine = tmp.split("\n");
        for (String s : msgLine) {
            sLen = s.length();
            if (sLen == 0) {
                nPos += 1;
                continue;
            }
            if (s.length() < 2) {
                nPos += s.length() + 1;
                continue;
            }

            if (StringUtils.isNumeric(String.valueOf(s.charAt(0))) && s.length() > 5) {  // timestamp + who
                if (s.substring(0, 5).equals(dayNow)) {
                    colorFore = colors[colorIdx][0];
                    colorBack = colors[colorIdx][1];
                } else {    // new day
                    colorIdx = (colorIdx + 1) % 2;
                    colorFore = colors[colorIdx][0];
                    colorBack = colors[colorIdx][1];
                    dayNow = s.substring(0, 5);
                    font = (fontSwitch) ? font1 : font2;
                    fontSwitch = !fontSwitch;
                }
            } else {
                colorFore = colors[colorIdx][2];
                colorBack = colors[colorIdx][3];
            }

            int endPos = nPos + sLen;
            ss.setSpan(new ForegroundColorSpan(colorFore), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new BackgroundColorSpan(colorBack), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new TypefaceSpan(font), nPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            nPos += sLen + 1;
        }
        return ss;
    }

    public Vars.DelItem delOneSet(String logNow, int ps, Context context) {
        ps = logNow.lastIndexOf("\n", ps - 1);
        int pf = logNow.indexOf("\n", ps+1);
        if (pf == -1) {
            pf = logNow.length();
            logNow += "\n";
        }
        ps = logNow.lastIndexOf("\n", ps - 1);
        if (ps < 2)
            ps = 0;
        if (ps > 0 && logNow.charAt(ps - 1) == '\n')
            logNow = logNow.substring(0, ps - 1) + logNow.substring(pf);
        else
            logNow = logNow.substring(0, ps) + logNow.substring(pf);
        if (logNow.charAt(0) == '\n')
            logNow = logNow.substring(1);
        if (logNow.charAt(0) == '\n')
            logNow = logNow.substring(1);
        SpannableString ss = make(logNow, context);
        logNow = ss.toString();
        if (ps >= logNow.length())
            ps = logNow.length() - 2;
        ps = logNow.lastIndexOf("\n", ps - 2) + 1;
        pf = logNow.indexOf("\n", ps);
        if (ps > logNow.length())
            ps = logNow.lastIndexOf("\n") - 1;
        if (pf > logNow.length() || pf == -1)
            pf = logNow.length();
        ss.setSpan(new StyleSpan(Typeface.ITALIC), ps, pf, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), ps, pf,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new Vars.DelItem(logNow, ps, pf, ss);
    }

    public Vars.DelItem delOneLine(String logNow, int ps, Context context) {
        ps = logNow.lastIndexOf("\n", ps -1);
        if (ps == -1)
            ps = 0;
        int pf = logNow.indexOf("\n", ps+1);
        if (pf == -1)
            pf = logNow.length() - 2;
        logNow = logNow.substring(0, ps) + logNow.substring(pf);
        SpannableString ss = make(logNow, context);
        logNow = ss.toString();
        ps = logNow.lastIndexOf("\n", ps - 1) + 1;
        pf = logNow.indexOf("\n", ps) - 1;

        ss.setSpan(new StyleSpan(Typeface.ITALIC), ps, pf, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), ps, pf,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new Vars.DelItem(logNow, ps, pf, ss);
    }
}
