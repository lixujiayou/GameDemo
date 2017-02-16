package com.example.administrator.gamedemo.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.model.Message_;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * 一行Text Adapter
 */
public class TextAdapter extends Adapter<ViewHolder> {

    public FootViewHolder mFootViewHolder;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context context;
    private List<Message_> itemsEntities;
    private  int[] colorBook;
    public TextAdapter(Context context, List<Message_> itemsEntities) {
        this.context = context;
        this.itemsEntities = itemsEntities;
        colorBook = context.getResources().getIntArray(R.array.style_topic);
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
        return itemsEntities.size() == 0 ? 0 : itemsEntities.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment_answer, parent,
                    false);
            return new ItemViewHolder(view);
        }else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.widget_lv_footer, parent,
                    false);
            mFootViewHolder = new FootViewHolder(view);
            return mFootViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Message_ momentsInfo = itemsEntities.get(position);



            ((ItemViewHolder) holder).tv_content.setText(momentsInfo.getmContent());
            ((ItemViewHolder) holder).tv_time.setText(momentsInfo.getUpdatedAt());
            ((ItemViewHolder) holder).tv_reply_person.setVisibility(View.GONE);
            ((ItemViewHolder) holder).tv_send_person.setVisibility(View.GONE);
            ((ItemViewHolder) holder).ll_gone.setVisibility(View.GONE);



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
        }else if(holder instanceof FootViewHolder){
            mFootViewHolder = (FootViewHolder) holder;
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

    static class FootViewHolder extends ViewHolder {

        TextView tv_state;

        public FootViewHolder(View view) {
            super(view);
            tv_state = (TextView) view.findViewById(R.id.tv_more);
        }
    }

    /**
     * 设置底部加载状态
     * isLoading:true 正在加载中 false 到底部了
     */
    public void setLoadStatus(boolean isLoading){
        if(mFootViewHolder != null ) {
            if (isLoading) {
                mFootViewHolder.tv_state.setText(R.string.load_ing);
            } else {
                mFootViewHolder.tv_state.setText(null);
            }
        }else{
            Logger.d("mFootViewHolder == null ");
        }
    }
    public void updateData(List<Message_> datas) {
        this.itemsEntities.clear();
        this.itemsEntities.addAll(datas);
        notifyDataSetChanged();
    }

    public void addMore(List<Message_> datas) {
        this.itemsEntities.addAll(datas);
        notifyDataSetChanged();
    }
}