package com.hung.circleimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * cách này tận dụng lại trường android:src=@drawable/test_image của ImageView
 */
public class CircleImageViewOld extends AppCompatImageView {

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private Drawable mImageDrawable;

    public CircleImageViewOld(Context context) {
        super(context);
    }

    public CircleImageViewOld(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageViewOld(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // declare custome Attribute: /res/values/attrs.xml
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, 0, 0);

        // Phần Source code lấy Attribute hiện tại ko dùng đến
        try {
            // get custom attribute from Layout of this ImageView

            mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, DEFAULT_BORDER_WIDTH);
            mBorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR);
        } finally {
            a.recycle();
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        Log.e("TAG", "=======================aaaaaaaaaaa" );
        //getDrawable(): trả về android:src="@drawable/test_image  với kích thước thực (ko phải kích thước của ImageView)
        // vi the phai Scale image moi dc
        Bitmap bitmap = circleBitmap(scaleBitmap(getBitmapFromDrawable(getDrawable())));
        canvas.drawBitmap(bitmap, 0, 0, null);
        //
    }

    private Bitmap circleBitmap(Bitmap bitmap){
        //================================Circle Image
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        //tranparency bitmap
        Bitmap bitmapDest = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapDest);
        canvas.drawCircle(this.getWidth()/2,this.getHeight()/2,this.getWidth()/2,paint);

        //
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,0,0,paint);
        return bitmapDest;
    }


    /**
     * Scale bitmap để kích thước vừa với View.
     * bitmap và View are square
     * Phải Scale trc khi circle bitmap để tối ưu performance
     */
    private Bitmap scaleBitmap(Bitmap bitmapSrc){
        Rect src, dst;

        //src Rect size
        src = new Rect(0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight()); //size of bitmap

        //dst Rect size
        Bitmap bitmapDest = Bitmap.createBitmap(this.getWidth(),this.getHeight(),Bitmap.Config.ARGB_8888);
        dst = new Rect(0,0,this.getWidth(),this.getHeight());  //size of this view

        Canvas canvas = new Canvas(bitmapDest);
        canvas.drawBitmap(bitmapSrc, src, dst, null); //scale bitmap from src to dst

        return bitmapDest;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) { // ảnh 1 màu
                bitmap =Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);
            } else { //anh thông thường
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            // thiet lap vung gioi han Cliping, chi dc ve trong vung nay
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
