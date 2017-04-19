package com.example.administrator.gamedemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.adapter.OneAnswerAdapter;
import com.example.administrator.gamedemo.adapter.OnlineAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.ReshEvent;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.MyEditText;
import com.example.administrator.gamedemo.utils.StringUtil;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.LoadingView;
import com.example.administrator.gamedemo.widget.SoftKeyboardStateHelper;
import com.example.administrator.gamedemo.widget.popup.DeleteCommentPopup;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by lixu on 2016/12/14 0014.
 * 在线答题界面
 */
public class OnlineAnswerActivity extends BaseActivity {
    @BindView(R.id.ry_answers)
    RecyclerView ryAnswers;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_result_data)
    TextView tvResultData;


    @BindView(R.id.tv_topic)
    TextView tv_topic;

    @BindView(R.id.ll_write)
    LinearLayout ll_write;

    @BindView(R.id.ed_comment_content)
    MyEditText et_write;

    @BindView(R.id.btn_send)
    TextView tv_send;

//    @BindView(R.id.view_loading)
//    LoadingView loadingView;

//    @BindView(R.id.rl_loading)
//    RelativeLayout rlLoading;

    @BindView(R.id.recyler_comment)
    RecyclerView recyle_comment;

    @BindView(R.id.tv_reminder)
    TextView tv_tv_reminder;

    private OneAnswerAdapter oneAnswerAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManager_comment;
    private OnlineAdapter onlineAdapter;

    private MomentsInfo mMomentsInfo;
    private ArrayList<String> mAnswers = new ArrayList<>();
    private ArrayList<CommentInfo> mCommentInfos = new ArrayList<>();

    private int[] Randoms;
    private DeleteCommentPopup deleteCommentPopup;

    private boolean mIsSelect = false;

    /**
     * 被回复者相关
     */
    private boolean isReply = false;
    private Students personReply;

    //键盘监控
    private SoftKeyboardStateHelper softKeyboardStateHelper;
    private boolean isShow = false;

    private int tNum;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_online);
    }

    @Override
    public void initViews() {

        mMomentsInfo = Constants.getInstance().getMomentsInfo();
        if(mMomentsInfo == null){
            ToastUtil3.showToast(OnlineAnswerActivity.this,"数据异常，请重试");
            finish();
            return;
        }


        initDeleteWidget();

        mLayoutManager = new LinearLayoutManager(this);
        ryAnswers.setLayoutManager(mLayoutManager);
        ryAnswers.setItemAnimator(new DefaultItemAnimator());

        //评论view
        mLayoutManager_comment  = new LinearLayoutManager(this);
        recyle_comment.setLayoutManager(mLayoutManager_comment);
        recyle_comment.setItemAnimator(new DefaultItemAnimator());
        onlineAdapter = new OnlineAdapter(OnlineAnswerActivity.this,mCommentInfos);
        recyle_comment.setAdapter(onlineAdapter);

        mToolbar.setNavigationIcon(R.drawable.icon_cancle_black);
        mToolbar.setTitle(R.string.main_answer);
        mToolbar.setTitleTextColor(ContextCompat.getColor(OnlineAnswerActivity.this,R.color.textcolor_1));
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_write:
                        if(isLogin()) {
                            et_write.setText("");
                            if (mIsSelect = true) {
                                isReply = false;
                                if (Constants.getInstance().isLogin(OnlineAnswerActivity.this)) {
                                    isSelected();
                                }
                            }
                        }else{
                            Intent lIntent = new Intent(OnlineAnswerActivity.this,LoginActivity.class);
                            startActivityForResult(lIntent,1);
                        }
                        break;
                }
                return true;
            }
        });

        //初始化答题
        if (mMomentsInfo != null) {
            tv_topic.setText("\t\t" + mMomentsInfo.getTopic());
            mAnswers.clear();
            mAnswers.addAll(mMomentsInfo.getAnswers());
            Randoms = getRandom(mAnswers.size());
            oneAnswerAdapter = new OneAnswerAdapter(this, mAnswers, Randoms);
            ryAnswers.setAdapter(oneAnswerAdapter);
            oneAnswerAdapter.notifyDataSetChanged();
        }

        //初始化评论
        if(mMomentsInfo.getCommentList() != null){
            mCommentInfos.clear();
            mCommentInfos.addAll(mMomentsInfo.getCommentList());
            onlineAdapter.notifyDataSetChanged();
            tv_tv_reminder.setVisibility(View.GONE);
        }else{
            tv_tv_reminder.setVisibility(View.VISIBLE);
        }

        oneAnswerAdapter.setOnItemClickListener(new OneAnswerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(isLogin()) {
                    isReply = false;
                    isSelected("ღ：" + mAnswers.get(Randoms[position]));
                }else{
                Intent lIntent = new Intent(OnlineAnswerActivity.this,LoginActivity.class);
                startActivityForResult(lIntent,1);
            }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });


        softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.ll_activity_online));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                //键盘打开
                isShow = true;
            }

            @Override
            public void onSoftKeyboardClosed() {
                ll_write.setVisibility(View.GONE);
                isShow = false;
            }
        });

        onlineAdapter.setOnItemClickListener(new OnlineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(isLogin()) {
                    et_write.setText("");
                    CommentInfo commentInfoTemp = mCommentInfos.get(position);
                    personReply = commentInfoTemp.getAuthor();
                        if (!personReply.getObjectId().equals(Constants.getInstance().getUser().getObjectId())) {
                            ppopupEdit(personReply);
                        } else {
                            deleteCommentPopup.showPopupWindow(commentInfoTemp);
                        }
                }else{
                    Intent lIntent = new Intent(OnlineAnswerActivity.this,LoginActivity.class);
                    startActivityForResult(lIntent,1);
                }



            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onSendClick(View view, int position) {

            }

            @Override
            public void onReplyClick(View view, int position) {

            }
        });

        if(isLogin()) {

            isHave();

        }else{
            tv_tv_reminder.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 弹出键盘
     * @param reply
     */
    private void ppopupEdit(Students reply){
        et_write.setText("");
//        if(reply != null){
            et_write.setHint("回复："+reply.getNick_name());
            isReply = true;
//        }else{
//            et_write.setHint("讨论");
//        }
        //获取焦点
        ll_write.setVisibility(View.VISIBLE);
        et_write.setFocusable(true);
        et_write.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et_write, InputMethodManager.SHOW_IMPLICIT);
    }


    @Override
    public void initData() {
    }

    //获取随机数
    private int[] getRandom(int num) {
        int[] randoms = new int[num];
        Vector<Integer> vector = new Vector<>();
        for (int i = 0; i < num; i++) {
            int random = (int) (Math.random() * num);
            while (vector.contains(random)) {
                random = (int) (Math.random() * num);
            }
            vector.add(random);
            randoms[i] = random;
        }
        return randoms;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    /**
     * 评论
     */
    private CommentInfo momentContent;
    private void sendContent(String content) {

        //使键盘消失
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        ll_write.setVisibility(View.GONE);
        showProgressBarDialog(OnlineAnswerActivity.this,"正在提交数据...");

        momentContent = new CommentInfo();
        momentContent.setContent(content);
        momentContent.setAuthor(mUser);
        if(isReply){
            momentContent.setReply(personReply);
        }
        momentContent.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    mMomentsInfo.addComment(momentContent);
                    mMomentsInfo.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            pDialog.dismiss();
                            tv_tv_reminder.setVisibility(View.GONE);

                            if(e == null){
                                mCommentInfos.add(momentContent);
                                onlineAdapter.notifyDataSetChanged();
                                EventBus.getDefault().post(new ReshEvent(ReshEvent.ReshOk));
                            }else{
                                ToastUtil3.showToast(OnlineAnswerActivity.this, "评论失败" + e);
                            }
                        }
                    });
                } else {
                    pDialog.dismiss();
                    ToastUtil3.showToast(OnlineAnswerActivity.this, "评论失败" + s);
                }
            }
        });

    }

    private void setAnswerNum(){
        mMomentsInfo.setAw_num(tNum+1);
        mMomentsInfo.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
            }
        });
    }

    /**
     * 删除
     */
    private void initDeleteWidget(){

        if (deleteCommentPopup == null) {
            deleteCommentPopup = new DeleteCommentPopup(this);
        }

        deleteCommentPopup.setOnDeleteCommentClickListener(new DeleteCommentPopup.OnDeleteCommentClickListener() {
            @Override
            public void onDelClick(final CommentInfo commentInfo) {
                deleteCommentPopup.dismiss();
                showProgressBarDialog(OnlineAnswerActivity.this,"正在删除...");
                mMomentsInfo.removeComment(commentInfo);
                mMomentsInfo.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        pDialog.dismiss();
                        if(e == null){
                            EventBus.getDefault().post(new ReshEvent(ReshEvent.ReshOk));
                            mCommentInfos.remove(commentInfo);
                            onlineAdapter.notifyDataSetChanged();
                        }else{
                            ToastUtil3.showToast(OnlineAnswerActivity.this, "删除失败" + e);
                        }
                    }
                });
            }
        });
    }

    /**
     * 选择答题 并进行关联
     */
    private void selectAnswer(final String sContent){
        showProgressBarDialog(OnlineAnswerActivity.this,"正在提交答案...");
        BmobRelation relation = new BmobRelation();
        relation.add(Constants.getInstance().getUser(OnlineAnswerActivity.this));
        mMomentsInfo.setLikes(relation);
        mMomentsInfo.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    pDialog.dismiss();
                    mIsSelect = true;
                    sendContent(sContent);

                    recyle_comment.setVisibility(View.VISIBLE);
                    tvResultData.setVisibility(View.VISIBLE);
                    tvResultData.setText("\t\t参考:" + mMomentsInfo.getHint()+"\n\t\t正确答案:"+mMomentsInfo.getAnswers().get(0));
                    setAnswerNum();
                }else{
                    mIsSelect = false;
                    pDialog.dismiss();
                    ToastUtil3.showToast(OnlineAnswerActivity.this,"选择失败"+e);
                }
            }
        });
    }

    /**
     * 是否选择过
     */
    private void isSelected(final String sAnswer){
        tNum = 0;
        showProgressBarDialog(OnlineAnswerActivity.this,"正在初始化数据...");
        BmobQuery<Students> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(mMomentsInfo));
        query.findObjects(new FindListener<Students>() {
            @Override
            public void done(List<Students> object, BmobException e) {
                pDialog.dismiss();
                if(e==null){
                    Students students = Constants.getInstance().getUser(OnlineAnswerActivity.this);

                        for(Students s : object){
                            if(students.getObjectId().equals(s.getObjectId())){
                                ToastUtil3.showToast(OnlineAnswerActivity.this,"您已答过此题,看看讨论吧");
                                tvResultData.setVisibility(View.VISIBLE);
                                tvResultData.setText("\t\t参考:" + mMomentsInfo.getHint()+"\n\t\t正确答案:"+mMomentsInfo.getAnswers().get(0));
                                mIsSelect = true;
                                pDialog.dismiss();
                                return;
                            }
                        }
                        tNum = object.size();
                        showAlertDialog(sAnswer);

                }else{
                    ToastUtil3.showToast(OnlineAnswerActivity.this,e.toString());
                    pDialog.dismiss();
                }
            }
        });
    }



    private void isHave(){
        tNum = 0;

        //showProgressBarDialog(OnlineAnswerActivity.this);

        BmobQuery<Students> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(mMomentsInfo));
        query.findObjects(new FindListener<Students>() {
            @Override
            public void done(List<Students> object, BmobException e) {
                if(e==null){
                    Students students = Constants.getInstance().getUser();
                    recyle_comment.setVisibility(View.GONE);
                        for(Students s : object){
                            if(students.getObjectId().equals(s.getObjectId())){
                                tvResultData.setVisibility(View.VISIBLE);
                                tvResultData.setText("\t\t参考:" + mMomentsInfo.getHint()+"\n\t\t正确答案:"+mMomentsInfo.getAnswers().get(0));
                                mIsSelect = true;

                                if(pDialog != null) {
                                    pDialog.dismiss();
                                }

                                recyle_comment.setVisibility(View.VISIBLE);
                                tv_tv_reminder.setVisibility(View.GONE);
                                return;
                            }
                        }

                    tNum = object.size();
                    tvResultData.setVisibility(View.GONE);

                    tv_tv_reminder.setVisibility(View.VISIBLE);

                }else{
                    ToastUtil3.showToast(OnlineAnswerActivity.this,"哎呀，系统发呆啦");
                    Logger.d("哎呀"+e.toString());
                    pDialog.dismiss();
                }
            }
        });
    }

    /**
     * 是否选择过
     */
    private void isSelected(){
        et_write.setText("");
        tNum = 0;
        showProgressBarDialog(OnlineAnswerActivity.this,"正在初始化数据...");
        BmobQuery<Students> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(mMomentsInfo));
        query.findObjects(new FindListener<Students>() {
            @Override
            public void done(List<Students> object, BmobException e) {
                if(e==null){
                    Students students = Constants.getInstance().getUser(OnlineAnswerActivity.this);
                        for(Students s : object){
                            if(students.getObjectId().equals(s.getObjectId())){
                                tvResultData.setVisibility(View.VISIBLE);
                                tvResultData.setText("\t\t参考:" + mMomentsInfo.getHint()+"\n\t\t正确答案:"+mMomentsInfo.getAnswers().get(0));
                                mIsSelect = true;
                                pDialog.dismiss();

                                isReply = false;
                                    //获取焦点
                                    ll_write.setVisibility(View.VISIBLE);
                                    et_write.setFocusable(true);
                                    et_write.requestFocus();

                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.showSoftInput(et_write, InputMethodManager.SHOW_IMPLICIT);

                                return;
                            }
                        }

                        ToastUtil3.showToast(OnlineAnswerActivity.this,"请答题后再发起讨论哦");
                        tNum = object.size();

                    if(pDialog != null){
                        pDialog.dismiss();
                    }
                }else{
                    ToastUtil3.showToast(OnlineAnswerActivity.this,e.toString());
                    pDialog.dismiss();
                }
            }
        });
    }

    @OnClick(R.id.btn_send)
    public void onClick() {
        String wContentTemp = et_write.getText().toString().trim();
        if(!StringUtil.noEmpty(wContentTemp)){
            ToastUtil3.showToast(OnlineAnswerActivity.this,"评论不能为空");
            return;
        }

        sendContent(wContentTemp);
    }


    @Override
    public void onBackPressed() {
        if(!isShow) {
            if(ll_write.getVisibility() == View.GONE){
                setResult(Constants.REFRESH_CODE);
                finish();
            }else{
                //使键盘消失
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                ll_write.setVisibility(View.GONE);
                isShow = false;
            }

        }else{
            //使键盘消失
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            ll_write.setVisibility(View.GONE);
            isShow = false;
        }
    }

    private void showAlertDialog(final String sAnswer){
        pDialog.dismiss();
        new AlertDialog.Builder(OnlineAnswerActivity.this)
                .setTitle("提示")
                .setMessage("您的选择将会被同步到讨论中,是否继续")
                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      //  showProgressBarDialog(OnlineAnswerActivity.this,"正在提交答案...");
                        selectAnswer(sAnswer);
                        dialogInterface.dismiss();
                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mIsSelect = false;
                        pDialog.dismiss();
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
    private SweetAlertDialog pDialog;
    public void showProgressBarDialog(final Activity mContext,String msg){
        try {
                pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText(msg);
                pDialog.setCancelable(true);
                pDialog.show();
        }catch (Exception e){
            Logger.d("ProgressBarDialog的上下文找不到啦！");
        }
    }

}
