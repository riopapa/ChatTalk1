package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.NotificationListener.logUpdate;
import static biz.riopapa.chattalk.NotificationListener.sounds;
import static biz.riopapa.chattalk.Vars.mContext;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Upload2Google {

    static class SheetQue {

        String group, timeStamp, who, percent, talk, statement, key12;

        SheetQue(String group, String timeStamp, String who, String percent, String talk, String statement, String key12) {
            this.group = group; this.timeStamp = timeStamp; this.who = who; this.percent = percent;
            this.talk = talk; this.statement = statement; this.key12 = key12;
        }
    }

    static ArrayList<SheetQue> sheetQues = null;
    static boolean nowUploading = false;
    static void initSheetQue() {
        sheetQues = new ArrayList<>();
    }

    public static void add2Que(String group, String timeStamp, String who, String percent, String talk, String statement, String key12) {
        sheetQues.add(new SheetQue(group, timeStamp, who, percent, talk, statement, key12));
        uploadStock();
    }
    static void uploadStock() {
        if (sheetQues.isEmpty() || nowUploading ) //  || WifiMonitor.wifiName.equals(none))
            return;
        nowUploading = true;
        SheetQue que = sheetQues.get(0);
        sheetQues.remove(0);
        String group = que.group, timeStamp = que.timeStamp, who = que.who;
        String percent = que.percent; //  + "Q"+hourMinFormat.format(System.currentTimeMillis());
        String talk = que.talk; String statement = que.statement; String key12 = que.key12;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                mContext.getString(R.string.sheets_stock),
                response -> {
                    nowUploading = false;
//                    Log.w("stock add response",response);
                    if (!response.startsWith("ok"))
                        logUpdate.addLog("sheet stock Not Ok", response);
                    uploadStock();
                },
                error -> {
                    nowUploading = false;
                    String s = group+", "+who+", "+timeStamp+", "+percent+", "+statement;
                    new Utils().logW("uploadStock()", s+"\n Error "+s+"\n"+error);
                    logUpdate.addLog("sheet stock error ", s);
                    sounds.beepOnce(Vars.soundType.ERR.ordinal());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> para = new HashMap<>();
                para.put("action", "stock");
                para.put("group", group);      para.put("timeStamp", timeStamp);
                para.put("who", who);          para.put("percent", percent);
                para.put("talk", talk);        para.put("statement", statement);
                para.put("key12", key12);
                return para;
            }
        };

        int socketTimeOut = 30000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }

    static void uploadGroupInfo(String group, String who, String percent, String talk, String statement) {
        nowUploading = true;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                mContext.getString(R.string.sheets_stock),
                response -> {
                    nowUploading = false;
                    Log.w("sheet response",response);
                    if (!response.startsWith("ok group"))
                        logUpdate.addLog("sheet group ok", response);
                    uploadStock();
                },
                error -> {
                    String ss = "코멘트 올리기 에러남 "+error.networkResponse.statusCode;
                    logUpdate.addLog("sheet group error", ss);
                    sounds.speakAfterBeep(ss);
                    nowUploading = false;
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> para = new HashMap<>();
                para.put("action", "group");
                para.put("group", group);     para.put("who", who);
                para.put("percent", percent); para.put("talk", talk);
                para.put("statement", statement);
                return para;
            }
        };

        int socketTimeOut = 30000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }
}