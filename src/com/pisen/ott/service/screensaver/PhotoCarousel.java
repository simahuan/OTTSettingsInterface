package com.pisen.ott.service.screensaver;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.izy.util.LogCat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pisen.ott.settings.R;

/**
 * A FrameLayout that holds two photos, back to back.
 * 重新继承的布局
 */
public class PhotoCarousel extends FrameLayout {
    private static final String TAG = "PhotoCarousel";
    private static final boolean DEBUG = false;
    private static final int LANDSCAPE = 1;
    private static final int PORTRAIT = 2;
    private final Flipper mFlipper;
    private final View[] mPanel;
    private final int mFlipDuration;
    private final int mDropPeriod;
    private final HashMap<View, Bitmap> mBitmapStore;
    private final LinkedList<Bitmap> mBitmapQueue;
    private int mOrientation;
    private int mWidth;
    private int mHeight;
    private long mLastFlipTime;
    private int mScreenWidth;
    private int mScreenHeight;
    private Resources resources;

    /**
     * 按显示屏幕缩放图片
     * @param drawable
     */
    public  Bitmap createFitinBitmap(int drawable) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, drawable, opts);
		
		int sampleSize1 = opts.outWidth / mScreenWidth;
		int sampleSize2 = opts.outHeight / mScreenHeight;
		opts.inSampleSize = sampleSize1>sampleSize2? sampleSize1 : sampleSize2;
		opts.inJustDecodeBounds = false;
        opts.inDither = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeResource(resources, drawable, opts);
	}
    
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//wakeUp();
		return true;
	}
    
	 class Flipper implements Runnable {
        public void run() {
            if (mBitmapQueue.isEmpty()) {
            		mBitmapQueue.add(createFitinBitmap(R.drawable.test_screenpro_one));
            		mBitmapQueue.add(createFitinBitmap(R.drawable.test_screenpro_two));
            		mBitmapQueue.add(createFitinBitmap(R.drawable.test_screenpro_three));
            		mBitmapQueue.add(createFitinBitmap(R.drawable.test_screenpro_four));
            		mBitmapQueue.add(createFitinBitmap(R.drawable.test_screenpro_five));
            } 
            long now = System.currentTimeMillis();
            long elapsed = now - mLastFlipTime;
            if (elapsed < mDropPeriod) {
                scheduleNext((int) mDropPeriod - elapsed);  
            } else {
                scheduleNext(mDropPeriod);					
                if (changePhoto() ||(elapsed > (5 * mDropPeriod) && canFlip())) {
                    flip(1f);
                    mLastFlipTime = now;
                }
            }
        }

        private void scheduleNext(long delay) {
            removeCallbacks(mFlipper);
            postDelayed(mFlipper, delay);
        }
    }


    public PhotoCarousel(Context context, AttributeSet as) {
        super(context, as);
        resources = getResources();
        mScreenWidth =  resources.getDisplayMetrics().widthPixels;  
        mScreenHeight = resources.getDisplayMetrics().heightPixels;
        mDropPeriod = resources.getInteger(R.integer.carousel_drop_period);  				//5000ms
        mFlipDuration = resources.getInteger(R.integer.flip_duration);                      //1张图片切换交互的时间 
                                                                                             
        mBitmapStore = new HashMap<View, Bitmap>();
        mBitmapQueue = new LinkedList<Bitmap>();
        
        mPanel = new View[2];
        mFlipper = new Flipper();
    }
    
    private float lockTo180(float a) {
        return 180f * (float) Math.floor(a / 180f);
    }
    
    private float wrap360(float a) {
        return a - 360f * (float) Math.floor(a / 360f);
    }
    
    private ImageView getBackface() {
        return (ImageView) ((mPanel[0].getAlpha() < 0.5f) ? mPanel[0] : mPanel[1]);
    }

    private boolean canFlip() {
    	 LogCat.e(TAG,"canFlip=:"+mBitmapStore.containsKey(getBackface()));
         return mBitmapStore.containsKey(getBackface());
    }

    private boolean changePhoto() {
        Bitmap photo = mBitmapQueue.poll();  
        LogCat.d(TAG,"photo=:"+photo);
        
        if (photo != null) {
            ImageView destination = getBackface();
            int width = photo.getWidth();
            int height = photo.getHeight();
            int orientation = (width > height ? LANDSCAPE : PORTRAIT);
            
            destination.setImageBitmap(photo);//设置图片 
            destination.setTag(R.id.photo_orientation, Integer.valueOf(orientation));
            destination.setTag(R.id.photo_width, Integer.valueOf(width));
            destination.setTag(R.id.photo_height, Integer.valueOf(height));          
            setScaleType(destination);

            return true;
        } else {
        	 LogCat.e(TAG,"changePhoto=:photo=null");
             return false;
        }
    }
    
	/**
	 * 	设置图片适应屏幕方式
	 */
    private void setScaleType(View photo) {
        if (photo.getTag(R.id.photo_orientation) != null) {
            int orientation = ((Integer) photo.getTag(R.id.photo_orientation)).intValue();
            int width = ((Integer) photo.getTag(R.id.photo_width)).intValue();
            int height = ((Integer) photo.getTag(R.id.photo_height)).intValue();
            
            if (width < mWidth && height < mHeight) {
                log("too small: FIT_CENTER");
                ((ImageView) photo).setScaleType(ImageView.ScaleType.FIT_XY);
            } else if (orientation == mOrientation) {
                log("orientations match: CENTER_CROP");
                ((ImageView) photo).setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                log("orientations do not match: CENTER_INSIDE");
                ((ImageView) photo).setScaleType(ImageView.ScaleType.FIT_XY);
            }
        } else {
            	log("no tag!,没有存入图片的方向问题.....");
        }
    }

    
    public void flip(float sgn) {
        mPanel[0].animate().cancel();
        mPanel[1].animate().cancel();

        float frontY = mPanel[0].getRotationY();
        float backY = mPanel[1].getRotationY();
        float frontA = mPanel[0].getAlpha();
        float backA = mPanel[1].getAlpha();
        
        frontY = wrap360(frontY);
        backY = wrap360(backY);
        
        mPanel[0].setRotationY(frontY);
        mPanel[1].setRotationY(backY);
        
        frontY = lockTo180(frontY + sgn * 180f);
        backY = lockTo180(backY + sgn * 180f);
        frontA = 1f - frontA;
        backA = 1f - backA;
        // Don't rotate
        frontY = backY = 0f;
        ViewPropertyAnimator frontAnim = mPanel[0].animate()
                .rotationY(frontY)
                .alpha(frontA)
                .setDuration(mFlipDuration);
        
        ViewPropertyAnimator backAnim = mPanel[1].animate()
                .rotationY(backY)
                .alpha(backA)
                .setDuration(mFlipDuration);
        
        frontAnim.start();
        backAnim.start();
    }
    
    @Override
    public void onAttachedToWindow() {
        mPanel[0]= findViewById(R.id.front);
        mPanel[1] = findViewById(R.id.back);
        mFlipper.run();
    }
    
    @Override
    protected void onDetachedFromWindow() {
    	 LogCat.e(TAG,"onDetachedFromWindow=:stopDayDream！");
    	 if(null!=mFlipper){
    	   removeCallbacks(mFlipper);
    	 }
    	super.onDetachedFromWindow();
    }
    
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mHeight = bottom - top;
        mWidth = right - left;
        mOrientation = (mWidth > mHeight ? LANDSCAPE : PORTRAIT);
        setScaleType(mPanel[0]);
        setScaleType(mPanel[1]);
        super.onLayout(changed, left, top, right, bottom);
    }
    
    private void log(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }
}
