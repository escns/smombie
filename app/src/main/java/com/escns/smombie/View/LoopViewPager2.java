package com.escns.smombie.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jaekwon.loopviewpagerlayout.lib.LoopViewPagerLayout;

/**
 * Created by Administrator on 2016-08-26.
 * @author hajaekwon
 * 제가 git에서 관리 중인 무한 Loop View Pager 오픈 소스를 상속받아 다시 Custom 한 LoopViewPager 입니다.
 * View가 3개 일 때  3 , 1 , 2 로 스크롤 이동이 가능합니다.
 */

public class LoopViewPager2 extends LoopViewPagerLayout {

    // ViewPager가 아닌 것이 터치 된 상태에서 false
    private boolean touchFlag = true;

    public LoopViewPager2(Context context) {
        super(context);
    }

    public LoopViewPager2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopViewPager2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
