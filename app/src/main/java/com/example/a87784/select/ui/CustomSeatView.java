package com.example.a87784.select.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.a87784.select.R;


/**
 * Created by 87784 on 2017/10/15.
 */

public class CustomSeatView extends View {


    private static final String TAG = "CustomSeatView";

    private Paint paint;
    private Canvas canvas;



    //座位有人图片
    private Bitmap availableSeatBitmap;
    //座位无人图片
    private Bitmap unavailableSeatBitmap;
    //座位被选中图片
    private Bitmap selectedSeatBitmap;
    //要显示的图片
    private Bitmap drawBitmap;
    //矩形原图
    private Rect srcRect;
    //矩形绘制的图
    private Rect dstRect;


    //座位图宽度
    private int seatBitmapWidth;
    //座位图高度
    private int seatBitmapHeight;
    //座位距离边缘
    private static final int marginEdge = 50;
    //座位之间的距离
    private static final int marginSeat = 50;
    //绘制的座位宽度
    private static final int seatWidth = 60;
    //绘制的座位高度
    private static final int seatHeight = 50;
    //点击误差范围
    private static final int faultRate = 10;


    //座位无人
    private static final int AVAILABLE_SEAT_TYPE = 0;
    //座位有人
    private static final int UNAVAILABLE_SEAT_TYPE = 1;
    //座位正被选中
    private static final int SELECTED_SEAT_TYPE = 2;

    //被点击的座位行
    private int clickRaw;
    //被点击的座位列
    private int clickCol;
    //座位行
    private int seatRaw;
    //座位列
    private int seatCol;
    //座位类型表
    private int[][] seatTypeLists;
    //最多选择个数
    private static final int maxSelectSeatNum = 1;
    //已选择的座位个数
    private int selectSeatNum;

    //点击座位后回调接口
    private ClickSeatCallBack clickSeatCallBack;
/*
    //顶部提示栏与边界的间距
    private static final int headTipsMarginTop = 50;
    //提示之间的间距
    private static final int headTipsMargin = 100;
    //提示字与图的间距
    private static final int headTipsImgMarginText = 25;
    //
    private Rect headTipDstRect;
    //顶部提示
    private String headTip;
    private int headTipWidth;
    private int headTipHeight;*/







    public CustomSeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        
        int left,top,right,bottom;
        left = top = marginEdge;
        right = marginEdge + seatWidth;
        bottom = marginEdge + seatHeight;

        for(int i=0;i<20;i++){
            if (getHeight() - bottom <= marginEdge){
                break;
            }
            for(int j=0;j<20;j++){
                if(getWidth() - right <= marginEdge){
                    continue;
                }
                dstRect = new Rect(left,top,right,bottom);
                canvas.drawBitmap(getBitmap(seatTypeLists[i][j]),srcRect,dstRect,paint);
                left += seatWidth + marginSeat;
                right += seatWidth + marginSeat;
            }
            left = marginEdge;
            right = marginSeat + seatWidth;
            top += seatHeight + marginSeat;
            bottom += seatHeight + marginSeat;
        }
    }


    /**
     * 初始化数据
     */
    public void init(){

        //座位bitmap图
        availableSeatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seat_noselected);
        unavailableSeatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seat_selected);
        selectedSeatBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seat_selcting);

        //座位bitmap图宽高
        seatBitmapWidth = availableSeatBitmap.getWidth();
        seatBitmapHeight = availableSeatBitmap.getHeight();

        //要画的图
        srcRect = new Rect(0,0,seatBitmapWidth,seatBitmapHeight);

        //
        seatTypeLists = new int[20][20];
    }

    /**
     * 根据座位类型获得对应座位图
     * @param seatType
     * @return
     */
    public Bitmap getBitmap(int seatType){
        if(seatType == AVAILABLE_SEAT_TYPE){
            return availableSeatBitmap;
        }else if(seatType == UNAVAILABLE_SEAT_TYPE){
            return unavailableSeatBitmap;
        }else {
            return selectedSeatBitmap;
        }
    }



    /**
     * 判断是否点击到座位
     * @param x
     * @param y
     * @return
     */
    public boolean isClickSeat(int x,int y){
        //左上边缘还没判断
        if((x % (seatWidth + marginSeat) <= seatWidth + faultRate)||(y % (seatHeight + marginSeat) <= seatHeight + faultRate)){
            return false;
        }
        return true;
    }




    /**
     * 根据点击的坐标，计算选择了哪个座位
     * @param x
     * @param y
     */
    public void countSeat(int x,int y){
        int colNum = x / (seatWidth + marginSeat);
        int rawNum= y / (seatHeight + marginSeat);
        /*if(x % (seatWidth + marginSeat) >= seatWidth + marginSeat - faultRate){
            rawNum++;
        }
        if(y % (seatHeight + marginSeat) >= seatHeight + marginSeat - faultRate){
            colNum++;
        }*/
        if(x % (seatWidth + marginSeat) >= seatWidth + marginSeat - faultRate){
            rawNum++;
        }
        if(y % (seatHeight + marginSeat) >= seatHeight + marginSeat - faultRate){
            colNum++;
        }
        clickRaw = rawNum;
        clickCol = colNum;

    }


    /**
     * 获得座位类型
     * @param clickRaw
     * @param clickCol
     * @return
     */
    public int getSeatType(int clickRaw,int clickCol){
        return seatTypeLists[clickRaw][clickCol];
    }



    /**
     * 判断座位无人
     * @param clickRaw
     * @param clickCol
     * @return
     */
    public boolean isAvailableSeat(int clickRaw,int clickCol){
        if(getSeatType(clickRaw,clickCol) == AVAILABLE_SEAT_TYPE){
            return true;
        }
        return false;
    }


    /**
     * 判断座位有人
     * @param clickRaw
     * @param clickCol
     * @return
     */
    public boolean isUnAvailableSeat(int clickRaw,int clickCol){
        if(getSeatType(clickRaw,clickCol) == UNAVAILABLE_SEAT_TYPE){
            return true;
        }
        return false;
    }

    /**
     * 判断座位被选中
     * @param clickRaw
     * @param clickCol
     * @return
     */
    public boolean isSelectedSeat(int clickRaw,int clickCol){
        if(getSeatType(clickRaw,clickCol) == SELECTED_SEAT_TYPE){
            return true;
        }
        return false;
    }



    public void changeSeatType(int clickRaw,int clickCol,int replaceSeatType){
        seatTypeLists[clickRaw][clickCol] = replaceSeatType;
    }



    /**
     * 点击事件分发
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //如果点中坐标
                if(isClickSeat(x,y)){
                    countSeat(x,y);
                    //如果点中无人的座位
                    if(isAvailableSeat(clickRaw,clickCol)){
                        selectSeatNum++;
                        if(selectSeatNum<=maxSelectSeatNum){
                            changeSeatType(clickRaw,clickCol,SELECTED_SEAT_TYPE);
                            clickSeatCallBack.onClickSeat(clickRaw,clickCol,showSelectSeat(true));
                            invalidate();
                        }else {
                            Toast.makeText(getContext(),"最多只能选择" + maxSelectSeatNum + "个座位",Toast.LENGTH_SHORT).show();
                        }
                    }else if(isUnAvailableSeat(clickRaw,clickCol)){
                        Toast.makeText(getContext(),"抱歉，此座已有人",Toast.LENGTH_SHORT).show();
                    }else if(isSelectedSeat(clickRaw,clickCol)){
                        selectSeatNum--;
                        changeSeatType(clickRaw,clickCol,AVAILABLE_SEAT_TYPE);
                        clickSeatCallBack.onClickSeat(clickRaw,clickCol,showSelectSeat(false));
                        invalidate();
                    }
                }
                Log.d(TAG, "onTouchEvent: -------------------");
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        return true;
    }


    /**
     * 提示选择的座位
     * @return
     */
    public String showSelectSeat(boolean isShow){
        if(isShow){
            return "您选择的是" + (clickRaw + 1) + "行" + (clickCol + 1) + "列";
        }
        return "您还未选择座位 (๑>\u0602<๑）";
    }


    /**
     *
     */
    public interface ClickSeatCallBack{
        void onClickSeat(int raw, int col, String s);
    }



    public void setOnClickSeatCallBack(ClickSeatCallBack clickSeatCallBack){
        this.clickSeatCallBack = clickSeatCallBack;
    }




}
