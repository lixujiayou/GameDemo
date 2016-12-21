package com.example.administrator.gamedemo.utils.viewholder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.OnlineAnswerActivity;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.UIHelper;
import com.example.administrator.gamedemo.utils.base.BaseRecyclerViewHolder;
import com.example.administrator.gamedemo.utils.base_image.RoundedImageView;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.orhanobut.logger.Logger;

import java.util.List;


/**
 * Created by lixu on 2016/12/14.
 * <p>
 * 问题基本item
 */
public abstract class TopicBaseViewHolder extends BaseRecyclerViewHolder<MomentsInfo> implements BaseMomentVH<MomentsInfo>, ViewGroup.OnHierarchyChangeListener {



    protected RoundedImageView rv_cover;     //封面
    protected TextView tv_topic; //问题

    protected TextView tv_name;
    protected TextView tv_num;
    protected Context mContext;


    public TopicBaseViewHolder(Context context, ViewGroup viewGroup, int layoutResId) {
        super(context, viewGroup, layoutResId);
        onFindView(itemView);
        this.mContext = context;
        rv_cover = (RoundedImageView) findView(rv_cover, R.id.rv_cover);
        tv_name = (TextView) findView(tv_name, R.id.tv_info);
        tv_topic = (TextView) findView(tv_topic, R.id.tv_title);
        tv_num = (TextView) findView(tv_num, R.id.tv_views);
    }

    @Override
    public void onBindData(MomentsInfo data, int position) {
        if (data == null) {
            Logger.t("问题无数据");
            return;
        }
        //通用数据绑定
        onBindMutualDataToViews(data);
        //传递到子类
        onBindDataToView(data, position, getViewType());
    }

    private void onBindMutualDataToViews(final MomentsInfo data) {
        //header
        if(data.getAuthor().getUser_icon() != null){
            ImageLoadMnanger.INSTANCE.loadImageForRv(rv_cover, data.getAuthor().getUser_icon().getFileUrl());
        }

        tv_name.setText(data.getAuthor().getNick_name());
        tv_topic.setText(data.getTopic());

        try {
            tv_num.setText("答题" + data.getAw_num() + "次");
        }catch (Exception e){}

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("topic",data);
                Intent gIntent = new Intent(mContext, OnlineAnswerActivity.class);
                gIntent.putExtras(bundle);
                mContext.startActivity(gIntent);
            }
        });

        rv_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil3.showToast(mContext,data.getAuthor().getNick_name());
            }
        });
    }



    /**
     * 添加点赞
     *
     * @param likesList
     * @return ture=显示点赞，false=不显示点赞
     */
    private boolean addLikes(List<Students> likesList) {
        if (ToolUtil.isListEmpty(likesList)) {
            return false;
        }
        return true;
    }


    private int commentPaddintRight = UIHelper.dipToPx(8f);

    @Override
    public void onChildViewAdded(View parent, View child) {

    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
    }



    /**
     * ==================  click listener block
     */

    private View.OnClickListener onCommentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnLongClickListener onCommentLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    };

    /**
     * ============  tools method block
     */

    protected final View findView(View view, int resid) {
        if (resid > 0 && itemView != null && view == null) {
            return itemView.findViewById(resid);
        }
        return view;
    }

}
