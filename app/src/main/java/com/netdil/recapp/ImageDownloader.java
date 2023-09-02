package com.netdil.recapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.util.HashMap;

class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    private static boolean debugging= true;
    public ImageDownloader(ImageView bmImage ) {
        this.bmImage = bmImage;
        debugging = BuildConfig.DEBUG;
    }


    static long lastCon = 0;
    static int period = 700;

    private static InputStream connect(String url) {

        long elapsed = System.currentTimeMillis() - lastCon;
        if (elapsed < period) {
            try {
                Thread.sleep(period - elapsed);
            } catch (Exception e) {
            }
        }
        lastCon = System.currentTimeMillis();
        try {
            return new java.net.URL(url).openStream();
        } catch (Exception e) {
            if(debugging) {
                System.out.println("ERROR: FTEEI TO CONNECTION " + e.getMessage());
            }
            return null;
        }
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon = null;

        try {            
            InputStream in =  new java.net.URL(url).openStream();
            if(debugging) {
                System.out.println("KANEI NEW CONNECTION");
            }
            mIcon = BitmapFactory.decodeStream(in);
            mIcon = Bitmap.createScaledBitmap(mIcon, 200, 300, true);

        }catch(RuntimeException e ){
            if(debugging) {
                System.out.println("RuntimeException in imagedownload");
            }
        }catch (Exception e) {
            if(debugging) {
                Log.e("Error", e.getMessage());
            }
        }

        if(mIcon == null) {
            mIcon = BitmapFactory.decodeResource(ApplContext.getAppContext().getResources(),
                    R.drawable.noimage);
            if(debugging) {
                System.out.println("icon is null :D");
            }
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);

    }

    private void bmOptions(Bitmap image){
        System.out.println("------------------------------------------------------------------------");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(ApplContext.getAppContext().getResources(), image.getGenerationId(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        System.out.println("height: " + imageHeight + " width: " + imageWidth + " imageType: " + imageType);
    }



}
