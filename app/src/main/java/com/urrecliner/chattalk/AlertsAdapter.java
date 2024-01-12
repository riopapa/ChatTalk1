package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.alertLines;
import static com.urrecliner.chattalk.Vars.alertPos;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.chattalk.model.AlertLine;
import com.urrecliner.chattalk.alerts.AlertTableIO;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        if (alertLines == null || alertLines.size() == 0) {
            alertLines = new AlertTableIO().get();
            AlertTable.updateMatched();
            AlertTable.makeArrays();
        }
        return alertLines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tGroup, tWho, tKey1, tKey2, tPrev, tNext, tTalk, tCount, tSkip, tMore;
        View tLine;

        ViewHolder(final View itemView) {
            super(itemView);
            tLine = itemView.findViewById(R.id.one_alert_line);
            tGroup = itemView.findViewById(R.id.one_group);
            tWho = itemView.findViewById(R.id.one_who);
            tKey1 = itemView.findViewById(R.id.one_key1);
            tKey2 = itemView.findViewById(R.id.one_key2);
            tPrev = itemView.findViewById(R.id.one_prev);
            tNext = itemView.findViewById(R.id.one_next);
            tTalk = itemView.findViewById(R.id.one_talk);
            tCount = itemView.findViewById(R.id.one_matched);
            tSkip = itemView.findViewById(R.id.one_skip);
            tMore = itemView.findViewById(R.id.one_memo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_line, parent, false);
        return new ViewHolder(view);
    }

    String svGroup = "sv", svWho = "sv";
    int textColor = 0xFF000000;
    final int textBlack = 0xff000000;
    final int textGray = 0x88000000;
    final int textRed = 0xff771111;

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        AlertLine al = alertLines.get(position);
        if (!svGroup.equals(al.group))
            svGroup = al.group;

        holder.tGroup.setText(al.group);
        int matched = al.matched;
        String who = al.who;
        holder.tWho.setText(who);
        if (!svWho.equals(al.who))
            svWho = al.who;

        holder.tKey1.setText(al.key1);
        holder.tKey2.setText(al.key2);
        holder.tPrev.setText(al.prev);
        holder.tNext.setText(al.next);
        if (al.matched == -1) {
            textColor = (al.more.length() > 1)? textGray : textBlack;
            holder.tCount.setVisibility(View.GONE);
            holder.tTalk.setText(al.talk);
            holder.tTalk.setTextColor(ContextCompat.getColor(mContext, R.color.textFore));
            holder.tSkip.setText(al.skip);
            holder.tSkip.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
            holder.tMore.setText(al.more);
            holder.tMore.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f));
            holder.tLine.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_header));
        } else {
            textColor = (al.talk.length() > 1)? textRed: textBlack;
            holder.tGroup.setText("");
            holder.tWho.setSingleLine(true);
            holder.tWho.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.tWho.setSelected(true);
            holder.tCount.setVisibility(View.VISIBLE);
            holder.tCount.setText(String.valueOf(matched));
            holder.tSkip.setText(al.skip);
            holder.tSkip.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f));
            holder.tTalk.setText(al.talk);
            holder.tTalk.setTextColor(ContextCompat.getColor(mContext, R.color.alertTalk));
            holder.tMore.setText(al.more);
            holder.tMore.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, .5f));
            holder.tLine.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_line));
        }

        holder.tGroup.setTextColor(textColor);
        holder.tWho.setTextColor(textColor);
        holder.tKey1.setTextColor(textColor);
        holder.tKey2.setTextColor(textColor);
        holder.tCount.setTextColor(textColor);
        holder.tSkip.setTextColor(textColor);
        holder.tMore.setTextColor(textColor);
        holder.tTalk.setTextColor(textColor);

        holder.tLine.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ActivityEditAlert.class);
            alertPos = holder.getAdapterPosition();
            mActivity.startActivity(intent);
        });
    }
}