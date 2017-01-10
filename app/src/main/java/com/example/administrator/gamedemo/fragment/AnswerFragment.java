package com.example.administrator.gamedemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.answer.AnswerHistoryActivity;
import com.example.administrator.gamedemo.activity.answer.AnswerListActivity;
import com.example.administrator.gamedemo.activity.answer.SelectTypeActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class AnswerFragment extends BaseFragment {

    @BindView(R.id.banner_imageView)
    ImageView bannerImageView;

    @BindView(R.id.ll_online)
    LinearLayout llOnline;
    @BindView(R.id.ll_start)
    LinearLayout llStart;
    @BindView(R.id.ll_history)
    LinearLayout llHistory;
    @BindView(R.id.ll_myerro)
    LinearLayout llMyerro;
    @BindView(R.id.tv_time)
    TextView tv_time;
    public AnswerFragment() {
    }

    public static AnswerFragment getInstance() {
        return answerFragmentHolder.instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick({R.id.ll_start, R.id.ll_history, R.id.ll_myerro, R.id.ll_online})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_start:
                Intent sIntent = new Intent(getActivity(), SelectTypeActivity.class);
                sIntent.putExtra("yinliang", true);
                startActivity(sIntent);
                break;
            case R.id.ll_history:
                Intent hIntent = new Intent(mContext, AnswerHistoryActivity.class);
                startActivityForResult(hIntent, 1);
                break;
            case R.id.ll_myerro:
                break;
            case R.id.ll_online:
                Intent gIntent = new Intent(mContext, AnswerListActivity.class);
                startActivityForResult(gIntent, 1);
                break;
        }
    }


    public static class answerFragmentHolder {
        public static final AnswerFragment instance = new AnswerFragment();
    }

    @Override
    public void initTheme() {
        getActivity().setTheme(R.style.AppTheme_NoActionBar_Immerse);
    }

    @Override
    public int initCreatView() {
        return R.layout.fragment_answer;
    }

    @Override
    public void initViews() {
        tv_time.setText(Constants.StringData());
        Logger.d("当前时间为：" + Constants.StringData());
    }

    @Override
    public void initData() {
        tv_time.setText(Constants.StringData());
    }


}
