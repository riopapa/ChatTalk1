package biz.riopapa.chattalk;

import static biz.riopapa.chattalk.ActivityAppList.fnd;
import static biz.riopapa.chattalk.Vars.appPos;
import static biz.riopapa.chattalk.Vars.apps;
import static biz.riopapa.chattalk.Vars.mActivity;
import static biz.riopapa.chattalk.Vars.mContext;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import biz.riopapa.chattalk.model.App;
import biz.riopapa.chattalk.Sub.AppsTable;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    static int colorT, colorF, colorExist, colorNone;
    static Drawable NotInstalled;

    @Override
    public int getItemCount() {
        if (apps == null || apps.isEmpty()) {
            new AppsTable().get();
        }
        return apps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        View tLine;
        TextView tFullName, tNickName, sSay, sLog, sGroup, sWho, sAddWho, sNum;
        PackageManager pm;
        ImageView icon;

        ViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            icon = itemView.findViewById(R.id.app_icon);

            tLine = itemView.findViewById(R.id.app_layout);
            tFullName = itemView.findViewById(R.id.app_full_name);
            tNickName = itemView.findViewById(R.id.app_nick_name);

            sSay = itemView.findViewById(R.id.app_say);
            sLog = itemView.findViewById(R.id.app_log);
            sGroup = itemView.findViewById(R.id.app_group);
            sWho = itemView.findViewById(R.id.app_who);
            sAddWho = itemView.findViewById(R.id.app_addWho);
            sNum = itemView.findViewById(R.id.app_num);

            colorT = ContextCompat.getColor(mContext,R.color.appTrue);
            colorF = ContextCompat.getColor(mContext,R.color.appFalse);
            pm = mContext.getPackageManager();
            colorExist = ContextCompat.getColor(mContext,R.color.appExist);
            colorNone = ContextCompat.getColor(mContext,R.color.appNone);
            NotInstalled = ContextCompat.getDrawable(mContext, R.drawable.delete);

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

        holder.tNickName.setText(al.nickName);
        holder.tNickName.setBackgroundColor((fnd[position]) ? Color.CYAN: Color.LTGRAY);
        String merged = (!al.memo.isEmpty()) ? al.memo + " | "+al.fullName: " "+ al.fullName;
        holder.tFullName.setText(merged);
        holder.tFullName.setBackgroundColor((fnd[position]) ? Color.CYAN: Color.LTGRAY);

        Drawable drawable = getPackageIcon(al.fullName, holder.pm);
        if (drawable == null ) {
            holder.icon.setImageDrawable(NotInstalled);
            holder.tFullName.setTextColor(colorNone);
            holder.tNickName.setTextColor(colorNone);
        } else {
            holder.icon.setImageDrawable(drawable);
            holder.tFullName.setTextColor(colorExist);
            holder.tNickName.setTextColor(colorExist);
        }

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
    private Drawable getPackageIcon(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}