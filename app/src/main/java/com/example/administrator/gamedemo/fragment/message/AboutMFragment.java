package com.example.administrator.gamedemo.fragment.message;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.adapter.AboutMAdapter;
import com.example.administrator.gamedemo.model.AboutMessage;
import com.example.administrator.gamedemo.utils.base.BaseFragment;

import java.util.List;

import butterknife.BindView;

/**
 * @auther lixu
 * Created by lixu on 2017/2/6 0006.
 * 关于我的消息
 */
public class AboutMFragment extends BaseFragment{

    @BindView(R.id.recycler)
    RecyclerView recyclerView;


    private List<AboutMessage> messageList;
    private AboutMAdapter aboutMAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void initTheme() {

    }

    @Override
    public int initCreatView() {
        return R.layout.fragment_message;
    }

    @Override
    public void initViews() {


        aboutMAdapter = new AboutMAdapter(mContext,messageList);
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(aboutMAdapter);

    }

    @Override
    public void initData() {

    }
}
