package com.example.administrator.gamedemo.widget.request;

import android.text.TextUtils;

import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToolUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author lixu
 * Created by lixu on 2016/12/19.
 * 添加分享
 */

public class AddShareRequest extends BaseRequestClient<String> {

    private String authId;
    private String hostId;
    //private MomentContent momentContent;
    private Share share;
    private List<Students> likesUserId;

    public AddShareRequest() {
        share = new Share();
        likesUserId = new ArrayList<>();
    }

    public AddShareRequest setAuthId(String authId) {
        this.authId = authId;
        return this;
    }

    public AddShareRequest setHostId(String hostId) {
        this.hostId = hostId;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        if (checkValided()) {


            Students author = new Students();
            author.setObjectId(authId);
            share.setAuthor(author);

//            Students host = new Students();
//            host.setObjectId(hostId);
//            share.setHostinfo(host);

            share.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        if (ToolUtil.isListEmpty(likesUserId)) {
                            onResponseSuccess(s, requestType);
                        } else {
                            Share resultMoment = new Share();
                            resultMoment.setObjectId(s);

                            //关联点赞的
                            BmobRelation relation = new BmobRelation();
                            for (Students user : likesUserId) {
                                relation.add(user);
                            }
                            resultMoment.setLikesBmobRelation(relation);
                            resultMoment.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        onResponseSuccess("添加成功", requestType);
                                    } else {
                                        onResponseError(e, requestType);
                                    }
                                }
                            });
                        }

                    }
                }
            });

        }
    }


    private boolean checkValided() {
        return !(TextUtils.isEmpty(authId) || TextUtils.isEmpty(hostId)) && share.isValided();
    }

    public AddShareRequest addText(String text) {
        share.setText(text);
        return this;
    }

    public AddShareRequest addPicture(BmobFile pic) {
        share.addPicture(pic);
        return this;
    }

//    public AddShareRequest addWebUrl(String webUrl) {
//        momentContent.addWebUrl(webUrl);
//        return this;
//    }
//
//    public AddShareRequest addWebTitle(String webTitle) {
//        momentContent.addWebTitle(webTitle);
//        return this;
//    }
//
//    public AddShareRequest addWebImage(String webImage) {
//        momentContent.addWebImage(webImage);
//        return this;
//    }

    public AddShareRequest addLikes(Students user) {
        likesUserId.add(user);
        return this;
    }
}
