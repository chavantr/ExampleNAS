
package com.mywings.appschedulling.lanucher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.PopupMenu;
import com.mywings.appschedulling.LastUnUsedAppActivity;
import com.mywings.appschedulling.R;
import com.mywings.appschedulling.locally.DbHelper;
import com.mywings.appschedulling.proces.OnConnectionListener;
import com.mywings.appschedulling.process.UtilityConnection;
import com.mywings.appschedulling.stats.UserInfoHolder;


import java.lang.reflect.Field;
import java.util.ArrayList;


public class AppsGridFragment extends GridFragment implements OnConnectionListener,
        LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    AppListAdapter mAdapter;
    private Boolean checkInstance = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("No Applications");
        mAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAdapter);
        setGridShown(false);
        UtilityConnection utilityConnection = new UtilityConnection();
        utilityConnection.setOnConnectionListener(this, DbHelper.EXIST);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {
        return new AppsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader,
                               ArrayList<AppModel> apps) {
        mAdapter.setData(apps);

        UserInfoHolder.getInstance().setApps(apps);

        if (isResumed()) {
            setGridShown(true);
        } else {
            setGridShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        final AppModel app = (AppModel) getGridAdapter().getItem(position);
        if (evaluate()) return;
        if (app != null) {
            Intent intent = getActivity().getPackageManager()
                    .getLaunchIntentForPackage(
                            app.getApplicationPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            if (intent != null) {
                startActivity(intent);
            }

        }
    }

    @Override
    public void onGridItemLongClick(GridView g, View v, int position, long id) {
        super.onGridItemLongClick(g, v, position, id);
        if (evaluate()) return;
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.getMenuInflater().inflate(R.menu.config_menu, popup.getMenu());
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_configure:
                        Intent intent = new Intent(getContext(), LastUnUsedAppActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_push:
                        return true;
                    case R.id.action_pull:
                        return true;
                }
                return true;
            }
        });
        popup.show();
    }

    private boolean evaluate() {
        if (!checkInstance) return true;
        return false;
    }

    @Override
    public void onConnectionSuccess(boolean result) {
        checkInstance = result;
    }
}
