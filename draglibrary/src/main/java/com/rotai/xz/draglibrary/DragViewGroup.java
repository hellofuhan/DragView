package com.rotai.xz.draglibrary;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.rotai.xz.draglibrary.def.DView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class DragViewGroup extends FrameLayout {

    private static float LONG_TOUCH_LENGTH;
    private static Class< ? extends  BuoyIF> buoyIFClass = DView.class;
    private static final long LONG_TOUCH_TIME = 250;
    private static final String TAG = "DragViewGroup";
    private Handler handler = new Handler();
    private boolean disallowIntercept;

    public DragViewGroup(Context context) {
        this(context, null);
    }

    public DragViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DragViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LONG_TOUCH_LENGTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics());
    }

    public void setBuoyIFClass(Class< ? extends  BuoyIF> buoyIFClass ){
        if (buoyIFClass == null)
            buoyIFClass = DView.class;
        this.buoyIFClass = buoyIFClass;
    }

    HashMap<Integer, BuoyHolder> holderMap = new HashMap<>();


    class BuoyHolder {
        private Runnable runnable;
        private boolean longTouchStart;
        private long longTouchStartTime;
        private boolean draging;
        private float offsetX, offsetY;
        private DragViewIF dragVew;
        private BuoyIF dView;
        private float downX;
        private float downY;
        private float moveX;
        private float moveY;
        DragViewGroupIF lastMoveDragViewGroup;
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (disallowIntercept) {
            stopDragView(ev.getPointerId(ev.getActionIndex()), true);
            return super.dispatchTouchEvent(ev);
        }
        int action = ev.getActionMasked();
        Log.w(TAG, "dispatchTouchEvent: " + ev.getActionIndex());
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "dispatchTouchEvent: ACTION_DOWN  " + ev.getActionIndex());
                int pointerId = ev.getPointerId(ev.getActionIndex());
                final BuoyHolder buoyHolder = new BuoyHolder();
                synchronized (holderMap) {
                    holderMap.put(pointerId, buoyHolder);
                }
                buoyHolder.downX = ev.getX(ev.getActionIndex());
                buoyHolder.downY = ev.getY(ev.getActionIndex());
                buoyHolder.moveX = buoyHolder.downX;
                buoyHolder.moveY = buoyHolder.downY;

                buoyHolder.offsetX = 0;
                buoyHolder.offsetY = 0;
                buoyHolder.longTouchStart = true;
                buoyHolder.longTouchStartTime = System.currentTimeMillis();
                buoyHolder.draging = false;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        onMove(buoyHolder);
                    }
                };
                buoyHolder.runnable = runnable;
                handler.postDelayed(runnable, LONG_TOUCH_TIME);
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.w(TAG, "dispatchTouchEvent: ACTION_MOVE  " + ev.getActionIndex());
                ArrayList<BuoyHolder> list = new ArrayList<>();
                synchronized (holderMap) {
                    Iterator<Map.Entry<Integer, BuoyHolder>> iterator = holderMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, BuoyHolder> next = iterator.next();
                        if (next.getValue() != null) {
                            next.getValue().moveX = ev.getX(ev.findPointerIndex(next.getKey()));
                            next.getValue().moveY = ev.getY(ev.findPointerIndex(next.getKey()));
                            list.add(next.getValue());
                        }
                    }
                }
                for (BuoyHolder holder : list) {
                    if (onMove(holder)) {
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.w(TAG, "dispatchTouchEvent: ACTION_CANCEL  " + ev.getActionIndex());
                stopDragView(ev.getPointerId(ev.getActionIndex()),true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                Log.w(TAG, "dispatchTouchEvent: ACTION_UP  " + ev.getActionIndex());
                stopDragView(ev.getPointerId(ev.getActionIndex()), false);
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean onMove(BuoyHolder buoyHolder) {
        if (buoyHolder!=null) {
            if (!buoyHolder.draging) {
                if (Math.abs(buoyHolder.moveX - buoyHolder.downX) > LONG_TOUCH_LENGTH || Math.abs(buoyHolder.moveY - buoyHolder.downY) > LONG_TOUCH_LENGTH) {
                    buoyHolder.longTouchStart = false;
                    Log.w(TAG, "onMove: longTouchStart = false");
                }
                if (buoyHolder.longTouchStart) {
                    if (System.currentTimeMillis() - buoyHolder.longTouchStartTime > LONG_TOUCH_TIME) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        startDragView(buoyHolder);
                        return true;
                    }
                }
            } else {
                buoyHolder.offsetX = buoyHolder.moveX - buoyHolder.downX;
                buoyHolder.offsetY = buoyHolder.moveY - buoyHolder.downY;
                moveTo(buoyHolder);
            }
        }
        return false;
    }


    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        this.disallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private void stopDragView(int pointerId, boolean isCancel) {
        BuoyHolder buoyHolder;
        synchronized (holderMap) {
            buoyHolder = holderMap.remove(pointerId);
        }
        if (buoyHolder!=null) {
            handler.removeCallbacks(buoyHolder.runnable);
            buoyHolder.longTouchStart = false;
            if (buoyHolder.dragVew != null) {
                Log.w(TAG, "stopDragView: ");
                removeView(((View) buoyHolder.dView));
                DragViewGroupIF dragVew2 = findDragVewGroup(this, buoyHolder.downX + buoyHolder.offsetX, buoyHolder.downY + buoyHolder.offsetY);
                if ((!isCancel) && dragVew2 != null) {
                    buoyHolder.dragVew.onPutIn(dragVew2);
                    dragVew2.onPutIn(buoyHolder.dragVew);
                } else {
                    buoyHolder.dragVew.onCancel();
                }
                scanList.remove(buoyHolder.dragVew);
                ArrayList<DragViewGroupIF> allDragVewGroup = findAllDragVewGroup(this);
                for (DragViewGroupIF dragViewGroupIF : allDragVewGroup) {
                    dragViewGroupIF.onScan(scanList);
                }
            }

            Iterator<Map.Entry<DragViewGroupIF, HashSet<DragViewIF>>> iterator = hoverMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<DragViewGroupIF, HashSet<DragViewIF>> next = iterator.next();
                HashSet<DragViewIF> value = next.getValue();
                if (value!=null){
                    Iterator<DragViewIF> iterator1 = value.iterator();
                    while (iterator1.hasNext()){
                        DragViewIF next1 = iterator1.next();
                        if (next1 == buoyHolder.dragVew){
                            iterator1.remove();
                            next.getKey().onHover(value);
                            break;
                        }
                    }
                }
            }
            buoyHolder.dragVew = null;
            buoyHolder.dView = null;

        }
    }

    HashSet<DragViewIF> scanList = new HashSet<>();
    private void startDragView(BuoyHolder buoyHolder) {
        Log.w(TAG, "startDragView: ");
        buoyHolder.draging = true;
        buoyHolder.dragVew = findDragVew(this, buoyHolder.downX, buoyHolder.downY);
        if (buoyHolder.dragVew != null) {

//            buoyHolder.dView = new DView(getContext());
            try {
                Constructor<? extends BuoyIF> constructor = buoyIFClass.getConstructor(Context.class);
                BuoyIF buoyIF = constructor.newInstance(getContext());
                buoyHolder.dView = buoyIF;
            } catch (Exception e) {
                e.printStackTrace();
                throw  new RuntimeException("创建BuoyIF失败");
            }

            buoyHolder.dView.setDragViewIF(buoyHolder.dragVew);
            buoyHolder.dView.setDragViewGroup(this);

            buoyHolder.dragVew.onHoldUp(buoyHolder.dView);
            ((View) buoyHolder.dView).setLayoutParams(new FrameLayout.LayoutParams(((View) buoyHolder.dragVew).getWidth(), ((View) buoyHolder.dragVew).getHeight()));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                dView.setTranslationZ(80);
//            }

            addView(((View) buoyHolder.dView));
            moveTo(buoyHolder);

            scanList.add(buoyHolder.dragVew);

            ArrayList<DragViewGroupIF> allDragVewGroup = findAllDragVewGroup(this);
            for (DragViewGroupIF dragViewGroupIF : allDragVewGroup) {
                dragViewGroupIF.onScan(scanList);
            }
        }

    }


    HashMap<DragViewGroupIF,HashSet<DragViewIF>> hoverMap = new HashMap<>();
    private void moveTo(BuoyHolder buoyHolder) {
        if (buoyHolder.dView != null) {
            buoyHolder.dView.moveTo(buoyHolder.downX, buoyHolder.downY, buoyHolder.offsetX, buoyHolder.offsetY);

            DragViewGroupIF dragVewGroup = findDragVewGroup(this, buoyHolder.downX + buoyHolder.offsetX, buoyHolder.downY + buoyHolder.offsetY);
            if (buoyHolder.lastMoveDragViewGroup != null && buoyHolder.lastMoveDragViewGroup != dragVewGroup) {
                HashSet<DragViewIF> dragViewIFS = hoverMap.get(buoyHolder.lastMoveDragViewGroup);
                if(dragViewIFS==null){
                    dragViewIFS = new HashSet<>();
                    hoverMap.put(buoyHolder.lastMoveDragViewGroup,dragViewIFS);
                }
                dragViewIFS.remove(buoyHolder.dragVew);
                buoyHolder.lastMoveDragViewGroup.onHover(dragViewIFS);
            }
            if (dragVewGroup != null) {
                HashSet<DragViewIF> dragViewIFS = hoverMap.get(dragVewGroup);
                if(dragViewIFS==null){
                    dragViewIFS = new HashSet<>();
                    hoverMap.put(dragVewGroup,dragViewIFS);
                }
                dragViewIFS.add(buoyHolder.dragVew);
                dragVewGroup.onHover(dragViewIFS);
            }
            buoyHolder.lastMoveDragViewGroup = dragVewGroup;
        }
    }

    private DragViewIF findDragVew(ViewGroup viewGroup, float x, float y) {
        DragViewIF dragVew = null;
        int childCount = viewGroup.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childAt = viewGroup.getChildAt(i);
            int left = childAt.getLeft();
            int right = childAt.getRight();
            int top = childAt.getTop();
            int bottom = childAt.getBottom();
            if (left <= x && right >= x && top <= y && bottom >= y) {
                if (childAt instanceof ViewGroup) {
                    dragVew = findDragVew((ViewGroup) childAt, x-left, y-top);
                }
                if (dragVew == null) {
                    if (childAt instanceof DragViewIF) {
//                        if (((DragViewIF) childAt).canDrag()) {
                        dragVew = (DragViewIF) childAt;
                        break;
//                        }
                    }
                }else{
                    return dragVew;
                }

            }
        }
        return dragVew;
    }

    private DragViewGroupIF findDragVewGroup(ViewGroup viewGroup, float x, float y) {
        DragViewGroupIF dragVew = null;
        int childCount = viewGroup.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childAt = viewGroup.getChildAt(i);
            int left = childAt.getLeft();
            int right = childAt.getRight();
            int top = childAt.getTop();
            int bottom = childAt.getBottom();
            if (left <= x && right >= x && top <= y && bottom >= y) {
                if (childAt instanceof ViewGroup) {
                    dragVew = findDragVewGroup((ViewGroup) childAt, x-left, y-top);
                }
                if (dragVew == null) {
                    if (childAt instanceof DragViewGroupIF) {
//                        if (((DragViewIF) childAt).canDrag()) {
                        dragVew = (DragViewGroupIF) childAt;
                        break;
//                        }
                    }
                }else{
                    return dragVew;
                }

            }
        }
        return dragVew;
    }

    private ArrayList<DragViewGroupIF> findAllDragVewGroup(ViewGroup viewGroup) {
        ArrayList<DragViewGroupIF> dragVews = new ArrayList<>();
        int childCount = viewGroup.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childAt = viewGroup.getChildAt(i);

            if (childAt instanceof ViewGroup) {
                dragVews.addAll(findAllDragVewGroup((ViewGroup) childAt));
            }
            if (childAt instanceof DragViewGroupIF) {
                dragVews.add((DragViewGroupIF) childAt);
            }

        }
        return dragVews;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean longTouchStart = false;
        synchronized (holderMap){
            Iterator<BuoyHolder> iterator = holderMap.values().iterator();
            while (iterator.hasNext()){
                if(iterator.next().longTouchStart){
                    longTouchStart = true;
                    break;
                }
            }
        }

        return longTouchStart;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(null);
    }
}
