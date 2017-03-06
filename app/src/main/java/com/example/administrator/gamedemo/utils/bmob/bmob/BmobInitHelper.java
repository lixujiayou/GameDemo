package com.example.administrator.gamedemo.utils.bmob.bmob;



import android.os.SystemClock;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.widget.request.AddCommentRequest;
import com.example.administrator.gamedemo.widget.request.AddMomentsRequest;
import com.example.administrator.gamedemo.widget.request.AddShareRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by lixu on 2016/10/28.
 * <p>
 * 为bmob数据库初始化的工具类
 */

public class BmobInitHelper {
    private static final String TAG = "BmobInitHelper";

    //private List<UserInfo> userInfoList;

    public BmobInitHelper() {
    }

    public void initUser(final SimpleResponseListener simpleResponseListener) {
        final BmobUser bmobUser = new BmobUser();
        BmobQuery<Students> userQuery = new BmobQuery<>();
        userQuery.findObjects(new FindListener<Students>() {
            @Override
            public void done(List<Students> list, BmobException e) {
                if (e == null) {
                    if (bmobUser == null) {
                       return;
                    }
                    if (simpleResponseListener != null) {
                        simpleResponseListener.onSuccess("成功", 0);
                    }
                }else{
                    Logger.d("初始化用户失败"+e);
                }
            }
        });
    }

    public void addComment(Share share, Students author, Students reply, String content) {
      //  if (ToolUtil.isListEmpty(userInfoList)) return;
        AddCommentRequest addCommentRequest = new AddCommentRequest();
        addCommentRequest.setContent(content);
        addCommentRequest.setMomentsInfoId(share);
        addCommentRequest.setAuthorId(author);
        addCommentRequest.setReplyUserId(reply);

        addCommentRequest.setOnResponseListener(new SimpleResponseListener<CommentInfo>() {
            @Override
            public void onSuccess(CommentInfo response, int requestType) {
                Logger.d( "添加成功  >>>  " + response.getCommentid());
            }

            @Override
            public void onProgress(int pro) {

            }
        });
        addCommentRequest.execute();
    }

   /* public void addMoments() {
        Students bmobUser =  BmobUser.getCurrentUser(Students.class);
        if(bmobUser == null ){
            Logger.d("用户为空");
            return;
        }
       // if (ToolUtil.isListEmpty(userInfoList)) return;
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            AddShareRequest addMomentsRequest = new AddShareRequest();
 //           int randomAuthPos = random.nextInt(userInfoList.size());
 //           Logger.d("随机发布者pos  >>>  " + randomAuthPos);
            String randomAuthId = bmobUser.getUsername();
            String hostid = bmobUser.getObjectId();

            addMomentsRequest.setAuth(bmobUser);
            addMomentsRequest.setHostId(hostid);
            ArrayList<String> strings = new ArrayList<>();
            strings.add("答案一");
            strings.add("答案二");
            strings.add("答案三");
            strings.add("答案四");
            addMomentsRequest.setAnswers(strings);
            addMomentsRequest.setTopic("测试问题问号?");

            int randomPicture = random.nextInt(9);
            for (int j = 0; j < randomPicture; j++) {
                int picturePos = random.nextInt(BmobTestDatasHelper.getImages().length);
                addMomentsRequest.addPicture(BmobTestDatasHelper.getImage(picturePos));
            }
            for (int j = 0; j < 1; j++) {
                addMomentsRequest.addLikes(bmobUser);
            }

            int randomText = random.nextInt(1000);
            addMomentsRequest.addText(BmobTestDatasHelper.getText(randomText));

            addMomentsRequest.setOnResponseListener(new SimpleResponseListener<String>() {
                @Override
                public void onSuccess(String response, int requestType) {
                    Logger.d(response);
                }
            });
            addMomentsRequest.execute();
        }
    }*/

   /* public void addShares() {
        Students students = Constants.getInstance().getUser();
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            AddShareRequest addMomentsRequest = new AddShareRequest();
            String randomAuthId = students.getObjectId();
            String hostid = "ysuw2A4A";
            addMomentsRequest.setAuthId(randomAuthId);
            addMomentsRequest.setHostId(hostid);

            int randomPicture = random.nextInt(9);
            Logger.d("随机插入图片数量  >>>  " + randomPicture);
            for (int j = 0; j < randomPicture; j++) {
                int picturePos = random.nextInt(BmobTestDatasHelper.getImages().length);
                addMomentsRequest.addPicture(BmobTestDatasHelper.getImage(picturePos));
            }
            int randomLikes = random.nextInt(20);
            Logger.d("随机插入点赞人数  >>>  " + randomLikes);
            for (int j = 0; j < randomLikes; j++) {
                addMomentsRequest.addLikes(students);
            }

            int randomText = random.nextInt(1000);
            addMomentsRequest.addText(BmobTestDatasHelper.getText(randomText));

            addMomentsRequest.setOnResponseListener(new SimpleResponseListener<String>() {
                @Override
                public void onSuccess(String response, int requestType) {
                    Logger.d(response);
                }
            });
            addMomentsRequest.execute();
        }
       *//* AddMomentsRequest addMomentsRequest = new AddMomentsRequest();
        addMomentsRequest.setAuthId(userInfoList.get(10).getUserid())
                         .setHostId("MMbKLCCU")
                         .addPicture("http://qn.ciyo.cn/upload/FgbnwPphrRD46RsX_gCJ8PxMZLNF")
                         .addPicture("http://qn.ciyo.cn/upload/Fne1GYidTXptXawWJ4j9dn26Fyei")
                         .addPicture("http://qn.ciyo.cn/upload/FpKe7Hwks4CKCGwpNKaLzz_9lb2u")
                         .addPicture("http://qn.ciyo.cn/upload/FnkfSeD7GNnoWcn3vlxiGRrN2sgP")
                         .addText("测试一下")
                         .addLikes(userInfoList.get(0))
                         .addLikes(userInfoList.get(1))
                         .addLikes(userInfoList.get(2))
                         .addLikes(userInfoList.get(3));
        addMomentsRequest.setOnResponseListener(new SimpleResponseListener<String>() {
            @Override
            public void onSuccess(String response, int requestType) {
                KLog.d(response);
            }
        });
        addMomentsRequest.execute();*//*

    }*/


    /**
     * 建立评论伪数据
     */
    public void addComments(List<Share> momentsList) {
        Random random = new Random();
        //随机为50条动态添加评论
        for (int i = 0; i < 50; i++) {
            Logger.d("添加评论第"+i);
 //           int momentIndex = random.nextInt(momentsList.size());

            boolean isReply = random.nextBoolean();

 //           String momentid = momentsList.get(momentIndex).getMomentid();
 //           String authorId = Constants.getInstance().getUser().getObjectId();
            String comment = "测试评论内容";
 //           String replyUserid;
            if (isReply) {
                String replyCommentContent = "测试评论内容";
 //               replyUserid = Constants.getInstance().getUser().getObjectId();
                //先添加评论，稍微延后一点再添加回复
                addComment(momentsList.get(i), Constants.getInstance().getUser(), null, comment);
                SystemClock.sleep(200);
                addComment(momentsList.get(i), Constants.getInstance().getUser(), Constants.getInstance().getUser(), replyCommentContent);
            } else {
                addComment(momentsList.get(i), Constants.getInstance().getUser(), null, comment);
            }
        }
    }
}