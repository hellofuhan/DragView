package com.rotai.xz.dragviewtest.demo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.rotai.xz.draglibrary.DragViewGroup;
import com.rotai.xz.dragviewtest.R;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private Handler handler;
    int count;
    private MyGroup groupA;
    private MyGroup groupB;
    private DragViewGroup dragViewGroup;
    private Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        dragViewGroup = findViewById(R.id.dragViewGroup);

        switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dragViewGroup.setBuoyIFClass(isChecked?MyBuoyIF.class:null);
            }
        });

        tv1 = findViewById(R.id.tv1);
        ((MyDataGroupIF) tv1).setGroupName("A");

        tv2 = findViewById(R.id.tv2);
        ((MyDataGroupIF) tv2).setGroupName("A");

        tv3 = findViewById(R.id.tv3);
        ((MyDataGroupIF) tv3).setGroupName("B");

        tv4 = findViewById(R.id.tv4);
        ((MyDataGroupIF) tv4).setGroupName("B");


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count++;
                tv1.setText(((MyDataGroupIF) tv1).getGroupName()+"组文本1 - "+count);
                tv2.setText(((MyDataGroupIF) tv2).getGroupName()+"组文本2 - "+count);
                tv4.setText(((MyDataGroupIF) tv4).getGroupName()+"组文本2 - "+count);
                tv3.setText(((MyDataGroupIF) tv3).getGroupName()+"组文本1 - "+count);
                handler.postDelayed(this, 1000);
            }
        }, 5000);

        groupA = findViewById(R.id.groupA);
        groupA.setGroupName("A");
        groupB = findViewById(R.id.groupB);
        groupB.setGroupName("B");



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
    }
}
