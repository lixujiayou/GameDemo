package com.example.administrator.gamedemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.MyBmobInstallation;
import com.example.administrator.gamedemo.utils.StringUtil;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.DeletableEditText;
import com.example.administrator.gamedemo.widget.LoadingView;
import com.example.administrator.gamedemo.widget.SmoothProgressBar;
import com.example.administrator.gamedemo.widget.request.UserProxy;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/12/15 0015.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener,UserProxy.ILoginListener,UserProxy.ISignUpListener,UserProxy.IResetPasswordListener{

    @BindView(R.id.login_menu)
    TextView loginTitle;
    @BindView(R.id.register_menu)
    TextView registerTitle;
    @BindView(R.id.reset_password_menu)
    TextView resetPassword;

    @BindView(R.id.user_name_input)
    DeletableEditText userNameInput;
    @BindView(R.id.user_password_input)
    DeletableEditText userPasswordInput;
    @BindView(R.id.user_email_input)
    DeletableEditText userEmailInput;

    @BindView(R.id.register)
    Button registerButton;
    @BindView(R.id.sm_progressbar)
    SmoothProgressBar progressbar;

    UserProxy userProxy;

    private void dimissProgressbar(){
        if(progressbar!=null&&progressbar.isShown()){
            progressbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoginSuccess() {
        // TODO Auto-generated method stub
        dimissProgressbar();
        BmobInstallation.getCurrentInstallation().save();
        bangdingId();
        setResult(Constants.LOGIN_OK);
        finish();
    }

    @Override
    public void onLoginFailure(String msg) {
        // TODO Auto-generated method stub
        dimissProgressbar();
        ToastUtil3.showToast(this, "登录失败,请查看网络并重试");
    }

    @Override
    public void onSignUpSuccess() {
        // TODO Auto-generated method stub
        dimissProgressbar();
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        operation = UserOperation.LOGIN;
        updateLayout(operation);
    }

    @Override
    public void onSignUpFailure(String msg) {
        // TODO Auto-generated method stub
        dimissProgressbar();
        ToastUtil3.showToast(this, "注册失败,请查看网络并重试");
    }

    @Override
    public void onResetSuccess() {
        // TODO Auto-generated method stub
        dimissProgressbar();
        Toast.makeText(this, "邮件已发送，请前往修改密码", Toast.LENGTH_SHORT).show();
        operation = UserOperation.LOGIN;
        updateLayout(operation);
    }

    @Override
    public void onResetFailure(String msg) {
        // TODO Auto-generated method stub
        dimissProgressbar();
        ToastUtil3.showToast(this, "邮件发送失败,请查看网络并重试");
    }

    private enum UserOperation{
        LOGIN,REGISTER,RESET_PASSWORD
    }

    UserOperation operation = UserOperation.LOGIN;



    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("账号");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        userProxy = new UserProxy(LoginActivity.this);

        loginTitle.setOnClickListener(this);
        registerTitle.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.register:
                if(operation == UserOperation.LOGIN){
                    if(TextUtils.isEmpty(userNameInput.getText())){
                        userNameInput.setShakeAnimation();
                        return;
                    }
                    if(TextUtils.isEmpty(userPasswordInput.getText())){
                        userPasswordInput.setShakeAnimation();
                        return;
                    }

                    userProxy.setOnLoginListener(LoginActivity.this);
                    progressbar.setVisibility(View.VISIBLE);
                    userProxy.login(userNameInput.getText().toString().trim(), userPasswordInput.getText().toString().trim());

                }else if(operation == UserOperation.REGISTER){
                    if(TextUtils.isEmpty(userNameInput.getText())){
                        userNameInput.setShakeAnimation();

                        return;
                    }
                    if(TextUtils.isEmpty(userPasswordInput.getText())){
                        userPasswordInput.setShakeAnimation();

                        return;
                    }
                    if(TextUtils.isEmpty(userEmailInput.getText())){
                        userEmailInput.setShakeAnimation();

                        return;
                    }
                    if(!StringUtil.isValidEmail(userEmailInput.getText())){
                        userEmailInput.setShakeAnimation();

                        return;
                    }

                    userProxy.setOnSignUpListener(this);
                    progressbar.setVisibility(View.VISIBLE);
                    userProxy.signUp(userNameInput.getText().toString().trim(),
                            userPasswordInput.getText().toString().trim(),
                            userEmailInput.getText().toString().trim());
                }else{
                    if(TextUtils.isEmpty(userEmailInput.getText())){
                        userEmailInput.setShakeAnimation();
                        return;
                    }
                    if(!StringUtil.isValidEmail(userEmailInput.getText())){
                        userEmailInput.setShakeAnimation();
                        return;
                    }

                    userProxy.setOnResetPasswordListener(this);
                    progressbar.setVisibility(View.VISIBLE);
                    userProxy.resetPassword(userEmailInput.getText().toString().trim());

                }
                break;
            case R.id.login_menu:
                operation = UserOperation.LOGIN;
                updateLayout(operation);
                break;
            case R.id.register_menu:
                operation = UserOperation.REGISTER;
                updateLayout(operation);
                break;
            case R.id.reset_password_menu:
                operation = UserOperation.RESET_PASSWORD;
                updateLayout(operation);
                break;
            default:
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

    private void updateLayout(UserOperation op){
        if(op == UserOperation.LOGIN){
            loginTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
            loginTitle.setBackgroundResource(R.drawable.bg_login_tab);
            loginTitle.setPadding(16, 16, 16, 16);
            loginTitle.setGravity(Gravity.CENTER);


            registerTitle.setTextColor(getResources().getColor(R.color.textcolor_2));
            registerTitle.setBackgroundDrawable(null);
            registerTitle.setPadding(16, 16, 16, 16);
            registerTitle.setGravity(Gravity.CENTER);

            resetPassword.setTextColor(getResources().getColor(R.color.textcolor_2));
            resetPassword.setBackgroundDrawable(null);
            resetPassword.setPadding(16, 16, 16, 16);
            resetPassword.setGravity(Gravity.CENTER);

            userNameInput.setVisibility(View.VISIBLE);
            userPasswordInput.setVisibility(View.VISIBLE);
            userEmailInput.setVisibility(View.GONE);
            registerButton.setText("登录");
        }else if(op == UserOperation.REGISTER){
            loginTitle.setTextColor(getResources().getColor(R.color.textcolor_2));
            loginTitle.setBackgroundDrawable(null);
            loginTitle.setPadding(16, 16, 16, 16);
            loginTitle.setGravity(Gravity.CENTER);

            registerTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
            registerTitle.setBackgroundResource(R.drawable.bg_login_tab);
            registerTitle.setPadding(16, 16, 16, 16);
            registerTitle.setGravity(Gravity.CENTER);

            resetPassword.setTextColor(getResources().getColor(R.color.textcolor_2));
            resetPassword.setBackgroundDrawable(null);
            resetPassword.setPadding(16, 16, 16, 16);
            resetPassword.setGravity(Gravity.CENTER);

            userNameInput.setVisibility(View.VISIBLE);
            userPasswordInput.setVisibility(View.VISIBLE);
            userEmailInput.setVisibility(View.VISIBLE);
            registerButton.setText("确认");
        }else{
            loginTitle.setTextColor(getResources().getColor(R.color.textcolor_2));
            loginTitle.setBackgroundDrawable(null);
            loginTitle.setPadding(16, 16, 16, 16);
            loginTitle.setGravity(Gravity.CENTER);

            registerTitle.setTextColor(getResources().getColor(R.color.textcolor_2));
            registerTitle.setBackgroundDrawable(null);
            registerTitle.setPadding(16, 16, 16, 16);
            registerTitle.setGravity(Gravity.CENTER);

            resetPassword.setTextColor(getResources().getColor(R.color.colorPrimary));
            resetPassword.setBackgroundResource(R.drawable.bg_login_tab);
            resetPassword.setPadding(16, 16, 16, 16);
            resetPassword.setGravity(Gravity.CENTER);


            userNameInput.setVisibility(View.GONE);
            userPasswordInput.setVisibility(View.GONE);
            userEmailInput.setVisibility(View.VISIBLE);
            registerButton.setText("确认");
        }
    }


    private void bangdingId(){
        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));
        query.findObjects(new FindListener<MyBmobInstallation>() {
            @Override
            public void done(List<MyBmobInstallation> list, BmobException e) {
                if(e == null ){
                    if(list.size() > 0){
                        MyBmobInstallation mbi = list.get(0);
                        mbi.setUid(Constants.getInstance().getUser().getObjectId());
                        mbi.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null ){
                                    Logger.d("设备信息更新成功");
                                }else{
                                    Logger.d("设备信息更新失败:"+e.toString());
                                }
                            }
                        });
                    }else{
                    }
                }else{

                }
            }
        });
    }

}
