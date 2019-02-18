package com.rotai.xz.draglibrary;

import java.util.HashSet;

public interface DragViewGroupIF {


    //当有'起点组件'被拽起时或被释放时调用   释放时传null
    void onScan(HashSet<DragViewIF> dragViewIF);

    //当有'起点组件'经过当前组件时调用   离开时传null
    void onHover(HashSet<DragViewIF> hoverSet);

    //当有'起点组件'被投入当前组件时
    void onPutIn(DragViewIF dragVew2);

}
