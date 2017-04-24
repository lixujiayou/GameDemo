package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.model.AboutMessage;
import com.example.administrator.gamedemo.model.Students;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lixu on 2017/04/21.
 * 删除关于我的消息
 */

//
public class DeleteMessageRequest extends BaseRequestClient<Boolean> {

    private String momentsId;//关联资源id

    public DeleteMessageRequest(String rId) {
        this.momentsId = rId;
    }

    public String getMomentsId() {
        return momentsId;
    }

    public DeleteMessageRequest setMomentsId(String momentsId) {
        this.momentsId = momentsId;
        return this;
    }



    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        AboutMessage info = new AboutMessage();
        info.setObjectId(momentsId);
        info.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                onResponseSuccess(e == null, requestType);
            }
        });


    }
}
