package com.wxapi;

/**
 * Created by Administrator on 2016/4/7 0007.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.administrator.gamedemo.R;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler
        {
private IWXAPI api;
            private final String APP_ID = "wxf9612b61458aff13";
@Override
protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.registerApp(APP_ID);
        api.handleIntent(getIntent(), this);
        }

@Override
public void onReq(BaseReq req)
        {
        }

@Override
public void onResp(BaseResp resp)
        {
        int result = 0;

        switch (resp.errCode)
        {
        case BaseResp.ErrCode.ERR_OK:
        result = R.string.errcode_success;
        break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:
        result = R.string.errcode_cancel;
        break;
        case BaseResp.ErrCode.ERR_AUTH_DENIED:
        result = R.string.errcode_deny;
        break;
default:
        result = R.string.errcode_unknown;

        break;

        }



        Toast.makeText(this, result, Toast.LENGTH_LONG).show();

        finish();

        overridePendingTransition(R.anim.change_in, R.anim.change_out);

        }



        }
