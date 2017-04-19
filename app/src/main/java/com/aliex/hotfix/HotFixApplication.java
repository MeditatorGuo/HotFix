package com.aliex.hotfix;

import android.app.Application;

import com.aliex.hotfixlib.HotFix;

/**
 * Created by Sim.G on 2017/4/18 20:44
 */
public class HotFixApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HotFix.init(this);
    }
}
