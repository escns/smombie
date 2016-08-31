package com.escns.smombie.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.escns.smombie.Item.ItemMain;
import com.escns.smombie.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2016-08-25.
 * MainFragment에서 사용하는 horizontal list에 들어갈 광고 list의 Adapter
 */

public class ItemMainAdapter extends ArrayAdapter<ItemMain> {

    private LayoutInflater mLayoutInflater;

    /**
     * 생성자에서 LayoutInflater 객체를 생성해 둔다. 파라미터로 List 형태의 오브젝트 변수가 필수
     * @param context   LayoutInflater를 위한 context
     * @param resource  사용하지 않음
     * @param objects   List를 채울 아이템들
     */
    public ItemMainAdapter(Context context, int resource, List<ItemMain> objects) {
        super(context, resource, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 현재 위치에서 그려야 할 정보를 List에서 가져와 그린 View를 리턴
     * @param position      몇번째 list인지 위치
     * @param convertView   그려질 View 객체
     * @param parent        상위 View 객체
     * @return              완성된 View를 리턴
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemMain item = (ItemMain) getItem(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_detail, null);
        }

        ImageView mItemDetailImage = (ImageView) convertView.findViewById(R.id.item_detail_image);


        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.ad_loading);
        progressBar.setVisibility(View.VISIBLE);

        Picasso.with(getContext())
                .load(item.getmUrl())
                .into(mItemDetailImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {
                    }
                });

        return convertView;
    }
}
