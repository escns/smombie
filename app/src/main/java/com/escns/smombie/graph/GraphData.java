package com.escns.smombie.graph;

/**
 * Created by Administrator on 2016-08-10.
 */

public class GraphData {
    private String name;
    private int color;
    private int angle;
    private int point;

    public GraphData(String name, int color, int angle, int point) {
        this.name = name;
        this.color = color;
        this.angle = angle;
        this.point = point;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
