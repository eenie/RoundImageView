package com.example.eenie.roundimageview;

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
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Eenie on 2015/12/15.
 * 圆形图片控件，最多支持两个外圆的颜色
 */
public class RoundImageView extends ImageView {


    private int mBorderThickness = 0;
    private Context mContext;
    private int defaultColor = 0xFFFFFFFF;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;


    public RoundImageView(Context context) {
        super(context);
        mContext = context;

    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setCustomAttributes(attrs);

    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomAttributes(attrs);
        mContext = context;
    }




    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }
        int radius = 0;
        if (mBorderInsideColor != defaultColor && mBorderOutsideColor != defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
            // 画内圆
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
            // 画外圆
            drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2, mBorderOutsideColor);
        } else if (mBorderInsideColor != defaultColor && mBorderOutsideColor == defaultColor) {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
        } else if (mBorderInsideColor == defaultColor && mBorderOutsideColor != defaultColor) {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderOutsideColor);
        } else {// 没有边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
        }
        Bitmap roundBitmap = getRoundBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
    }


    /**
     * 将原bitmap转换成圆形bitmap
     * @param bmp    原bitmap
     * @param radius 圆形的半径
     * @return 变形后的bitmap
     */
    public Bitmap getRoundBitmap(Bitmap bmp, int radius) {

        //圆形的直径
        int diameter = radius * 2;

        //获取原bitmap的宽高
        int bmpHeight = bmp.getHeight();
        int bmpWidth = bmp.getWidth();

        //正方形宽高
        int squareWidth = 0;
        int squareHeght = 0;

        //开始截取正方形的位置
        int x = 0;
        int y = 0;

        //正方形bitmap
        Bitmap squareBitmap = null;

        //根据直径缩放后的bitmap
        Bitmap scaledBitmap;

        //返回的bitmap
        Bitmap outBitmap = null;

        //从原有的bitmap上截取正方形bitmap
//        squareBitmap.createBitmap(bmp,
//                x,
//                y,
//                squareWidth,
//                squareHeght);


        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeght = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeght);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeght = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeght);
        } else {
            squareBitmap = bmp;
        }


        //缩放bitmap
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledBitmap = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
        } else {
            scaledBitmap = squareBitmap;
        }


        outBitmap = scaledBitmap.createBitmap(scaledBitmap.getWidth(),
                scaledBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        //设置画笔、画布
        Canvas bmpCanvas = new Canvas(outBitmap);
        Paint bmpPaint = new Paint();
        Rect rect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        bmpPaint.setAntiAlias(true);
        bmpPaint.setFilterBitmap(true);
        bmpPaint.setDither(true);

        bmpCanvas.drawARGB(0, 0, 0, 0);
        bmpCanvas.drawCircle(scaledBitmap.getWidth() / 2,
                scaledBitmap.getHeight() / 2,
                scaledBitmap.getWidth() / 2,
                bmpPaint);
        bmpPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        bmpCanvas.drawBitmap(scaledBitmap, rect, rect, bmpPaint);

        //释放资源
        bmp = null;
        squareBitmap = null;
        scaledBitmap = null;
        return outBitmap;
    }


    //设置自定义参数
    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.roundedimageview);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.roundedimageview_border_thickness, 0);
        mBorderOutsideColor = a.getColor(R.styleable.roundedimageview_border_outside_color, defaultColor);
        mBorderInsideColor = a.getColor(R.styleable.roundedimageview_border_inside_color, defaultColor);
    }


    /**
     * 画边缘的圆形
     *
     * @param canvas 画布
     * @param radius 半径
     * @param color  颜色
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
        /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        System.out.println("widthMeasureSpec" + widthMeasureSpec);
        System.out.println("heightMeasureSpec" + heightMeasureSpec);


        /**
         * 最后调用父类方法,把View的大小告诉父布局。
         */
//        setMeasuredDimension(width, height);

    }
}
