package com.aliex.hotfixlib;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Sim.G on 2017/4/19 09:52
 */
public class AssetsUtil {

    private static final int BUF_SIZE = 2048;

    public static void copyAssets(Context context, String assetsName, String destFilePath) {

        BufferedInputStream bis = null;
        OutputStream dexWriter = null;
        try {
            bis = new BufferedInputStream(context.getAssets().open(assetsName));
            dexWriter = new BufferedOutputStream(new FileOutputStream(new File(destFilePath)));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while ((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            dexWriter.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
