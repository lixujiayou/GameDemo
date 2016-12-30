package com.example.administrator.gamedemo.utils.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Togther;


/**
 * Created by lixu on 2016/12/27.
 *
 * 空内容的vh
 *
 */

public class EmptyMomentsVHTogther extends TogtherViewHolder {

    public EmptyMomentsVHTogther(Context context, ViewGroup viewGroup, int layoutResId) {
        super(context, viewGroup, layoutResId);
    }

    @Override
    public void onFindView(@NonNull View rootView) {

    }

    @Override
    public void onBindDataToView(@NonNull Togther data, int position, int viewType) {

    }


}
