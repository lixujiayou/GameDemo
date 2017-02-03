package com.example.administrator.gamedemo.utils.presenter;

import android.support.annotation.Nullable;
import android.text.TextUtils;


import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CollectImpl;
import com.example.administrator.gamedemo.model.CommentImpl;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.LikeImpl;
import com.example.administrator.gamedemo.model.MyBmobInstallation;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.impl.MessageImpl;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.view.IMomentView;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.request.callback.OnCollectChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnLikeChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnMessageCallback;
import com.orhanobut.logger.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by lixu on 2016/12/7.
 * <p>
 * presenter
 */

public class MomentPresenter implements IMomentPresenter {
    private IMomentView momentView;
    private CommentImpl commentModel;
    private LikeImpl likeModel;
    private CollectImpl collectModel;
    private MessageImpl message;
    private Students cUser;

    public MomentPresenter() {
        this(null);
    }

    public MomentPresenter(IMomentView momentView) {
        this.momentView = momentView;
        commentModel = new CommentImpl();
        likeModel = new LikeImpl();
        collectModel = new CollectImpl();
        message = new MessageImpl();
        cUser = BmobUser.getCurrentUser(Students.class);
    }

    @Override
    public IBasePresenter<IMomentView> bindView(IMomentView view) {
        this.momentView = view;
        return this;
    }

    @Override
    public IBasePresenter<IMomentView> unbindView() {
        return this;
    }

    @Override
    public void addLike(final int viewHolderPos, String momentid, final List<Students> currentLikeUserList) {
        likeModel.addLike(momentid, new OnLikeChangeCallback() {
            @Override
            public void onLike() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(currentLikeUserList)) {
                    resultLikeList.addAll(currentLikeUserList);
                }
                boolean hasLocalLiked = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId()) > -1;
                if (!hasLocalLiked) {
                    resultLikeList.add(0, Constants.getInstance().getUser());
                }
                if (momentView != null) {
                    momentView.onLikeChange(viewHolderPos, resultLikeList);

                }
            }

            @Override
            public void onUnLike() {

            }
        });
    }
    @Override
    public void collect(final int viewHolderPos, String momentid, final List<Students> collectUserList) {
        collectModel.addCollect(momentid,collectUserList, new OnCollectChangeCallback() {
            @Override
            public void onCollect() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(collectUserList)) {
                    resultLikeList.addAll(collectUserList);
                }
                boolean hasLocalLiked = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId()) > -1;
                if (!hasLocalLiked) {
                    resultLikeList.add(0, Constants.getInstance().getUser());
                }
                if (momentView != null) {
                    momentView.onCollectChange(viewHolderPos, resultLikeList);
                }
            }

            @Override
            public void onUnCollect() {

            }
        });
    }

    @Override
    public void unCollect(final int viewHolderPos, String momentid, final List<Students> unCollectUserList) {
        collectModel.unCollect(momentid,unCollectUserList, new OnCollectChangeCallback() {
            @Override
            public void onCollect() {

            }

            @Override
            public void onUnCollect() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(unCollectUserList)) {
                    resultLikeList.addAll(unCollectUserList);
                }
                final int localLikePos = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId());
                if (localLikePos > -1) {
                    resultLikeList.remove(localLikePos);
                }
                if (momentView != null) {
                    momentView.onCollectChange(viewHolderPos, resultLikeList);
                }
            }
        });
    }

    @Override
    public void addMessage(final String cUserId,final String rId, String type, final String content) {
        message.addMessage(cUserId, rId, type, content, new OnMessageCallback() {
            @Override
            public void onMessage() {
                BmobQuery<MyBmobInstallation> bmobQuery = new BmobQuery<>();
                bmobQuery.addWhereEqualTo("uid",cUserId);
                bmobQuery.findObjects(new FindListener<MyBmobInstallation>() {
                    @Override
                    public void done(List<MyBmobInstallation> list, BmobException e) {
                        if(list.size() > 0){
                            momentView.onMessageChange(list.get(0).getInstallationId(),content);
                        }
                    }
                });



            }

            @Override
            public void onUnMessage() {

            }
        });
    }

    /**
     * 推送反馈信息给isDeveloper的设备
     * @param message 反馈信息
     */
    private void sendMessage(String message){
//        BmobPushManager bmobPush = new BmobPushManager(this);
//        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
//        query.addWhereEqualTo("isDeveloper", true);
//        bmobPush.setQuery(query);
//        bmobPush.pushMessage(message);



    }
    private void HttpPostData() {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            String uri = "https://api.bmob.cn/1/push";
            HttpPost httppost = new HttpPost(uri);
            //添加http头信息
            httppost.addHeader("X-Bmob-Application-Id", "95246b6418bbbe7f25241c33f8d414be"); //认证token
            httppost.addHeader("X-Bmob-REST-API-Key", "525aefaa352c3c5195410f87241b1a52"); //认证token
            httppost.addHeader("Content-Type", "application/json");
            //http post的json数据格式：  {"name": "your name","parentId": "id_of_parent"}


            JSONObject obj = new JSONObject();
            obj.put("deviceToken", "");
            JSONObject js = new JSONObject();
            js.put("where",obj);

            JSONObject obj2 = new JSONObject();
            obj2.put("alert", "cashi");
            obj2.put("badge", 1);
            obj2.put("sound", "default");

            JSONObject obj3 = new JSONObject();
            obj3.put("aps", obj2);



            js.put("data",obj3);


            httppost.setEntity(new StringEntity(js.toString()));
            HttpResponse response;
            response = httpclient.execute(httppost);
            //检验状态码，如果成功接收数据
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                String rev = EntityUtils.toString(response.getEntity());//返回json格式： {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}
                Logger.d("推送成功返回的数据"+rev);
                obj = new JSONObject(rev);
//                String id = obj.getString("id");
//                String version = obj.getString("version");
            }else{
                Logger.d("推送失败返回的数据"+code);
            }
        } catch (Exception e) {
            Logger.d("推送失败返回的数据"+e.toString());
        }
    }
    @Override
    public void unLike(final int viewHolderPos, String momentid, final List<Students> currentLikeUserList) {
        likeModel.unLike(momentid, new OnLikeChangeCallback() {
            @Override
            public void onLike() {

            }

            @Override
            public void onUnLike() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(currentLikeUserList)) {
                    resultLikeList.addAll(currentLikeUserList);
                }
                final int localLikePos = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId());
                if (localLikePos > -1) {
                    resultLikeList.remove(localLikePos);
                }
                if (momentView != null) {
                    momentView.onLikeChange(viewHolderPos, resultLikeList);
                }
            }

        });
    }

    @Override
    public void addComment(final int viewHolderPos, Share momentid, Students replyUserid, String commentContent, final List<CommentInfo> currentCommentList) {
        if (TextUtils.isEmpty(commentContent)) return;
        commentModel.addComment(momentid, Constants.getInstance().getUser(), replyUserid, commentContent, new OnCommentChangeCallback() {
            @Override
            public void onAddComment(CommentInfo response) {
                    List<CommentInfo> resultLikeList = new ArrayList<>();
                if (!ToolUtil.isListEmpty(currentCommentList)) {
                    resultLikeList.addAll(currentCommentList);
                }
                resultLikeList.add(response);
                Logger.d("评论成功 >>>  " + response.getAuthor().getNick_name());
                if (momentView != null) {
                    momentView.onCommentChange(viewHolderPos, resultLikeList);
                }
            }

            @Override
            public void onDeleteComment(String response) {

            }
        });
    }

    @Override
    public void deleteComment(final int viewHolderPos, String commentid, final List<CommentInfo> currentCommentList) {
        if (TextUtils.isEmpty(commentid)) return;
        commentModel.deleteComment(commentid, new OnCommentChangeCallback() {
            @Override
            public void onAddComment(CommentInfo response) {

            }

            @Override
            public void onDeleteComment(String commentid) {
                if (TextUtils.isEmpty(commentid)) return;
                List<CommentInfo> resultLikeList = new ArrayList<CommentInfo>();
                if (!ToolUtil.isListEmpty(currentCommentList)) {
                    resultLikeList.addAll(currentCommentList);
                }
                Iterator<CommentInfo> iterator = resultLikeList.iterator();
                while (iterator.hasNext()) {
                    CommentInfo info = iterator.next();
                    if (TextUtils.equals(info.getCommentid(), commentid)) {
                        iterator.remove();
                        break;
                    }
                }
                Logger.i("删除评论成功 >>>  " + commentid);
                if (momentView != null) {
                    momentView.onCommentChange(viewHolderPos, resultLikeList);
                }

            }
        });

    }




    public void showCommentBox(int itemPos, Share momentid, @Nullable CommentWidget commentWidget) {
        if (momentView != null) {
            momentView.showCommentBox(itemPos, momentid, commentWidget);
        }
    }


    /**
     * 从bmobobj列表寻找符合id的位置
     *
     * @return -1:找不到
     */
    private int findPosByObjid(List<? extends BmobObject> objectList, String id) {
        int result = -1;
        if (ToolUtil.isListEmpty(objectList)) return result;
        for (int i = 0; i < objectList.size(); i++) {
            BmobObject object = objectList.get(i);
            if (TextUtils.equals(object.getObjectId(), id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    //------------------------------------------interface impl-----------------------------------------------
}
