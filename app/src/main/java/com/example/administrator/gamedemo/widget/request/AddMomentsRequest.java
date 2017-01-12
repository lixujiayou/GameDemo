package com.example.administrator.gamedemo.widget.request;

import android.text.TextUtils;

import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lixu on 2016/12/13.
 * <p>
 * 添加动态(暂时不处理文件上传)
 */

public class AddMomentsRequest extends BaseRequestClient<String> {

    private Students auth;
    private String hostId;
    private List<Students> likesUserId;
    private String topic;
    private String ps;
    private String rp;
    private int  color;
    private ArrayList<String> answers;

    public AddMomentsRequest() {
        likesUserId = new ArrayList<>();
        answers = new ArrayList<>();
    }

    public AddMomentsRequest setAuth(Students auth) {
        this.auth = auth;
        return this;
    }

    public AddMomentsRequest setHostId(String hostId) {
        this.hostId = hostId;
        return this;
    }
 public AddMomentsRequest setPs(String ps) {
        this.ps = ps;
        return this;
    }

    public AddMomentsRequest setColor(int tColor){
        this.color = tColor;
        return this;
    }

    public AddMomentsRequest setRp(String Rp){
        this.rp = Rp;
        return this;
    }
    /**
     * 设置问题题目
     * @param topic
     * @return
     */
    public AddMomentsRequest setTopic(String topic){
        this.topic = topic;
        return this;
    }

    public AddMomentsRequest setAnswers(ArrayList<String>answers){
        this.answers = answers;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        if (checkValided()) {
            MomentsInfo momentsInfo = new MomentsInfo();

            momentsInfo.setTopic(topic);
            for(String as : answers){
                momentsInfo.addAnswer(as);
            }
            momentsInfo.setHint(ps);  //参考经节
            momentsInfo.setAuthor(auth);//作者
            momentsInfo.setColor(color);//背景

//            UserInfo host = new UserInfo();
//            host.setObjectId(hostId);
//            momentsInfo.setHostinfo(host);

//            momentsInfo.setContent(momentContent);

            momentsInfo.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        if (ToolUtil.isListEmpty(likesUserId)) {
                            onResponseSuccess("提交成功", requestType);
                        } else {
                            MomentsInfo resultMoment = new MomentsInfo();
                            resultMoment.setObjectId(s);
                            onResponseSuccess("提交失败"+s, requestType);

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
                                        Logger.d("添加失败"+e);
                                    }
                                }
                            });
                        }

                    }else{
                        Logger.d("添加失败"+e);
                    }
                }
            });

        }else{
            Logger.d("用户信息失效");
        }
    }


    private boolean checkValided() {
        return !(auth==null);
    }


}
