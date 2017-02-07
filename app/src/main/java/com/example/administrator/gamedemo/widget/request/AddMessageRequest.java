package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.model.AboutMessage;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lixu on 2016/12/30.
 */

//
public class AddMessageRequest extends BaseRequestClient<Boolean> {

    private String momentsId;//关联资源id
    private String userid;//主动人
    private String mUserid;//被动人
    private String mType;//
    private String mContent;//
    Students mUserInfo;
    public AddMessageRequest(String userId,String rId,String type,String content) {
        this.momentsId = rId;
        this.userid = userId;
        this.mUserid = BmobUser.getCurrentUser().getObjectId();
        this.mType = type;
        this.mContent = content;
    }

    public String getMomentsId() {
        return momentsId;
    }

    public AddMessageRequest setMomentsId(String momentsId) {
        this.momentsId = momentsId;
        return this;
    }

    public String getUserid() {
        return userid;
    }

    public AddMessageRequest setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {

        AboutMessage info = new AboutMessage();
        info.setType(mType);
        info.setContent(mContent);
        info.setId(momentsId);

        Students userInfo = new Students();
        userInfo.setObjectId(userid);
        info.setcUsers(userInfo);

         mUserInfo = new Students();
        mUserInfo.setObjectId(mUserid);
        info.setmUsers(mUserInfo);

        info.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    BmobQuery<AboutMessage> commentQuery = new BmobQuery<>();
                    commentQuery.getObject(s, new QueryListener<AboutMessage>() {
                        @Override
                        public void done(AboutMessage aboutMessage, BmobException e) {
                            if (e == null) {
                                BmobRelation mbmobRelation = new BmobRelation();
                                mbmobRelation.add(aboutMessage);
                                mUserInfo.setMessage(mbmobRelation);
                                mUserInfo.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });

                            } else {
                                onResponseSuccess(e == null, requestType);
                            }
                        }
                    });
                }else{
                    Logger.d("保存失败"+e.toString());
                    onResponseSuccess(e == null, requestType);
                }

            }
        });
    }
}
