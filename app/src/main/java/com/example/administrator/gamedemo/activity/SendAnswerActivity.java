package com.example.administrator.gamedemo.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.utils.StringUtil;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.request.AddMomentsRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/14 0014.
 * 上传题
 */
public class SendAnswerActivity extends BaseActivity {

    @BindView(R.id.et_send_topic)
    EditText etSendTopic;
    @BindView(R.id.et_send_answer_y)
    EditText etSendAnswerY;
    @BindView(R.id.et_send_answer_n_1)
    EditText etSendAnswerN1;
    @BindView(R.id.et_send_answer_n_2)
    EditText etSendAnswerN2;
    @BindView(R.id.et_send_answer_n_3)
    EditText etSendAnswerN3;
    @BindView(R.id.cb_agree)
    CheckBox cbAgree;
    @BindView(R.id.tv_send_norm)
    TextView tvSendNorm;
    @BindView(R.id.et_send_ps)
    EditText etSendPs;

    @BindView(R.id.loading)
    RotateLoading loading;

    private ArrayList<String> mAnswerList = new ArrayList<>();
    private String mTopic;
    private String mAnser1;
    private String mAnser2;
    private String mAnser3;
    private String mAnser4;
    private String mPs;

    private boolean isAgree = true;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_answer);
    }

    @Override
    public void initViews() {
        mToolbar.setNavigationIcon(R.drawable.icon_cancle_black);
        mToolbar.setTitle("上传答题");
        mToolbar.setTitleTextColor(ContextCompat.getColor(SendAnswerActivity.this,R.color.textcolor_1));
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_send:
                        mTopic = etSendTopic.getText().toString().trim();
                        mAnser1 = etSendAnswerY.getText().toString().trim();
                        mAnser2 = etSendAnswerN1.getText().toString().trim();
                        mAnser3 = etSendAnswerN2.getText().toString().trim();
                        mAnser4 = etSendAnswerN3.getText().toString().trim();
                        mPs = etSendPs.getText().toString().trim();
                        if (!isEmpty()) {
                            if(isAgree) {
                                showDiaLog();
                            }else{
                                ToastUtil3.showToast(SendAnswerActivity.this,"请阅读并同意《圣经问答APP》上传规范");
                            }
                        }
                        break;
                }

                return true;
            }
        });

        cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAgree = b;
            }
        });
    }


    @Override
    public void initData() {
    }


    private void showDiaLog() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("即将提交审核,期间无法修改,是否继续?")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startSend();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("再看看", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            ToastUtil3.showToast(SendAnswerActivity.this, "程序异常,请稍后重试");
        }
    }

    private void startSend() {
        if (mUser != null) {
            loading.start();
            mAnswerList.clear();
            mAnswerList.add(mAnser1);
            mAnswerList.add(mAnser2);
            mAnswerList.add(mAnser3);
            mAnswerList.add(mAnser4);

            AddMomentsRequest addMomentsRequest = new AddMomentsRequest();
            addMomentsRequest.setTopic(mTopic);
            addMomentsRequest.setAnswers(mAnswerList);
            addMomentsRequest.setPs(mPs);
            addMomentsRequest.setAuth(mUser);
            addMomentsRequest.setOnResponseListener(new SimpleResponseListener<String>() {
                @Override
                public void onSuccess(String response, int requestType) {
                    loading.stop();
                    ToastUtil3.showToast(SendAnswerActivity.this, response);
                    Logger.d(response);

                    setResult(Constants.REFRESH_CODE);
                    finish();

                }
            });
            addMomentsRequest.execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }


    private boolean isEmpty() {
        if (StringUtil.noEmpty(mTopic)) {
            if (StringUtil.noEmpty(mAnser1)) {
                if (StringUtil.noEmpty(mAnser2)) {
                    if (StringUtil.noEmpty(mAnser3)) {
                        if (StringUtil.noEmpty(mAnser4)) {
                            if (StringUtil.noEmpty(mPs)) {
                                return false;
                            } else {
                                ToastUtil3.showToast(SendAnswerActivity.this, "请填写参考经节或资料");
                                return true;
                            }
                        } else {
                            ToastUtil3.showToast(SendAnswerActivity.this, "请填写第最后一个错误答案");
                            return true;
                        }
                    } else {
                        ToastUtil3.showToast(SendAnswerActivity.this, "请填写第二个错误答案");
                        return true;
                    }
                } else {
                    ToastUtil3.showToast(SendAnswerActivity.this, "请填写第一个错误答案");
                    return true;
                }
            } else {
                ToastUtil3.showToast(SendAnswerActivity.this, "请填写正确答案");
                return true;
            }
        } else {
            ToastUtil3.showToast(SendAnswerActivity.this, "请填写题目内容");
            return true;
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            isAgree = true;
            cbAgree.setChecked(true);
        }
    }




    @OnClick({R.id.tv_send_norm})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_send_norm:
                Intent lIntent = new Intent(SendAnswerActivity.this, UpLoadRuleActivity.class);
                startActivityForResult(lIntent, 1);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Constants.REFRESH_CODE);
        finish();
    }
}
