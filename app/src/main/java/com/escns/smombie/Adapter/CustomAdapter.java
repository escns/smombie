package com.escns.smombie.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.escns.smombie.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-08-16.
 */

/**
 * 메인 화면 하단의 ScrollView과 Item들을 연결해주는 Adapter. 추후 아이템 Layout에 따라 수정 필요
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<String> mDatas = new ArrayList<String>();

    public CustomAdapter() {
        for (int i = 0; i < 50; i++)
        {
            mDatas.add("Test" + " -> " + i);
            Log.i("tag", "CustomAdapter " + mDatas.get(i));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("tag", "onBindViewHolder" + mDatas.get(position));
        holder.tvNature.setText(mDatas.get(position));
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNature;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNature = (TextView) itemView.findViewById(R.id.id_info);
        }
    }
}