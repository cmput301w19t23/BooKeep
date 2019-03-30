package com.example.bookeep;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

/**
 * Used to download images for ImageViews
 * @author Jeff Kirker
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    /**
     * Downloads a bitmap in the background and returns it
     * @param urls the image's URL to download
     * @return The specified image
     */
    protected Bitmap doInBackground(String... urls) {

        String urldisplay = urls[0];
        Bitmap mIcon11 = null;

        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    /**
     *
     * @param result
     */
    protected void onPostExecute(Bitmap result) {}
}