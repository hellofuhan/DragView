package com.rotai.xz.dragviewtest.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rotai.xz.draglibrary.DragViewGroupIF;
import com.rotai.xz.draglibrary.DragViewIF;
import com.rotai.xz.dragviewtest.R;

import java.util.HashSet;
import java.util.Iterator;


public class MyGroup extends LinearLayout implements DragViewGroupIF ,MyDataGroupIF{

    private TextView name;
    private TextView scan;
    private TextView hover;
    private TextView content;
    private String groupName;

    public MyGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MyGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        name = findViewById(R.id.name);
        scan = findViewById(R.id.scan);
        hover = findViewById(R.id.hover);
        content = findViewById(R.id.content);
    }

    int demand ;
    @Override
    public void onScan(HashSet<DragViewIF> dragViewIF) {
        demand = 0;
        scan.setText("当前抓起:" + dragViewIF.size() + "个 : \n[");
        Iterator<DragViewIF> iterator = dragViewIF.iterator();
        while (iterator.hasNext()) {
            MyDataGroupIF next = (MyDataGroupIF) iterator.next();
            scan.append("{组:" + next.getGroupName() + ",内容:" + ((MyTextView) next).getText() + "}");
            if (next.getGroupName().equals(getGroupName())) {
                demand ++;
            }
        }
        scan.append("]\n已匹配:"+demand+"个");

        refreshBorder();
    }

    int hoverNum;
    @Override
    public void onHover(HashSet<DragViewIF> hoverSet) {
        hoverNum = 0;
        if (hoverSet != null) {
            hover.setText("当前悬停:" + hoverSet.size() + "个 : \n[");
            Iterator<DragViewIF> iterator = hoverSet.iterator();
            while (iterator.hasNext()) {
                MyDataGroupIF next = (MyDataGroupIF) iterator.next();
                hover.append("{组:" + next.getGroupName() + ",内容:" + ((MyTextView) next).getText() + "}");
                if (next.getGroupName().equals(getGroupName())) {
                    hoverNum++;
                }
            }
            hover.append("]\n已匹配:"+hoverNum+"个");
        }
        refreshBorder();
    }

    void refreshBorder(){
        if (demand > 0){
            if (hoverNum > 0) {
                setBackgroundResource(R.drawable.shap_97bf5e);
            }else{
                setBackgroundResource(R.drawable.shap_97bedb);
            }
        }else{
            setBackground(null);
        }
    }

    @Override
    public void onPutIn(DragViewIF dragVew2) {
        if(((MyDataGroupIF) dragVew2).getGroupName().equals(getGroupName())){
            CharSequence text = ((MyTextView) dragVew2).getText();
            TextView textView = new TextView(getContext());
            textView.setText(text);
            addView(textView);
        }else{
            CharSequence text = ((MyTextView) dragVew2).getText();
            TextView textView = new TextView(getContext());
            textView.setText("非同组的内容 : "+text);
            addView(textView);
        }
    }


    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
        name.setText("当前组 : " + groupName);
    }

    @Override
    public String getGroupName() {
        return groupName;
    }
}
