package com.rotai.xz.dragviewtest.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.rotai.xz.draglibrary.BuoyIF;
import com.rotai.xz.draglibrary.DragViewGroupIF;
import com.rotai.xz.draglibrary.DragViewIF;


public class MyTextView extends android.support.v7.widget.AppCompatTextView implements DragViewIF,MyDataGroupIF {
    private static final String TAG = "MyTextView";
    private BuoyIF dView;
    private String groupName;

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dView!=null){
            dView.refreshCanvas();
        }
    }

    @Override
    public void onHoldUp(BuoyIF dView) {
        this.dView = dView;
        Log.e(TAG, "onHoldUp: " +getText());
    }

    @Override
    public void onPutIn(DragViewGroupIF dragVew2) {
        Log.e(TAG, "onPutIn: "+getText()+" to "+ ((MyGroup) dragVew2));
    }

    @Override
    public void onCancel() {
        Log.e(TAG, "onCancel: "+getText() );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }


    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
        setText(groupName+"组文本 ");
    }

    @Override
    public String getGroupName() {
        return groupName;
    }
}
