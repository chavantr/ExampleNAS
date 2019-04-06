package com.mywings.appschedulling;

import android.Manifest;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.mywings.appschedulling.joint.UnUsedAppsAdapter;
import com.mywings.appschedulling.lanucher.AppModel;
import com.mywings.appschedulling.stats.UserInfoHolder;

import java.util.ArrayList;
import java.util.List;

public class LastUnUsedAppActivity extends Activity {

    private static final int EXTERNAL_REQUEST = 1000;
    private UnUsedAppsAdapter unUsedAppsAdapter;
    private RecyclerView lstUnUsed;
    private List<AppModel> lstAppModel;
    private List<AppModel> lstUnAppModel;
    private Button btnUnUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_un_used_app);
        lstUnUsed = findViewById(R.id.lstUnused);
        btnUnUsed = findViewById(R.id.btnUnUsed);
        btnUnUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LastUnUsedAppActivity.this, UnUsedActivity.class);
                startActivity(intent);
            }
        });
        lstUnUsed.setLayoutManager(new LinearLayoutManager(this));
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_REQUEST);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, currentTime - 100 * 1000, currentTime);
            if (stats != null) {
                long lastUsedAppTime = 0;
                lstAppModel = new ArrayList<>();
                lstUnAppModel = new ArrayList<>();

                for (UsageStats usageStats : stats) {
                    for (AppModel appModel : UserInfoHolder.getInstance().getApps()) {
                        if (appModel.getAppInfo().packageName.equalsIgnoreCase(usageStats.getPackageName())) {
                            appModel.setLastTime(usageStats.getLastTimeUsed());
                            //if (usageStats.getLastTimeUsed() > lastUsedAppTime) {
                            if (!lstAppModel.contains(appModel))
                                lstAppModel.add(appModel);
                            //}
                        }
                    }

                    if (usageStats.getLastTimeUsed() > lastUsedAppTime) {
                        String foregroundApp = usageStats.getPackageName();
                        lastUsedAppTime = usageStats.getLastTimeUsed();
                    }
                }

                for (UsageStats usageStats : stats) {
                    for (AppModel appModel : UserInfoHolder.getInstance().getApps()) {
                        if (!appModel.getAppInfo().packageName.equalsIgnoreCase(usageStats.getPackageName())) {
                            appModel.setLastTime(usageStats.getLastTimeUsed());
                            if (!lstUnAppModel.contains(appModel))
                                lstUnAppModel.add(appModel);
                        }
                    }
                }
            }
        }

        UserInfoHolder.getInstance().setUnapps(lstUnAppModel);
        unUsedAppsAdapter = new UnUsedAppsAdapter(lstAppModel, false);
        lstUnUsed.setAdapter(unUsedAppsAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
        }
    }
}
