package com.escns.smombie.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016-08-07.
 */

/**
 * ViewPager 위의 오브젝트들의 클릭을 원할하게 하기 위해서 ViewPager의 특정함수들을 오버라이드할 필요가 있어 새로 만들었습니다.
 */
public class LockViewPager extends ViewPager {

    // ViewPager가 아닌 것이 터치 된 상태에서 false
    private boolean touchFlag = true;

    public LockViewPager(Context context) {
        super(context);
    }

    public LockViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // touchFlag의 상태에 따라 ViewPager의 터치를 가로챌 수 있게 했습니다. false를 리턴하게 되면 ViewPager의 터치가 불능이 됩니다.
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.touchFlag && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.touchFlag && super.onTouchEvent(ev);
    }

    public void setTouchFlag(boolean touchFlag) {
        this.touchFlag = touchFlag;
    }

}
