package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.appPos;
import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.chattalk.Sub.App;
import com.urrecliner.chattalk.Sub.AppsTable;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    static int colorT, colorF;

    @Override
    public int getItemCount() {
        if (apps == null || apps.size() == 0) {
            apps = new AppsTable().get();
        }
        return apps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        View tLine;
        TextView tFullName, tNickName, tNote, sSay, sLog, sGroup, sWho, sAddWho, sNum;

        ViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            tLine = itemView.findViewById(R.id.app_layout);
            tFullName = itemView.findViewById(R.id.app_full_name);
            tNickName = itemView.findViewById(R.id.app_nick_name);
            tNote = itemView.findViewById(R.id.app_note);

            sSay = itemView.findViewById(R.id.app_say);
            sLog = itemView.findViewById(R.id.app_log);
            sGroup = itemView.findViewById(R.id.app_group);
            sWho = itemView.findViewById(R.id.app_who);
            sAddWho = itemView.findViewById(R.id.app_addWho);
            sNum = itemView.findViewById(R.id.app_num);
            colorT = ContextCompat.getColor(mContext,R.color.appTrue);
            colorF = ContextCompat.getColor(mContext,R.color.appFalse);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        appPos = holder.getAdapterPosition();

        App al = apps.get(appPos);

        holder.tFullName.setText(al.fullName);
        holder.tNickName.setText(al.nickName);
        holder.tNote.setText(al.memo);

        holder.sSay.setTextColor((al.say)? colorT:colorF);
        holder.sLog.setTextColor((al.log)? colorT:colorF);
        holder.sGroup.setTextColor((al.grp)? colorT:colorF);
        holder.sWho.setTextColor((al.who)? colorT:colorF);
        holder.sAddWho.setTextColor((al.addWho)? colorT:colorF);
        holder.sNum.setTextColor((al.num)? colorT:colorF);

        holder.tLine.setOnClickListener(v -> {
            appPos = holder.getAdapterPosition();
            Intent intent = new Intent(holder.context, ActivityAppEdit.class);
            mActivity.startActivity(intent);
        });
    }
}