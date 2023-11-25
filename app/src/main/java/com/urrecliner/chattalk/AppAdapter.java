package com.urrecliner.chattalk;

import static com.urrecliner.chattalk.Vars.appPos;
import static com.urrecliner.chattalk.Vars.apps;
import static com.urrecliner.chattalk.Vars.mActivity;
import static com.urrecliner.chattalk.Vars.mContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.chattalk.Sub.App;
import com.urrecliner.chattalk.Sub.AppsTable;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    static int color0, color1;

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
            color0 = ContextCompat.getColor(mContext,R.color.appLine0);
            color1 = ContextCompat.getColor(mContext,R.color.appLine1);
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
        holder.tLine.setBackgroundColor((position % 2 == 1) ? color0 : color1);

        holder.tFullName.setText(al.fullName);
        holder.tNickName.setText(al.nickName);
        holder.tNote.setText(al.memo);

        holder.sSay.setText((al.say)? "Say": "Quiet");
        holder.sLog.setText((al.log)? "Log": "noLog");
        holder.sGroup.setText((al.grp)? "Grp": "NoGrp");
        holder.sWho.setText((al.who)? "Who": "NoWho");
        holder.sAddWho.setText((al.addWho)? "AddWho": "noAdd");
        holder.sNum.setText((al.num)? "Num": "noNum");

        holder.tLine.setOnClickListener(v -> {
            Intent intent = new Intent(holder.context, ActivityAppEdit.class);
            mActivity.startActivity(intent);
        });
    }
}