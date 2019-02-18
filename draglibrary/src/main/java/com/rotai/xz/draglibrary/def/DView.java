package com.rotai.xz.draglibrary.def;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.rotai.xz.draglibrary.BuoyIF;
import com.rotai.xz.draglibrary.DragViewGroup;
import com.rotai.xz.draglibrary.DragViewIF;


public class DView extends View implements BuoyIF {

    private static final String TAG = "DView";
    private DragViewIF dragVew;
    private float offsetX;
    private float offsetY;
    private DragViewGroup dragViewGroup;
    private float startX;
    private float startY;
    private float moveX;
    private float moveY;

    public DView(Context context) {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        ((View) dragVew).onDraw(canvas);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        ((View) dragVew).draw(canvas);
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
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG, "onLayout: "+changed+","+left+","+ top+","+ right+","+ bottom);
    }

    @Override
    public void layout(int l, int t, int r, int b) {


        int width = ((View) dragVew).getWidth();
        int height = ((View) dragVew).getHeight();
        int moveX = (int) this.moveX;
        int moveY = (int) this.moveY;
        super.layout(moveX - (width / 2), moveY - height, moveX + (width - width / 2), moveY);

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

//        layout((int) (((View) dragVew).getLeft() + offsetX)
//                , (int) (((View) dragVew).getTop() + offsetY)
//                , (int) (((View) dragVew).getRight() + offsetX)
//                , (int) (((View) dragVew).getBottom() + offsetY));
    }
}
