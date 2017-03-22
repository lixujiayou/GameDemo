package com.example.administrator.gamedemo.utils.viewholder;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.utils.base_image.RoundedImageView;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.imageview.ForceClickImageView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import razerdp.github.com.widget.PhotoContents;
import razerdp.github.com.widget.adapter.PhotoContentsBaseAdapter;

/**
 * Created by lixu on 2016/12/14.
 * <p>
 *
 *
 */

public class AnswerViewHolder extends TopicBaseViewHolder  implements BaseMomentVH<MomentsInfo>, ViewGroup.OnHierarchyChangeListener{

    private ImageView rv_cover;

    public AnswerViewHolder(Context context, ViewGroup viewGroup, int layoutResId) {
        super(context, viewGroup, layoutResId);
        this.mContext = context;
    }


    @Override
    public void onFindView(@NonNull View rootView) {
        rv_cover = (ImageView) findView(rv_cover, R.id.rv_cover);
    }

    @Override
    public void onBindDataToView(@NonNull MomentsInfo data, int position, int viewType) {
            Logger.i("onBindDataToView");
    }
}
