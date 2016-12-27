package com.example.administrator.gamedemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.utils.base.BaseRecyclerViewAdapter;
import com.example.administrator.gamedemo.utils.base.BaseRecyclerViewHolder;
import com.example.administrator.gamedemo.utils.viewholder.TopicBaseViewHolder;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 大灯泡 on 2016/11/1.
 * <p>
 * 问题adapter
 */

public class AnswersAdapter extends BaseRecyclerViewAdapter<MomentsInfo> {
    private SparseArray<ViewHoldernfo> viewHolderKeyArray;

    private AnswersAdapter(@NonNull Context context,
                           @NonNull List<MomentsInfo> datas) {
        super(context, datas);
    }

    private AnswersAdapter(Builder builder) {
        this(builder.context, builder.datas);
        this.viewHolderKeyArray = builder.viewHolderKeyArray;
    }

    @Override
    protected int getViewType(int position, @NonNull MomentsInfo data) {
        return data.getMomentType();
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return 0;
    }

    @Override
    protected BaseRecyclerViewHolder getViewHolder(ViewGroup parent, View rootView, int viewType) {
        ViewHoldernfo viewHoldernfo = viewHolderKeyArray.get(viewType);
        if (viewHoldernfo != null) {
            return createCircleViewHolder(context, parent, viewHoldernfo);
        }
        return null;
    }

    public static final class Builder<T> {
        private Context context;
        private SparseArray<ViewHoldernfo> viewHolderKeyArray = new SparseArray<>();
        private List<T> datas;

        public Builder(Context context) {
            this.context = context;
            datas = new ArrayList<>();
        }

        public Builder<T> addType(Class<? extends TopicBaseViewHolder> viewHolderClass,
                                  int viewType,
                                  int layoutResId) {
            final ViewHoldernfo info = new ViewHoldernfo();
            info.holderClass = viewHolderClass;
            info.viewType = viewType;
            info.layoutResID = layoutResId;
            viewHolderKeyArray.put(viewType, info);
            return this;
        }

        public Builder<T> setData(List<T> datas) {
            this.datas = datas;
            return this;
        }

        public AnswersAdapter build() {
            return new AnswersAdapter(this);
        }
    }

    /**
     * vh的信息类
     */
    private static final class ViewHoldernfo {
        public Class<? extends TopicBaseViewHolder> holderClass;
        public int viewType;
        public int layoutResID;
    }

    private TopicBaseViewHolder createCircleViewHolder(Context context, ViewGroup viewGroup, ViewHoldernfo viewHoldernfo) {
        if (viewHoldernfo == null) {
            throw new NullPointerException("木有这个viewholder信息哦");
        }
        Class<? extends TopicBaseViewHolder> className = viewHoldernfo.holderClass;
        Logger.i("class  >>>  " + className);
        Constructor constructor = null;
        try {
            constructor = className.getConstructor(Context.class, ViewGroup.class, int.class);
            return (TopicBaseViewHolder) constructor.newInstance(context, viewGroup, viewHoldernfo.layoutResID);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return null;
    }


}
