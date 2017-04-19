package com.example.administrator.gamedemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.utils.base.BaseRecyclerViewAdapter;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenterTogther;
import com.example.administrator.gamedemo.utils.viewholder.TogtherViewHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixu on 2016/12/27.
 * <p>
 *
 */

public class AnswerAdapterU extends BaseRecyclerViewAdapter<MomentsInfo> {


    private SparseArray<ViewHoldernfo> viewHolderKeyArray;
    private MomentPresenterTogther momentPresenter;


    private AnswerAdapterU(@NonNull Context context,
                           @NonNull List<MomentsInfo> datas) {
        super(context, datas);
    }

    private AnswerAdapterU(Builder builder) {
        this(builder.context, builder.datas);
        this.viewHolderKeyArray = builder.viewHolderKeyArray;
        this.momentPresenter = builder.momentPresenter;
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
    protected TogtherViewHolder getViewHolder(ViewGroup parent, View rootView, int viewType) {
        ViewHoldernfo viewHoldernfo = viewHolderKeyArray.get(viewType);
        if (viewHoldernfo != null) {
            TogtherViewHolder circleBaseViewHolder = createCircleViewHolder(context, parent, viewHoldernfo);
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
        private MomentPresenterTogther momentPresenter;

        public Builder(Context context) {
            this.context = context;
            datas = new ArrayList<>();
        }

        public Builder<T> addType(Class<? extends TogtherViewHolder> viewHolderClass,
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

        public Builder<T> setPresenter(MomentPresenterTogther presenter) {
            this.momentPresenter = presenter;
            return this;
        }

        public AnswerAdapterU build() {
            return new AnswerAdapterU(this);
        }
    }


    /**
     * vh的信息类
     */
    private static final class ViewHoldernfo {
        public Class<? extends TogtherViewHolder> holderClass;
        public int viewType;
        public int layoutResID;
    }

    private TogtherViewHolder createCircleViewHolder(Context context, ViewGroup viewGroup, ViewHoldernfo viewHoldernfo) {
        if (viewHoldernfo == null) {
            throw new NullPointerException("木有这个viewholder信息哦");
        }
        Class<? extends TogtherViewHolder> className = viewHoldernfo.holderClass;
        Constructor constructor = null;
        try {
            constructor = className.getConstructor(Context.class, ViewGroup.class, int.class);
            return (TogtherViewHolder) constructor.newInstance(context, viewGroup, viewHoldernfo.layoutResID);
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
