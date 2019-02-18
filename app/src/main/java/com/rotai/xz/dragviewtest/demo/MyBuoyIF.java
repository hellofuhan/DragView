package com.rotai.xz.dragviewtest.demo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rotai.xz.draglibrary.BuoyIF;
import com.rotai.xz.draglibrary.DragViewGroup;
import com.rotai.xz.draglibrary.DragViewIF;
import com.rotai.xz.dragviewtest.R;

public class MyBuoyIF extends FrameLayout implements BuoyIF {

    private static final String TAG = "MyBuoyIF";
    private final int dp140;
    private DragViewIF dragVew;
    private float offsetX;
    private float offsetY;
    private float startX;
    private float startY;
    private float moveX;
    private float moveY;
    private TextView groupTV;
    private TextView contentTV;
    private DragViewGroup dragViewGroup;

    public MyBuoyIF(Context context) {
        this(context, null);
    }

    public MyBuoyIF(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyBuoyIF(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View inflate = View.inflate(context, R.layout.demo_buoy, null);
        addView(inflate);
        groupTV = inflate.findViewById(R.id.group);
        contentTV = inflate.findViewById(R.id.content);
//        LayoutInflater factory = LayoutInflater.from(context);
//        View inflate = factory.inflate(R.layout.demo_buoy, this, false);
        dp140 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, context.getResources().getDisplayMetrics());
//        LayoutParams layoutParams = new LayoutParams(dp140,dp140);
//        inflate.setLayoutParams(layoutParams);
//        addView(inflate);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


    }

    @Override
    public void setDragViewIF(DragViewIF dragViewIF) {
        this.dragVew = dragViewIF;
    }

    @Override
    public void setDragViewGroup(DragViewGroup dragViewGroup) {
        this.dragViewGroup = dragViewGroup;
    }

    @Override
    public void refreshCanvas() {
        if (groupTV != null)
            groupTV.setText(((MyTextView) dragVew).getGroupName() + "组");
        if (contentTV != null)
            contentTV.setText(((MyTextView) dragVew).getText());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            int width = getWidth();
            int height =getHeight();
//        int width = dp140;//getWidth();
//        int height =dp140;//getHeight();
            Log.e(TAG, "onLayout: "+width+"x"+height );
            int moveX = (int) this.moveX;
            int moveY = (int) this.moveY;
            layout(moveX - (width / 2), moveY - height, moveX + (width - width / 2), moveY);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.UNSPECIFIED);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void moveTo(float startX, float startY, float offsetX, float offsetY) {
        this.startX = startX;
        this.startY = startY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        moveX = startX + offsetX;
        moveY = startY + offsetY;



        requestLayout();
    }
}
