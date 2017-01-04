package com.example.administrator.gamedemo.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @auther lixu
 * Created by lixu on 2017/1/4 0004.
 */
public class AllModifyActivity extends BaseActivity {
    public static final String MODIFY_NICKNAME = "NICKNAME";//用户名
    public static final String MODIFY_PWD = "PWD";//密码
    public static final String MODIFY_NOTE = "NOTE";  //个人说明
    public static final String MODIFY = "MODIFY";  //Intent传参

    public String cType;
    public Students mUser;

    @BindView(R.id.et_modify)
    EditText etModify;
    @BindView(R.id.et_modify_pwd_new_1)
    EditText etModifyPwdNew1;
    @BindView(R.id.et_modify_pwd_new_2)
    EditText etModifyPwdNew2;
    @BindView(R.id.ll_modifypwd)
    LinearLayout llModifypwd;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_allmodify);
    }

    @Override
    public void initViews() {
        mUser = Constants.getInstance().getUser();
        if (mUser == null) {
            ToastUtil3.showToast(AllModifyActivity.this, "登录失效，请重新登录");
            finish();
        } else {
            Intent gIntent = getIntent();
            cType = gIntent.getExtras().getString(MODIFY);
            if (cType.equals(MODIFY_NICKNAME)) {
                etModify.setText(mUser.getNick_name());
                mToolbar.setTitle("修改昵称");
            } else if (cType.equals(MODIFY_PWD)) {
                mToolbar.setTitle("修改密码");
                llModifypwd.setVisibility(View.VISIBLE);
                etModify.setHint("点击输入当前密码");
            } else if (cType.equals(MODIFY_NOTE)) {
                mToolbar.setTitle("修改个人说明");
                etModify.setText(mUser.getMyself_speak());
            }

            mToolbar.setNavigationIcon(R.drawable.icon_cancle);
            setSupportActionBar(mToolbar);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_send_whrite:

                            String textMODIFY = etModify.getText().toString().trim();
                            String textPWD_1 = etModifyPwdNew1.getText().toString().trim();
                            String textPWD_2 = etModifyPwdNew2.getText().toString().trim();

                            if (cType.equals(MODIFY_NICKNAME)) {

                                if(textMODIFY == null || textMODIFY.trim().length() == 0){
                                    ToastUtil3.showToast(AllModifyActivity.this,"昵称不能为空");

                                }else {
                                    commitData(textMODIFY,textMODIFY,textMODIFY);
                                }

                            } else if (cType.equals(MODIFY_PWD)) {

                                if(textMODIFY == null || textMODIFY.length() == 0){
                                    ToastUtil3.showToast(AllModifyActivity.this,"请填写当前密码");
                                }else if(textPWD_1 == null || textPWD_1.length() == 0){
                                    ToastUtil3.showToast(AllModifyActivity.this,"请填写第一遍新密码");
                                }else if(textPWD_2 == null || textPWD_2.length() == 0){
                                    ToastUtil3.showToast(AllModifyActivity.this,"请填写第二遍新密码");
                                }else if(!textPWD_1.equals(textPWD_2)){
                                    ToastUtil3.showToast(AllModifyActivity.this,"两次输入密码不一致");
                                }else {
                                    commitData(textMODIFY,textMODIFY,textMODIFY);
                                }

                            } else if (cType.equals(MODIFY_NOTE)) {//个人说明可以为空
                                commitData(textMODIFY,textMODIFY,textMODIFY);
                            }


                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void initData() {

    }


    private void commitData(String modify,String pwd1,String pwd2){
        showProgressBarDialog(AllModifyActivity.this);
        if (cType.equals(MODIFY_NICKNAME) || cType.equals(MODIFY_NOTE)) {
            if(cType.equals(MODIFY_NICKNAME)){
                mUser.setNick_name(modify);
            }else if(cType.equals(MODIFY_NOTE)){
                mUser.setMyself_speak(modify);
            }
            mUser.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    pDialog.dismiss();
                    if(e == null){
                        ToastUtil3.showToast(AllModifyActivity.this,"修改成功");
                        setResult(Constants.LOGIN_OK);
                        finish();
                    }else{
                        ToastUtil3.showToast(AllModifyActivity.this,"修改失败,请检查网络并重试"+e );
                    }
                }
            });

        } else if (cType.equals(MODIFY_PWD)) {

            mUser.updateCurrentUserPassword(pwd1, pwd2, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    pDialog.dismiss();
                    if(e==null){
                        BmobUser.logOut();
                        ToastUtil3.showToast(AllModifyActivity.this,"修改成功,请重新登录");
                        setResult(Constants.GOLOGIN);
                        finish();
                    }else{
                        ToastUtil3.showToast(AllModifyActivity.this,"修改失败,请检查网络并重试"+e );
                    }
                }

            });

        }else{ pDialog.dismiss();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_send_whrite, menu);
        return true;
    }
    private SweetAlertDialog pDialog;
    public void showProgressBarDialog(final Activity mContext){
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
