package com.example.administrator.gamedemo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.adapter.BaseFragmentAdapter;
import com.example.administrator.gamedemo.fragment.AnswerFragment;
import com.example.administrator.gamedemo.fragment.MineFragment;
import com.example.administrator.gamedemo.fragment.ShareFragment;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseFragmentActivity {

    @BindView(R.id.ll_main_answer)
    LinearLayout ll_answer;
    @BindView(R.id.ll_main_share)
    LinearLayout ll_share;
    @BindView(R.id.ll_main_mine)
    LinearLayout ll_mine;
    @BindView(R.id.main_bottome)
    LinearLayout mainBottome;
    @BindView(R.id.tv_answer)
    TextView tv_answer;
    @BindView(R.id.tv_share)
    TextView tv_share;
    @BindView(R.id.tv_mine)
    TextView tv_mine;


    private List<Fragment> list_fragmet = new ArrayList<>();

    private FragmentManager fragmentManager;


    private AnswerFragment answerFragment;
    private ShareFragment shareFragment;
    private MineFragment mineFragment;
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initViews() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();


        hideFragments(transaction);

        if(answerFragment == null) {
            answerFragment = new AnswerFragment();
            transaction.add(R.id.framelayout, AnswerFragment.getInstance());
        }else{
            transaction.show(answerFragment);
        }
        transaction.commit();

      /*  vp.setOffscreenPageLimit(2);
        vp.setAdapter(new BaseFragmentAdapter(list_fragmet, myFM));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changePage(position);
                // invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    @Override
    public void initData() {

    }


    private void OnTabSelected(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (index) {
            case 0:
                hideFragments(transaction);
                if (answerFragment == null) {
                    answerFragment = AnswerFragment.getInstance();
                    transaction.add(R.id.framelayout, answerFragment);
                } else {
                    transaction.show(answerFragment);
                }
                break;
            case 1:
                hideFragments(transaction);
                if (shareFragment == null) {
                    shareFragment = new ShareFragment();
                    transaction.add(R.id.framelayout, shareFragment);
                } else {
                    transaction.show(shareFragment);
                }
                break;
            case 2:
                hideFragments(transaction);
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.framelayout, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;
                default:
                    break;
        }
        transaction.commit();
    }


    private void hideFragments(FragmentTransaction transaction) {
        if(answerFragment != null){
            transaction.hide(answerFragment);
        }
        if(shareFragment != null){
            transaction.hide(shareFragment);
        }
        if(mineFragment != null){
            transaction.hide(mineFragment);
        }
    }


  /*  @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        // 动态设置ToolBar状态
        switch (vp.getCurrentItem()) {
            case 0:
                setSupportActionBar(AnswerFragment.getInstance().toolbar);
                menu.findItem(R.id.action_share).setVisible(false);
                menu.findItem(R.id.action_message).setVisible(false);
                break;
            case 1:
                setSupportActionBar(ShareFragment.getInstance().toolbar);
                menu.findItem(R.id.action_share).setVisible(true);
                menu.findItem(R.id.action_message).setVisible(false);
                break;
            case 2:
             //   setSupportActionBar(MineFragment.getInstance().toolbar);
                menu.findItem(R.id.action_share).setVisible(false);
                menu.findItem(R.id.action_message).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }*/


    private void changePage(int postion) {
        switch (postion) {
            case 0:
                tv_answer.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                tv_share.setTextColor(ContextCompat.getColor(this,R.color.textcolor_3));
                tv_mine.setTextColor(ContextCompat.getColor(this,R.color.textcolor_3));
                break;
            case 1:
                tv_answer.setTextColor(ContextCompat.getColor(this,R.color.textcolor_3));
                tv_share.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                tv_mine.setTextColor(ContextCompat.getColor(this,R.color.textcolor_3));
                break;
            case 2:
                tv_answer.setTextColor(ContextCompat.getColor(this,R.color.textcolor_3));
                tv_share.setTextColor(ContextCompat.getColor(this,R.color.textcolor_3));
                tv_mine.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                break;
        }
    }


    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil3.showToast(MainActivity.this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick({R.id.ll_main_answer, R.id.ll_main_mine, R.id.main_bottome})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_main_answer:
                changePage(0);
                OnTabSelected(0);
                break;
            case R.id.ll_main_mine:
                changePage(2);
                OnTabSelected(2);
                break;
            case R.id.main_bottome:
                changePage(1);
                OnTabSelected(1);
                break;
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/
}
