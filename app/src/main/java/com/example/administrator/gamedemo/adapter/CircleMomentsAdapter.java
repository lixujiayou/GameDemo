package com.example.administrator.gamedemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.utils.base.BaseRecyclerViewAdapter;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenter;
import com.example.administrator.gamedemo.utils.viewholder.ShareViewHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixu on 2016/12/27.
 * <p>
 * 朋友圈adapter
 */

public class CircleMomentsAdapter extends BaseRecyclerViewAdapter<Share> {


    private SparseArray<ViewHoldernfo> viewHolderKeyArray;
    private MomentPresenter momentPresenter;


    private CircleMomentsAdapter(@NonNull Context context,
                                 @NonNull List<Share> datas) {
        super(context, datas);
    }

    private CircleMomentsAdapter(Builder builder) {
        this(builder.context, builder.datas);
        this.viewHolderKeyArray = builder.viewHolderKeyArray;
        this.momentPresenter = builder.momentPresenter;
    }

    @Override
    protected int getViewType(int position, @NonNull Share data) {
        return data.getMomentType();
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return 0;
    }

    @Override
    protected ShareViewHolder getViewHolder(ViewGroup parent, View rootView, int viewType) {
        ViewHoldernfo viewHoldernfo = viewHolderKeyArray.get(viewType);
        if (viewHoldernfo != null) {
            ShareViewHolder circleBaseViewHolder = createCircleViewHolder(context, parent, viewHoldernfo);
            if (circleBaseViewHolder != null) {
                circleBaseViewHolder.setPresenter(momentPresenter);
            }
            return circleBaseViewHolder;
        }
        return null;
    }


    public static final class Builder<T> {
        private Context context;
        private SparseArray<ViewHoldernfo> viewHolderKeyArray = new SparseArray<>();
        private List<T> datas;
        private MomentPresenter momentPresenter;


        public Builder(Context context) {
            this.context = context;
            datas = new ArrayList<>();
        }

        public Builder<T> addType(Class<? extends ShareViewHolder> viewHolderClass,
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

        public Builder<T> setPresenter(MomentPresenter presenter) {
            this.momentPresenter = presenter;
            return this;
        }

        public CircleMomentsAdapter build() {
            return new CircleMomentsAdapter(this);
        }

    }


    /**
     * vh的信息类
     */
    private static final class ViewHoldernfo {
        public Class<? extends ShareViewHolder> holderClass;
        public int viewType;
        public int layoutResID;
    }

    private ShareViewHolder createCircleViewHolder(Context context, ViewGroup viewGroup, ViewHoldernfo viewHoldernfo) {
        if (viewHoldernfo == null) {
            throw new NullPointerException("木有这个viewholder信息哦");
        }
        Class<? extends ShareViewHolder> className = viewHoldernfo.holderClass;
        Constructor constructor = null;
        try {
            constructor = className.getConstructor(Context.class, ViewGroup.class, int.class);
            return (ShareViewHolder) constructor.newInstance(context, viewGroup, viewHoldernfo.layoutResID);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }


}
