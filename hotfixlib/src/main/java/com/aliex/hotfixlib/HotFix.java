package com.aliex.hotfixlib;

import java.io.File;
import java.lang.reflect.Array;

import android.content.Context;
import android.util.Log;

import dalvik.system.DexClassLoader;

/**
 * Created by Sim.G on 2017/4/19 09:49
 */
public class HotFix {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        File hackDir = context.getDir("hackDir", 0);
        File hackJar = new File(hackDir, "hack_dex.jar");
        try {
            AssetsUtil.copyAssets(context, "hack_dex.jar", hackJar.getAbsolutePath());
            inject(hackJar.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void inject(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                // 获取classes的dexElements
                Class<?> cl = Class.forName("dalvik.system.BaseDexClassLoader");
                Object pathList = ReflectUtil.getField(cl, "pathList", mContext.getClassLoader());
                Object baseElements = ReflectUtil.getField(pathList.getClass(), "dexElements", pathList);

                // 获取patch_dex的dexElements（需要先加载dex）
                String dexopt = mContext.getDir("dexopt", 0).getAbsolutePath();
                DexClassLoader dexClassLoader = new DexClassLoader(path, dexopt, dexopt, mContext.getClassLoader());
                Object obj = ReflectUtil.getField(cl, "pathList", dexClassLoader);
                Object dexElements = ReflectUtil.getField(obj.getClass(), "dexElements", obj);

                // 合并两个Elements
                Object combineElements = ReflectUtil.combineArray(dexElements, baseElements);

                // 将合并后的Element数组重新赋值给app的classLoader
                ReflectUtil.setField(pathList.getClass(), "dexElements", pathList, combineElements);

                // ======== 以下是测试是否成功注入 =================
                Object object = ReflectUtil.getField(pathList.getClass(), "dexElements", pathList);
                int length = Array.getLength(object);
                Log.e("BugFixApplication", "length = " + length);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("HotFix", file.getAbsolutePath() + "does not exists");
        }
    }

}
