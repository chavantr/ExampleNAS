package com.mywings.appschedulling.stats;

import com.mywings.appschedulling.lanucher.AppModel;

import java.util.ArrayList;
import java.util.List;

public class UserInfoHolder {


    private ArrayList<AppModel> apps;
    private List<AppModel> unapps;
    private AppModel appModel;

    private ArrayList batch;

    public static UserInfoHolder getInstance() {
        return UserInfoHolderHelper.INSTANCE;
    }

    public ArrayList<AppModel> getApps() {
        return apps;
    }

    public void setApps(ArrayList<AppModel> apps) {
        this.apps = apps;
    }

    public List<AppModel> getUnapps() {
        return unapps;
    }

    public void setUnapps(List<AppModel> unapps) {
        this.unapps = unapps;
    }

    public AppModel getAppModel() {
        return appModel;
    }

    public void setAppModel(AppModel appModel) {
        this.appModel = appModel;
    }

    public ArrayList getBatch() {
        return batch;
    }

    public void setBatch(ArrayList batch) {
        this.batch = batch;
    }

    private static class UserInfoHolderHelper {
        static final UserInfoHolder INSTANCE = new UserInfoHolder();
    }

}
