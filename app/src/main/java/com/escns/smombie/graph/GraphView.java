package com.escns.smombie.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016-08-10.
 */

public class GraphView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private GraphDataVO mGraphDataVO;
    private GraphThread mGraphThread;

    public GraphView(Context context, GraphDataVO mGraphDataVO) {
        super(context);
        this.mGraphDataVO = mGraphDataVO;

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mGraphThread == null) {
            mGraphThread = new GraphThread();
            mGraphThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mGraphThread != null) {
            mGraphThread.setRunFlag(false);
            mGraphThread = null;
        }
    }

    class GraphThread extends Thread {
        private boolean isRun = true;
        private boolean isDirty = true;
        private int height = getHeight();
        private int width = getWidth();

        private int total;
        private Matrix matrix;
        private PointF startPoint;

        GraphCanvasWrapper graphCanvasWrapper = null;

        public GraphThread() {
            total = getTotalPoint();
            matrix = new Matrix();
            startPoint = new PointF(mGraphDataVO.getStartX() + width /2, mGraphDataVO.getStartY() + height/2);
        }

        public void setRunFlag(boolean flag) {
            isRun = flag;
        }

        @Override
        public void run() {
            super.run();

            Canvas canvas = null;
            while(isRun) {
                canvas = mSurfaceHolder.lockCanvas();
                if(canvas != null) {
                    canvas.drawColor(Color.WHITE);
                }

                graphCanvasWrapper = new GraphCanvasWrapper(canvas, android.R.attr.width, height, 0, 0);

                synchronized (mSurfaceHolder) {
                    try {
                        // 사각형 그리기
                        for(int i=0; i<mGraphDataVO.getList().size(); i++) {
                            float x1 = width /5 * (1+(2*i));
                            float y1 = height - (height * (float)mGraphDataVO.getList().get(i).getPoint()/total);
                            float x2 = x1 + width /5;
                            //float y2 = y1 + height * mGraphDataVO.getList().get(i).getPoint()/total;
                            float y2 = height;
                            Paint tempPaint = new Paint();
                            tempPaint.setColor(Color.BLUE);
                            graphCanvasWrapper.drawRect(x1,y1,x2,y2,tempPaint);
                            //graphCanvasWrapper.drawRect(i*100,i*100,i*200,i*200,new Paint(Color.RED));
                            Log.i("tag", "GraphThread - canvas.drawRect");
                            Log.i("tag", "Point : " + mGraphDataVO.getList().get(i).getPoint());
                            Log.i("tag", "x1 : " + x1);
                            Log.i("tag", "y1 : " + y1);
                            Log.i("tag", "x2 : " + x2);
                            Log.i("tag", "y2 : " + y2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();;
                    } finally {
                        if(graphCanvasWrapper.getCanvas() != null){
                            mSurfaceHolder.unlockCanvasAndPost(graphCanvasWrapper.getCanvas());
                        }
                    }
                }
            }
        }


        public int getTotalPoint() {
            int sum=0;
            for(GraphData gd : mGraphDataVO.getList()) {
                Log.i("tag", "getTotalPoint : "+ gd.getPoint());
                sum += gd.getPoint();
            }
            return sum;
        }
    }
}
