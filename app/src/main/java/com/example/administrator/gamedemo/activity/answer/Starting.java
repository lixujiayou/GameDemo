package com.example.administrator.gamedemo.activity.answer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.utils.TopicUtil;
import com.example.administrator.gamedemo.widget.database.Sq;
import com.orhanobut.logger.Logger;


import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016-01-26.
 */

public class Starting extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private final int mytime = 25;
    private List<String[]> answers = new ArrayList<>();
    private List<String[]> answers_temp = new ArrayList<>();
    private List<String[]> problems = new ArrayList<>();
    private int erroNum = 0;
    private GridView answers_gv;
    private TextView problems_tv,problems_skip,count_down,my_choice,verify,currentMyScore,tv_help,tv_starting_main;
    private  ProgressBar alwaysplan;
    private  ProgressBar alwaysplan_2;
    private CDHandler CH = new CDHandler();
    //首发个数
    private int first_publish = 15;
    private answersAdapter aAdapter;
    //问题的出场顺序
    private int[] sequence;
    private int[] aw_sq;
    DialogInterface.OnKeyListener keylistener;
    private int currentNum = 0;
    private int currentNum_2 = 0;
    private int count_down_num =25;
    private TimerTask timerTask;
    private Timer timer;
    private SweetAlertDialog awtrue;
    private SweetAlertDialog awfalse;
    private SweetAlertDialog awTimeover;
    //选项是否移动
    private boolean isMove = false;
    //我的得分
    private int myScore = 0;
    //计算连续对题的个数
    private int cts_right_num = 1;
    //略过扣的分数
    private int skipcore = 50;
    private int Tnum = 1;
    //计算连续错题的个数
    private int cts_false_num = -1;
    private int myScore_temp = 0;
   private SweetAlertDialog.OnSweetClickListener nextF;
    private SweetAlertDialog.OnSweetClickListener nextT;
    private SweetAlertDialog.OnSweetClickListener nextTimeover;
    private SweetAlertDialog.OnSweetClickListener nextTimeover_2;
    private SweetAlertDialog back_;
    private Dialog gameover_d;
    private Dialog dialog_help;
    private TextView bt_help1,bt_help2;
    private int num_help1 = 2;
    private int num_help2 = 1;
    private MediaPlayer starting_m = new MediaPlayer();
    private MediaPlayer timesmall_m = new MediaPlayer();
    private MediaPlayer timeover_m = new MediaPlayer();
    private MediaPlayer right1_m = new MediaPlayer();
    private MediaPlayer right2_m = new MediaPlayer();
    private MediaPlayer right3_m = new MediaPlayer();
    private MediaPlayer right4_m = new MediaPlayer();
    private MediaPlayer right66_m = new MediaPlayer();
    private MediaPlayer click_m = new MediaPlayer();
    private MediaPlayer result_m = new MediaPlayer();
    private SQLiteDatabase db;
    private Sq s;
    private String[] pb;
    private int type_num;
    private String cType = "未知";
    private String mErroNum = "";
    private String mErroSelect = "";

    private LinearLayout tv_actionbar_line;
    private ImageView iv_anim;

    private int mCurrentSub = 0;

    //整体布局，动画使用
    private RelativeLayout rl_main_anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

    initView();
    initOnClick();
    inittime();
    initMusic();

    Intent inte = getIntent();
     type_num  = inte.getExtras().getInt("type");
    boolean aa = inte.getExtras().getBoolean("yinliang");
    if (aa == false) {
        initMusicJingYin();
    }
    switch (type_num){
        case 2:
            answers.addAll(TopicUtil.initDate_MXWJ(Starting.this));
            pb = getResources().getStringArray(R.array.problem_mxwj);
            tv_starting_main.setText("正在答题：摩西五经");
            cType = "摩西五经";
            break;
        case 4:
            answers.addAll(TopicUtil.initDate_LSSG(Starting.this));
            pb= getResources().getStringArray(R.array.problem_lssg);
            tv_starting_main.setText("正在答题：历史诗歌书");
            cType = "历史诗歌书";
            break;
        case 3:
            answers.addAll(TopicUtil.initDate_XZ(Starting.this));
            pb= getResources().getStringArray(R.array.problem_xz);
            tv_starting_main.setText("正在答题：大小先知书");
            cType = "大小先知书";
            break;
        case 0:
            answers.addAll(TopicUtil.initDate_SFY(Starting.this));
            pb= getResources().getStringArray(R.array.problem_sfy);
            tv_starting_main.setText("正在答题：四福音");
            cType = "四福音";
            break;
        case 1:
            answers.addAll(TopicUtil.initDate_SX(Starting.this));
            pb= getResources().getStringArray(R.array.problem_sx);
            tv_starting_main.setText("正在答题：新约书信");
            cType = "新约书信";
            break;
        case 5:
            answers.addAll(TopicUtil.initDate(Starting.this));
            pb= getResources().getStringArray(R.array.problem_1);
            tv_starting_main.setText("正在答题：圣经");
            cType = "圣经";
            break;
    }

    problems.add(pb);
    sequence = getRandom(pb.length);

    answers_temp.clear();
    answers_temp.add(answers.get(sequence[0]));
    problems_tv.setText(problems.get(0)[sequence[0]]);


    alwaysplan.setMax(first_publish);

    alwaysplan_2.setMax(first_publish-1);
    alwaysplan.setProgress(1);
    alwaysplan_2.setProgress(0);
    aAdapter = new answersAdapter(answers.get(sequence[0]));
    answers_gv.setAdapter(aAdapter);
    initnext();

    timer.schedule(timerTask, 0, 1000);
    count_down.setVisibility(View.VISIBLE);

    s = new Sq(Starting.this, "USER1.db", null, 1);

    db = s.getWritableDatabase();
    keylistener = new SweetAlertDialog.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };

        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tv_actionbar_line.setVisibility(View.VISIBLE);
            // tv_actionbar_line.getBackground().setAlpha(0);
            int statusHeight= getStatusBarHeight();
            LayoutParams lp =tv_actionbar_line.getLayoutParams();
            lp.height =statusHeight;
        }

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate_anim);
        iv_anim.startAnimation(animation);
    }

    /**
     * 获取状态栏的高度
     * @return
     */
    private int getStatusBarHeight(){
        try
        {
            Class<?> c=Class.forName("com.android.internal.R$dimen");
            Object obj=c.newInstance();
            Field field=c.getField("status_bar_height");
            int x=Integer.parseInt(field.get(obj).toString());
            return  getResources().getDimensionPixelSize(x);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void initView(){
        tv_actionbar_line = (LinearLayout) findViewById(R.id.activity_starting_ll_c);
        iv_anim = (ImageView) findViewById(R.id.activity_starting_iv_backround);
        rl_main_anim = (RelativeLayout) findViewById(R.id.rl_main_main);
        answers_gv = (GridView) findViewById(R.id.starting_gv);
        problems_tv = (TextView) findViewById(R.id.starting_problem);
        problems_skip = (TextView) findViewById(R.id.starting_skip);
        alwaysplan = (ProgressBar) findViewById(R.id.starting_pb);
        alwaysplan_2 = (ProgressBar) findViewById(R.id.starting_pb_2);
        count_down = (TextView) findViewById(R.id.count_down);
        my_choice = (TextView) findViewById(R.id.tv_nullselect);
        verify = (TextView) findViewById(R.id.tv_ok);
        problems_tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        currentMyScore = (TextView) findViewById(R.id.tv_current_score);
        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_starting_main = (TextView) findViewById(R.id.starting_tv_main);
    }

    public void initOnClick(){
        problems_skip.setOnClickListener(this);
        my_choice.setOnClickListener(this);
        tv_help.setOnClickListener(this);
        answers_gv.setOnItemClickListener(this);
        verify.setOnClickListener(this);
    }

    //获取随机数
    private int[] getRandom(int num){
        int[] randoms = new int[num];
        Vector<Integer> vector = new Vector<>();
        for(int i = 0;i<num;i++){
            int random = (int) (Math.random()*num);
            while (vector.contains(random)){
                random = (int) (Math.random()*num);
            }
            vector.add(random);
            randoms[i] = random;
        }
        return randoms;
    }
    public void inittime(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                CH.sendEmptyMessage(0);
            }
        };
        timer = new Timer();
    }
    public void initMusic(){
        starting_m = MediaPlayer.create(Starting.this, R.raw.gaming);
        timesmall_m = MediaPlayer.create(Starting.this, R.raw.timeover);
        timeover_m = MediaPlayer.create(Starting.this, R.raw.timeover_);
        right1_m = MediaPlayer.create(Starting.this, R.raw.lianji101);
        right2_m = MediaPlayer.create(Starting.this, R.raw.lianji102);
        right3_m = MediaPlayer.create(Starting.this, R.raw.lianji103);
        right4_m = MediaPlayer.create(Starting.this, R.raw.lianji104);
        right66_m = MediaPlayer.create(Starting.this, R.raw.congraduate_sound);
        click_m = MediaPlayer.create(Starting.this, R.raw.click_sound);
        result_m = MediaPlayer.create(Starting.this, R.raw.jiesuan01);

    }
    public void initMusicJingYin(){
        starting_m.setVolume(0,0);
        timesmall_m.setVolume(0,0);
        timeover_m.setVolume(0,0);
        right1_m.setVolume(0,0);
        right2_m.setVolume(0,0);
        right3_m.setVolume(0,0);
        right4_m.setVolume(0,0);
        right66_m.setVolume(0,0);
        click_m.setVolume(0,0);
        result_m.setVolume(0,0);
    }

    public void initnext(){
    nextF = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {

            if(mErroNum.isEmpty()){
                mErroNum = sequence[mCurrentSub]+"";
            }else {
                mErroNum = mErroNum+"," + sequence[mCurrentSub];
            }

            mCurrentSub += 1;


            if(currentNum != first_publish) {

                awfalse.dismiss();
                Animation anim = AnimationUtils.loadAnimation(Starting.this,
                        R.anim.flip_horizontal_in);
                rl_main_anim.startAnimation(anim);

                if(erroNum>1){
                    dialogGameOver();
                }else {
                    count_down_num = mytime;
                    ReTime();
                    problems_tv.setText(problems.get(0)[sequence[currentNum]]);
                    aAdapter.updateListView(answers.get(sequence[currentNum]));
                    answers_temp.clear();
                    answers_temp.add(answers.get(sequence[currentNum]));
                    alwaysplan.setProgress(currentNum + 1);

                    my_choice.setText("未选择");

                }
            }
            else{
                awfalse.dismiss();
                dialogGameOver();
            }
        }
    };

    nextT = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            mCurrentSub += 1;
            if(currentNum != first_publish) {

                Animation anim = AnimationUtils.loadAnimation(Starting.this,
                        R.anim.flip_horizontal_in);
                rl_main_anim.startAnimation(anim);
                count_down_num = mytime;

                problems_tv.setText(problems.get(0)[sequence[currentNum]]);
                aAdapter.updateListView(answers.get(sequence[currentNum]));
                answers_temp.clear();
                answers_temp.add(answers.get(sequence[currentNum]));

                alwaysplan.setProgress(currentNum + 1);
                currentNum_2 += 1;
                alwaysplan_2.setProgress(currentNum_2);

                CountDown();
                count_down.setVisibility(View.VISIBLE);
                my_choice.setText("未选择");
                awtrue.dismiss();
                ReTime();
            }
            else{
                awtrue.dismiss();
                dialogGameOver();
            }
        }
    };
    nextTimeover = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            mCurrentSub += 1;
            if(currentNum != first_publish) {
                awTimeover.dismiss();

                Animation anim = AnimationUtils.loadAnimation(Starting.this,
                        R.anim.flip_horizontal_in);
                rl_main_anim.startAnimation(anim);

                count_down_num = mytime;

                problems_tv.setText(problems.get(0)[sequence[currentNum]]);
                aAdapter.updateListView(answers.get(sequence[currentNum]));
                answers_temp.clear();
                answers_temp.add(answers.get(sequence[currentNum]));
                alwaysplan.setProgress(currentNum + 1);
                my_choice.setText("未选择");
                ReTime();
                alwaysplan_2.setProgress(currentNum_2+1);
            }
            else{
                awTimeover.dismiss();
                dialogGameOver();
            }
        }
    };

    nextTimeover_2 = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            if(mErroNum.isEmpty()){
                mErroNum = sequence[mCurrentSub]+"";
            }else {
                mErroNum = mErroNum+"," + sequence[mCurrentSub];
            }
            mCurrentSub += 1;
            if(currentNum != first_publish) {
                awTimeover.dismiss();
                Animation anim = AnimationUtils.loadAnimation(Starting.this,
                        R.anim.flip_horizontal_in);
                rl_main_anim.startAnimation(anim);


                if(erroNum>1){
                    dialogGameOver();
                }else {
                    count_down_num = mytime;
                    ReTime();
                    problems_tv.setText(problems.get(0)[sequence[currentNum]]);
                    aAdapter.updateListView(answers.get(sequence[currentNum]));
                    answers_temp.clear();
                    answers_temp.add(answers.get(sequence[currentNum]));
                    currentNum+=1;
                    alwaysplan.setProgress(currentNum);
                    my_choice.setText("未选择");
                }

            }
            else{
                awTimeover.dismiss();
                dialogGameOver();
            }
        }
    };
}
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.starting_skip:
                click_m.start();
                if(currentNum == first_publish - 1){
                    Toast.makeText(Starting.this,"本题是最后一题",Toast.LENGTH_SHORT).show();
                    timeover_m.start();
                }
                else {
                    mCurrentSub += 1;
                    currentNum += 1;
                    cts_right_num += 1;
                    Tnum = 1;
                    myScore -= skipcore;
                    problems_tv.setText(problems.get(0)[sequence[currentNum]]);
                    aAdapter.updateListView(answers.get(sequence[currentNum]));
                    answers_temp.clear();
                    answers_temp.add(answers.get(sequence[currentNum]));
                    alwaysplan.setProgress(currentNum + 1);
                    CountDown();
                    count_down.setVisibility(View.VISIBLE);
                    my_choice.setText("未选择");
                    currentMyScore.setText(myScore+"");
                }
                break;
            case R.id.tv_help:
                click_m.start();
                showHelpDialog();
                break;
            case R.id.tv_ok:
                if(my_choice.getText().toString().equals("未选择")){
                    Toast.makeText(Starting.this,"还未选择任何答案",Toast.LENGTH_SHORT).show();
                    timeover_m.start();
                }
                else{
                    myScore_temp = myScore;
                    musicLooping();
                    timer.cancel();
                    timerTask.cancel();
                    currentNum += 1;
                    if(my_choice.getText().equals(answers_temp.get(0)[0])) {
                       if(Tnum ==1) {
                           myScore += 100;
                           right1_m.start();
                       }else{
                           myScore += (100*Tnum);
                           if(Tnum ==2){
                               right2_m.start();
                           }else if(Tnum ==3){
                               right3_m.start();
                           }else if(Tnum ==4){
                               right4_m.start();
                           }else{
                               right66_m.start();
                           }
                       }
                        if(erroNum>0) {
                            erroNum -= 1;
                        }else{
                            erroNum=0;
                        }

                        Animation anim = AnimationUtils.loadAnimation(Starting.this,
                                R.anim.small_2_big);
                        rl_main_anim.startAnimation(anim);
                        awtrue = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("嗯哼Good job!");
                        awtrue.setCancelable(false);

                        awtrue.setContentText(myScore_temp + " + " + (myScore - myScore_temp) + " = " + myScore);
                        awtrue.setConfirmClickListener(nextT);
                        awtrue.show();

                        currentMyScore.setText(myScore+ "");
                        cts_right_num += 1;
                        cts_false_num = -1;
                        Tnum += 1;

                    }

                    else{
                        if(mErroSelect.isEmpty()){
                            mErroSelect = my_choice.getText().toString();
                        }else{
                            mErroSelect = mErroSelect+"&"+my_choice.getText().toString();
                        }
                        Animation anim_3 = AnimationUtils.loadAnimation(Starting.this,
                                R.anim.flip_horizontal_out);
                        rl_main_anim.startAnimation(anim_3);
                        timeover_m.start();
                        timesmall_m.stop();
                        starting_m.pause();
                        if(cts_right_num <= 2){
                            if(cts_false_num < -1){
                                myScore -= (2*Math.abs(cts_false_num)+cts_false_num)*90;
                            }

                            else{
                                myScore -= 90;
                            }
                        }else{
                            myScore -= 90*(cts_right_num-1);
                        }
                        erroNum+=1;

                        awfalse = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("正确答案："+answers_temp.get(0)[0]);
                        awfalse.setContentText(myScore_temp+" + "+(myScore-myScore_temp)+" = "+myScore);
                        awfalse.setConfirmClickListener(nextF)
                                .setCancelable(false);
                        awfalse.setConfirmText("记住啦");
                        awfalse.show();
                        cts_right_num = 1;
                        cts_false_num -= 1;
                        currentMyScore.setText(myScore+"");
                        Tnum = 1;
                    }
                }
                break;

            case R.id.bt_help1:
                if(num_help1 > 0) {
                    num_help1 -= 1;
                    hi.tv_help1.setText("重新计时(" + num_help1 + ")");
                    count_down_num = mytime;
                    dialog_help.dismiss();
                    click_m.start();
                    if(num_help1 == 0){
                        hi.tv_help1.setBackground(getDrawable(R.drawable.photo_gallery_selector));
                    }
                }else {
                    dialog_help.dismiss();
                }
                break;
            case R.id.bt_help2:
                if(num_help2 > 0) {
                    click_m.start();
                    num_help2 -= 1;
                    hi.tv_help2.setText("去掉两个错误答案(" + num_help2 + ")");
                    answers.get(sequence[currentNum])[1] = " ";
                    answers.get(sequence[currentNum])[2] = " ";
                    aAdapter.updateListView(answers.get(sequence[currentNum]));
                    dialog_help.dismiss();
                    if(num_help2 == 0){
                        hi.tv_help2.setBackground(ContextCompat.getDrawable( Starting.this,R.drawable.photo_camera_selector));
                    }
                    dialog_help.dismiss();
                }else {
                    dialog_help.dismiss();
                }

                break;
            case R.id.bt_helpcancle:
                click_m.start();
                dialog_help.dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                click_m.start();
                TextView newTextView = (TextView) view.findViewById(R.id.answer_item_tv);
                final int[] startLocation = new int[2];
                newTextView.getLocationInWindow(startLocation);

                String str = (String) (parent.getAdapter()).getItem(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int[] endLocation = new int[2];
                        my_choice.getLocationInWindow(endLocation);
                        //得到要移动的VIEW,并放入对应的容器中
                        final ViewGroup moveViewGroup = getMoveViewGroup();
                        final ImageView moveImageView = getView(view);
                        MoveAnim(moveImageView, startLocation, endLocation, aw_sq[position], answers_gv);
                    }
                }, 50L);


    }



    class answersAdapter extends BaseAdapter{
        String[] a;
        int[] answerNum;
        public answersAdapter(String[] a){
            this.a = a;
            this.answerNum = getRandom(a.length);
            aw_sq = answerNum;
        }
        public void updateListView(String[] a){
            this.a = a;
            this.answerNum = getRandom(a.length);
            aw_sq = answerNum;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return a.length;
        }

        @Override
        public Object getItem(int position) {
            return a[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            answerItem ai = null;
            if(ai == null){
                ai = new answerItem();
                convertView = getLayoutInflater().inflate(R.layout.text_anwser_gvitem,null);
                ai.answer = (TextView) convertView.findViewById(R.id.answer_item_tv);
                convertView.setTag(ai);
            }
            else{
                ai = (answerItem) convertView.getTag();
            }
            ai.answer.setText(a[answerNum[position]]);
            return convertView;
        }
    }
    class answerItem{
        private TextView answer;
    }

    public void CountDown(){
        count_down_num = mytime;
    }
    class CDHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(count_down_num == 0){
                count_down_num = -1;
                timer.cancel();
                timerTask.cancel();
                timeStop_dialog();
            }
            else if(count_down_num > 0){

                if(count_down_num == 11){
                    starting_m.pause();
                    try {
                        timesmall_m.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timesmall_m.start();
                }
                if(count_down_num > 11){
                    musicLooping();
                }
                count_down_num -= 1;
                count_down.setVisibility(View.VISIBLE);
                count_down.setText(count_down_num+"");
            }
        }
    }
    public void musicLooping(){
        starting_m.start();
        starting_m.setLooping(true);
        timesmall_m.stop();
    }

boolean timeoverb = true;
public void timeStop_dialog(){
    myScore_temp = myScore;
        if(my_choice.getText().equals(answers_temp.get(0)[0])) {

            Animation anim = AnimationUtils.loadAnimation(Starting.this,
                    R.anim.small_2_big);
            rl_main_anim.startAnimation(anim);

            timeoverb = true;
            if (cts_right_num > 1) {
                myScore += (100 * cts_right_num);
                if(cts_right_num ==2){
                    right2_m.start();
                }else if(cts_right_num ==3){
                    right3_m.start();
                }else if(cts_right_num ==4){
                    right4_m.start();
                }else{
                    right66_m.start();
                }
            } else {
                myScore += 100;
                right1_m.start();
            }
            cts_right_num += 1;
            currentNum += 1;
            if(erroNum>0){
                erroNum-=1;
            }else{
                erroNum=0;
            }
            awTimeover = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("时间结束TIME IS OVER")
                    .setContentText(myScore_temp+" + "+(myScore-myScore_temp)+" = "+myScore)
                    .setConfirmText("继续答题CHEER UP")
                    .setConfirmClickListener(nextTimeover);
            awTimeover.setCancelable(false);
            awTimeover.show();
        }
        else{
            if(mErroSelect.isEmpty()){
                mErroSelect = my_choice.getText().toString();
            }else{
                mErroSelect = mErroSelect+"&"+my_choice.getText().toString();
            }
            Animation anim_3 = AnimationUtils.loadAnimation(Starting.this,
                    R.anim.flip_horizontal_out);
            rl_main_anim.startAnimation(anim_3);

            timeoverb = false;
            if(cts_right_num <= 2){
                myScore -= 90;
            }
            else{
                myScore -= 90*cts_right_num;
            }
            cts_right_num -= 1;
            timeover_m.start();
            currentNum += 1;
            erroNum+=1;

            awTimeover = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("正确答案："+answers_temp.get(0)[0])
                    .setContentText(myScore_temp+" + "+(myScore-myScore_temp)+" = "+myScore)
                    .setConfirmText("记住啦")
                    .setConfirmClickListener(nextTimeover_2);
            awTimeover.setCancelable(false);
            awTimeover.show();
        }

    currentMyScore.setText(myScore + "");

}

    /**
     * 答题结束的弹窗
     */
    public void dialogGameOver(){
        final View view_d = getLayoutInflater().inflate(R.layout.dialog_gameover,null);
        TextView tv_mtscore = (TextView) view_d.findViewById(R.id.tv_gameover);
        final TextView tv = (TextView) view_d.findViewById(R.id.tv_gameover_chenghao);

        tv_mtscore.setText("得分："+myScore);
         if(myScore >=9600){
            tv.setText("在路上");
        }
        else if(myScore >=7600 && myScore<9600){
            tv.setText("摸爬滚打");
        }else if(myScore>=3600 && myScore<7600){
            tv.setText("跌跌撞撞");
        }else if(myScore>=2000 && myScore<3600){
            tv.setText("还差一点");
        }
        else{
            tv.setText("云里雾里");
        }
        gameover_d = new Dialog(Starting.this, R.style.dialog);
        gameover_d.setContentView(view_d);
        //gameover_d.setCanceledOnTouchOutside(false);
        gameover_d.show();
        starting_m.pause();
        result_m.start();
        view_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Starting.this.finish();
                overridePendingTransition(R.anim.start_, R.anim.nullanim);

                ContentValues cv = new ContentValues();
                cv.put("username",  Constants.StringData());
                cv.put("pwd", myScore+"");
                cv.put("name_h", tv.getText().toString());

                cv.put("is_upload", Constants.SYNC_OK);
                cv.put("type_", cType);
                cv.put("erro_", mErroNum);
                cv.put("erro_select", mErroSelect);
                db.insert("user1", null, cv);
            }
        });

        gameover_d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Starting.this.finish();
                overridePendingTransition(R.anim.start_, R.anim.nullanim);

                ContentValues cv = new ContentValues();
                cv.put("username", Constants.StringData());
                cv.put("pwd", myScore+"");
                cv.put("name_h", tv.getText().toString());

                cv.put("is_upload", Constants.SYNC_OK);
                cv.put("type_", cType);
                cv.put("erro_", mErroNum);
                cv.put("erro_select", mErroSelect);
                db.insert("user1", null, cv);
            }
        });
    }

    private String getLastUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 点击ITEM移动动画
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation,int[] endLocation,final int num,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);

        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0]-80, endLocation[0]-80, startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间

        //动画配置
        final AnimationSet moveAnimationSet = new AnimationSet(true);

        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                my_choice.setText(answers_temp.get(0)[num]);
                isMove = false;
                mMoveView.setVisibility(View.GONE);
            }
        });
    }
    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }
    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }
    /**
     * 获取点击的Item的对应View，
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 重启倒计时
     */
    private void ReTime(){
        count_down_num = mytime;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                CH.sendEmptyMessage(0);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public void onBackPressed() {

            back_ = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("正在答题，确定退出吗?")
                    .setContentText("在此结束同样会保存到历史记录.")
                    .setCancelText("确定退出")
                    .setConfirmText("继续答题")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Starting.this.finish();
                            overridePendingTransition(R.anim.block_move_right, R.anim.small_2_big);
                            ContentValues cv = new ContentValues();
                            cv.put("username",  Constants.StringData());
                            cv.put("pwd", myScore+"");
                            cv.put("name_h", "中途退出");

                            cv.put("is_upload", Constants.SYNC_OK);
                            cv.put("type_", cType);
                            cv.put("erro_", mErroNum);
                            cv.put("erro_select", mErroSelect);
                            db.insert("user1", null, cv);
                            timer.cancel();
                            timerTask.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            back_.dismiss();
                        }
                    });
           back_.setCanceledOnTouchOutside(true);
            back_.show();
    }
    View view;
    helpdialog_item hi = null;
    private void showHelpDialog() {
        if(hi==null){
            hi= new helpdialog_item();
            view = getLayoutInflater().inflate(R.layout.dialog_help, null);
            hi.tv_help1 = (TextView) view.findViewById(R.id.bt_help1);
            hi.tv_help2 = (TextView) view.findViewById(R.id.bt_help2);
            hi.tv_helpcancle = (Button) view.findViewById(R.id.bt_helpcancle);
            dialog_help = new Dialog(this, R.style.transparentFrameWindowStyle);
            dialog_help.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            Window window = dialog_help.getWindow();
            // 璁剧疆鏄剧ず鍔ㄧ敾
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = getWindowManager().getDefaultDisplay().getHeight();
            // 浠ヤ笅杩欎袱鍙ユ槸涓轰簡淇濊瘉鎸夐挳鍙互姘村钩婊″睆
            wl.width = LayoutParams.MATCH_PARENT;
            wl.height = LayoutParams.WRAP_CONTENT;

            // 璁剧疆鏄剧ず浣嶇疆
            dialog_help.onWindowAttributesChanged(wl);

            dialog_help.setCanceledOnTouchOutside(true);
            view.setTag(hi);

        }else{
            hi = (helpdialog_item) view.getTag();
        }
        hi.tv_help1.setOnClickListener(this);
        hi.tv_help2.setOnClickListener(this);
        hi.tv_helpcancle.setOnClickListener(this);
        dialog_help.show();
    }
    class helpdialog_item{
        TextView tv_help1;
        TextView tv_help2;
        TextView tv_helpcancle;
    }

//    HandlerThread localHandlerThread;
//    Handler handler;
//    private void huilurry()
//    {
//        localHandlerThread=new HandlerThread("huilurry");
//        localHandlerThread.start();
//        handler=new Handler(localHandlerThread.getLooper());
//        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
//    }

    @Override
    protected void onRestart() {
        starting_m.start();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        starting_m.pause();
        timesmall_m.pause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(timer!=null){
            timer.cancel();
        }
        if(timerTask!=null){
            timerTask.cancel();
        }
        starting_m.stop();
        timesmall_m.stop();
        super.onDestroy();
    }

}
