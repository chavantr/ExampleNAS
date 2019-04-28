
package com.mywings.appschedulling.lanucher;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.mywings.appschedulling.LastUnUsedAppActivity;
import com.mywings.appschedulling.R;
import com.mywings.appschedulling.locally.AppSchedulingDatabaseHelper;
import com.mywings.appschedulling.locally.DbHelper;
import com.mywings.appschedulling.proces.OnConnectionListener;
import com.mywings.appschedulling.process.UtilityConnection;
import com.mywings.appschedulling.stats.UserInfoHolder;
import com.mywings.appschedulling.stats.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class AppsGridFragment extends GridFragment implements OnConnectionListener,
        LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    AppListAdapter mAdapter;
    private Boolean checkInstance = false;
    private AppSchedulingDatabaseHelper appSchedulingDatabaseHelper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("No Applications");
        mAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAdapter);
        setGridShown(false);
        appSchedulingDatabaseHelper = new AppSchedulingDatabaseHelper(getContext(), DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
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
    public void onGridItemLongClick(GridView g, View v, final int position, long id) {
        super.onGridItemLongClick(g, v, position, id);
        if (evaluate()) return;
        final PopupMenu popup = new PopupMenu(getContext(), v);
        popup.getMenuInflater().inflate(R.menu.config_menu, popup.getMenu());
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            AppModel appModel = UserInfoHolder.getInstance().getApps().get(position);

            if (appSchedulingDatabaseHelper.checkState(appModel.getAppInfo().packageName) > 0) {
                popup.getMenu().findItem(R.id.action_install).setVisible(true);
                popup.getMenu().findItem(R.id.action_uninstall).setVisible(false);
            } else {
                popup.getMenu().findItem(R.id.action_install).setVisible(false);
                popup.getMenu().findItem(R.id.action_uninstall).setVisible(true);
            }
        } catch (Exception e) {
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                AppModel appModel = UserInfoHolder.getInstance().getApps().get(position);

                switch (menuItem.getItemId()) {
                    case R.id.action_configure:
                        Intent intent = new Intent(getContext(), LastUnUsedAppActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_push:
                        if (appSchedulingDatabaseHelper.checkSynced(appModel.getAppInfo().packageName) > 0) {
                            Toast.makeText(getContext(), "Application in queue", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Configuration setting not yet completed", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case R.id.action_pull:
                        if (appSchedulingDatabaseHelper.checkSynced(appModel.getAppInfo().packageName) > 0) {
                            Toast.makeText(getContext(), "Pull request added in batch", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "No application found on server", Toast.LENGTH_LONG).show();
                        }
                        return true;

                    case R.id.action_uninstall:

                        if (appSchedulingDatabaseHelper.setState(appModel.getAppInfo().packageName) > 0) {
                            popup.getMenu().findItem(R.id.action_install).setVisible(true);
                            popup.getMenu().findItem(R.id.action_uninstall).setVisible(false);
                            Toast.makeText(getContext(), "Backup initiated, process will start soon", Toast.LENGTH_LONG).show();
                            Utils.hideAppIcon(getContext(), appModel.getAppInfo().packageName);
                            if (!check(appModel.getAppInfo().packageName)) showNotification();
                        }

                        return true;

                    case R.id.action_install:
                        if (appSchedulingDatabaseHelper.deleteState(appModel.getAppInfo().packageName) > 0) {
                            popup.getMenu().findItem(R.id.action_install).setVisible(false);
                            popup.getMenu().findItem(R.id.action_uninstall).setVisible(true);
                            Utils.showAppIcon(getContext(), appModel.getAppInfo().packageName);
                            Toast.makeText(getContext(), "Configuration of clone started, processing with adb commands", Toast.LENGTH_LONG).show();
                        }
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

    private boolean check(String packageName) {
        return "com.mywings.appschedulling".equalsIgnoreCase(packageName);
    }

    @Override
    public void onConnectionSuccess(boolean result) {
        checkInstance = result;
        if (result) Toast.makeText(getContext(), "Connected to server", Toast.LENGTH_LONG).show();
    }

    private void showNotification() {

        Intent intent = new Intent();
        final PendingIntent pendingIntent = PendingIntent.getActivity(
                getContext(), 0, intent, 0);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_cloud_upload_black_24dp)
                        .setContentTitle(getContext().getString(R.string.label_upload));

        mBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setProgress(100
                , 0, true);

        notificationManager.notify(1, mBuilder.build());
    }
}
