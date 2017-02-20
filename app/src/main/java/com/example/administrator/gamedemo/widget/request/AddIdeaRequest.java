package com.example.administrator.gamedemo.widget.request;

import android.text.TextUtils;

import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.bean.IdeaBean;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * @author lixu
 * Created by lixu on 2017/02/20.
 * 提交建议
 */

public class AddIdeaRequest extends BaseRequestClient<String> {
    private String iContent;
    private String iTell;
    public AddIdeaRequest() {
    }

    public AddIdeaRequest setSth(String mContent,String mTell) {
        this.iContent = mContent;
        this.iTell = mTell;
        return this;
    }



    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        IdeaBean ideaBean = new IdeaBean();
        ideaBean.setContent(iContent);
        ideaBean.setPhone_(iTell);
        ideaBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    onResponseSuccess("提交成功", requestType);
                }else{
                    onResponseError(e, requestType);
                }

            }
        });
    }
}
