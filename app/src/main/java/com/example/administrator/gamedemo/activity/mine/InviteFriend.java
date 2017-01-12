package com.example.administrator.gamedemo.activity.mine;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;


import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.BaseUiListener;
import com.example.administrator.gamedemo.utils.UIHelper;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.Tencent;

/**
 * Created by Administrator on 2016/5/20 0020.
 */
public class InviteFriend extends BaseActivity {

    private String invite_title = "圣经问答APP(安卓)";
    private String invite_content = "点击下载";
    private final String APP_ID = "wxf9612b61458aff13";


    private EditText et_title,et_content;
    private Button bt_commit;
    private RadioButton rb_qq,rb_qz,rb_wx,rb_pyq;
    private int cuNum = 1;
    private IWXAPI api;
    private Tencent mTencent;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_invite_friend);
    }

    @Override
    public void initViews() {
        initView();
        initRB();

        api = WXAPIFactory.createWXAPI(InviteFriend.this, APP_ID);
        mTencent = Tencent.createInstance("1105176662", InviteFriend.this);
        bt_commit.setOnClickListener(this);
    }

    @Override
    public void initData() {
    }


    private void initView(){
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        mToolbar.setTitle("推荐");
        et_title = (EditText) findViewById(R.id.activity_invite_friend_et_title);
        et_content = (EditText) findViewById(R.id.activity_invite_friend_et_content);

        rb_qq = (RadioButton) findViewById(R.id.activity_invite_rb_qq);
        rb_qz = (RadioButton) findViewById(R.id.activity_invite_rb_qz);
        rb_wx = (RadioButton) findViewById(R.id.activity_invite_rb_wx);
        rb_pyq = (RadioButton) findViewById(R.id.activity_invite_rb_pyq);
        bt_commit = (Button) findViewById(R.id.activity_invite_friend_bt_commit);

    }
    private void initRB(){
        rb_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_qq.setChecked(true);
                rb_qz.setChecked(false);
                rb_wx.setChecked(false);
                rb_pyq.setChecked(false);
                cuNum = 1;
            }
        });
        rb_qz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_qq.setChecked(false);
                rb_qz.setChecked(true);
                rb_wx.setChecked(false);
                rb_pyq.setChecked(false);
                cuNum = 2;

            }
        });
        rb_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_qq.setChecked(false);
                rb_qz.setChecked(false);
                rb_wx.setChecked(true);
                rb_pyq.setChecked(false);
                cuNum = 3;
            }
        });
        rb_pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_qq.setChecked(false);
                rb_qz.setChecked(false);
                rb_wx.setChecked(false);
                rb_pyq.setChecked(true);
                cuNum = 3;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(et_title.getText().toString().length() != 0){
            invite_title = et_title.getText().toString();
        }

        if(et_content.getText().toString().length() != 0){
            invite_content = et_content.getText().toString();
        }

        switch (v.getId()){

            case R.id.activity_invite_friend_bt_commit:
                if(cuNum==1){
                    sendToQQ();

                }else if(cuNum==2){

                    sendToQQKongjian();

                }else if(cuNum==3){

                    TuiJianToFriend();

                }else if(cuNum==4){
                    TuiJianToWX();
                }
                break;

        }
    }


    /**
     * 推荐app到朋友圈
     */
    private void TuiJianToWX(){

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://android.myapp.com/myapp/detail.htm?apkName=com.example.administrator.gamedemo";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = invite_title;
        msg.description = invite_content;
        Bitmap thumb = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
        msg.thumbData = UIHelper.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 推荐app到微信好友
     */
    private void TuiJianToFriend(){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://android.myapp.com/myapp/detail.htm?apkName=com.example.administrator.gamedemo";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = invite_title;
        msg.description = invite_content;
        Bitmap thumb = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
        msg.thumbData = UIHelper.bmpToByteArray(thumb, false);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    /**
     * 推荐到qq空间
     */
    private void sendToQQKongjian(){
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, R.drawable.ic_launcher);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, invite_title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  invite_content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://android.myapp.com/myapp/detail.htm?apkName=com.example.administrator.gamedemo");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://file.bmob.cn/M03/1E/78/oYYBAFcHYt6AaWTTAABArgK6VGo182.png");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "圣经问答");
        //params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");
        mTencent.shareToQQ((Activity) this, params, new BaseUiListener());
    }

    /**
     * 推荐APP到QQ好友
     */
    private void sendToQQ(){
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, R.drawable.ic_launcher);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, invite_title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  invite_content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://android.myapp.com/myapp/detail.htm?apkName=com.example.administrator.gamedemo");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://file.bmob.cn/M03/1E/78/oYYBAFcHYt6AaWTTAABArgK6VGo182.png");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "圣经问答");
        //params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");
        mTencent.shareToQQ((Activity) this, params, new BaseUiListener());
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
