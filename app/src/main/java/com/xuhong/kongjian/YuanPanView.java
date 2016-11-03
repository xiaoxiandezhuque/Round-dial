package com.xuhong.kongjian;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by BHKJ on 2016/11/2.
 */

public class YuanPanView extends View {

    private int mWidth;
    private int spotCenter, bitmapLeft, bitmapWidth;

    private int downRegion, upRegin = 2;

    //当前选中的，去除重复点击
    private int upReginLast = 2;


    private boolean isDown;


    private Paint paint;
    private Bitmap bitmap;

    public YuanPanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.beiji);
        bitmapWidth = bitmap.getWidth();

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        spotCenter = mWidth / 2;
        bitmapLeft = (mWidth - bitmapWidth) / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画出点击事件的背景
        if (isDown) {
            int startAngle = 0, sweepAngle = 0;
            switch (downRegion) {
                case 1:
                    startAngle = 180;
                    sweepAngle = 60;
                    break;
                case 2:
                    startAngle = 240;
                    sweepAngle = 60;
                    break;
                case 3:
                    startAngle = 300;
                    sweepAngle = 60;
                    break;
                case 4:
                    startAngle = 0;
                    sweepAngle = 90;
                    break;
                case 5:
                    startAngle = 90;
                    sweepAngle = 90;
                    break;

            }

            paint.setColor(Color.argb(200, 229, 229, 229));
            paint.setStrokeWidth(bitmapLeft);
            paint.setStyle(Paint.Style.STROKE);
            RectF rectF = new RectF(0 + bitmapLeft / 2, 0 + bitmapLeft / 2, mWidth - bitmapLeft / 2, mWidth - bitmapLeft / 2);
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
        }


        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);


        //划线
        canvas.drawCircle(spotCenter, spotCenter, spotCenter, paint);
        canvas.drawLine(0, spotCenter, mWidth, spotCenter, paint);
        canvas.drawLine(spotCenter, spotCenter, spotCenter, mWidth, paint);
        //左上的线
        int spotLeftX = spotCenter - (int) (spotCenter * Math.cos(Math.PI * 2 / 360 * 60));
        int spotRightX = spotCenter + (int) (spotCenter * Math.cos(Math.PI * 2 / 360 * 60));
        int spotY = spotCenter - (int) (spotCenter * Math.sin(Math.PI * 2 / 360 * 60));
        canvas.drawLine(spotCenter, spotCenter, spotLeftX, spotY, paint);
        canvas.drawLine(spotCenter, spotCenter, spotRightX, spotY, paint);

//画中间的圆
        canvas.save();
        canvas.rotate(bitmapRotate, spotCenter, spotCenter);
        canvas.drawBitmap(bitmap, bitmapLeft, bitmapLeft, paint);
        canvas.restore();

// 画字

        paint.setTextSize(60);
        paint.setStyle(Paint.Style.FILL);
        canvas.save();
        String displayText = "2";
        int textWidth = (int) paint.measureText(displayText);
        Paint.FontMetrics fm = paint.getFontMetrics();
        int textHeight = (int) Math.ceil(fm.descent - fm.ascent);
        int spotWordX = spotCenter - textWidth / 2;
        int spotWordY = bitmapLeft / 2 + textHeight / 2;

        canvas.drawText("2", spotWordX, spotWordY, paint);
        canvas.rotate(60, spotCenter, spotCenter);
        canvas.drawText("3", spotWordX, spotWordY, paint);
        canvas.rotate(75, spotCenter, spotCenter);
        canvas.drawText("+", spotWordX, spotWordY, paint);
        canvas.rotate(90, spotCenter, spotCenter);
        canvas.drawText("-", spotWordX, spotWordY, paint);
        canvas.rotate(75, spotCenter, spotCenter);
        canvas.drawText("1", spotWordX, spotWordY, paint);
        canvas.restore();


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRotate){
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) event.getX();
                int downY = (int) event.getY();
                downRegion = downRegion(downX, downY);
                if (downRegion != 0) {
                    isDown = true;
                    postInvalidate();
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                postInvalidate();
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                upRegin = downRegion(upX, upY);
                if (upRegin == downRegion && mListener != null) {
                    mListener.onClick(upRegin);
                    isDown = false;
                    mHandler.sendEmptyMessage(1);
                    return true;
                }
            case MotionEvent.ACTION_CANCEL:
                isDown = false;
                postInvalidate();
                break;

        }


        return super.onTouchEvent(event);
    }

    //计算点击的区域
    private int downRegion(int x, int y) {
        int absX = Math.abs(spotCenter - x);
        int absY = Math.abs(spotCenter - y);
        int r = absX * absX + absY * absY;
        //比较半径
        if (r <= spotCenter * spotCenter && r >= ((bitmapWidth / 2) * (bitmapWidth / 2))) {
//                   在上 还是在下
            if (y <= spotCenter) {
                int y1 = spotCenter - (int) (absX * Math.tan(Math.PI * 2 / 360 * 60));
                if (x < spotCenter && y >= y1) {
                    return 1;
                } else if (x > spotCenter && y > y1) {
                    return 3;
                } else {
                    return 2;
                }

            } else {
//                        左  右
                if (x <= spotCenter) {
                    return 5;
                } else {
                    return 4;
                }
            }
        }
        return 0;
    }

    int bitmapRotate = 0;
    int rotateNum;
    //是否想右转
    boolean isRotateRight;

    boolean  isRotate;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    if (upRegin != upReginLast && upRegin > 0 && upRegin < 4) {
                        int rotate = (upRegin - upReginLast) * 60;
                        isRotateRight = rotate > 0 ? true : false;

                        rotateNum = Math.abs(rotate) / 6;
//                        bitmapRotate = rotate/rotateNum;
                        upReginLast = upRegin;
                        isRotate = true;
                        this.sendEmptyMessage(2);

                    }
                    break;
                case 2:

                    if (rotateNum != 0) {
                        if (isRotateRight){
                            bitmapRotate = bitmapRotate + 6;
                        }else {
                            bitmapRotate = bitmapRotate - 6;
                        }

                        postInvalidate();
                        this.sendEmptyMessageDelayed(2, 5);
                        rotateNum--;
                    }else {
                        isRotate=false;
                    }

                    break;
            }


        }
    };


    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    private OnClickListener mListener;

    //  1 2 3   4+   5-
    public interface OnClickListener {

        void onClick(int num);
    }


}
