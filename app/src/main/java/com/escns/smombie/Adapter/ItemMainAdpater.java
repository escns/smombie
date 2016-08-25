package com.escns.smombie.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.escns.smombie.Item.ItemMain;
import com.escns.smombie.R;

import java.util.List;

/**
 * Created by Administrator on 2016-08-16.
 */

public class ItemMainAdpater extends RecyclerView.Adapter<ItemMainAdpater.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    public List<ItemMain> mItems;

    public ItemMainAdpater(List<ItemMain> Items) {
        mItems = Items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_VIEW_TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            ViewHolder viewHolder = new ViewHolder(v, viewType);
            return viewHolder;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
            ViewHolder viewHolder = new ViewHolder(v, viewType);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mItems.get(position).isHeader()) {
            holder.mItemMainText.setText(mItems.get(position).getmTitle());
            holder.mItemMainIcon.setImageResource(mItems.get(position).getmIcon());
        } else {
            //holder.mItemDetailImage.setImageResource(mItems.get(position).getmImage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader() ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mItemMainText;
        public ImageView mItemMainIcon;
        public ImageView mItemDetailImage;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType==ITEM_VIEW_TYPE_HEADER) {
                //mItemMainText = (TextView) itemView.findViewById(R.id.item_main_text);
                //mItemMainIcon = (ImageView) itemView.findViewById(R.id.item_main_icon);
            } else {
                mItemDetailImage = (ImageView) itemView.findViewById(R.id.item_detail_image);
            }
        }
    }
}
