package com.escns.smombie.graph;

import java.util.List;

/**
 * Created by Administrator on 2016-08-10.
 */

public class GraphDataVO {
    private List<GraphData> list;
    private int barColor;
    private int textColor;
    private int startX;
    private int startY;


    public GraphDataVO(List<GraphData> list, int barColor, int textColor, int startX, int startY) {
        this.list = list;
        this.barColor = barColor;
        this.textColor = textColor;
        this.startX = startX;
        this.startY = startY;
    }

    public List<GraphData> getList() {
        return list;
    }

    public void setList(List<GraphData> list) {
        this.list = list;
    }

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }
}
