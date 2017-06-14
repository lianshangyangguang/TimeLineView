package com.time.view.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

import java.util.Arrays;


public class TimeLineView extends TextureView implements TextureView.SurfaceTextureListener, ScaleScroller.ScrollingListener {

    ScaleScroller mScroller;

    private Rect mTextRect = new Rect();
    private RectF mBorderRectF = new RectF();

    private Paint mBorderPaint = new Paint();
    private Paint mCurrentMarkPaint = new Paint();
    private Paint mScaleMarkPaint = new Paint();
    //数字字体大小
    private int mTextHeight = 20;
    //中心点数字
    private int mCenterNum=0;
    //刻度间距
    private int mdis = 14;
    private int dis;
    //最大数字
    private int maxNum = 24;
    //最小数字
    private int minNum = 0;
    //每一个刻度间相差数
    private int scaleNum = 1;
    //一个小时中的刻度数
    private int hourNum = 1;
    //刻度尺上显示的分钟
    private int leftM,rightM;
    //时间监听，可以获取当前位置所代表时间
    private NumberListener numberListener;
    //偏移量，滑动时在重绘中使用
    private int distance = 0;
    //背景颜色
    private int bgColor = Color.WHITE;
    //刻度线颜色
    private int markColor = 0xff979797;

    private Context context;



    public TimeLineView(Context context) {
        this(context, null,0);
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        mScroller = new ScaleScroller(getContext(), this);
        setSurfaceTextureListener(this);
        initPaints();
    }

    private void initPaints() {
        dis = dip2px(mdis);

        //边界线
        mBorderPaint.setColor(0xffffdfbe);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2);

        //刻度线
        mScaleMarkPaint.setColor(markColor);
        mScaleMarkPaint.setStyle(Paint.Style.FILL);
        mScaleMarkPaint.setStrokeWidth(3);
        mScaleMarkPaint.setTextSize(mTextHeight);

        //当前位置线
        mCurrentMarkPaint.setColor(Color.RED);
        mCurrentMarkPaint.setStyle(Paint.Style.FILL);
        mCurrentMarkPaint.setStrokeWidth(3);

    }

    //刷新视图
    private void refreshCanvas() {
        if (mBorderRectF.isEmpty()) {
            return;
        }
        Canvas canvas = lockCanvas();
        if (canvas != null) {
            canvas.drawColor(bgColor);
            drawBorder(canvas);
            drawScaleMark(canvas);
            drawMarkPoint(canvas);
        }
        unlockCanvasAndPost(canvas);
    }

    //画出所有刻度:从中间向两边画
    private void drawScaleMark(Canvas canvas) {
        int count = 0;
        final int centerX = (int) mBorderRectF.centerX();
        if (mCenterNum > maxNum)
            mCenterNum = maxNum;
        if (mCenterNum < minNum)
            mCenterNum = minNum;
        if (numberListener != null)
            numberListener.onChanged(getFormatTime());

        while (true) {
            int left = centerX + distance - dis * count;
            int leftNum = mCenterNum - count * scaleNum;
            int right = centerX + distance + dis * count;
            int rightNum = mCenterNum + count * scaleNum;
            String leftText ="";
            String rightText ="";
            if (hourNum <= 60 ){
                leftText = String.valueOf(Math.round(leftNum / hourNum)) + ":00";
                rightText = String.valueOf(Math.round(rightNum / hourNum)) + ":00";
            }else {
                leftM = (int)Math.round(((double) leftNum / hourNum)*60%60);
                rightM =(int) Math.round(((double) rightNum / hourNum)*60%60);
                leftText = String.valueOf(Math.round(leftNum / hourNum)) + ":"+formatTime(leftM);
                rightText = String.valueOf(Math.round(rightNum / hourNum)) + ":"+formatTime(rightM);
            }

            int showNum = hourNum;
            if (hourNum == 1) {
                showNum = 3;
            }
            if (hourNum > 60){
                showNum = hourNum/60;
            }
            //间隔showNum 个刻度画文字信息
            if (leftNum >= minNum) {
                if (leftNum % (showNum * scaleNum) == 0) {
                    canvas.drawLine(left, 0, left, canvas.getHeight() / 2, mScaleMarkPaint);
                    mScaleMarkPaint.getTextBounds(leftText, 0, leftText.length(), mTextRect);
                    canvas.drawText(leftText, left - mTextRect.centerX(), canvas.getHeight() * 2 / 3, mScaleMarkPaint);
                } else
                    canvas.drawLine(left, 0, left, canvas.getHeight() * 1 / 3, mScaleMarkPaint);
            }

            if (rightNum <= maxNum) {
                if (rightNum % (showNum * scaleNum) == 0) {
                    canvas.drawLine(right, 0, right, canvas.getHeight() / 2, mScaleMarkPaint);
                    mScaleMarkPaint.getTextBounds(rightText, 0, rightText.length(), mTextRect);
                    canvas.drawText(rightText, right - mTextRect.centerX(), canvas.getHeight() * 2 / 3, mScaleMarkPaint);
                } else
                    canvas.drawLine(right, 0, right, canvas.getHeight() * 1 / 3, mScaleMarkPaint);
            }
            count++;
            if (left < -Math.abs(distance) * 2)
                break;
        }
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawLine(mBorderRectF.left, mBorderRectF.bottom - 1, mBorderRectF.right, mBorderRectF.bottom - 1, mScaleMarkPaint);
    }


    private void drawMarkPoint(Canvas canvas) {
        int centerX = (int) mBorderRectF.centerX();
        canvas.drawLine(centerX, 0, centerX, canvas.getHeight() - 1, mCurrentMarkPaint);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mBorderRectF.set(mBorderPaint.getStrokeWidth(), mBorderPaint.getStrokeWidth(),
                width - mBorderPaint.getStrokeWidth(), height - mBorderPaint.getStrokeWidth());
        refreshCanvas();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}



    @Override
    public void onScroll(int distance) {
        this.distance += distance;
        mCenterNum -= (this.distance / dis);
        this.distance = this.distance % dis;

        if (mCenterNum <= 0 && this.distance > 0) {
            return;
        }
        if (mCenterNum >= 24 * hourNum && this.distance < 0) {
            return;
        }
        refreshCanvas();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onFinished() {
        //还原中心点在刻度位置上
        mCenterNum -= Math.round((float) distance / dis);
        distance = 0;
        refreshCanvas();
    }

    @Override
    public void onZoom(float mScale,double time) {
        int index = 0;
        index = getIndex(hourNum);
        if (index == -1) {
           return;
        }
        if (mScale > 1) {
            if (index + 1 <= multiple.length - 1) {
                hourNum = multiple[index + 1];
            }
        } else if (mScale < 1) {
            if (index - 1 >= 0) {
                hourNum = multiple[index - 1];
            }
        }
        maxNum = hourNum * 24;
        mCenterNum = (int)Math.round(time * hourNum);
        distance = 0;
        refreshCanvas();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScroller.onTouchEvent(event);
    }

    public interface NumberListener {
         void onChanged(String time);
    }


    public void setNumberListener(NumberListener listener) {
        this.numberListener = listener;
    }

    /**
     * 设置字体颜色
     * @param textSize
     * @return
     */
    public TimeLineView setTextSize(int textSize) {
        this.mTextHeight = sp2px(context,textSize);
        initPaints();
        return TimeLineView.this;
    }

    /**
     * 设置最大数字（/hourNum为最大小时数）
     * @param maxNum
     * @return
     */
    public TimeLineView setMaxNumber(int maxNum) {
        this.maxNum = maxNum;
        hourNum =  maxNum/24;
        return TimeLineView.this;
    }

    /**
     * 设置最小数字（/hourNum为最小小时数）
     * @param minNum
     * @return
     */
    public TimeLineView setMinNumber(int minNum) {
        this.minNum = minNum;
        return TimeLineView.this;
    }

    /**
     * 设置每一个刻度间相差数
     * @param scaleNum
     * @return
     */
    public TimeLineView setScaleNumber(int scaleNum) {
        this.scaleNum = scaleNum;
        return TimeLineView.this;
    }

    /**
     * 设置当前位置 （/hourNum为当前小时数）
     * @param centerNum
     * @return
     */
    public TimeLineView setCenterNum(int centerNum) {
        this.mCenterNum = centerNum;
        return TimeLineView.this;
    }

    /**
     * 设置背景颜色
     * @param color
     */
    public void setBackgroundColor(int color){
        this.bgColor = color;
    }

    /**
     * 设置刻度线颜色
     * @param markColor
     * @return
     */
    public TimeLineView setMarkColor(int markColor){
        this.markColor = markColor;
        initPaints();
        return TimeLineView.this;
    }

    /**
     * 设置刻度间距
     * @param dis   单位dp
     * @return
     */
    public TimeLineView setGap(int dis){
        this.dis = dip2px(dis);
        return TimeLineView.this;
    }

    /**
     * 设置最大分辨率
     * @param grade  级别  1-16 越大最大分辨率越大，反之越小
     */
    public TimeLineView setMultiple(int grade){

        if (grade < 1){
            grade = 1;
        }else if (grade >16){
            grade = 16;
        }
        multiple =  Arrays.copyOf(multiple,grade);
        return TimeLineView.this;
    }

    @Override
    public float getTime() {
        return (float) mCenterNum / hourNum;
    }

    //获取当前时间（毫秒）
    public long getCurrentTime() {
        return (long) ((double) mCenterNum / hourNum) * 60 * 24 * 1000 * 60;
    }

    @Override
    public void setTime(double time) {

        mCenterNum = (int)Math.round(time * hourNum);
        distance = 0;
        refreshCanvas();
    }

    private int[] multiple = new int[]{1, 2, 3, 4, 5, 6, 10, 15, 20, 30, 60,60*2,60*3,60*4,60*5,60*6};

    private int getIndex(int num) {
        for (int i = 0; i < multiple.length; i++) {
            if (multiple[i] == num) {
                return i;
            }
        }
        return -1;
    }

    private int dip2px(float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public String formatTime(int num){
        String snum = "";

        if (num < 10 ){
            snum = "0"+num;
        }else {
            snum = num+"";
        }
        return snum;
    }

    private String getFormatTime(){
        float time = getTime();
        String ssecond = "";
        int m  = (int)(( time * 60 )%60);
        int h  =(int) ( time * 60 )/60;
        int second = Math.round(( time * 3600 )%60);
        if (second == 60){
            m += 1;
        }else if (second != 0){
            ssecond =":"+second;
        }
        return formatTime(h)+":"+formatTime(m)+ssecond;
    }
}