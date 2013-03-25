package com.neemtec.android.library;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public abstract class ImageListAdapter extends BaseAdapter {

    public enum CacheMethod {
        MEMORY, DISK
    }

    private final static String TAG = "ImageListAdapter";

    protected List<Object> listItems;

    private Handler handler = null;
    private ExecutorService executorService = null;
    private String cacheDir = "";
    private CacheMethod cacheMethod;
    private int resource = 0;
    private int defaultIconResource = 0;
    private IImageCache cache = null;
    private List<Integer> notFoundItems;

    //----------------------------------------------------------------------------//

    public ImageListAdapter(Context context, List<Object> listItems) {
        this.listItems = listItems;
        handler = new Handler();
        executorService = Executors.newFixedThreadPool(5);
        notFoundItems = new ArrayList<Integer>();
        cacheMethod = CacheMethod.MEMORY;
        cache = new MemoryCache();
    }

    //----------------------------------------------------------------------------//

    @Override
    public int getCount() {
        return listItems.size();
    }

    //----------------------------------------------------------------------------//

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //----------------------------------------------------------------------------//

    public String getCacheDir() {
        return cacheDir;
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    //----------------------------------------------------------------------------//

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    //----------------------------------------------------------------------------//

    public int getDefaultIconResource() {
        return defaultIconResource;
    }

    public void setDefaultIconResource(int defaultIconResource) {
        this.defaultIconResource = defaultIconResource;
    }

    //----------------------------------------------------------------------------//

    public CacheMethod getCacheMethod() {
        return cacheMethod;
    }

    public void setCacheMethod(CacheMethod cacheMethod) {
        this.cacheMethod = cacheMethod;
        cache = (cacheMethod.equals(CacheMethod.MEMORY)) ? new MemoryCache() : new FileCache(cacheDir);
    }

    //----------------------------------------------------------------------------//

    public void loadImage(ImageView imageView, String url, int position) {
        executorService.submit(new Displayer(imageView, url, position));
    }

    //----------------------------------------------------------------------------//

    public Bitmap getBitmap(String url, int position) {
        if (notFoundItems.contains(position)) {
            Log.i(TAG, "Not Found");
            return null;
        }
        Bitmap b = null;
        try {
            b = cache.get(url);

            if (b != null) {
                Log.i(TAG, "Loading bitmap from cache");
                return b;
            }

            Log.i(TAG, "getBitmap ************* : " + String.valueOf(position));

            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            b = decodeFile(is);
            conn.disconnect();
            cache.add(url, b);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Not found : " + url);
            e.printStackTrace();
            _addToNotFoundList(position);
        } catch (Exception e) {
            e.printStackTrace();
            _addToNotFoundList(position);
        }
        return b;
    }

    //----------------------------------------------------------------------------//

    protected Bitmap decodeFile(InputStream in) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream is = new BufferedInputStream(in);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = false;
            o.inSampleSize = 2;
            bitmap = BitmapFactory.decodeStream(is, null, o);
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Decode Error " + e.getMessage());
        }
        return bitmap;
    }

    //----------------------------------------------------------------------------//

    private void _addToNotFoundList(int position) {
        if (!notFoundItems.contains(position)) {
            notFoundItems.add(position);
        }
    }

    //----------------------------------------------------------------------------//

    private class Displayer implements Runnable {
        private ImageView imageView = null;
        private String url;
        private int position;

        public Displayer(ImageView imageView, String url, int position) {
            this.imageView = imageView;
            this.url = url;
            this.position = position;
            imageView.setImageResource(defaultIconResource);
        }

        @Override
        public void run() {
            Bitmap bitmap = getBitmap(url, position);
            handler.post(new BitmapDisplayer(bitmap, imageView));
        }
    }

    //----------------------------------------------------------------------------//

    class BitmapDisplayer implements Runnable {
        private Bitmap bitmap;
        private ImageView imageView;

        public BitmapDisplayer(Bitmap bitmap, ImageView imageView) {
            this.bitmap = bitmap;
            this.imageView = imageView;
        }

        public void run() {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //----------------------------------------------------------------------------//
}