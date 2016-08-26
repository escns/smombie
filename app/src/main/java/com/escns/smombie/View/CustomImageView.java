package com.escns.smombie.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.escns.smombie.R;

/**
 * Created by Administrator on 2016-08-14.
 */

public class CustomImageView extends ImageView {

    private String type;
    private boolean border;
    private int borderWidth;
    private int borderColor;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs, defStyleAttr);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomImageView);
        setTypeArray(typedArray);
    }


    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);
        setTypeArray(typedArray);
    }

    /**
     * typeArray에 적힌 속성값들을 파싱하여 현재 View의 속성 변수들에 대입하여 줍니다.
     * 현재 View는 values/attribute.xml에 CustomImageView의 속성들을 사용합니다.
     * @param typedArray
     */
    private void setTypeArray(TypedArray typedArray) {

        type = typedArray.getString(R.styleable.CustomImageView_customImageViewType);

        border = typedArray.getBoolean(R.styleable.CustomImageView_customImageViewBorder, false);

        borderWidth = typedArray.getInteger(R.styleable.CustomImageView_customImageViewBorderWidth, 0);

        borderColor = typedArray.getColor(R.styleable.CustomImageView_customImageViewBorderColor, 0);

        typedArray.recycle();
    }

    /**
     * 기존의 onDraw를 Override하여 속성값들에 해당하는 방향으로 canvas에 image를 다시 그립니다
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        Drawable drawable = getDrawable();

        if(drawable==null) return;

        if(getWidth()==0 || getHeight()==0) return;

        Bitmap b = ((BitmapDrawable)drawable).getBitmap();

        if(b != null) {
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

            Bitmap roundBitmap = getRoundedCroppedBitmap(bitmap, getWidth(), getHeight());

            canvas.drawBitmap(roundBitmap,0,0,null);
        }
    }

    /**
     * 실질적으로 이미지를 그리고 Bitmap형식으로 return 해줍니다.
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int w, int h) {

        Bitmap sbmp;

        if (bitmap.getWidth() != w || bitmap.getHeight() != h) {
            float smallest = Math.min(bitmap.getWidth(), bitmap.getHeight());
            float factor = smallest / w;
            sbmp = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth() / factor), (int)(bitmap.getHeight() / factor), false);
        } else {
            sbmp = bitmap;
        }

        int radiusTo = Math.min(w / 2, h / 2);
        int radiusFrom = Math.min(bitmap.getWidth() / 2, bitmap.getHeight() / 2);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(w / 2, h / 2, radiusTo, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        final Rect rect = new Rect(0, 0, w, w);

        canvas.drawBitmap(sbmp, rect, rect, paint);

        //draw border

        if(border == true) {
            final Paint paint2 = new Paint();
            paint2.setAntiAlias(true);
            paint2.setColor(borderColor);
            paint2.setStrokeWidth(borderWidth);
            paint2.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(w / 2, h / 2, radiusTo - borderWidth/2, paint2);
        }

        return output;
    }
}
