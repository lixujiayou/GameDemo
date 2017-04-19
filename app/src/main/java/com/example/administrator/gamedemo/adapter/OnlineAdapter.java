package com.example.administrator.gamedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.utils.TimeUtil;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * @author lixu
 * 正在答题adapter
 */
public class OnlineAdapter extends Adapter<ViewHolder> {

    private Context context;
    private List<CommentInfo> itemsEntities;

    public OnlineAdapter(Context context, List<CommentInfo> itemsEntities) {
        this.context = context;
        this.itemsEntities = itemsEntities;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onSendClick(View view , int position);
        void onReplyClick(View view , int position);
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public int getItemCount() {
        if(itemsEntities == null){
            return 0;
        }
        return itemsEntities.size() == 0 ? 0 : itemsEntities.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment_answer, parent, false);
            return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CommentInfo itemTemp = itemsEntities.get(position);
        if(itemTemp.getAuthor() != null){
            ((ItemViewHolder) holder).tv_send_person.setText(itemTemp.getAuthor().getNick_name());

        }

        if(itemTemp.getReply() != null){
            ((ItemViewHolder) holder).ll_gone.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).tv_reply_person.setText(itemTemp.getReply().getNick_name());
        }else{
                ((ItemViewHolder) holder).ll_gone.setVisibility(View.GONE);
        }

        if(itemTemp.getContent() != null){
            ((ItemViewHolder) holder).tv_content.setText(itemTemp.getContent());
        }

        ((ItemViewHolder) holder).tv_time.setText(TimeUtil.getTimeStringFromBmob(itemTemp.getCreatedAt()));

        ((ItemViewHolder) holder).tv_reply_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getLayoutPosition();
                onItemClickListener.onReplyClick(holder.itemView,position);
            }
        });


        ((ItemViewHolder) holder).tv_send_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getLayoutPosition();
                onItemClickListener.onSendClick(holder.itemView,position);
            }
        });


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

        TextView tv_send_person;//发送人
        TextView tv_reply_person;//被回复人
        TextView tv_content;//评论内容
        TextView tv_time;//评论内容
        LinearLayout ll_gone;//


        public ItemViewHolder(View view) {
            super(view);
            tv_send_person = (TextView) view.findViewById(R.id.tv_send_person);
            tv_reply_person = (TextView) view.findViewById(R.id.tv_reply_person);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            ll_gone = (LinearLayout) view.findViewById(R.id.ll_item_gone);
        }
    }

}