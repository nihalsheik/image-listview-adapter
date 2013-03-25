package com.neemtec.android.library;

import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;

@SuppressWarnings("unused")
public class MemoryCache implements IImageCache {

    private final static String TAG = "ImageListAdapter";
    private Map<String, Bitmap> list;

    //----------------------------------------------------------------------------//

    public MemoryCache() {
        list = new HashMap<String, Bitmap>();
    }

    //----------------------------------------------------------------------------//

    @Override
    public void add(String url, Bitmap bitmap) {
        list.put(String.valueOf(url.hashCode()), bitmap);
    }

    //----------------------------------------------------------------------------//

    @Override
    public Bitmap get(String url) {
        return list.get(String.valueOf(url.hashCode()));
    }

    //----------------------------------------------------------------------------//
}
