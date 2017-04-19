package com.example.administrator.gamedemo.widget.request;

import android.text.TextUtils;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * @author lixu
 * Created by lixu on 2016/12/17.
 * 添加一起
 */

public class AddTogtherRequest extends BaseRequestClient<String> {

    private String authId;
    private String hostId;
    private Togther togther;
    private List<String> picList;
    private List<LikesInfo> likesUserId;
   // private List<Students> likesUserId;
    private String[] pics;
    public AddTogtherRequest() {
        togther = new Togther();
    }

    public AddTogtherRequest setAuthId(String authId) {
        this.authId = authId;
        return this;
    }
    public void setLikesUserId(List<LikesInfo> mLikesUserId){
        this.likesUserId = mLikesUserId;
    }

    public AddTogtherRequest setHostId(String hostId) {
        this.hostId = hostId;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        if (checkValided()) {
            if(picList == null || picList.size() == 0){
                insertObject(null, requestType);
            }else {
                pics = new String[picList.size()];
                for (int i = 0; i < picList.size(); i++) {
                    pics[i] = picList.get(i);
                }
                BmobFile.uploadBatch(pics, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> list, List<String> list1) {
                        if (list1.size() == pics.length) {//如果数量相等，则代表文件全部上传完成
                            insertObject(list, requestType);
                        }
                    }

                    @Override
                    public void onProgress(int i, int i1, int i2, int i3) {
                       // setProgressDialogText(String.valueOf(i3));
                        onResponseProgress(i3);
                        Logger.d("当前上传进度：" + i3);
                    }

                    @Override
                    public void onError(int i, String s) {
                        BmobException e = new BmobException();
                        onResponseError(e, requestType);
                        Logger.d(i + "上传失败：" + s);
                    }
                });
            }
        }
    }


    private boolean checkValided() {
        return !TextUtils.isEmpty(authId)  && togther.isValided();
    }

    public AddTogtherRequest addText(String text) {
        togther.setText(text);
        return this;
    }

    public AddTogtherRequest addPicture(String pic) {
        if(picList == null){
            picList = new ArrayList<>();
        }
        picList.add(pic);
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

//    public AddTogtherRequest addLikes(Students user) {
//        likesUserId.add(user);
//        return this;
//    }

    private void insertObject(List<BmobFile> list, final int type){

        Students author = new Students();
        author.setObjectId(authId);
        togther.setAuthor(author);
        togther.setLikesList(likesUserId);

        if(list != null) {
            togther.setPics(list);
        }

        togther.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                        onResponseSuccess(s, type);
                }else{
                        onResponseError(e, type);
                }
            }
        });
    }

//    Share resultMoment = new Share();
//    resultMoment.setObjectId(s);
//
//    //关联点赞的
//    BmobRelation relation = new BmobRelation();
//    for (Students user : likesUserId) {
//        relation.add(user);
//    }
//    resultMoment.setLikesBmobRelation(relation);
//    resultMoment.update(new UpdateListener() {
//        @Override
//        public void done(BmobException e) {
//            if (e == null) {
//                onResponseSuccess("添加成功", type);
//            } else {
//                onResponseError(e, type);
//            }
//        }
//    });

}
