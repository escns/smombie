package com.escns.smombie.ScreenFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.escns.smombie.Adapter.ItemMainAdapter;
import com.escns.smombie.Item.ItemMain;
import com.escns.smombie.R;
import com.escns.smombie.Utils.RandomAd;
import com.escns.smombie.View.CustomImageView;

import org.lucasr.twowayview.TwoWayView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hyo99 on 2016-08-23.
 */

/**
 * 홈 화면
 */
public class MainFragment extends Fragment {

    public final static int UPDATE_PROFILE_DATA = 1;
    public final static int DEFAULT_GOAL = 1000;

    private Context mContext;

    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체

    private boolean isProfileDataLoaded;
    private Bitmap mFbProfileImage;

    private boolean statAppFirst = true;

    View rootView;

    ScrollView mRecyclerView;
    FrameLayout mHeader;

    /**
     * Profile Image 다운 성공 시 호출하여 profile update를 수행한다.
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==UPDATE_PROFILE_DATA) {
                // 사이드 메뉴 header
                ((CustomImageView)rootView.findViewById(R.id.profile_view)).setImageBitmap(mFbProfileImage);
                ((TextView)rootView.findViewById(R.id.user_email)).setText(pref.getString("EMAIL", "사용자 이메일"));
                isProfileDataLoaded=true;
            }
        }
    };

    /**
     * 최초로 실행되는 생명주기. 뷰를 그려야 하기 때문에 inflater 객체 설정이 필요하다.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        pref = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        return rootView;
    }

    /**
     * View가 생성되고 난 다음에 수행되어야 할 것들이다.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (ScrollView) view.findViewById(R.id.recyclerview);
        mHeader = (FrameLayout) view.findViewById(R.id.header);
    }

    /**
     * Activity가 생성되고 난 다음에 수행되어야 할 것들이다.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int actionBarHeight = 200;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        Log.i("tag", "height : " + actionBarHeight);


        StikkyHeaderBuilder.stickTo(mRecyclerView)
                .setHeader(mHeader)
                .minHeightHeaderPixel(actionBarHeight+35)
                .build();

        init();
    }

    /**
     * 화면이 꺼졌다가 살아났을 때 유지되어야 하는 View들을 그려준다.
     */
    @Override
    public void onResume() {

        ((TextView) rootView.findViewById(R.id.section1_text)).setText(""+pref.getInt("POINT", 0));
        ((TextView) rootView.findViewById(R.id.section2_text)).setText(""+pref.getInt("GOAL", 0));
        ((TextView) rootView.findViewById(R.id.section3_text)).setText(""+pref.getInt("REWORD", 0));

        if (!statAppFirst) {
            try {
                Thread.sleep(100);

                Message message = handler.obtainMessage();
                message.what = UPDATE_PROFILE_DATA;
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            statAppFirst = false;
        }

        super.onResume();
    }

    /**
     * 최초의 View 설정들을 해준다.
     */
    public void init() {

        mContext = getContext();

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isProfileDataLoaded) {
                    try{
                        Log.i("tag","requset profile image of FACEBOOK");
                        URL url = new URL("https://graph.facebook.com/" + pref.getString("USER_ID_TEXT", "1111") + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();              //  아래 코드는 웹에서 이미지를 가져온 뒤
                        conn.setDoInput(true);
                        conn.connect();
                        mFbProfileImage = BitmapFactory.decodeStream(conn.getInputStream());               //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                        Message message = handler.obtainMessage();
                        message.what = UPDATE_PROFILE_DATA;
                        handler.sendMessage(message);

                        Thread.sleep(100);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        TwoWayView twoWayView1 = (TwoWayView) rootView.findViewById(R.id.item_main_detail1);
        twoWayView1.setAdapter(new ItemMainAdapter(mContext, 0, getItemMains(3)));

        TwoWayView twoWayView2 = (TwoWayView) rootView.findViewById(R.id.item_main_detail2);
        twoWayView2.setAdapter(new ItemMainAdapter(mContext, 0, getItemMains(4)));
    }

    /**
     * 사용하고자 하는 광고의 수 cnt를 넣어주면 그 개수만큼 랜덤하게 List에 담아 Url을 리턴 해준다.
     * @param cnt
     * @return
     */
    public List<ItemMain> getItemMains(int cnt) {
        RandomAd randomAd = new RandomAd();
        List<String> adUrlList1 = randomAd.getRandomAdUrl(cnt);

        final List<ItemMain> ItemMains = new ArrayList<>();
        for(int i=0; i<cnt; i++) {
            ItemMains.add(new ItemMain(adUrlList1.get(i)));
        }
        return ItemMains;
    }

    /**
     * ThreadService와 MainActivity를 연결 시켜줄 ServiceConnection
     *
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /**
         * 리턴되는 Binder를 다시 Service로 꺼내서 ThreadSerivce내부의 함수 사용이 가능하다.
         * @param className
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("tag", "onServiceConnected");
            //WalkCheckService.LocalBinder binder = (WalkCheckService.LocalBinder) service;
            //mService = binder.getService();
            //mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("tag", "onServiceDisconnected");
            //mBound = false;
        }
    };
}
