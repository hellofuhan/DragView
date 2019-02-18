package com.rotai.xz.draglibrary;

/**
 * 标记一个View可以被抓区 必须得是个View
 */
public interface DragViewIF {

    void onHoldUp(BuoyIF dView);
    void onPutIn(DragViewGroupIF dragVew2);
    void onCancel();


}
