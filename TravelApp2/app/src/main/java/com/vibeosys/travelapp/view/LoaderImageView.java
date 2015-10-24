package com.vibeosys.travelapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.vibeosys.travelapp.RecyclingImageView;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Free for anyone to use, just say thanks and share
 * @author Blundell
 *
 */
public class LoaderImageView extends LinearLayout{

    private static final int COMPLETE = 0;
    private static final int FAILED = 1;

    private Context mContext;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private ProgressBar mSpinner;
    private RecyclingImageView mImage;

    /**
     * This is used when creating the view in XML
     * To have an image load in XML use the tag 'image="http://developer.android.com/images/dialog_buttons.png"'
     * Replacing the url with your desired image
     * Once you have instantiated the XML view you can call
     * setImageDrawable(url) to change the image
     * @param context
     * @param attrSet
     */
    public LoaderImageView(final Context context, final AttributeSet attrSet) {
        super(context, attrSet);
        final String url = attrSet.getAttributeValue(null, "image");
        if(url != null){
            instantiate(context, url);
        } else {
            instantiate(context, null);
        }
    }

    /**
     * This is used when creating the view programatically
     * Once you have instantiated the view you can call
     * setImageDrawable(url) to change the image
     * @param context the Activity context
     * @param imageUrl the Image URL you wish to load
     */
    public LoaderImageView(final Context context, final String imageUrl) {
        super(context);
        instantiate(context, imageUrl);
    }

    /**
     *  First time loading of the LoaderImageView
     *  Sets up the LayoutParams of the view, you can change these to
     *  get the required effects you want
     */
    private void instantiate(final Context context, final String imageUrl) {
        mContext = context;

        mImage = new RecyclingImageView(mContext);
        mImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        mSpinner = new ProgressBar(mContext);
        mSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mSpinner.setIndeterminate(true);

        addView(mSpinner);
        addView(mImage);

        if(imageUrl != null){
            setImageDrawable(imageUrl);
        }
    }

    /**
     * Set's the view's drawable, this uses the internet to retrieve the image
     * don't forget to add the correct permissions to your manifest
     * @param imageUrl the url of the image you wish to load
     */
    public void setImageDrawable(final String imageUrl) {
        mDrawable = null;
        mSpinner.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.GONE);
        new Thread(){
            public void run() {
                try {
                    if(imageUrl.startsWith("file")) {
                        if(mBitmap == null) {
                            mBitmap = decodeURI(imageUrl.replaceAll("file:", ""));
                        }
                    }
                    else {
                        mDrawable = getDrawableFromUrl(imageUrl);
                    }
                    imageLoadedHandler.sendEmptyMessage(COMPLETE);
                } catch (MalformedURLException e) {
                    imageLoadedHandler.sendEmptyMessage(FAILED);
                } catch (IOException e) {
                    imageLoadedHandler.sendEmptyMessage(FAILED);
                }
            };
        }.start();
    }

    /**
     * Callback that is received once the image has been downloaded
     */
    private final Handler imageLoadedHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE:

                    if(mBitmap != null) {
                        mImage.setImageBitmap(mBitmap);
                    }
                    else {
                        mImage.setImageDrawable(mDrawable);
                    }

                    mImage.setVisibility(View.VISIBLE);
                    mSpinner.setVisibility(View.GONE);
                    break;
                case FAILED:
                default:
                    // Could change image here to a 'failed' image
                    // otherwise will just keep on spinning
                    break;
            }
            return true;
        }
    });

    public void setImageResource(int imageResource) {
        mSpinner.setVisibility(View.GONE);
        mImage.setVisibility(View.VISIBLE);
        mImage.setImageResource(imageResource);
    }

    /*
     * Pass in an image url to get a drawable object
     * @return a drawable object
     * @throws IOException
     * @throws MalformedURLException
     */
    private static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {

        return Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), "name");
    }


    public void loadImageFromFile(String path) {
        setImageDrawable("file:"+path);
    }

    public Bitmap decodeURI(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;


        // Only scale if we need to
        // (16384 buffer for img processing)
        //Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        /*if(options.outHeight * options.outWidth * 2 >= 16384){
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int)Math.pow(2d, Math.floor(
                            Math.log(sampleSize)/Math.log(2d)));
        }*/

        options.outHeight = 200;
        options.outWidth = 200;
        BitmapFactory.decodeFile(filePath, options);
        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, 200, 200);
        //options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);
        return output;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}