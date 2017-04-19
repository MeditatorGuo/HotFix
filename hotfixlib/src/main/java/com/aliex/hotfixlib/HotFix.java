package com.aliex.hotfixlib;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.util.Log;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by Sim.G on 2017/4/19 09:49
 */
public class HotFix {

    public static void inject(Context context, String patchDexFile, String patchClassName) {
        if (patchDexFile != null && new File(patchDexFile).exists()) {
            try {
                if (hasLexClassLoader()) {
                    injectInAliyunOs(context, patchDexFile, patchClassName);
                } else if (hasDexClassLoader()) {
                    injectAboveEqualApiLevel14(context, patchDexFile, patchClassName);
                } else {
                    injectBelowApiLevel14(context, patchDexFile, patchClassName);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean hasLexClassLoader() {
        try {
            Class.forName("dalvik.system.LexClassLoader");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean hasDexClassLoader() {
        try {
            Class.forName("dalvik.system.BaseDexClassLoader");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void injectInAliyunOs(Context context, String patchDexFile, String patchClassName)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, NoSuchFieldException {
        PathClassLoader obj = (PathClassLoader) context.getClassLoader();
        String replaceAll = new File(patchDexFile).getName().replaceAll("\\.[a-zA-Z0-9]+", ".lex");
        Class cls = Class.forName("dalvik.system.LexClassLoader");
        Object newInstance = cls
                .getConstructor(new Class[] { String.class, String.class, String.class, ClassLoader.class })
                .newInstance(new Object[] { context.getDir("dex", 0).getAbsolutePath() + File.separator + replaceAll,
                        context.getDir("dex", 0).getAbsolutePath(), patchDexFile, obj });
        cls.getMethod("loadClass", new Class[] { String.class }).invoke(newInstance, new Object[] { patchClassName });
        ReflectUtil.setField(PathClassLoader.class, "mPaths",
                ReflectUtil.appendArray(ReflectUtil.getField(PathClassLoader.class, "mPaths", obj),
                        ReflectUtil.getField(cls, "mRawDexPath", newInstance)),
                obj);
        ReflectUtil.setField(PathClassLoader.class, "mFiles",
                ReflectUtil.combineArray(ReflectUtil.getField(PathClassLoader.class, "mFiles", obj),
                        ReflectUtil.getField(cls, "mFiles", newInstance)),
                obj);
        ReflectUtil.setField(PathClassLoader.class, "mZips",
                ReflectUtil.combineArray(ReflectUtil.getField(PathClassLoader.class, "mZips", obj),
                        ReflectUtil.getField(cls, "mZips", newInstance)),
                obj);
        ReflectUtil.setField(PathClassLoader.class, "mLexs",
                ReflectUtil.combineArray(ReflectUtil.getField(PathClassLoader.class, "mLexs", obj),
                        ReflectUtil.getField(cls, "mDexs", newInstance)),
                obj);
    }

    private static void injectAboveEqualApiLevel14(Context context, String str1, String str2)
            throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {

        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = ReflectUtil.getPathList(pathClassLoader);
        Object baseElements = ReflectUtil.getDexElements(pathList);

        DexClassLoader dexClassLoader = new DexClassLoader(str1, context.getDir("dex", 0).getAbsolutePath(), str1,
                context.getClassLoader());

        Object obj = ReflectUtil.getPathList(dexClassLoader);
        Object dexElements = ReflectUtil.getDexElements(obj);

        Object combineElements = ReflectUtil.combineArray(dexElements, baseElements);
        // 将合并后的Element数组重新赋值给app的classLoader
        ReflectUtil.setField(pathList.getClass(), "dexElements", pathList, combineElements);
        pathClassLoader.loadClass(str2);
    }

    private static void injectBelowApiLevel14(Context context, String str1, String str2)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

        PathClassLoader obj = (PathClassLoader) context.getClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(str1, context.getDir("dex", 0).getAbsolutePath(), str1,
                context.getClassLoader());
        dexClassLoader.loadClass(str2);

        ReflectUtil.setField(PathClassLoader.class, "mPaths",
                ReflectUtil.appendArray(ReflectUtil.getField(PathClassLoader.class, "mPaths", obj),
                        ReflectUtil.getField(DexClassLoader.class, "mRawDexPath", dexClassLoader)),
                obj);
        ReflectUtil.setField(PathClassLoader.class, "mFiles",
                ReflectUtil.combineArray(ReflectUtil.getField(PathClassLoader.class, "mFiles", obj),
                        ReflectUtil.getField(DexClassLoader.class, "mFiles", dexClassLoader)),
                obj);
        ReflectUtil.setField(PathClassLoader.class, "mZips",
                ReflectUtil.combineArray(ReflectUtil.getField(PathClassLoader.class, "mZips", obj),
                        ReflectUtil.getField(DexClassLoader.class, "mZips", dexClassLoader)),
                obj);
        ReflectUtil.setField(PathClassLoader.class, "mDexs",
                ReflectUtil.combineArray(ReflectUtil.getField(PathClassLoader.class, "mDexs", obj),
                        ReflectUtil.getField(DexClassLoader.class, "mDexs", dexClassLoader)),
                obj);
        obj.loadClass(str2);

    }

}
