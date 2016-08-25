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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.escns.smombie.Adapter.ItemMainAdpater;
import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.Item.ItemMain;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;
import com.escns.smombie.View.CustomImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hyo99 on 2016-08-23.
 */

public class MainFragment extends Fragment {

    public final static int UPDATE_PROFILE_DATA = 1;
    public final static int DEFAULT_GOAL = 1000;

    private Context mContext;

    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체
    private DBManager mDbManager;            // DB 선언

    private Retrofit mRetrofit;
    private ApiService mApiService;

    private boolean isProfileDataLoaded;
    private Bitmap mFbProfileImage;

    private boolean statAppFirst = true;

    View rootView;

    RecyclerView mRecyclerView;
    FrameLayout mHeader;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main2, container, false);

        pref = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mHeader = (FrameLayout) view.findViewById(R.id.header);
    }

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

    public void init() {

        mContext = getContext();

        mDbManager = new DBManager(mContext);

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isProfileDataLoaded) {
                    try{
                        Log.d("tag","몇번 오나 보자!!!!!");
                        URL url = new URL("https://graph.facebook.com/" + pref.getString("USER_ID_TEXT", "1111") + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();              //  아래 코드는 웹에서 이미지를 가져온 뒤
                        conn.setDoInput(true);
                        conn.connect();
                        mFbProfileImage = BitmapFactory.decodeStream(conn.getInputStream());               //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                        Thread.sleep(100);

                        Message message = handler.obtainMessage();
                        message.what = UPDATE_PROFILE_DATA;
                        handler.sendMessage(message);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        final List<ItemMain> ItemMains = new ArrayList<>();
        ItemMains.add(new ItemMain(true, "이벤트", R.drawable.title_icon_event));
        ItemMains.add(new ItemMain(false, R.drawable.img_event1));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event1));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event1));

        ItemMains.add(new ItemMain(true, "제휴 서비스", R.drawable.title_icon_service));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event1));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event1));

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position==2) return 1;
                if(ItemMains.get(position).isHeader()) return gridLayoutManager.getSpanCount();
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(new ItemMainAdpater(ItemMains));
    }

    // ThreadService와 MainActivity를 연결 시켜줄 ServiceConnection
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        // 리턴되는 Binder를 다시 Service로 꺼내서 ThreadSerivce내부의 함수 사용이 가능하다.
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //WalkCheckService.LocalBinder binder = (WalkCheckService.LocalBinder) service;
            //mService = binder.getService();
            //mBound = true;
            Log.d("tag", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //mBound = false;
            Log.d("tag", "onServiceDisconnected");
        }
    };
}
