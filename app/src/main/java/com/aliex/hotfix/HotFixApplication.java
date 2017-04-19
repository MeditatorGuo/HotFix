package com.aliex.hotfix;

import android.app.Application;
import android.content.Context;

import com.aliex.hotfixlib.AssetsUtil;
import com.aliex.hotfixlib.HotFix;

import java.io.File;

/**
 * Created by Sim.G on 2017/4/18 20:44
 */
public class HotFixApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        File dexPath = new File(getDir("dex", Context.MODE_PRIVATE), "hack_dex.jar");
        AssetsUtil.prepareDex(this.getApplicationContext(), dexPath, "hack_dex.jar");
        HotFix.inject(this, dexPath.getAbsolutePath(), "com.aliex.hack.AntilazyLoad");
    }
}
