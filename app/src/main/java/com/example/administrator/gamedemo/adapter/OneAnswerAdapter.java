package com.example.administrator.gamedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.administrator.gamedemo.R;
import com.orhanobut.logger.Logger;

import java.util.List;

public class OneAnswerAdapter extends Adapter<ViewHolder> {

    private Context context;
    private List<String> itemsEntities;
    private int[] randomNum;

    public OneAnswerAdapter(Context context, List<String> itemsEntities,int[] Random) {
        this.context = context;
        this.itemsEntities = itemsEntities;
        this.randomNum = Random;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public int getItemCount() {
        return itemsEntities.size() == 0 ? 0 : itemsEntities.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_topic_answers, parent, false);
            return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String selectText = null;
           switch (position){
               case 0:
                   selectText = "A";
                   break;
               case 1:
                   selectText = "B";
                   break;
               case 2:
                   selectText = "C";
                   break;
               case 3:
                   selectText = "D";
                   break;
           }
            ((ItemViewHolder) holder).tv.setText(selectText+"、"+itemsEntities.get(randomNum[position]));

            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });


                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        return true;
                    }
                });

        }
    }

    static class ItemViewHolder extends ViewHolder {

        TextView tv;

        public ItemViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.item_tv_name);

        }
    }

}