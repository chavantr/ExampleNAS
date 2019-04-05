package com.mywings.appschedulling.stats;

import com.mywings.appschedulling.lanucher.AppModel;

import java.util.ArrayList;

public class UserInfoHolder {


    private ArrayList<AppModel> apps;

    public static UserInfoHolder getInstance() {
        return UserInfoHolderHelper.INSTANCE;
    }

    public ArrayList<AppModel> getApps() {
        return apps;
    }

    public void setApps(ArrayList<AppModel> apps) {
        this.apps = apps;
    }

    private static class UserInfoHolderHelper {
        static final UserInfoHolder INSTANCE = new UserInfoHolder();
    }

}
