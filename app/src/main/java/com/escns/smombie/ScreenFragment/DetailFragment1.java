package com.escns.smombie.ScreenFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.escns.smombie.Adapter.ItemMainAdpater;
import com.escns.smombie.Item.ItemMain;
import com.escns.smombie.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-08-25.
 */

public class DetailFragment1 extends Fragment {

    private static DetailFragment1 mDetailFragment1;

    private StickyScrollView scrollView;
    private TextView descriptionTv;
    private AppDetailFragment.StickyScrollCallBack scrollListener;

    RecyclerView mRecyclerView;

    public static DetailFragment1 getInstance() {
        if(mDetailFragment1==null) {
            mDetailFragment1=new DetailFragment1();
        }
        return mDetailFragment1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main4, null);
        initView(view);
        return view;
    }

    /**
     * ³õÊ¼»¯View
     */
    private void initView(View view) {
        scrollView = (StickyScrollView) view.findViewById(R.id.scrollview);
        View placeHolder = view.findViewById(R.id.placeHolder);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

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

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
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

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) placeHolder.getLayoutParams();
        lp.height = AppDetailFragment.STICKY_HEIGHT2;
        scrollView.setScrollCallBack(scrollListener);


    }


    public void setScrollCallBack(AppDetailFragment.StickyScrollCallBack scrollListener) {
        this.scrollListener = scrollListener;
    }

    public int getStickyHeight() {
        int scrollHeight = scrollView.getScrollY();
        if (scrollHeight > AppDetailFragment.STICKY_HEIGHT1) {
            return AppDetailFragment.STICKY_HEIGHT1;
        }
        return scrollHeight;
    }

    public void invalidScroll() {
        scrollView.invalidScroll();
    }

    public void setStickyH(int stickyH) {
        if (Math.abs(stickyH - getStickyHeight()) < 10) {
            return;
        }

        scrollView.scrollTo(0, stickyH);
    }

}
