package com.example.administrator.gamedemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.LoadingView;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/12/15 0015.
 */
public class LoginActivity extends BaseActivity {



    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.et_pwd)
    EditText et_pwd;

    @BindView(R.id.bt_login)
    Button bt_login;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("登录");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);

        bt_login.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.bt_login:
                showProgressBarDialog(this);
                BmobUser bu2 = new BmobUser();
                bu2.setUsername(et_name.getText().toString().trim());
                bu2.setPassword(et_pwd.getText().toString().trim());
                bu2.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        pDialog.dismiss();
                        if(e==null){
                            setResult(Constants.LOGIN_OK);
                            finish();
                        }else{
                            ToastUtil3.showToast(LoginActivity.this,"登录失败，请检查网络并重试"+e);
                        }
                    }
                });
                break;
        }
    }
    private SweetAlertDialog pDialog;
    private void showProgressBarDialog(final Activity mContext){
        try {
            if(mContext.hasWindowFocus()){
                pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText("正在提交数据，请稍等");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }catch (Exception e){
            Logger.d("ProgressBarDialog的上下文找不到啦！");
        }
    }



}
