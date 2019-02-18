package com.rotai.xz.draglibrary;

/**
 * 必须是个View  必须保留传一个Context的构造器
 */
public interface BuoyIF {
    void setDragViewIF(DragViewIF dragViewIF);
    void setDragViewGroup(DragViewGroup dragViewGroup);
    void refreshCanvas();
    void moveTo(float startX, float startY, float offsetX, float offsetY);
}
