package com.escns.smombie.ScreenFragment;

import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016-08-25.
 */

@SuppressLint({ "NewApi", "HandlerLeak" })
public class StickyScrollView extends ScrollView {
    private int lastTouchAction;
    private AppDetailFragment.StickyScrollCallBack scrollCallBack;

    private DetectThread detectThread;
    private boolean needDetect = false;
    private int lastDetectScrollY = DETECT_SCROLL_INVALID;
    private static final int DETECT_SCROLL_INIT = -1;
    private static final int DETECT_SCROLL_INVALID = -2;

    public StickyScrollView(Context context) {
        this(context, null);
    }

    public StickyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOverScrollMode(OVER_SCROLL_ALWAYS);

        needDetect = true;
        detectThread = new DetectThread();
        detectThread.start();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);


        if (null != scrollCallBack) {
            int stickyTranslate = t;
            if (t > AppDetailFragment.STICKY_HEIGHT1) {
                stickyTranslate = AppDetailFragment.STICKY_HEIGHT1;
            }
            scrollCallBack.onScrollChanged(-stickyTranslate);
        }
    }

    public void setScrollCallBack(AppDetailFragment.StickyScrollCallBack scrollCallBack) {
        this.scrollCallBack = scrollCallBack;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        lastTouchAction = ev.getAction() & MotionEvent.ACTION_MASK;

        if (lastTouchAction == MotionEvent.ACTION_DOWN) {
            lastDetectScrollY = DETECT_SCROLL_INIT;
            animScrollY();
        }

        return super.dispatchTouchEvent(ev);
    }

    private void animScrollY() {
        int thisScrollY = getScrollY();
        if (thisScrollY == 0 || thisScrollY >= AppDetailFragment.STICKY_HEIGHT1) {
            return;
        }

        int scrollY = 0;
        if (thisScrollY > AppDetailFragment.STICKY_HEIGHT1 / 2) {
            scrollY = AppDetailFragment.STICKY_HEIGHT1;
        }

        ValueAnimator scrollAnim = ObjectAnimator.ofInt(this, "scrollY",
                scrollY);
        scrollAnim.setDuration(200);
        scrollAnim.setEvaluator(new IntEvaluator());
        scrollAnim.start();
    }

    class DetectThread extends Thread {
        private Message msg;

        @Override
        public void run() {
            while (needDetect) {
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                msg = handler.obtainMessage();
                msg.sendToTarget();
            }
        };
    };

    /*
    @Override
    protected void onDetachedFromWindow() {
        needDetect = false;
        detectThread = null;
    };
    */

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int thisScrollY = getScrollY();
            if (lastDetectScrollY != DETECT_SCROLL_INVALID
                    && lastTouchAction == MotionEvent.ACTION_UP
                    && lastDetectScrollY == thisScrollY) {
                animScrollY();
                lastDetectScrollY = DETECT_SCROLL_INVALID;
            } else if (lastDetectScrollY != DETECT_SCROLL_INVALID) {
                lastDetectScrollY = thisScrollY;
            }
        };
    };

    public void invalidScroll() {
        lastDetectScrollY = DETECT_SCROLL_INVALID;
    }
}