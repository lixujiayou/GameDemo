package com.example.administrator.gamedemo.activity.mine;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.request.AddIdeaRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @auther lixu
 * Created by lixu on 2017/2/20 0020.
 * 提交建议Activity
 */
public class IdeaActivity extends BaseActivity {
    @BindView(R.id.et_idea_content)
    EditText etIdeaContent;
    @BindView(R.id.et_idea_tell)
    EditText etIdeaTell;
    @BindView(R.id.bt_idea_commit)
    Button btIdeaCommit;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_idea);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("提交建议");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.bt_idea_commit)
    public void onClick() {

        String ideaContent = etIdeaContent.getText().toString().trim();
        String ideaTell = etIdeaTell.getText().toString().trim();

        if(ideaContent != null || !ideaContent.isEmpty()){
            ToastUtil3.showToast(IdeaActivity.this,"请填写内容");
            return;
        }else if(ideaTell != null || !ideaTell.isEmpty()){
            ToastUtil3.showToast(IdeaActivity.this,"请填写联系方式");
            return;
        }

        showProgressBarDialog(IdeaActivity.this);
        AddIdeaRequest addIdeaRequest = new AddIdeaRequest();
        addIdeaRequest.setOnResponseListener(momentsRequestCallBack);
        addIdeaRequest.setSth(ideaContent,ideaTell);
        addIdeaRequest.execute();

    }

    //call back block
    //==============================================
    private SimpleResponseListener<String> momentsRequestCallBack = new SimpleResponseListener<String>() {
        @Override
        public void onSuccess(String response, int requestType) {
            ToastUtil3.showToast(IdeaActivity.this,"提交成功");
            pDialog.dismiss();
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            showErroDialog(IdeaActivity.this,"提示","提交失败,请检查网络并重试");
            Logger.d("提交失败"+e.toString());
            pDialog.dismiss();

        }

        @Override
        public void onProgress(int pro) {

        }
    };

    private SweetAlertDialog pDialog;
    public void showProgressBarDialog(final Activity mContext){
        try {
            pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.setTitleText("正在提交数据，请稍等...");
            pDialog.setCancelable(false);
            pDialog.show();
        }catch (Exception e){
            Logger.d("ProgressBarDialog的上下文找不到啦！");
        }
    }

    /**
     * 错误的dialog
     * @param mContext
     * @param title
     * @param message
     */
    private SweetAlertDialog eDialog;
    public void showErroDialog(final Activity mContext,String title,String message){
        try {
            if(mContext.hasWindowFocus()) {
                eDialog = new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE);
                eDialog.setTitleText(title);
                eDialog.setContentText(message);
                eDialog.setConfirmText("知道啦");
                eDialog.show();
            }
        }catch (Exception e){
            Logger.d("ErroDialog的上下文找不到啦！");
        }
    }
}
