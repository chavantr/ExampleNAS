package com.mywings.appschedulling.joint;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mywings.appschedulling.R;
import com.mywings.appschedulling.lanucher.AppModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UnUsedAppsAdapter extends RecyclerView.Adapter<UnUsedAppsAdapter.UnUsedAppsViewHolder> {

    private List<AppModel> lstApps;

    public UnUsedAppsAdapter(List<AppModel> apps) {
        this.lstApps = apps;
    }

    @Override
    public UnUsedAppsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UnUsedAppsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_last_unused, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UnUsedAppsViewHolder unUsedAppsViewHolder, int position) {
        unUsedAppsViewHolder.lblName.setText(lstApps.get(position).getLabel());
        unUsedAppsViewHolder.lblLastTime.setText("Last used : " + convertMilliSecondsToDate(lstApps.get(position).getLastTime(), "MM-dd-yy hh-mm-ss"));
        unUsedAppsViewHolder.imgIcon.setImageDrawable(lstApps.get(position).getIcon());
    }

    private String convertMilliSecondsToDate(long milliSeconds, String format) {
        Date date = new Date(milliSeconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }

    @Override
    public int getItemCount() {
        return lstApps.size();
    }

    class UnUsedAppsViewHolder extends RecyclerView.ViewHolder {

        TextView lblName;
        TextView lblLastTime;
        ImageView imgIcon;

        public UnUsedAppsViewHolder(@NonNull View itemView) {
            super(itemView);
            lblName = itemView.findViewById(R.id.lblName);
            lblLastTime = itemView.findViewById(R.id.lblLastUnUsedTime);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }

}
