package com.escns.smombie.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.escns.smombie.R;
import com.escns.smombie.View.LockViewPager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 2016-08-04.
 */

public class LockScreenReceiver extends BroadcastReceiver {

    private Context mContext;

    private WindowManager.LayoutParams mParams;     // 최상단에 그려질 뷰의 파라미터
    private WindowManager mWindowManager;           // 최상단에 뷰를 그릴 WindowManager
    private View mLockScreenView;                   // 최상단 뷰
    private LockViewPager mLockViewPager;            // Viewpager

    int windowWidth;                                // 전체 윈도우 너비
    int windowHeight;                               // 전체 윈도우 높이

    private static boolean isLock;                  // lock의 상태
    private static boolean isWalking;

    /**
     * BroadcastReceiver에 메시지가 왔을 때
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Tag", "LockReceiver - onReceive "+intent.getAction());

        mContext = context;

        String action = intent.getAction();

        // 어떤 메시지인지 확인
        if(action.equals(intent.ACTION_SCREEN_OFF)) {
            if(isWalking) {

                drawLockScreen(context);

            }
        } else if(action.equals("com.escns.smombie.CALL_STATE_RINGING")) {
            if(mWindowManager!=null && isLock) {
                mWindowManager.removeView(mLockScreenView);
                mWindowManager = null;
                isLock=false;
            }
        } else if(action.equals("com.escns.smombie.CALL_STATE_OFFHOOK")) {

        } else if(action.equals("com.escns.smombie.CALL_STATE_IDLE")) {

        } else if(action.equals("com.escns.smombie.LOCK_SCREEN_ON")) {

            if(isLock) Log.i("tag", "isLock = true");
            else Log.i("tag", "isLock = false");
            if(isWalking) Log.i("tag", "isWalking = true");
            else Log.i("tag", "isWalking = false");

            if(!isLock) {
                isWalking=true;
                drawLockScreen(context);
            }
        } else if(action.equals("com.escns.smombie.LOCK_SCREEN_OFF")) {
            isWalking=false;
            if(mWindowManager!=null) {
                mWindowManager.removeView(mLockScreenView);
                mWindowManager = null;
                isLock=false;
            }
        }
    }

    private void drawLockScreen(Context context) {

        // lock이 활성화 된 상태라면 지우고 다시
        if(mWindowManager!=null && isLock) {
            mWindowManager.removeView(mLockScreenView);
            mWindowManager = null;
        }

        isLock = true;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);                      // WindowManager 객체 생성
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   // Layout을 가져오기 위해 LayoutInflater 객체 생성
        mLockScreenView = (View) inflater.inflate(R.layout.lockscreen, null);                                   // LayoutInflater를 이용하여 R.layout.lockscreen을 불러온다

        mLockViewPager = (LockViewPager) mLockScreenView.findViewById(R.id.lockscreen_viewpager);
        mLockViewPager.setAdapter(new CustomPagerAdapter(mContext));

        mParams = new WindowManager.LayoutParams(                                       // View의 파라미터 결정
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,                           // 최상단 뷰로 설정
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN                     // 풀 스크린으로 설정
        );
        mParams.screenOrientation=12;                                                   // 반드시 세로

        windowHeight = mWindowManager.getDefaultDisplay().getHeight();
        windowWidth = mWindowManager.getDefaultDisplay().getWidth();

        init();

        mWindowManager.addView(mLockScreenView, mParams);
    }

    public void init() {

        final ImageView button_lock = (ImageView) mLockScreenView.findViewById(R.id.button_lock);
        final ImageView button_unlock = (ImageView) mLockScreenView.findViewById(R.id.button_unlock);
        final int offsetLeft = ((RelativeLayout.LayoutParams)button_lock.getLayoutParams()).leftMargin;
        //final int offsetRight = ((RelativeLayout.LayoutParams)button_unlock.getLayoutParams()).leftMargin;
        //final int offsetLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
        final int offsetRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());

        button_lock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) button_lock.getLayoutParams();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        mLockViewPager.setTouchFlag(false);
                        //layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources().getDisplayMetrics());
                        button_lock.setImageResource(R.drawable.btn_key_tap);
                        button_unlock.setImageResource(R.drawable.btn_unlock);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int x_cord = (int)event.getRawX();
                        int btnWidth = button_lock.getWidth();

                        if(x_cord>windowWidth - btnWidth- offsetRight) {
                            mWindowManager.removeView(mLockScreenView);
                            isLock=false;
                            break;
                        }
                        if(x_cord - btnWidth - offsetLeft < 0) {x_cord=offsetLeft+btnWidth;}

                        // 왼쪽 margin 값을 터치 위치로 변경함으로써 마치 움직이는 것처럼 보인다
                        layoutParams.leftMargin = x_cord - btnWidth - offsetLeft;

                        // 변경된 파라미터를 적용
                        button_lock.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        mLockViewPager.setTouchFlag(true);
                        if(isLock) {
                            layoutParams.leftMargin = offsetLeft;
                            button_lock.setLayoutParams(layoutParams);
                            button_lock.setImageResource(R.drawable.btn_key);
                            button_unlock.setImageResource(R.drawable.btn_lock);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public enum CustomPagerEnum {

        RED(0, R.drawable.test_image1),
        BLUE(1, R.drawable.test_image2),
        ORANGE(2, R.drawable.profile);

        private int mTitleResId;
        private int mLayoutResId;

        CustomPagerEnum(int titleResId, int layoutResId) {
            mTitleResId = titleResId;
            mLayoutResId = layoutResId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }

    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context mContext) {
            this.mContext = mContext;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];

            ViewGroup layout = (ViewGroup) mLayoutInflater.inflate(R.layout.lockscreen_view, collection, false);
            ImageView background = (ImageView) layout.findViewById(R.id.lockscreen_background);


            final ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.lockscreen_loading);
            progressBar.setVisibility(View.VISIBLE);

            Picasso.with(mContext)
                    .load(customPagerEnum.getLayoutResId())
                    .into(background, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onError() {
                            Log.i("tag", "onERROR");
                        }
                    });

            /*
            Picasso.Builder builder = new Picasso.Builder(mContext);
            builder.listener(new Picasso.Listener()
            {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                {
                    exception.printStackTrace();
                }
            });
            builder.build().load(R.drawable.bg_round_gray_age).fit().into(background);
            */

            collection.addView(layout);
            return layout;
        }
        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return CustomPagerEnum.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }
}
