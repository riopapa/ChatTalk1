package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.ActivityMain.fragNumber;
import static biz.riopapa.chattalk.NotificationListener.utils;
import static biz.riopapa.chattalk.Vars.aBar;
import static biz.riopapa.chattalk.Vars.chatGroup;
import static biz.riopapa.chattalk.Vars.mActivity;
import static biz.riopapa.chattalk.Vars.mContext;
import static biz.riopapa.chattalk.Vars.topTabs;
import static biz.riopapa.chattalk.Vars.viewPager2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Arrays;

import biz.riopapa.chattalk.Sub.SnackBar;

public class Fragment_5Chat extends Fragment {

    ViewGroup rootView;
    SpannableString selChat;
    Menu mainMenu;
    File[] chatFolders = null; // KakaoTalk > Chats > KakaoTalk_Chats_nnnn s
    File[] nowChatFiles;
            // KakaoTalk > Chats > KakaoTalk_Chats_2021-04-01_2 > KakaoTalkChats.txt
    File nowChatFile;
    int chatIdx = -1, chatMax;
    int chatPos = -1;
    EditText etChat;
    ImageView ivNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.frag5_chat, container, false);
        etChat = rootView.findViewById(R.id.table_text);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mContext == null) {
            mContext = context;
            mActivity = (Activity) mContext;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (utils == null)
            utils = new Utils();
        fragNumber = 5;
        viewPager2.setCurrentItem(fragNumber);
        topTabs.getTabAt(fragNumber).select();

        etChat.setFocusableInTouchMode(true);
        etChat.setEnabled(true);
        etChat.setFocusable(true);
        chatPos = -1;
        chatIdx = -1;
        chatMax = -1;
        getChatFolders();
        showChats();
    }

    private void showChats() {
        if (chatFolders.length == 0)
            return;
        nowChatFiles = chatFolders[chatIdx].listFiles(file -> (file.getPath().endsWith(".txt")));
        if (nowChatFiles == null || nowChatFiles.length == 0) {
            new SnackBar().show("Folder "+ chatFolders[chatIdx]," empty or Error");
            //noinspection ResultOfMethodCallIgnored
            chatFolders[chatIdx].delete();
            getChatFolders();
            return;
        }
        nowChatFile = nowChatFiles[0];
        if (nowChatFile.getName().startsWith("Kakao"))
            selChat = new SelectChats().generate(nowChatFile, false);
        else {
            new SnackBar().show("Chat File", "not start with Kakao");
            getChatFolders();
            return;
        }
        etChat.setText(selChat);
        rootView.findViewById(R.id.search).setOnClickListener(v -> {
            chatPos = -1;
            EditText etKey1 = rootView.findViewById(R.id.search_key1);
            String key1 = etKey1.getText().toString();          // .replace(" ","\u00A0");
            EditText etKey2 = rootView.findViewById(R.id.search_key2);
            String key2 = etKey2.getText().toString();
            if (key2.length() < 2)
                key2 = null;
            String chatText = selChat.toString();
            String[] lines = chatText.split("\n");
            int cnt = 0;
            int pos1, pos2;
            int nPos = 0;
            for (String line : lines) {
                boolean found = false;
                pos1 = line.indexOf(key1);
                if (pos1 > 0) {
                    if (chatPos < 0)
                        chatPos = chatText.indexOf(key1);
                    if (key2 != null) {
                        pos2 = line.indexOf(key2);
                        if (pos2 > 0) {
                            selChat.setSpan(new BackgroundColorSpan(0xFF00FF00), nPos + pos1,
                                    nPos + pos1 + key1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            selChat.setSpan(new BackgroundColorSpan(0xFF00FF00), nPos + pos2,
                                    nPos + pos2 + key2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            found = true;
                        }
                    } else {
                        selChat.setSpan(new BackgroundColorSpan(0xFFFFFF00), nPos + pos1,
                                nPos + pos1 + key1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        found = true;
                    }
                }
                if (found)
                    cnt++;
                nPos += line.length() + 1;
            }

            etChat.setText(selChat);
            new SnackBar().show("Keywords","Total "+cnt+" keywords found");
            if (chatPos > 0) {
                Editable editText = etChat.getText();
                Selection.setSelection(editText, chatPos);
                etChat.requestFocus();
                ivNext.setVisibility(View.VISIBLE);
            }
        });

        rootView.findViewById(R.id.clear_key).setOnClickListener(v -> {
            EditText etKey1 = rootView.findViewById(R.id.search_key1);
            EditText etKey2 = rootView.findViewById(R.id.search_key2);
            etKey1.setText("");
            etKey2.setText("");
        });

        ivNext = rootView.findViewById(R.id.find_next);
        ivNext.setVisibility(View.GONE);
        ivNext.setOnClickListener(v -> {
            if (chatPos > 0) {
                EditText etKey1 = rootView.findViewById(R.id.search_key1);
                String key1 = etKey1.getText().toString();
                String chatText = etChat.getText().toString();
                chatPos = chatText.indexOf(key1, chatPos + 1);
                if (chatPos > 0) {
                    Editable editText = etChat.getText();
                    Selection.setSelection(editText, chatPos);
                    etChat.requestFocus();
                }
            }
        });
        showChatCounts();
    }

    private void showChatCounts() {
        if (chatIdx == -1) {
            getChatFolders();
            chatIdx = chatMax - 1;
        }
        aBar.setTitle(chatGroup);
        aBar.setSubtitle("  "+(chatIdx+1)+" / "+(chatFolders.length));
    }

    private void getChatFolders() {
        File kChatFolder = new File(Environment.getExternalStorageDirectory(), "Documents/KakaoTalk/Chats");
        chatFolders = kChatFolder.listFiles();
        if (chatFolders == null || chatFolders.length == 0)
            return;
        Arrays.sort(chatFolders);
        chatMax = chatFolders.length;
        chatIdx = chatMax-1;
    }

    View uploadMenu;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        mainMenu = menu;
        inflater.inflate(R.menu.menu_5chat, menu);
        super.onCreateOptionsMenu(menu, inflater);
        new Handler(Looper.getMainLooper()).post(() -> {
            uploadMenu = mActivity.findViewById(R.id.action_upload);
            if (uploadMenu != null) {
                uploadMenu.setOnLongClickListener(view -> {
                    selChat = new SelectChats().generate(nowChatFile, true);
                    return false;
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_left && chatIdx > 0) {
            chatIdx--;
            showChats();
        } else if (item.getItemId() == R.id.action_right && chatIdx < chatFolders.length-1) {
            chatIdx++;
            showChats();
        } else if (item.getItemId() == R.id.action_delete) {
            nowChatFile.delete();
            String name = nowChatFile.getName();
            String fullName = nowChatFile.toString().replace("/"+name, "");
            //noinspection ResultOfMethodCallIgnored
            new File (fullName).delete();
            new SnackBar().show(chatGroup, chatGroup+ " deleted\n");
            getChatFolders();
            chatIdx--;
            if (chatIdx < 0)
                chatIdx = 0;
            showChats();

        }
        return super.onOptionsItemSelected(item);
    }
}