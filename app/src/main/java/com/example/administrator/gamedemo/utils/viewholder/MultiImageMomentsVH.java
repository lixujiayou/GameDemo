package com.example.administrator.gamedemo.utils.viewholder;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.image.PhotoBrowseActivity;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.utils.base_image.PhotoBrowseInfo;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.imageview.ForceClickImageView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import razerdp.github.com.widget.PhotoContents;
import razerdp.github.com.widget.adapter.PhotoContentsBaseAdapter;

/**
 * Created by lixu on 2016/12/22.
 * <p>
 * 九宮格圖片的vh
 *
 */

public class MultiImageMomentsVH extends ShareViewHolder implements PhotoContents.OnItemClickListener {


    private PhotoContents imageContainer;
    private InnerContainerAdapter adapter;

    public MultiImageMomentsVH(Context context, ViewGroup viewGroup, int layoutResId) {
        super(context, viewGroup, layoutResId);
    }

    @Override
    public void onFindView(@NonNull View rootView) {
        imageContainer = (PhotoContents) findView(imageContainer, R.id.circle_image_container);
        if (imageContainer.getmOnItemClickListener() == null) {
            imageContainer.setmOnItemClickListener(this);
        }
    }

    @Override
    public void onBindDataToView(@NonNull Share data, int position, int viewType) {
        if (adapter == null) {
            adapter = new InnerContainerAdapter(getContext(), data.getPics());
            imageContainer.setAdapter(adapter);
        } else {
            adapter.updateData(data.getPics());
        }
    }

    @Override
    public void onItemClick(ImageView imageView, int i) {
        PhotoBrowseInfo info = PhotoBrowseInfo.create(adapter.datas, imageContainer.getContentViewsGlobalVisibleRects(), i);
        PhotoBrowseActivity.startToPhotoBrowseActivity((Activity) getContext(), info);
    }


    private static class InnerContainerAdapter extends PhotoContentsBaseAdapter {


        private Context context;
        private List<String> datas;

        InnerContainerAdapter(Context context, List<BmobFile> datas) {
            this.context = context;
            this.datas = new ArrayList<>();
            for(int i = 0;i<datas.size();i++){
                this.datas.add(datas.get(i).getFileUrl());
            }
        }

        @Override
        public ImageView onCreateView(ImageView convertView, ViewGroup parent, int position) {
            if (convertView == null) {
                convertView = new ForceClickImageView(context);
                convertView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            return convertView;
        }

        @Override
        public void onBindData(int position, @NonNull ImageView convertView) {
            ImageLoadMnanger.INSTANCE.loadImage(convertView, datas.get(position));
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        public void updateData(List<BmobFile> datas) {
            this.datas.clear();
            for(int i = 0 ;i< datas.size(); i++){
                this.datas.add(datas.get(i).getFileUrl());
            }
            notifyDataChanged();
        }

    }
}
