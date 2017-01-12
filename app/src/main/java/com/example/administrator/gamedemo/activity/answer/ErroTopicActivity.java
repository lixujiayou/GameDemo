package com.example.administrator.gamedemo.activity.answer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.model.AnswerHistory;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.TopicUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @auther lixu
 * Created by lixu on 2017/1/11 0011.
 */
public class ErroTopicActivity extends BaseActivity{

    public final static String INTENT_HISTORY = "history";

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    @BindView(R.id.rl_hint)
    RelativeLayout rl_hint;


    private UploadAdapter uploadAdapter;
    private AnswerHistory mAnswerHistory;
    private LinearLayoutManager mLayoutManager;
    private String[] erroSub;//错题下标
    private String erroType;//答题类型
    private String[] erroSelect;//选择的错误选项

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_erro);
    }

    @Override
    public void initViews() {
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        mToolbar.setTitle("本次错题");

        Intent gIntent = getIntent();
        mAnswerHistory = (AnswerHistory) gIntent.getSerializableExtra(INTENT_HISTORY);

        if(mAnswerHistory.getErro() != null){

            Logger.d("erro="+mAnswerHistory.getErro().toString());
            Logger.d("erroS="+mAnswerHistory.getErroSelect().toString());

        if(!mAnswerHistory.getErro().toString().isEmpty()){
            rl_hint.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            erroType = mAnswerHistory.getType();
            erroSub = mAnswerHistory.getErro().split(",");
            erroSelect = mAnswerHistory.getErroSelect().split("&");
        }else{
            rl_hint.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        }else{
            rl_hint.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {
        mLayoutManager = new LinearLayoutManager(ErroTopicActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        uploadAdapter = new UploadAdapter(erroSub,erroSelect);
        recyclerView.setAdapter(uploadAdapter);
    }

    class UploadAdapter extends RecyclerView.Adapter{
         private ItemViewHolder itemViewHolder;
         private String[] mErroSub;
         private String[] mErroSelect;
        public UploadAdapter(String[] erroSub, String[] erroSelect) {
            this.mErroSub = erroSub;
            this.mErroSelect = erroSelect;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_my_erro, parent,
                    false);
            itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            setText(((ItemViewHolder)holder).title
                    ,((ItemViewHolder)holder).tv_correct
                    ,((ItemViewHolder)holder).tv_erro_1
                    ,((ItemViewHolder)holder).tv_erro_2
                    ,((ItemViewHolder)holder).tv_erro_3
                    ,mErroSub[position]
                    ,mErroSelect[position]);
        }

        @Override
        public int getItemCount() {
            return mErroSub.length == 0 ? 0 : mErroSub.length;
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView title;     //
        protected TextView tv_correct; //

        protected TextView tv_erro_1;
        protected TextView tv_erro_2;
        protected TextView tv_erro_3;

        public ItemViewHolder(View view) {
            super(view);
            title = (TextView) findView(title, R.id.tv_erro_title);
            tv_correct = (TextView) findView(tv_correct, R.id.tv_erro_a1);
            tv_erro_1 = (TextView) findView(tv_erro_1, R.id.tv_erro_a2);
            tv_erro_2 = (TextView) findView(tv_erro_2, R.id.tv_erro_a3);
            tv_erro_3 = (TextView) findView(tv_erro_3, R.id.tv_erro_a4);
        }

        protected final View findView(View view, int resid) {
            if (resid > 0 && itemView != null && view == null) {
                return itemView.findViewById(resid);
            }
            return view;
        }
    }



    private void setText(TextView mTitle, TextView m1, TextView m2, TextView m3, TextView m4, String sub,String mSelect){
        int cSub = Integer.parseInt(sub);
        String[] pArrayList;
        List<String[]> aList;
        String[] aArrayList;
        if(erroType.equals("四福音")){
            pArrayList = getResources().getStringArray(R.array.problem_sfy);
            aList = TopicUtil.initDate_SFY(ErroTopicActivity.this);
        }else if(erroType.equals("摩西五经")){
            pArrayList = getResources().getStringArray(R.array.problem_mxwj);
            aList = TopicUtil.initDate_MXWJ(ErroTopicActivity.this);
         }else if(erroType.equals("历史诗歌书")){
            pArrayList = getResources().getStringArray(R.array.problem_lssg);
            aList = TopicUtil.initDate_LSSG(ErroTopicActivity.this);
        }else if(erroType.equals("大小先知书")){
            pArrayList = getResources().getStringArray(R.array.problem_xz);
            aList = TopicUtil.initDate_XZ(ErroTopicActivity.this);
        }else if(erroType.equals("新约书信")){
            pArrayList = getResources().getStringArray(R.array.problem_sx);
            aList = TopicUtil.initDate_SX(ErroTopicActivity.this);
        }else if(erroType.equals("圣经")){
            pArrayList = getResources().getStringArray(R.array.problem_1);
            aList = TopicUtil.initDate(ErroTopicActivity.this);
        }else{
            ToastUtil3.showToast(ErroTopicActivity.this,"本次答题类型未知，无法查看记录");
            return;
        }

        aArrayList = aList.get(cSub);
        mTitle.setText(pArrayList[cSub]);
        for(int i = 0;i < aArrayList.length;i++){
            if(i == 0){
                m1.setText(aArrayList[i]);
            }else if(i == 1){
                m2.setText(aArrayList[i]);
                if(aArrayList[i].equals(mSelect)){
                    m2.setTextColor(ContextCompat.getColor(ErroTopicActivity.this,R.color.red));
                }else{
                    m2.setTextColor(ContextCompat.getColor(ErroTopicActivity.this,R.color.white));
                }
            }else if(i == 2){
                m3.setText(aArrayList[i]);
                if(aArrayList[i].equals(mSelect)){
                    m3.setTextColor(ContextCompat.getColor(ErroTopicActivity.this,R.color.red));
                }else{
                    m3.setTextColor(ContextCompat.getColor(ErroTopicActivity.this,R.color.white));
                }
            }else if(i == 3){
                m4.setText(aArrayList[i]);
                if(aArrayList[i].equals(mSelect)){
                    m4.setTextColor(ContextCompat.getColor(ErroTopicActivity.this,R.color.red));
                }else{
                    m4.setTextColor(ContextCompat.getColor(ErroTopicActivity.this,R.color.white));
                }
            }
        }
    }
}
