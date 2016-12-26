package com.example.administrator.gamedemo.activity.mine.togther;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lixu on 2016/12/22 0022.
 * 一起
 */
public class TogetherActivity extends BaseActivity {


    @BindView(R.id.rv_togther)
    RecyclerView rvTogther;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_togther);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("一起");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_jiahao:

                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void initData() {

    }



    @OnClick(R.id.toolbar)
    public void onClick() {
        rvTogther.smoothScrollToPosition(0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }
}
