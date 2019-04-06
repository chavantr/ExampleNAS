package com.mywings.appschedulling;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.mywings.appschedulling.joint.UnUsedAppsAdapter;
import com.mywings.appschedulling.stats.UserInfoHolder;

public class UnUsedActivity extends Activity {

    private RecyclerView lstUnUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_used);
        lstUnUsed = findViewById(R.id.lstUnused);
        lstUnUsed.setLayoutManager(new LinearLayoutManager(this));
        UnUsedAppsAdapter unUsedAppsAdapter = new UnUsedAppsAdapter(UserInfoHolder.getInstance().getUnapps(), true);
        lstUnUsed.setAdapter(unUsedAppsAdapter);
    }
}
