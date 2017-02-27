package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.model.bean.Idea;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

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
        Idea idea = new Idea();
        idea.setContent(iContent);
        idea.setPhone_(iTell);
        idea.save(new SaveListener<String>() {
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
