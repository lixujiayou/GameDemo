package com.example.administrator.gamedemo.fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.base.BaseFragment;

import butterknife.BindView;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class MineFragment extends BaseFragment{
   /* @BindView(R.id.toolbar)
    public Toolbar toolbar;*/

    public MineFragment(){
    }

    public static MineFragment getInstance() {
        return answerFragmentHolder.instance;
    }

    public static class answerFragmentHolder {
        public static final MineFragment instance = new MineFragment();
    }

    @Override
    public void initTheme() {
        getActivity().setTheme(R.style.AppBaseTheme);
    }

    @Override
    public int initCreatView() {
        return R.layout.fragment_mine;
       // return R.layout.fragment_answer;
    }

    @Override
    public void initViews() {
       /* toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.white));
        toolbar.setTitle(R.string.main_mine);*/

    }

    @Override
    public void initData() {

    }



}
