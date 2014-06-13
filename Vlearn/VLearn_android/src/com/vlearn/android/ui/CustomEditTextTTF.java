package com.vlearn.android.ui;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class CustomEditTextTTF extends EditText {

    Context context;
    String ttfName;
    boolean isDisable;

    String TAG = getClass().getName();

    public CustomEditTextTTF(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

      //  for (int i = 0; i < attrs.getAttributeCount(); i++) {
        
            this.ttfName = attrs.getAttributeValue(
                    "http://schemas.android.com/apk/res/com.vlearn.android", "customttfEditText");
            this.isDisable = Boolean.parseBoolean(attrs.getAttributeValue(
                    "http://schemas.android.com/apk/res/com.vlearn.android", "customttdisable"));
            
            if(ttfName != null)
            	init();

     //   }

    }

    private void init() {
    	try{
        Typeface font = Typeface.createFromAsset(context.getAssets(), ttfName);
        setTypeface(font);
        if(isDisable){ 
        	setKeyListener(null);
        	setClickable(true);
        	setFocusable(false);
        	setFocusableInTouchMode(true);
        }
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
       
    }

    @Override
    public void setTypeface(Typeface tf) {

        // TODO Auto-generated method stub
        super.setTypeface(tf);
    }


    /**
     * @see android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)
     */
    @Override
    public void setCompoundDrawables(Drawable left, Drawable top,
    		Drawable right, Drawable bottom) {
    	// TODO Auto-generated method stub
    	final Drawable previousDrawable = getCompoundDrawables()[0];
    	super.setCompoundDrawables(left, top, right, bottom);
        // Notify new Drawable that it is being displayed
        notifyDrawable(left, true);

        // Notify old Drawable so it is no longer being displayed
        notifyDrawable(previousDrawable, false);
    }
    
    
    /**
     * Notifies the drawable that it's displayed state has changed.
     *
     * @param drawable
     * @param isDisplayed
     */
    private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
        if (drawable instanceof RecyclingBitmapDrawable) {
            // The drawable is a CountingBitmapDrawable, so notify it
            ((RecyclingBitmapDrawable) drawable).setIsDisplayed(isDisplayed);
        } else if (drawable instanceof LayerDrawable) {
            // The drawable is a LayerDrawable, so recurse on each layer
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
            }
        }
    }

    /**
     * @see android.widget.ImageView#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        // This has been detached from Window, so clear the drawable
    	setCompoundDrawables(null,null,null,null);

        super.onDetachedFromWindow();
    }
}