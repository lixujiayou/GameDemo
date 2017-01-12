package com.example.administrator.gamedemo.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * 我的上传答题
 */
public class UploadAdapter extends Adapter<ViewHolder> {

    public FootViewHolder mFootViewHolder;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context context;
    private List<MomentsInfo> itemsEntities;
    private  int[] colorBook;
    public UploadAdapter(Context context, List<MomentsInfo> itemsEntities) {
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
            View view = LayoutInflater.from(context).inflate(R.layout.item_problem, parent,
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
            MomentsInfo momentsInfo = itemsEntities.get(position);

            if(momentsInfo.getAuthor().getUser_icon() != null){
                ImageLoadMnanger.INSTANCE.loadRoundImage(((ItemViewHolder) holder).rv_cover, momentsInfo.getAuthor().getUser_icon().getFileUrl());
            }

            ((ItemViewHolder) holder).tv_name.setText(momentsInfo.getAuthor().getNick_name());
            ((ItemViewHolder) holder).tv_topic.setText(momentsInfo.getTopic());
            try {
                ((ItemViewHolder) holder).cv.setCardBackgroundColor(colorBook[momentsInfo.getColor()]);
            }catch (Exception e){
                Logger.d(position+"设置背景色异常"+e);
            }
            try {
                ((ItemViewHolder) holder).tv_num.setText("答题" + momentsInfo.getAw_num() + "次");
            }catch (Exception e){}

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

        protected ImageView rv_cover;     //封面
        protected TextView tv_topic; //问题

        protected TextView tv_name;
        protected TextView tv_num;
        protected CardView cv;

        public ItemViewHolder(View view) {
            super(view);
            rv_cover = (ImageView) findView(rv_cover, R.id.rv_cover);
            tv_name = (TextView) findView(tv_name, R.id.tv_info);
            tv_topic = (TextView) findView(tv_topic, R.id.tv_title);
            tv_num = (TextView) findView(tv_num, R.id.tv_views);
            cv = (CardView) findView(cv,R.id.cv_item);
        }

        protected final View findView(View view, int resid) {
            if (resid > 0 && itemView != null && view == null) {
                return itemView.findViewById(resid);
            }
            return view;
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
    public void updateData(List<MomentsInfo> datas) {
        this.itemsEntities.clear();
        this.itemsEntities.addAll(datas);
        notifyDataSetChanged();
    }

    public void addMore(List<MomentsInfo> datas) {
        this.itemsEntities.addAll(datas);
        notifyDataSetChanged();
    }
}