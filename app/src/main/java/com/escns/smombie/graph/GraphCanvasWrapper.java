package com.escns.smombie.graph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Administrator on 2016-08-11.
 */

public class GraphCanvasWrapper {

    private MatrixTranslator mMt;
    private Canvas mCanvas;

    public GraphCanvasWrapper(Canvas canvas, int width, int height, int paddingLeft, int paddingBottom) {
        mMt = new MatrixTranslator(width, height, paddingLeft, paddingBottom);
        mCanvas = canvas;
    }

    public Canvas getCanvas(){
        return mCanvas;
    }

    public void drawRect(float startX, float startY, float stopX, float stopY, Paint paint) {
        //mCanvas.drawRect(mMt.calcX(startX), mMt.calcY(startY), mMt.calcX(stopX), mMt.calcY(stopY), paint);
        mCanvas.drawRect(startX, startY, stopX, stopY, paint);
    }

}
