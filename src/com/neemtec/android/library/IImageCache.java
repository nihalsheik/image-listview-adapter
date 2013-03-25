package com.neemtec.android.library;

import android.graphics.Bitmap;

public interface IImageCache {

    public void add(String url, Bitmap bitmap);

    public Bitmap get(String url);

}
