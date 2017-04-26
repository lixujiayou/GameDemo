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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.mine.UploadActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.MomentsInfo;
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
import cn.bmob.v3.exception.BmobException;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/12/14 0014.
 * 上传题/上传失败的答题
 */
public class SendAnswerActivity extends BaseActivity {

    public final static String SEND = "SEND";
    public final static String CHANGE = "CHANGE";
    public final static String INTENT = "INTENT";
    public final static String TOPIC = "topic";

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

//    @BindView(R.id.loading)
//    RotateLoading loading;
    @BindView(R.id.view_red)
    View viewRed;
    @BindView(R.id.ll_red)
    LinearLayout llRed;
    @BindView(R.id.view_green)
    View viewGreen;
    @BindView(R.id.ll_green)
    LinearLayout llGreen;
    @BindView(R.id.view_blue)
    View viewBlue;
    @BindView(R.id.ll_blue)
    LinearLayout llBlue;
    @BindView(R.id.view_gray)
    View viewGray;
    @BindView(R.id.ll_gray)
    LinearLayout llGray;
    @BindView(R.id.ll_check)
    LinearLayout llCheck;
    @BindView(R.id.tv_s_name)
    TextView tvSName;
    @BindView(R.id.tv_s_idea)
    TextView tvSIdea;
    @BindView(R.id.tv_s_time)
    TextView tvSTime;
    @BindView(R.id.ll_hint)
    LinearLayout llHint;

    private ArrayList<String> mAnswerList = new ArrayList<>();
    private String mTopic;
    private String mAnser1;
    private String mAnser2;
    private String mAnser3;
    private String mAnser4;
    private String mPs;

    private boolean isAgree = true;
    private int mColor = 0;//当前选择的色值
    private String cType = "";
    private MomentsInfo cMomentsInfo;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_answer);
    }

    /**
     * 一起分享，一起讨论，一起交流神的恩典！
     */
    @Override
    public void initViews() {
        mToolbar.setNavigationIcon(R.drawable.icon_cancle_black);
        Intent gIntent = getIntent();
        cType = gIntent.getExtras().getString(INTENT);

        if (cType.equals(CHANGE)) {
            mToolbar.setTitle("审核详情");
            llHint.setVisibility(View.VISIBLE);
            cMomentsInfo = (MomentsInfo) gIntent.getSerializableExtra(TOPIC);
            tvSIdea.setText(cMomentsInfo.getIdea());
            tvSTime.setText(cMomentsInfo.getUpdatedAt());
            etSendTopic.setText(cMomentsInfo.getTopic());

            etSendAnswerY.setText(cMomentsInfo.getAnswers().get(0));
            etSendAnswerN1.setText(cMomentsInfo.getAnswers().get(1));
            etSendAnswerN2.setText(cMomentsInfo.getAnswers().get(2));
            etSendAnswerN3.setText(cMomentsInfo.getAnswers().get(3));
            etSendPs.setText(cMomentsInfo.getHint());

            mColor =  cMomentsInfo.getColor();
            changeView();

            if(cMomentsInfo.getCperson()!=null){
                tvSName.setText(cMomentsInfo.getCperson().getNick_name());
            }

        } else {
            mToolbar.setTitle("上传答题");
            llHint.setVisibility(View.GONE);
        }

        mToolbar.setTitleTextColor(ContextCompat.getColor(SendAnswerActivity.this, R.color.textcolor_1));
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
                            //if (isAgree) {
                                showDiaLog();
                            /*} else {
                                ToastUtil3.showToast(SendAnswerActivity.this, "请阅读并同意《圣经问答APP》上传规范");
                            }*/
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
            showProgressBarDialog();
            mAnswerList.clear();
            mAnswerList.add(mAnser1);
            mAnswerList.add(mAnser2);
            mAnswerList.add(mAnser3);
            mAnswerList.add(mAnser4);

            AddMomentsRequest addMomentsRequest = new AddMomentsRequest();
            addMomentsRequest.setTopic(mTopic);
            addMomentsRequest.setAnswers(mAnswerList);
            addMomentsRequest.setPs(mPs);
            addMomentsRequest.setColor(mColor);
            addMomentsRequest.setRp(Constants.UPLOAD_ING);
            addMomentsRequest.setAuth(mUser);
            addMomentsRequest.setOnResponseListener(new SimpleResponseListener<String>() {
                @Override
                public void onSuccess(String response, int requestType) {

                    pDialog.dismiss();

                    ToastUtil3.showToast(SendAnswerActivity.this, response);
                    Logger.d(response);
                    /*if(cType.equals(SEND)){
                        Intent sIntent = new Intent(SendAnswerActivity.this, UploadActivity.class);
                        startActivity(sIntent);
                    }*/
                    setResult(Constants.REFRESH_CODE);
                    finish();

                }

                @Override
                public void onError(BmobException e, int requestType) {
                    super.onError(e, requestType);
                    pDialog.dismiss();
                    ToastUtil3.showToast(SendAnswerActivity.this,"上传失败，请检查网络并重试。");
                }

                @Override
                public void onProgress(int pro) {

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

    @Override
    public void onBackPressed() {
        setResult(Constants.REFRESH_CODE);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_send_norm, R.id.ll_red, R.id.ll_green, R.id.ll_blue, R.id.ll_gray})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send_norm:
                Intent lIntent = new Intent(SendAnswerActivity.this, UpLoadRuleActivity.class);
                startActivityForResult(lIntent, 1);
                break;
            case R.id.ll_red:
                mColor = 0;
                changeView();
                break;
            case R.id.ll_green:
                mColor = 1;
                changeView();
                break;
            case R.id.ll_blue:
                mColor = 2;
                changeView();
                break;
            case R.id.ll_gray:
                mColor = 3;
                changeView();
                break;
        }
    }

    private void changeView() {
        viewRed.setBackground(ContextCompat.getDrawable(SendAnswerActivity.this, R.drawable.shape_red));
        viewGreen.setBackground(ContextCompat.getDrawable(SendAnswerActivity.this, R.drawable.shape_green));
        viewBlue.setBackground(ContextCompat.getDrawable(SendAnswerActivity.this, R.drawable.shape_blue));
        viewGray.setBackground(ContextCompat.getDrawable(SendAnswerActivity.this, R.drawable.shape_gray));
        switch (mColor) {
            case 0:
                viewRed.setBackground(ContextCompat.getDrawable(SendAnswerActivity.this, R.drawable.shape_red_s));
                break;
            case 1:
                viewGreen.setBackground(ContextCompat.getDrawable(SendAnswerActivity.this, R.drawable.shape_green_s));
                break;
            case 2:
                viewBlue.setBackground(ContextCompat.getDrawable(SendAnswerActivity.this, R.drawable.shape_blue_s));
                break;
            case 3:
                viewGray.setBackground(ContextCompat.getDrawable(SendAnswerActivity.this, R.drawable.shape_gray_s));
                break;
        }
    }
    private SweetAlertDialog pDialog;
    public void showProgressBarDialog(){
        try {
                pDialog = new SweetAlertDialog(SendAnswerActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText("正在上传审核...");
                pDialog.setCancelable(true);
                pDialog.show();

        }catch (Exception e){
            Logger.d("ProgressBarDialog的上下文找不到啦！");
        }
    }
}
