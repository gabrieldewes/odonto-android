package com.dewes.odonto.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dewes on 20/05/2017.
 */

public class ImageHelper extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private boolean blur;

    // Supported range 0 < radius <= 25
    private static final float BLUR_RADIUS = 10f;

    public ImageHelper(ImageView imageView) {
        this.imageView = imageView;
        this.blur = false;
    }

    public ImageHelper(ImageView imageView, boolean blur) {
        this.imageView = imageView;
        this.blur = blur;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        }
        catch (IOException ex) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null)
            imageView.setVisibility(View.GONE);
        else {
            if (blur) {
                Bitmap blurredBitmap = blur(bitmap, imageView.getContext());
                imageView.setImageBitmap(blurredBitmap);
            }
            else {
                imageView.setImageBitmap(bitmap);
            }

            imageView.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static Bitmap blur(Bitmap image, Context context) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
}
