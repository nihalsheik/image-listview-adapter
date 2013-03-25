package com.neemtec.android.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileCache implements IImageCache {

    private final static String TAG = "ImageListAdapter";
    private String cacheDir = "";

    //----------------------------------------------------------------------------//

    public FileCache(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    //----------------------------------------------------------------------------//

    @Override
    public void add(String url, Bitmap bitmap) {
        File f = new File(cacheDir, String.valueOf(url.hashCode()) + ".png");
        try {
            FileOutputStream os = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "add Bitmap error");
        } catch (IOException e) {
            Log.e(TAG, "add Bitmap error");
        }

    }

    //----------------------------------------------------------------------------//

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = null;
        try {
            File f = new File(cacheDir, String.valueOf(url.hashCode()) + ".png");
            if (f.exists()) {
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "cache get error " + e.getMessage());
                }
            }
        } catch (Exception ex) {
        }
        return bitmap;
    }

    //----------------------------------------------------------------------------//
}
