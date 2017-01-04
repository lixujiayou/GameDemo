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
import com.example.administrator.gamedemo.widget.database.Sq;


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

private LinearLayout tv_actionbar_line;
    private ImageView iv_anim;

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
    int type_num  = inte.getExtras().getInt("type");
    boolean aa = inte.getExtras().getBoolean("yinliang");
    if (aa == false) {
        initMusicJingYin();
    }
    switch (type_num){
        case 2:
            initDate_MXWJ();
            pb= getResources().getStringArray(R.array.problem_mxwj);
            tv_starting_main.setText("正在答题：摩西五经");
            break;
        case 4:
            initDate_LSSG();
            pb= getResources().getStringArray(R.array.problem_lssg);
            tv_starting_main.setText("正在答题：历史诗歌书");
            break;
        case 3:
            initDate_XZ();
            pb= getResources().getStringArray(R.array.problem_xz);
            tv_starting_main.setText("正在答题：大小先知书");
            break;
        case 0:
            initDate_SFY();
            pb= getResources().getStringArray(R.array.problem_sfy);
            tv_starting_main.setText("正在答题：四福音");
            break;
        case 1:
            initDate_SX();
            pb= getResources().getStringArray(R.array.problem_sx);
            tv_starting_main.setText("正在答题：新约书信");
            break;
        case 5:
            initDate();
            pb= getResources().getStringArray(R.array.problem_1);
            tv_starting_main.setText("正在答题：圣经");
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
//                CountDown();
//                count_down.setVisibility(View.VISIBLE);
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
                cv.put("username",  getLastUpdateTime() + "");
                cv.put("pwd", myScore+"");
                cv.put("name_h", tv.getText().toString());
                db.insert("user1", null, cv);
            }
        });
        gameover_d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Starting.this.finish();
                overridePendingTransition(R.anim.start_, R.anim.nullanim);

                ContentValues cv = new ContentValues();
                cv.put("username",  getLastUpdateTime() + "");
                cv.put("pwd", myScore+"");
                cv.put("name_h", tv.getText().toString());
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
                            cv.put("username",  getLastUpdateTime() + "");
                            cv.put("pwd", myScore+"");
                            cv.put("name_h", "中途退出");
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
    public void initDate_MXWJ(){
        String[] a1 = getResources().getStringArray(R.array.as_mxwj_1);
        String[] a2 = getResources().getStringArray(R.array.as_mxwj_2);
        String[] a3 = getResources().getStringArray(R.array.as_mxwj_3);
        String[] a4 = getResources().getStringArray(R.array.as_mxwj_4);
        String[] a5 = getResources().getStringArray(R.array.as_mxwj_5);
        String[] a6 = getResources().getStringArray(R.array.as_mxwj_6);
        String[] a7 = getResources().getStringArray(R.array.as_mxwj_7);
        String[] a8 = getResources().getStringArray(R.array.as_mxwj_8);
        String[] a9 = getResources().getStringArray(R.array.as_mxwj_9);
        String[] a10 = getResources().getStringArray(R.array.as_mxwj_10);
        String[] a11 = getResources().getStringArray(R.array.as_mxwj_11);
        String[] a12 = getResources().getStringArray(R.array.as_mxwj_12);
        String[] a13 = getResources().getStringArray(R.array.as_mxwj_13);
        String[] a14 = getResources().getStringArray(R.array.as_mxwj_14);
        String[] a15 = getResources().getStringArray(R.array.as_mxwj_15);
        String[] a16 = getResources().getStringArray(R.array.as_mxwj_16);
        String[] a17 = getResources().getStringArray(R.array.as_mxwj_17);
        String[] a18 = getResources().getStringArray(R.array.as_mxwj_18);
        String[] a19 = getResources().getStringArray(R.array.as_mxwj_19);
        String[] a20 = getResources().getStringArray(R.array.as_mxwj_20);
        String[] a21 = getResources().getStringArray(R.array.as_mxwj_21);
        String[] a22 = getResources().getStringArray(R.array.as_mxwj_22);
        String[] a23 = getResources().getStringArray(R.array.as_mxwj_23);
        String[] a24 = getResources().getStringArray(R.array.as_mxwj_24);
        String[] a25 = getResources().getStringArray(R.array.as_mxwj_25);
        String[] a26 = getResources().getStringArray(R.array.as_mxwj_26);
        String[] a27 = getResources().getStringArray(R.array.as_mxwj_27);
        String[] a28 = getResources().getStringArray(R.array.as_mxwj_28);
        String[] a29 = getResources().getStringArray(R.array.as_mxwj_29);
        String[] a30 = getResources().getStringArray(R.array.as_mxwj_30);
        String[] a31 = getResources().getStringArray(R.array.as_mxwj_31);
        String[] a32 = getResources().getStringArray(R.array.as_mxwj_32);
        String[] a33 = getResources().getStringArray(R.array.as_mxwj_33);
        String[] a34 = getResources().getStringArray(R.array.as_mxwj_34);
        String[] a35 = getResources().getStringArray(R.array.as_mxwj_35);
        String[] a36 = getResources().getStringArray(R.array.as_mxwj_36);
        String[] a37 = getResources().getStringArray(R.array.as_mxwj_37);
        String[] a38 = getResources().getStringArray(R.array.as_mxwj_38);
        String[] a39 = getResources().getStringArray(R.array.as_mxwj_39);
        String[] a40 = getResources().getStringArray(R.array.as_mxwj_40);
        String[] a41 = getResources().getStringArray(R.array.as_mxwj_41);
        String[] a42 = getResources().getStringArray(R.array.as_mxwj_42);
        String[] a43 = getResources().getStringArray(R.array.as_mxwj_43);
        String[] a44 = getResources().getStringArray(R.array.as_mxwj_44);
        String[] a45 = getResources().getStringArray(R.array.as_mxwj_45);
        String[] a46 = getResources().getStringArray(R.array.as_mxwj_46);
        String[] a47 = getResources().getStringArray(R.array.as_mxwj_47);
        String[] a48 = getResources().getStringArray(R.array.as_mxwj_48);
        String[] a49 = getResources().getStringArray(R.array.as_mxwj_49);
        String[] a50 = getResources().getStringArray(R.array.as_mxwj_50);
        String[] a51 = getResources().getStringArray(R.array.as_mxwj_51);
        String[] a52 = getResources().getStringArray(R.array.as_mxwj_52);
        String[] a53 = getResources().getStringArray(R.array.as_mxwj_53);
        String[] a54 = getResources().getStringArray(R.array.as_mxwj_54);
        String[] a55 = getResources().getStringArray(R.array.as_mxwj_55);
        String[] a56 = getResources().getStringArray(R.array.as_mxwj_56);
        String[] a57 = getResources().getStringArray(R.array.as_mxwj_57);
        String[] a58 = getResources().getStringArray(R.array.as_mxwj_58);
        String[] a59 = getResources().getStringArray(R.array.as_mxwj_59);
        String[] a60 = getResources().getStringArray(R.array.as_mxwj_60);
        String[] a61 = getResources().getStringArray(R.array.as_mxwj_61);
        String[] a62 = getResources().getStringArray(R.array.as_mxwj_62);
        String[] a63 = getResources().getStringArray(R.array.as_mxwj_63);
        String[] a64 = getResources().getStringArray(R.array.as_mxwj_64);
        String[] a65 = getResources().getStringArray(R.array.as_mxwj_65);
        String[] a66 = getResources().getStringArray(R.array.as_mxwj_66);
        String[] a67 = getResources().getStringArray(R.array.as_mxwj_67);
        String[] a68 = getResources().getStringArray(R.array.as_mxwj_68);
        String[] a69 = getResources().getStringArray(R.array.as_mxwj_69);
        String[] a70 = getResources().getStringArray(R.array.as_mxwj_70);
        String[] a71 = getResources().getStringArray(R.array.as_mxwj_71);
        String[] a72 = getResources().getStringArray(R.array.as_mxwj_72);
        String[] a73 = getResources().getStringArray(R.array.as_mxwj_73);
        String[] a74 = getResources().getStringArray(R.array.as_mxwj_74);
        String[] a75 = getResources().getStringArray(R.array.as_mxwj_75);
        String[] a76 = getResources().getStringArray(R.array.as_mxwj_76);
        String[] a77 = getResources().getStringArray(R.array.as_mxwj_77);
        String[] a78 = getResources().getStringArray(R.array.as_mxwj_78);
        String[] a79 = getResources().getStringArray(R.array.as_mxwj_79);
        String[] a80 = getResources().getStringArray(R.array.as_mxwj_80);
        String[] a81 = getResources().getStringArray(R.array.as_mxwj_81);
        String[] a82 = getResources().getStringArray(R.array.as_mxwj_82);
        String[] a83 = getResources().getStringArray(R.array.as_mxwj_83);
        String[] a84 = getResources().getStringArray(R.array.as_mxwj_84);
        String[] a85 = getResources().getStringArray(R.array.as_mxwj_85);
        String[] a86 = getResources().getStringArray(R.array.as_mxwj_86);
        String[] a87 = getResources().getStringArray(R.array.as_mxwj_87);
        String[] a88 = getResources().getStringArray(R.array.as_mxwj_88);
        String[] a89 = getResources().getStringArray(R.array.as_mxwj_89);
        String[] a90 = getResources().getStringArray(R.array.as_mxwj_90);
        String[] a91 = getResources().getStringArray(R.array.as_mxwj_91);
        String[] a92 = getResources().getStringArray(R.array.as_mxwj_92);
        String[] a93 = getResources().getStringArray(R.array.as_mxwj_93);
        String[] a94 = getResources().getStringArray(R.array.as_mxwj_94);
        String[] a95 = getResources().getStringArray(R.array.as_mxwj_95);
        String[] a96 = getResources().getStringArray(R.array.as_mxwj_96);
        String[] a97 = getResources().getStringArray(R.array.as_mxwj_97);
        String[] a98 = getResources().getStringArray(R.array.as_mxwj_98);
        String[] a99 = getResources().getStringArray(R.array.as_mxwj_99);
        String[] a100 = getResources().getStringArray(R.array.as_mxwj_100);
        String[] a101 = getResources().getStringArray(R.array.as_mxwj_101);
        String[] a102 = getResources().getStringArray(R.array.as_mxwj_102);
        String[] a103 = getResources().getStringArray(R.array.as_mxwj_103);
        String[] a104 = getResources().getStringArray(R.array.as_mxwj_104);
        String[] a105 = getResources().getStringArray(R.array.as_mxwj_105);
        String[] a106 = getResources().getStringArray(R.array.as_mxwj_106);
        String[] a107 = getResources().getStringArray(R.array.as_mxwj_107);
        answers.add(a1);
        answers.add(a2);
        answers.add(a3);
        answers.add(a4);
        answers.add(a5);
        answers.add(a6);
        answers.add(a7);
        answers.add(a8);
        answers.add(a9);
        answers.add(a10);
        answers.add(a11);
        answers.add(a12);
        answers.add(a13);
        answers.add(a14);
        answers.add(a15);
        answers.add(a16);
        answers.add(a17);
        answers.add(a18);
        answers.add(a19);
        answers.add(a20);
        answers.add(a21);
        answers.add(a22);
        answers.add(a23);
        answers.add(a24);
        answers.add(a25);
        answers.add(a26);
        answers.add(a27);
        answers.add(a28);
        answers.add(a29);
        answers.add(a30);
        answers.add(a31);
        answers.add(a32);
        answers.add(a33);
        answers.add(a34);
        answers.add(a35);
        answers.add(a36);
        answers.add(a37);
        answers.add(a38);
        answers.add(a39);
        answers.add(a40);
        answers.add(a41);
        answers.add(a42);
        answers.add(a43);
        answers.add(a44);
        answers.add(a45);
        answers.add(a46);
        answers.add(a47);
        answers.add(a48);
        answers.add(a49);
        answers.add(a50);
        answers.add(a51);
        answers.add(a52);
        answers.add(a53);
        answers.add(a54);
        answers.add(a55);
        answers.add(a56);
        answers.add(a57);
        answers.add(a58);
        answers.add(a59);
        answers.add(a60);
        answers.add(a61);
        answers.add(a62);
        answers.add(a63);
        answers.add(a64);
        answers.add(a65);
        answers.add(a66);
        answers.add(a67);
        answers.add(a68);
        answers.add(a69);
        answers.add(a70);
        answers.add(a71);
        answers.add(a72);
        answers.add(a73);
        answers.add(a74);
        answers.add(a75);
        answers.add(a76);
        answers.add(a77);
        answers.add(a78);
        answers.add(a79);
        answers.add(a80);
        answers.add(a81);
        answers.add(a82);
        answers.add(a83);
        answers.add(a84);
        answers.add(a85);
        answers.add(a86);
        answers.add(a87);
        answers.add(a88);
        answers.add(a89);
        answers.add(a90);
        answers.add(a91);
        answers.add(a92);
        answers.add(a93);
        answers.add(a94);
        answers.add(a95);
        answers.add(a96);
        answers.add(a97);
        answers.add(a98);
        answers.add(a99);
        answers.add(a100);
        answers.add(a101);
        answers.add(a102);
        answers.add(a103);
        answers.add(a104);
        answers.add(a105);
        answers.add(a106);
        answers.add(a107);

    }
    public void initDate_LSSG(){
        String[] a1 = getResources().getStringArray(R.array.as_lssg_1);
        String[] a2 = getResources().getStringArray(R.array.as_lssg_2);
        String[] a3 = getResources().getStringArray(R.array.as_lssg_3);
        String[] a4 = getResources().getStringArray(R.array.as_lssg_4);
        String[] a5 = getResources().getStringArray(R.array.as_lssg_5);
        String[] a6 = getResources().getStringArray(R.array.as_lssg_6);
        String[] a7 = getResources().getStringArray(R.array.as_lssg_7);
        String[] a8 = getResources().getStringArray(R.array.as_lssg_8);
        String[] a9 = getResources().getStringArray(R.array.as_lssg_9);
        String[] a10 = getResources().getStringArray(R.array.as_lssg_10);
        String[] a11 = getResources().getStringArray(R.array.as_lssg_11);
        String[] a12 = getResources().getStringArray(R.array.as_lssg_12);
        String[] a13 = getResources().getStringArray(R.array.as_lssg_13);
        String[] a14 = getResources().getStringArray(R.array.as_lssg_14);
        String[] a15 = getResources().getStringArray(R.array.as_lssg_15);
        String[] a16 = getResources().getStringArray(R.array.as_lssg_16);
        String[] a17 = getResources().getStringArray(R.array.as_lssg_17);
        String[] a18 = getResources().getStringArray(R.array.as_lssg_18);
        String[] a19 = getResources().getStringArray(R.array.as_lssg_19);
        String[] a20 = getResources().getStringArray(R.array.as_lssg_20);
        String[] a21 = getResources().getStringArray(R.array.as_lssg_21);
        String[] a22 = getResources().getStringArray(R.array.as_lssg_22);
        String[] a23 = getResources().getStringArray(R.array.as_lssg_23);
        String[] a24 = getResources().getStringArray(R.array.as_lssg_24);
        String[] a25 = getResources().getStringArray(R.array.as_lssg_25);
        String[] a26 = getResources().getStringArray(R.array.as_lssg_26);
        String[] a27 = getResources().getStringArray(R.array.as_lssg_27);
        String[] a28 = getResources().getStringArray(R.array.as_lssg_28);
        String[] a29 = getResources().getStringArray(R.array.as_lssg_29);
        String[] a30 = getResources().getStringArray(R.array.as_lssg_30);
        String[] a31 = getResources().getStringArray(R.array.as_lssg_31);
        String[] a32 = getResources().getStringArray(R.array.as_lssg_32);
        String[] a33 = getResources().getStringArray(R.array.as_lssg_33);
        String[] a34 = getResources().getStringArray(R.array.as_lssg_34);
        String[] a35 = getResources().getStringArray(R.array.as_lssg_35);
        String[] a36 = getResources().getStringArray(R.array.as_lssg_36);
        String[] a37 = getResources().getStringArray(R.array.as_lssg_37);
        String[] a38 = getResources().getStringArray(R.array.as_lssg_38);
        String[] a39 = getResources().getStringArray(R.array.as_lssg_39);
        String[] a40 = getResources().getStringArray(R.array.as_lssg_40);
        String[] a41 = getResources().getStringArray(R.array.as_lssg_41);
        String[] a42 = getResources().getStringArray(R.array.as_lssg_42);
        String[] a43 = getResources().getStringArray(R.array.as_lssg_43);
        String[] a44 = getResources().getStringArray(R.array.as_lssg_44);
        String[] a45 = getResources().getStringArray(R.array.as_lssg_45);
        String[] a46 = getResources().getStringArray(R.array.as_lssg_46);
        String[] a47 = getResources().getStringArray(R.array.as_lssg_47);
        String[] a48 = getResources().getStringArray(R.array.as_lssg_48);
        String[] a49 = getResources().getStringArray(R.array.as_lssg_49);
        String[] a50 = getResources().getStringArray(R.array.as_lssg_50);
        String[] a51 = getResources().getStringArray(R.array.as_lssg_51);
        String[] a52 = getResources().getStringArray(R.array.as_lssg_52);
        String[] a53 = getResources().getStringArray(R.array.as_lssg_53);
        String[] a54 = getResources().getStringArray(R.array.as_lssg_54);
        String[] a55 = getResources().getStringArray(R.array.as_lssg_55);
        String[] a56 = getResources().getStringArray(R.array.as_lssg_56);
        String[] a57 = getResources().getStringArray(R.array.as_lssg_57);
        String[] a58 = getResources().getStringArray(R.array.as_lssg_58);
        String[] a59 = getResources().getStringArray(R.array.as_lssg_59);
        String[] a60 = getResources().getStringArray(R.array.as_lssg_60);
        String[] a61 = getResources().getStringArray(R.array.as_lssg_61);
        String[] a62 = getResources().getStringArray(R.array.as_lssg_62);
        String[] a63 = getResources().getStringArray(R.array.as_lssg_63);
        String[] a64 = getResources().getStringArray(R.array.as_lssg_64);
        String[] a65 = getResources().getStringArray(R.array.as_lssg_65);
        String[] a66 = getResources().getStringArray(R.array.as_lssg_66);
        String[] a67 = getResources().getStringArray(R.array.as_lssg_67);
        String[] a68 = getResources().getStringArray(R.array.as_lssg_68);
        String[] a69 = getResources().getStringArray(R.array.as_lssg_69);
        String[] a70 = getResources().getStringArray(R.array.as_lssg_70);
        String[] a71 = getResources().getStringArray(R.array.as_lssg_71);
        String[] a72 = getResources().getStringArray(R.array.as_lssg_72);
        String[] a73 = getResources().getStringArray(R.array.as_lssg_73);
        String[] a74 = getResources().getStringArray(R.array.as_lssg_74);
        String[] a75 = getResources().getStringArray(R.array.as_lssg_75);
        String[] a76 = getResources().getStringArray(R.array.as_lssg_76);
        String[] a77 = getResources().getStringArray(R.array.as_lssg_77);
        String[] a78 = getResources().getStringArray(R.array.as_lssg_78);
        String[] a79 = getResources().getStringArray(R.array.as_lssg_79);
        String[] a80 = getResources().getStringArray(R.array.as_lssg_80);
        String[] a81 = getResources().getStringArray(R.array.as_lssg_81);
        String[] a82 = getResources().getStringArray(R.array.as_lssg_82);
        String[] a83 = getResources().getStringArray(R.array.as_lssg_83);
        String[] a84 = getResources().getStringArray(R.array.as_lssg_84);
        String[] a85 = getResources().getStringArray(R.array.as_lssg_85);
        String[] a86 = getResources().getStringArray(R.array.as_lssg_86);
        String[] a87 = getResources().getStringArray(R.array.as_lssg_87);
        String[] a88 = getResources().getStringArray(R.array.as_lssg_88);
        String[] a89 = getResources().getStringArray(R.array.as_lssg_89);
        String[] a90 = getResources().getStringArray(R.array.as_lssg_90);
        String[] a91 = getResources().getStringArray(R.array.as_lssg_91);
        String[] a92 = getResources().getStringArray(R.array.as_lssg_92);
        String[] a93 = getResources().getStringArray(R.array.as_lssg_93);
        String[] a94 = getResources().getStringArray(R.array.as_lssg_94);
        String[] a95 = getResources().getStringArray(R.array.as_lssg_95);
        String[] a96 = getResources().getStringArray(R.array.as_lssg_96);
        String[] a97 = getResources().getStringArray(R.array.as_lssg_97);
        String[] a98 = getResources().getStringArray(R.array.as_lssg_98);
        String[] a99 = getResources().getStringArray(R.array.as_lssg_99);
        String[] a100 = getResources().getStringArray(R.array.as_lssg_100);
        String[] a101 = getResources().getStringArray(R.array.as_lssg_101);
        String[] a102 = getResources().getStringArray(R.array.as_lssg_102);
        String[] a103 = getResources().getStringArray(R.array.as_lssg_103);
        String[] a104 = getResources().getStringArray(R.array.as_lssg_104);
        String[] a105 = getResources().getStringArray(R.array.as_lssg_105);
        String[] a106 = getResources().getStringArray(R.array.as_lssg_106);
        String[] a107 = getResources().getStringArray(R.array.as_lssg_107);
        String[] a108 = getResources().getStringArray(R.array.as_lssg_108);
        String[] a109 = getResources().getStringArray(R.array.as_lssg_109);
        String[] a110 = getResources().getStringArray(R.array.as_lssg_110);
        String[] a111 = getResources().getStringArray(R.array.as_lssg_111);
        String[] a112 = getResources().getStringArray(R.array.as_lssg_112);
        answers.add(a1);
        answers.add(a2);
        answers.add(a3);
        answers.add(a4);
        answers.add(a5);
        answers.add(a6);
        answers.add(a7);
        answers.add(a8);
        answers.add(a9);
        answers.add(a10);
        answers.add(a11);
        answers.add(a12);
        answers.add(a13);
        answers.add(a14);
        answers.add(a15);
        answers.add(a16);
        answers.add(a17);
        answers.add(a18);
        answers.add(a19);
        answers.add(a20);
        answers.add(a21);
        answers.add(a22);
        answers.add(a23);
        answers.add(a24);
        answers.add(a25);
        answers.add(a26);
        answers.add(a27);
        answers.add(a28);
        answers.add(a29);
        answers.add(a30);
        answers.add(a31);
        answers.add(a32);
        answers.add(a33);
        answers.add(a34);
        answers.add(a35);
        answers.add(a36);
        answers.add(a37);
        answers.add(a38);
        answers.add(a39);
        answers.add(a40);
        answers.add(a41);
        answers.add(a42);
        answers.add(a43);
        answers.add(a44);
        answers.add(a45);
        answers.add(a46);
        answers.add(a47);
        answers.add(a48);
        answers.add(a49);
        answers.add(a50);
        answers.add(a51);
        answers.add(a52);
        answers.add(a53);
        answers.add(a54);
        answers.add(a55);
        answers.add(a56);
        answers.add(a57);
        answers.add(a58);
        answers.add(a59);
        answers.add(a60);
        answers.add(a61);
        answers.add(a62);
        answers.add(a63);
        answers.add(a64);
        answers.add(a65);
        answers.add(a66);
        answers.add(a67);
        answers.add(a68);
        answers.add(a69);
        answers.add(a70);
        answers.add(a71);
        answers.add(a72);
        answers.add(a73);
        answers.add(a74);
        answers.add(a75);
        answers.add(a76);
        answers.add(a77);
        answers.add(a78);
        answers.add(a79);
        answers.add(a80);
        answers.add(a81);
        answers.add(a82);
        answers.add(a83);
        answers.add(a84);
        answers.add(a85);
        answers.add(a86);
        answers.add(a87);
        answers.add(a88);
        answers.add(a89);
        answers.add(a90);
        answers.add(a91);
        answers.add(a92);
        answers.add(a93);
        answers.add(a94);
        answers.add(a95);
        answers.add(a96);
        answers.add(a97);
        answers.add(a98);
        answers.add(a99);
        answers.add(a100);
        answers.add(a101);
        answers.add(a102);
        answers.add(a103);
        answers.add(a104);
        answers.add(a105);
        answers.add(a106);
        answers.add(a107);
        answers.add(a108);
        answers.add(a109);
        answers.add(a110);
        answers.add(a111);
        answers.add(a112);
    }
    public void initDate_XZ(){
        String[] a1 = getResources().getStringArray(R.array.as_xz_1);
        String[] a2 = getResources().getStringArray(R.array.as_xz_2);
        String[] a3 = getResources().getStringArray(R.array.as_xz_3);
        String[] a4 = getResources().getStringArray(R.array.as_xz_4);
        String[] a5 = getResources().getStringArray(R.array.as_xz_5);
        String[] a6 = getResources().getStringArray(R.array.as_xz_6);
        String[] a7 = getResources().getStringArray(R.array.as_xz_7);
        String[] a8 = getResources().getStringArray(R.array.as_xz_8);
        String[] a9 = getResources().getStringArray(R.array.as_xz_9);
        String[] a10 = getResources().getStringArray(R.array.as_xz_10);
        String[] a11 = getResources().getStringArray(R.array.as_xz_11);
        String[] a12 = getResources().getStringArray(R.array.as_xz_12);
        String[] a13 = getResources().getStringArray(R.array.as_xz_13);
        String[] a14 = getResources().getStringArray(R.array.as_xz_14);
        String[] a15 = getResources().getStringArray(R.array.as_xz_15);
        String[] a16 = getResources().getStringArray(R.array.as_xz_16);
        String[] a17 = getResources().getStringArray(R.array.as_xz_17);
        String[] a18 = getResources().getStringArray(R.array.as_xz_18);
        String[] a19 = getResources().getStringArray(R.array.as_xz_19);
        String[] a20 = getResources().getStringArray(R.array.as_xz_20);
        String[] a21 = getResources().getStringArray(R.array.as_xz_21);
        String[] a22 = getResources().getStringArray(R.array.as_xz_22);
        String[] a23 = getResources().getStringArray(R.array.as_xz_23);
        String[] a24 = getResources().getStringArray(R.array.as_xz_24);
        String[] a25 = getResources().getStringArray(R.array.as_xz_25);
        String[] a26 = getResources().getStringArray(R.array.as_xz_26);
        String[] a27 = getResources().getStringArray(R.array.as_xz_27);
        String[] a28 = getResources().getStringArray(R.array.as_xz_28);
        String[] a29 = getResources().getStringArray(R.array.as_xz_29);
        String[] a30 = getResources().getStringArray(R.array.as_xz_30);
        String[] a31 = getResources().getStringArray(R.array.as_xz_31);
        String[] a32 = getResources().getStringArray(R.array.as_xz_32);
        String[] a33 = getResources().getStringArray(R.array.as_xz_33);
        String[] a34 = getResources().getStringArray(R.array.as_xz_34);
        String[] a35 = getResources().getStringArray(R.array.as_xz_35);
        String[] a36 = getResources().getStringArray(R.array.as_xz_36);
        String[] a37 = getResources().getStringArray(R.array.as_xz_37);
        String[] a38 = getResources().getStringArray(R.array.as_xz_38);
        String[] a39 = getResources().getStringArray(R.array.as_xz_39);
        String[] a40 = getResources().getStringArray(R.array.as_xz_40);
        String[] a41 = getResources().getStringArray(R.array.as_xz_41);
        String[] a42 = getResources().getStringArray(R.array.as_xz_42);
        String[] a43 = getResources().getStringArray(R.array.as_xz_43);
        String[] a44 = getResources().getStringArray(R.array.as_xz_44);
        String[] a45 = getResources().getStringArray(R.array.as_xz_45);
        String[] a46 = getResources().getStringArray(R.array.as_xz_46);
        String[] a47 = getResources().getStringArray(R.array.as_xz_47);
        String[] a48 = getResources().getStringArray(R.array.as_xz_48);
        String[] a49 = getResources().getStringArray(R.array.as_xz_49);
        String[] a50 = getResources().getStringArray(R.array.as_xz_50);
        answers.add(a1);
        answers.add(a2);
        answers.add(a3);
        answers.add(a4);
        answers.add(a5);
        answers.add(a6);
        answers.add(a7);
        answers.add(a8);
        answers.add(a9);
        answers.add(a10);
        answers.add(a11);
        answers.add(a12);
        answers.add(a13);
        answers.add(a14);
        answers.add(a15);
        answers.add(a16);
        answers.add(a17);
        answers.add(a18);
        answers.add(a19);
        answers.add(a20);
        answers.add(a21);
        answers.add(a22);
        answers.add(a23);
        answers.add(a24);
        answers.add(a25);
        answers.add(a26);
        answers.add(a27);
        answers.add(a28);
        answers.add(a29);
        answers.add(a30);
        answers.add(a31);
        answers.add(a32);
        answers.add(a33);
        answers.add(a34);
        answers.add(a35);
        answers.add(a36);
        answers.add(a37);
        answers.add(a38);
        answers.add(a39);
        answers.add(a40);
        answers.add(a41);
        answers.add(a42);
        answers.add(a43);
        answers.add(a44);
        answers.add(a45);
        answers.add(a46);
        answers.add(a47);
        answers.add(a48);
        answers.add(a49);
        answers.add(a50);
    }
    public void initDate_SFY(){
        String[] a1 = getResources().getStringArray(R.array.as_s_1);
        String[] a2 = getResources().getStringArray(R.array.as_s_2);
        String[] a3 = getResources().getStringArray(R.array.as_s_3);
        String[] a4 = getResources().getStringArray(R.array.as_s_4);
        String[] a5 = getResources().getStringArray(R.array.as_s_5);
        String[] a6 = getResources().getStringArray(R.array.as_s_6);
        String[] a7 = getResources().getStringArray(R.array.as_s_7);
        String[] a8 = getResources().getStringArray(R.array.as_s_8);
        String[] a9 = getResources().getStringArray(R.array.as_s_9);
        String[] a10 = getResources().getStringArray(R.array.as_s_10);
        String[] a11 = getResources().getStringArray(R.array.as_s_11);
        String[] a12 = getResources().getStringArray(R.array.as_s_12);
        String[] a13 = getResources().getStringArray(R.array.as_s_13);
        String[] a14 = getResources().getStringArray(R.array.as_s_14);
        String[] a15 = getResources().getStringArray(R.array.as_s_15);
        String[] a16 = getResources().getStringArray(R.array.as_s_16);
        String[] a17 = getResources().getStringArray(R.array.as_s_17);
        String[] a18 = getResources().getStringArray(R.array.as_s_18);
        String[] a19 = getResources().getStringArray(R.array.as_s_19);
        String[] a20 = getResources().getStringArray(R.array.as_s_20);
        String[] a21 = getResources().getStringArray(R.array.as_s_21);
        String[] a22 = getResources().getStringArray(R.array.as_s_22);
        String[] a23 = getResources().getStringArray(R.array.as_s_23);
        String[] a24 = getResources().getStringArray(R.array.as_s_24);
        String[] a25 = getResources().getStringArray(R.array.as_s_25);
        String[] a26 = getResources().getStringArray(R.array.as_s_26);
        String[] a27 = getResources().getStringArray(R.array.as_s_27);
        String[] a28 = getResources().getStringArray(R.array.as_s_28);
        String[] a29 = getResources().getStringArray(R.array.as_s_29);
        String[] a30 = getResources().getStringArray(R.array.as_s_30);
        String[] a31 = getResources().getStringArray(R.array.as_s_31);
        String[] a32 = getResources().getStringArray(R.array.as_s_32);
        String[] a33 = getResources().getStringArray(R.array.as_s_33);
        String[] a34 = getResources().getStringArray(R.array.as_s_34);
        String[] a35 = getResources().getStringArray(R.array.as_s_35);
        String[] a36 = getResources().getStringArray(R.array.as_s_36);
        String[] a37 = getResources().getStringArray(R.array.as_s_37);
        String[] a38 = getResources().getStringArray(R.array.as_s_38);
        String[] a39 = getResources().getStringArray(R.array.as_s_39);
        String[] a40 = getResources().getStringArray(R.array.as_s_40);
        String[] a41 = getResources().getStringArray(R.array.as_s_41);
        String[] a42 = getResources().getStringArray(R.array.as_s_42);
        String[] a43 = getResources().getStringArray(R.array.as_s_43);
        String[] a44 = getResources().getStringArray(R.array.as_s_44);
        String[] a45 = getResources().getStringArray(R.array.as_s_45);
        String[] a46 = getResources().getStringArray(R.array.as_s_46);
        String[] a47 = getResources().getStringArray(R.array.as_s_47);
        String[] a48 = getResources().getStringArray(R.array.as_s_48);
        String[] a49 = getResources().getStringArray(R.array.as_s_49);
        String[] a50 = getResources().getStringArray(R.array.as_s_50);
        String[] a51 = getResources().getStringArray(R.array.as_s_51);
        String[] a52 = getResources().getStringArray(R.array.as_s_52);
        String[] a53 = getResources().getStringArray(R.array.as_s_53);
        String[] a54 = getResources().getStringArray(R.array.as_s_54);
        String[] a55 = getResources().getStringArray(R.array.as_s_55);
        String[] a56 = getResources().getStringArray(R.array.as_s_56);
        String[] a57 = getResources().getStringArray(R.array.as_s_57);
        String[] a58 = getResources().getStringArray(R.array.as_s_58);
        String[] a59 = getResources().getStringArray(R.array.as_s_59);
        String[] a60 = getResources().getStringArray(R.array.as_s_60);
        String[] a61 = getResources().getStringArray(R.array.as_s_61);
        String[] a62 = getResources().getStringArray(R.array.as_s_62);
        String[] a63 = getResources().getStringArray(R.array.as_s_63);
        String[] a64 = getResources().getStringArray(R.array.as_s_64);
        String[] a65 = getResources().getStringArray(R.array.as_s_65);
        String[] a66 = getResources().getStringArray(R.array.as_s_66);
        String[] a67 = getResources().getStringArray(R.array.as_s_67);
        String[] a68 = getResources().getStringArray(R.array.as_s_68);
        String[] a69 = getResources().getStringArray(R.array.as_s_69);
        String[] a70 = getResources().getStringArray(R.array.as_s_70);
        String[] a71 = getResources().getStringArray(R.array.as_s_71);
        String[] a72 = getResources().getStringArray(R.array.as_s_72);
        String[] a73 = getResources().getStringArray(R.array.as_s_73);
        String[] a74 = getResources().getStringArray(R.array.as_s_74);
        String[] a75 = getResources().getStringArray(R.array.as_s_75);
        String[] a76 = getResources().getStringArray(R.array.as_s_76);
        String[] a77 = getResources().getStringArray(R.array.as_s_77);
        String[] a78 = getResources().getStringArray(R.array.as_s_78);
        String[] a79 = getResources().getStringArray(R.array.as_s_79);
        String[] a80 = getResources().getStringArray(R.array.as_s_80);
        String[] a81 = getResources().getStringArray(R.array.as_s_81);
        String[] a82 = getResources().getStringArray(R.array.as_s_82);
        String[] a83 = getResources().getStringArray(R.array.as_s_83);
        String[] a84 = getResources().getStringArray(R.array.as_s_84);
        String[] a85 = getResources().getStringArray(R.array.as_s_85);
        String[] a86 = getResources().getStringArray(R.array.as_s_86);
        String[] a87 = getResources().getStringArray(R.array.as_s_87);
        String[] a88 = getResources().getStringArray(R.array.as_s_88);
        String[] a89 = getResources().getStringArray(R.array.as_s_89);
        String[] a90 = getResources().getStringArray(R.array.as_s_90);
        String[] a91 = getResources().getStringArray(R.array.as_s_91);
        String[] a92 = getResources().getStringArray(R.array.as_s_92);
        String[] a93 = getResources().getStringArray(R.array.as_s_93);
        String[] a94 = getResources().getStringArray(R.array.as_s_94);
        String[] a95 = getResources().getStringArray(R.array.as_s_95);
        String[] a96 = getResources().getStringArray(R.array.as_s_96);
        String[] a97 = getResources().getStringArray(R.array.as_s_97);
        String[] a98 = getResources().getStringArray(R.array.as_s_98);
        String[] a99 = getResources().getStringArray(R.array.as_s_99);
        String[] a100 = getResources().getStringArray(R.array.as_s_100);
        String[] a101 = getResources().getStringArray(R.array.as_s_101);
        String[] a102 = getResources().getStringArray(R.array.as_s_102);
        String[] a103 = getResources().getStringArray(R.array.as_s_103);
        String[] a104 = getResources().getStringArray(R.array.as_s_104);
        String[] a105 = getResources().getStringArray(R.array.as_s_105);
        String[] a106 = getResources().getStringArray(R.array.as_s_106);
        String[] a107 = getResources().getStringArray(R.array.as_s_107);
        String[] a108 = getResources().getStringArray(R.array.as_s_108);
        String[] a109 = getResources().getStringArray(R.array.as_s_109);
        String[] a110 = getResources().getStringArray(R.array.as_s_110);
        String[] a111 = getResources().getStringArray(R.array.as_s_111);
        String[] a112 = getResources().getStringArray(R.array.as_s_112);
        String[] a113 = getResources().getStringArray(R.array.as_s_113);
        String[] a114 = getResources().getStringArray(R.array.as_s_114);
        String[] a115 = getResources().getStringArray(R.array.as_s_115);
        String[] a116 = getResources().getStringArray(R.array.as_s_116);
        String[] a117 = getResources().getStringArray(R.array.as_s_117);
        String[] a118 = getResources().getStringArray(R.array.as_s_118);
        String[] a119 = getResources().getStringArray(R.array.as_s_119);
        String[] a120 = getResources().getStringArray(R.array.as_s_120);
        String[] a121 = getResources().getStringArray(R.array.as_s_121);
        String[] a122 = getResources().getStringArray(R.array.as_s_122);
        String[] a123 = getResources().getStringArray(R.array.as_s_123);
        String[] a124 = getResources().getStringArray(R.array.as_s_124);
        String[] a125 = getResources().getStringArray(R.array.as_s_125);
        String[] a126 = getResources().getStringArray(R.array.as_s_126);
        String[] a127 = getResources().getStringArray(R.array.as_s_127);
        String[] a128 = getResources().getStringArray(R.array.as_s_128);
        String[] a129 = getResources().getStringArray(R.array.as_s_129);
        String[] a130 = getResources().getStringArray(R.array.as_s_130);
        String[] a131 = getResources().getStringArray(R.array.as_s_131);
        String[] a132 = getResources().getStringArray(R.array.as_s_132);
        String[] a133 = getResources().getStringArray(R.array.as_s_133);
        String[] a134 = getResources().getStringArray(R.array.as_s_134);
        String[] a135 = getResources().getStringArray(R.array.as_s_135);
        String[] a136 = getResources().getStringArray(R.array.as_s_136);
        String[] a137 = getResources().getStringArray(R.array.as_s_137);
        String[] a138 = getResources().getStringArray(R.array.as_s_138);
        String[] a139 = getResources().getStringArray(R.array.as_s_139);
        String[] a140 = getResources().getStringArray(R.array.as_s_140);
        String[] a141 = getResources().getStringArray(R.array.as_s_141);
        answers.add(a1);
        answers.add(a2);
        answers.add(a3);
        answers.add(a4);
        answers.add(a5);
        answers.add(a6);
        answers.add(a7);
        answers.add(a8);
        answers.add(a9);
        answers.add(a10);
        answers.add(a11);
        answers.add(a12);
        answers.add(a13);
        answers.add(a14);
        answers.add(a15);
        answers.add(a16);
        answers.add(a17);
        answers.add(a18);
        answers.add(a19);
        answers.add(a20);
        answers.add(a21);
        answers.add(a22);
        answers.add(a23);
        answers.add(a24);
        answers.add(a25);
        answers.add(a26);
        answers.add(a27);
        answers.add(a28);
        answers.add(a29);
        answers.add(a30);
        answers.add(a31);
        answers.add(a32);
        answers.add(a33);
        answers.add(a34);
        answers.add(a35);
        answers.add(a36);
        answers.add(a37);
        answers.add(a38);
        answers.add(a39);
        answers.add(a40);
        answers.add(a41);
        answers.add(a42);
        answers.add(a43);
        answers.add(a44);
        answers.add(a45);
        answers.add(a46);
        answers.add(a47);
        answers.add(a48);
        answers.add(a49);
        answers.add(a50);
        answers.add(a51);
        answers.add(a52);
        answers.add(a53);
        answers.add(a54);
        answers.add(a55);
        answers.add(a56);
        answers.add(a57);
        answers.add(a58);
        answers.add(a59);
        answers.add(a60);
        answers.add(a61);
        answers.add(a62);
        answers.add(a63);
        answers.add(a64);
        answers.add(a65);
        answers.add(a66);
        answers.add(a67);
        answers.add(a68);
        answers.add(a69);
        answers.add(a70);
        answers.add(a71);
        answers.add(a72);
        answers.add(a73);
        answers.add(a74);
        answers.add(a75);
        answers.add(a76);
        answers.add(a77);
        answers.add(a78);
        answers.add(a79);
        answers.add(a80);
        answers.add(a81);
        answers.add(a82);
        answers.add(a83);
        answers.add(a84);
        answers.add(a85);
        answers.add(a86);
        answers.add(a87);
        answers.add(a88);
        answers.add(a89);
        answers.add(a90);
        answers.add(a91);
        answers.add(a92);
        answers.add(a93);
        answers.add(a94);
        answers.add(a95);
        answers.add(a96);
        answers.add(a97);
        answers.add(a98);
        answers.add(a99);
        answers.add(a100);
        answers.add(a101);
        answers.add(a102);
        answers.add(a103);
        answers.add(a104);
        answers.add(a105);
        answers.add(a106);
        answers.add(a107);
        answers.add(a108);
        answers.add(a109);
        answers.add(a110);
        answers.add(a111);
        answers.add(a112);
        answers.add(a113);
        answers.add(a114);
        answers.add(a115);
        answers.add(a116);
        answers.add(a117);
        answers.add(a118);
        answers.add(a119);
        answers.add(a120);
        answers.add(a121);
        answers.add(a122);
        answers.add(a123);
        answers.add(a124);
        answers.add(a125);
        answers.add(a126);
        answers.add(a127);
        answers.add(a128);
        answers.add(a129);
        answers.add(a130);
        answers.add(a131);
        answers.add(a132);
        answers.add(a133);
        answers.add(a134);
        answers.add(a135);
        answers.add(a136);
        answers.add(a137);
        answers.add(a138);
        answers.add(a139);
        answers.add(a140);
        answers.add(a141);
    }
    public void initDate_SX(){
        String[] a1 = getResources().getStringArray(R.array.as_sx_1);
        String[] a2 = getResources().getStringArray(R.array.as_sx_2);
        String[] a3 = getResources().getStringArray(R.array.as_sx_3);
        String[] a4 = getResources().getStringArray(R.array.as_sx_4);
        String[] a5 = getResources().getStringArray(R.array.as_sx_5);
        String[] a6 = getResources().getStringArray(R.array.as_sx_6);
        String[] a7 = getResources().getStringArray(R.array.as_sx_7);
        String[] a8 = getResources().getStringArray(R.array.as_sx_8);
        String[] a9 = getResources().getStringArray(R.array.as_sx_9);
        String[] a10 = getResources().getStringArray(R.array.as_sx_10);
        String[] a11 = getResources().getStringArray(R.array.as_sx_11);
        String[] a12 = getResources().getStringArray(R.array.as_sx_12);
        String[] a13 = getResources().getStringArray(R.array.as_sx_13);
        String[] a14 = getResources().getStringArray(R.array.as_sx_14);
        String[] a15 = getResources().getStringArray(R.array.as_sx_15);
        String[] a16 = getResources().getStringArray(R.array.as_sx_16);
        String[] a17 = getResources().getStringArray(R.array.as_sx_17);
        String[] a18 = getResources().getStringArray(R.array.as_sx_18);
        String[] a19 = getResources().getStringArray(R.array.as_sx_19);
        String[] a20 = getResources().getStringArray(R.array.as_sx_20);
        String[] a21 = getResources().getStringArray(R.array.as_sx_21);
        String[] a22 = getResources().getStringArray(R.array.as_sx_22);
        String[] a23 = getResources().getStringArray(R.array.as_sx_23);
        String[] a24 = getResources().getStringArray(R.array.as_sx_24);
        String[] a25 = getResources().getStringArray(R.array.as_sx_25);
        String[] a26 = getResources().getStringArray(R.array.as_sx_26);
        String[] a27 = getResources().getStringArray(R.array.as_sx_27);
        String[] a28 = getResources().getStringArray(R.array.as_sx_28);
        String[] a29 = getResources().getStringArray(R.array.as_sx_29);
        String[] a30 = getResources().getStringArray(R.array.as_sx_30);
        String[] a31 = getResources().getStringArray(R.array.as_sx_31);
        String[] a32 = getResources().getStringArray(R.array.as_sx_32);
        String[] a33 = getResources().getStringArray(R.array.as_sx_33);
        String[] a34 = getResources().getStringArray(R.array.as_sx_34);
        String[] a35 = getResources().getStringArray(R.array.as_sx_35);
        String[] a36 = getResources().getStringArray(R.array.as_sx_36);
        String[] a37 = getResources().getStringArray(R.array.as_sx_37);
        String[] a38 = getResources().getStringArray(R.array.as_sx_38);
        String[] a39 = getResources().getStringArray(R.array.as_sx_39);
        String[] a40 = getResources().getStringArray(R.array.as_sx_40);
        String[] a41 = getResources().getStringArray(R.array.as_sx_41);
        String[] a42 = getResources().getStringArray(R.array.as_sx_42);
        String[] a43 = getResources().getStringArray(R.array.as_sx_43);
        String[] a44 = getResources().getStringArray(R.array.as_sx_44);
        String[] a45 = getResources().getStringArray(R.array.as_sx_45);
        String[] a46 = getResources().getStringArray(R.array.as_sx_46);
        String[] a47 = getResources().getStringArray(R.array.as_sx_47);
        String[] a48 = getResources().getStringArray(R.array.as_sx_48);
        String[] a49 = getResources().getStringArray(R.array.as_sx_49);
        String[] a50 = getResources().getStringArray(R.array.as_sx_50);
        String[] a51 = getResources().getStringArray(R.array.as_sx_51);
        String[] a52 = getResources().getStringArray(R.array.as_sx_52);
        String[] a53 = getResources().getStringArray(R.array.as_sx_53);
        String[] a54 = getResources().getStringArray(R.array.as_sx_54);
        String[] a55 = getResources().getStringArray(R.array.as_sx_55);
        String[] a56 = getResources().getStringArray(R.array.as_sx_56);
        String[] a57 = getResources().getStringArray(R.array.as_sx_57);
        String[] a58 = getResources().getStringArray(R.array.as_sx_58);
        String[] a59 = getResources().getStringArray(R.array.as_sx_59);
        String[] a60 = getResources().getStringArray(R.array.as_sx_60);
        String[] a61 = getResources().getStringArray(R.array.as_sx_61);
        String[] a62 = getResources().getStringArray(R.array.as_sx_62);
        String[] a63 = getResources().getStringArray(R.array.as_sx_63);
        String[] a64 = getResources().getStringArray(R.array.as_sx_64);
        String[] a65 = getResources().getStringArray(R.array.as_sx_65);
        String[] a66 = getResources().getStringArray(R.array.as_sx_66);
        String[] a67 = getResources().getStringArray(R.array.as_sx_67);
        String[] a68 = getResources().getStringArray(R.array.as_sx_68);
        String[] a69 = getResources().getStringArray(R.array.as_sx_69);
        String[] a70 = getResources().getStringArray(R.array.as_sx_70);
        String[] a71 = getResources().getStringArray(R.array.as_sx_71);
        String[] a72 = getResources().getStringArray(R.array.as_sx_72);
        String[] a73 = getResources().getStringArray(R.array.as_sx_73);
        String[] a74 = getResources().getStringArray(R.array.as_sx_74);
        String[] a75 = getResources().getStringArray(R.array.as_sx_75);
        String[] a76 = getResources().getStringArray(R.array.as_sx_76);
        String[] a77 = getResources().getStringArray(R.array.as_sx_77);
        String[] a78 = getResources().getStringArray(R.array.as_sx_78);
        String[] a79 = getResources().getStringArray(R.array.as_sx_79);
        String[] a80 = getResources().getStringArray(R.array.as_sx_80);
        String[] a81 = getResources().getStringArray(R.array.as_sx_81);
        String[] a82 = getResources().getStringArray(R.array.as_sx_82);
        String[] a83 = getResources().getStringArray(R.array.as_sx_83);
        String[] a84 = getResources().getStringArray(R.array.as_sx_84);
        String[] a85 = getResources().getStringArray(R.array.as_sx_85);
        String[] a86 = getResources().getStringArray(R.array.as_sx_86);
        String[] a87 = getResources().getStringArray(R.array.as_sx_87);
        String[] a88 = getResources().getStringArray(R.array.as_sx_88);
        String[] a89 = getResources().getStringArray(R.array.as_sx_89);
        String[] a90 = getResources().getStringArray(R.array.as_sx_90);
        String[] a91 = getResources().getStringArray(R.array.as_sx_91);
        String[] a92 = getResources().getStringArray(R.array.as_sx_92);
        answers.add(a1);
        answers.add(a2);
        answers.add(a3);
        answers.add(a4);
        answers.add(a5);
        answers.add(a6);
        answers.add(a7);
        answers.add(a8);
        answers.add(a9);
        answers.add(a10);
        answers.add(a11);
        answers.add(a12);
        answers.add(a13);
        answers.add(a14);
        answers.add(a15);
        answers.add(a16);
        answers.add(a17);
        answers.add(a18);
        answers.add(a19);
        answers.add(a20);
        answers.add(a21);
        answers.add(a22);
        answers.add(a23);
        answers.add(a24);
        answers.add(a25);
        answers.add(a26);
        answers.add(a27);
        answers.add(a28);
        answers.add(a29);
        answers.add(a30);
        answers.add(a31);
        answers.add(a32);
        answers.add(a33);
        answers.add(a34);
        answers.add(a35);
        answers.add(a36);
        answers.add(a37);
        answers.add(a38);
        answers.add(a39);
        answers.add(a40);
        answers.add(a41);
        answers.add(a42);
        answers.add(a43);
        answers.add(a44);
        answers.add(a45);
        answers.add(a46);
        answers.add(a47);
        answers.add(a48);
        answers.add(a49);
        answers.add(a50);
        answers.add(a51);
        answers.add(a52);
        answers.add(a53);
        answers.add(a54);
        answers.add(a55);
        answers.add(a56);
        answers.add(a57);
        answers.add(a58);
        answers.add(a59);
        answers.add(a60);
        answers.add(a61);
        answers.add(a62);
        answers.add(a63);
        answers.add(a64);
        answers.add(a65);
        answers.add(a66);
        answers.add(a67);
        answers.add(a68);
        answers.add(a69);
        answers.add(a70);
        answers.add(a71);
        answers.add(a72);
        answers.add(a73);
        answers.add(a74);
        answers.add(a75);
        answers.add(a76);
        answers.add(a77);
        answers.add(a78);
        answers.add(a79);
        answers.add(a80);
        answers.add(a81);
        answers.add(a82);
        answers.add(a83);
        answers.add(a84);
        answers.add(a85);
        answers.add(a86);
        answers.add(a87);
        answers.add(a88);
        answers.add(a89);
        answers.add(a90);
        answers.add(a91);
        answers.add(a92);
    }
    public void initDate(){
        String[] a1 = getResources().getStringArray(R.array.answers_1);
        String[] a2 = getResources().getStringArray(R.array.answers_2);
        String[] a3 = getResources().getStringArray(R.array.answers_3);
        String[] a4 = getResources().getStringArray(R.array.answers_4);
        String[] a5 = getResources().getStringArray(R.array.answers_5);
        String[] a6 = getResources().getStringArray(R.array.answers_6);
        String[] a7 = getResources().getStringArray(R.array.answers_7);
        String[] a8 = getResources().getStringArray(R.array.answers_8);
        String[] a9 = getResources().getStringArray(R.array.answers_9);
        String[] a10 = getResources().getStringArray(R.array.answers_10);
        String[] a11= getResources().getStringArray(R.array.answers_11);
        String[] a12 = getResources().getStringArray(R.array.answers_12);
        String[] a13 = getResources().getStringArray(R.array.answers_13);
        String[] a14 = getResources().getStringArray(R.array.answers_14);
        String[] a15 = getResources().getStringArray(R.array.answers_15);
        String[] a16 = getResources().getStringArray(R.array.answers_16);
        String[] a17 = getResources().getStringArray(R.array.answers_17);
        String[] a18 = getResources().getStringArray(R.array.answers_18);
        String[] a19 = getResources().getStringArray(R.array.answers_19);
        String[] a20 = getResources().getStringArray(R.array.answers_20);
        String[] a21 = getResources().getStringArray(R.array.answers_21);
        String[] a22 = getResources().getStringArray(R.array.answers_22);
        String[] a23 = getResources().getStringArray(R.array.answers_23);
        String[] a24 = getResources().getStringArray(R.array.answers_24);
        String[] a25 = getResources().getStringArray(R.array.answers_25);
        String[] a26 = getResources().getStringArray(R.array.answers_26);
        String[] a27 = getResources().getStringArray(R.array.answers_27);
        String[] a28 = getResources().getStringArray(R.array.answers_28);
        String[] a29 = getResources().getStringArray(R.array.answers_29);
        String[] a30 = getResources().getStringArray(R.array.answers_30);
        String[] a31 = getResources().getStringArray(R.array.answers_31);
        String[] a32 = getResources().getStringArray(R.array.answers_32);
        String[] a33 = getResources().getStringArray(R.array.answers_33);
        String[] a34 = getResources().getStringArray(R.array.answers_34);
        String[] a35 = getResources().getStringArray(R.array.answers_35);
        String[] a36 = getResources().getStringArray(R.array.answers_36);
        String[] a37 = getResources().getStringArray(R.array.answers_37);
        String[] a38 = getResources().getStringArray(R.array.answers_38);
        String[] a39 = getResources().getStringArray(R.array.answers_39);
        String[] a40 = getResources().getStringArray(R.array.answers_40);
        String[] a41 = getResources().getStringArray(R.array.answers_41);
        String[] a42 = getResources().getStringArray(R.array.answers_42);
        String[] a43 = getResources().getStringArray(R.array.answers_43);
        String[] a44 = getResources().getStringArray(R.array.answers_44);
        String[] a45 = getResources().getStringArray(R.array.answers_45);
        String[] a46 = getResources().getStringArray(R.array.answers_46);
        String[] a47 = getResources().getStringArray(R.array.answers_47);
        String[] a48 = getResources().getStringArray(R.array.answers_48);
        String[] a49 = getResources().getStringArray(R.array.answers_49);
        String[] a50 = getResources().getStringArray(R.array.answers_50);
        String[] a51 = getResources().getStringArray(R.array.answers_51);
        String[] a52 = getResources().getStringArray(R.array.answers_52);
        String[] a53 = getResources().getStringArray(R.array.answers_53);
        String[] a54 = getResources().getStringArray(R.array.answers_54);
        String[] a55 = getResources().getStringArray(R.array.answers_55);
        String[] a56 = getResources().getStringArray(R.array.answers_56);
        String[] a57 = getResources().getStringArray(R.array.answers_57);
        String[] a58 = getResources().getStringArray(R.array.answers_58);
        String[] a59 = getResources().getStringArray(R.array.answers_59);
        String[] a60 = getResources().getStringArray(R.array.answers_60);
        String[] a61 = getResources().getStringArray(R.array.answers_61);
        String[] a62 = getResources().getStringArray(R.array.answers_62);
        String[] a63 = getResources().getStringArray(R.array.answers_63);
        String[] a64= getResources().getStringArray(R.array.answers_64);
        String[] a65 = getResources().getStringArray(R.array.answers_65);
        String[] a66 = getResources().getStringArray(R.array.answers_66);
        String[] a67 = getResources().getStringArray(R.array.answers_67);
        String[] a68 = getResources().getStringArray(R.array.answers_68);
        String[] a69 = getResources().getStringArray(R.array.answers_69);
        String[] a70 = getResources().getStringArray(R.array.answers_70);
        String[] a71 = getResources().getStringArray(R.array.answers_71);
        String[] a72 = getResources().getStringArray(R.array.answers_72);
        String[] a73 = getResources().getStringArray(R.array.answers_73);
        String[] a74 = getResources().getStringArray(R.array.answers_74);
        String[] a75 = getResources().getStringArray(R.array.answers_75);
        String[] a76 = getResources().getStringArray(R.array.answers_76);
        String[] a77 = getResources().getStringArray(R.array.answers_77);
        String[] a78 = getResources().getStringArray(R.array.answers_78);
        String[] a79 = getResources().getStringArray(R.array.answers_79);
        String[] a80 = getResources().getStringArray(R.array.answers_80);
        String[] a81 = getResources().getStringArray(R.array.answers_81);
        String[] a82 = getResources().getStringArray(R.array.answers_82);
        String[] a83 = getResources().getStringArray(R.array.answers_83);
        String[] a84 = getResources().getStringArray(R.array.answers_84);
        String[] a85 = getResources().getStringArray(R.array.answers_85);
        String[] a86 = getResources().getStringArray(R.array.answers_86);
        String[] a87 = getResources().getStringArray(R.array.answers_87);
        String[] a88 = getResources().getStringArray(R.array.answers_88);
        String[] a89 = getResources().getStringArray(R.array.answers_89);
        String[] a90 = getResources().getStringArray(R.array.answers_90);
        String[] a91 = getResources().getStringArray(R.array.answers_91);
        String[] a92 = getResources().getStringArray(R.array.answers_92);
        String[] a93 = getResources().getStringArray(R.array.answers_93);
        String[] a94 = getResources().getStringArray(R.array.answers_94);
        String[] a95 = getResources().getStringArray(R.array.answers_95);
        String[] a96 = getResources().getStringArray(R.array.answers_96);
        String[] a97 = getResources().getStringArray(R.array.answers_97);
        String[] a98 = getResources().getStringArray(R.array.answers_98);
        String[] a99 = getResources().getStringArray(R.array.answers_99);
        String[] a100 = getResources().getStringArray(R.array.answers_100);
        String[] a101 = getResources().getStringArray(R.array.answers_101);
        String[] a102 = getResources().getStringArray(R.array.answers_102);
        String[] a103 = getResources().getStringArray(R.array.answers_103);
        String[] a104 = getResources().getStringArray(R.array.answers_104);
        String[] a105= getResources().getStringArray(R.array.answers_105);
        String[] a106 = getResources().getStringArray(R.array.answers_106);
        String[] a107 = getResources().getStringArray(R.array.answers_107);
        String[] a108 = getResources().getStringArray(R.array.answers_108);
        String[] a109 = getResources().getStringArray(R.array.answers_109);
        String[] a110 = getResources().getStringArray(R.array.answers_110);
        String[] a111 = getResources().getStringArray(R.array.answers_111);
        String[] a112 = getResources().getStringArray(R.array.answers_112);
        String[] a113 = getResources().getStringArray(R.array.answers_113);
        String[] a114 = getResources().getStringArray(R.array.answers_114);
        String[] a115 = getResources().getStringArray(R.array.answers_115);
        String[] a116 = getResources().getStringArray(R.array.answers_116);
        String[] a117 = getResources().getStringArray(R.array.answers_117);
        String[] a118 = getResources().getStringArray(R.array.answers_118);
        String[] a119 = getResources().getStringArray(R.array.answers_119);
        String[] a120 = getResources().getStringArray(R.array.answers_120);
        String[] a121 = getResources().getStringArray(R.array.answers_121);
        String[] a122 = getResources().getStringArray(R.array.answers_122);
        String[] a123 = getResources().getStringArray(R.array.answers_123);
        String[] a124 = getResources().getStringArray(R.array.answers_124);
        String[] a125 = getResources().getStringArray(R.array.answers_125);
        String[] a126 = getResources().getStringArray(R.array.answers_126);
        String[] a127 = getResources().getStringArray(R.array.answers_127);
        String[] a128 = getResources().getStringArray(R.array.answers_128);
        String[] a129 = getResources().getStringArray(R.array.answers_129);
        String[] a130 = getResources().getStringArray(R.array.answers_130);
        String[] a131 = getResources().getStringArray(R.array.answers_131);
        String[] a132 = getResources().getStringArray(R.array.answers_132);
        String[] a133 = getResources().getStringArray(R.array.answers_133);
        String[] a134 = getResources().getStringArray(R.array.answers_134);
        String[] a135 = getResources().getStringArray(R.array.answers_135);
        String[] a136 = getResources().getStringArray(R.array.answers_136);
        String[] a137 = getResources().getStringArray(R.array.answers_137);
        String[] a138 = getResources().getStringArray(R.array.answers_138);
        String[] a139 = getResources().getStringArray(R.array.answers_139);
        String[] a140 = getResources().getStringArray(R.array.answers_140);
        String[] a141 = getResources().getStringArray(R.array.answers_141);
        String[] a142 = getResources().getStringArray(R.array.answers_142);
        String[] a143 = getResources().getStringArray(R.array.answers_143);
        String[] a144 = getResources().getStringArray(R.array.answers_144);
        String[] a145 = getResources().getStringArray(R.array.answers_145);
        String[] a146 = getResources().getStringArray(R.array.answers_146);
        String[] a147 = getResources().getStringArray(R.array.answers_147);
        String[] a148 = getResources().getStringArray(R.array.answers_148);
        String[] a149 = getResources().getStringArray(R.array.answers_149);
        String[] a150 = getResources().getStringArray(R.array.answers_150);
        String[] a151 = getResources().getStringArray(R.array.answers_151);
        String[] a152 = getResources().getStringArray(R.array.answers_152);
        String[] a153 = getResources().getStringArray(R.array.answers_153);
        String[] a154 = getResources().getStringArray(R.array.answers_154);
        String[] a155 = getResources().getStringArray(R.array.answers_155);
        String[] a156 = getResources().getStringArray(R.array.answers_156);
        String[] a157 = getResources().getStringArray(R.array.answers_157);
        String[] a158 = getResources().getStringArray(R.array.answers_158);
        String[] a159 = getResources().getStringArray(R.array.answers_159);
        String[] a160 = getResources().getStringArray(R.array.answers_160);
        String[] a161 = getResources().getStringArray(R.array.answers_161);
        String[] a162 = getResources().getStringArray(R.array.answers_162);
        String[] a163 = getResources().getStringArray(R.array.answers_163);
        String[] a164 = getResources().getStringArray(R.array.answers_164);
        String[] a165 = getResources().getStringArray(R.array.answers_165);
        String[] a166 = getResources().getStringArray(R.array.answers_166);
        String[] a167 = getResources().getStringArray(R.array.answers_167);
        String[] a168 = getResources().getStringArray(R.array.answers_168);
        String[] a169 = getResources().getStringArray(R.array.answers_169);
        String[] a170 = getResources().getStringArray(R.array.answers_170);
        String[] a171 = getResources().getStringArray(R.array.answers_171);
        String[] a172 = getResources().getStringArray(R.array.answers_172);
        String[] a173 = getResources().getStringArray(R.array.answers_173);
        String[] a174 = getResources().getStringArray(R.array.answers_174);
        String[] a175 = getResources().getStringArray(R.array.answers_175);
        String[] a176 = getResources().getStringArray(R.array.answers_176);
        String[] a177 = getResources().getStringArray(R.array.answers_177);
        String[] a178 = getResources().getStringArray(R.array.answers_178);
        String[] a179 = getResources().getStringArray(R.array.answers_179);
        String[] a180 = getResources().getStringArray(R.array.answers_180);
        String[] a181 = getResources().getStringArray(R.array.answers_181);
        String[] a182 = getResources().getStringArray(R.array.answers_182);
        String[] a183 = getResources().getStringArray(R.array.answers_183);
        String[] a184 = getResources().getStringArray(R.array.answers_184);
        String[] a185 = getResources().getStringArray(R.array.answers_185);
        String[] a186 = getResources().getStringArray(R.array.answers_186);
        String[] a187 = getResources().getStringArray(R.array.answers_187);
        String[] a188 = getResources().getStringArray(R.array.answers_188);
        String[] a189 = getResources().getStringArray(R.array.answers_189);
        String[] a190 = getResources().getStringArray(R.array.answers_190);
        String[] a191 = getResources().getStringArray(R.array.answers_191);
        String[] a192 = getResources().getStringArray(R.array.answers_192);
        String[] a193 = getResources().getStringArray(R.array.answers_193);
        String[] a194 = getResources().getStringArray(R.array.answers_194);
        String[] a195 = getResources().getStringArray(R.array.answers_195);
        String[] a196 = getResources().getStringArray(R.array.answers_196);
        String[] a197 = getResources().getStringArray(R.array.answers_197);
        String[] a198 = getResources().getStringArray(R.array.answers_198);
        String[] a199 = getResources().getStringArray(R.array.answers_199);
        String[] a200 = getResources().getStringArray(R.array.answers_200);
        String[] a201 = getResources().getStringArray(R.array.answers_201);
        String[] a202 = getResources().getStringArray(R.array.answers_202);
        String[] a203 = getResources().getStringArray(R.array.answers_203);
        String[] a204 = getResources().getStringArray(R.array.answers_204);
        String[] a205 = getResources().getStringArray(R.array.answers_205);
        String[] a206 = getResources().getStringArray(R.array.answers_206);
        String[] a207 = getResources().getStringArray(R.array.answers_207);
        String[] a208 = getResources().getStringArray(R.array.answers_208);
        String[] a209 = getResources().getStringArray(R.array.answers_209);
        String[] a210 = getResources().getStringArray(R.array.answers_210);
        String[] a211 = getResources().getStringArray(R.array.answers_211);
        String[] a212 = getResources().getStringArray(R.array.answers_212);
        String[] a213 = getResources().getStringArray(R.array.answers_213);
        String[] a214 = getResources().getStringArray(R.array.answers_214);
        String[] a215 = getResources().getStringArray(R.array.answers_215);
        String[] a216 = getResources().getStringArray(R.array.answers_216);
        String[] a217 = getResources().getStringArray(R.array.answers_217);
        String[] a218 = getResources().getStringArray(R.array.answers_218);
        String[] a219 = getResources().getStringArray(R.array.answers_219);
        String[] a220 = getResources().getStringArray(R.array.answers_220);
        String[] a221 = getResources().getStringArray(R.array.answers_221);
        String[] a222 = getResources().getStringArray(R.array.answers_222);
        String[] a223 = getResources().getStringArray(R.array.answers_223);
        String[] a224 = getResources().getStringArray(R.array.answers_224);
        String[] a225 = getResources().getStringArray(R.array.answers_225);
        String[] a226 = getResources().getStringArray(R.array.answers_226);
        String[] a227 = getResources().getStringArray(R.array.answers_227);
        String[] a228 = getResources().getStringArray(R.array.answers_228);
        String[] a229 = getResources().getStringArray(R.array.answers_229);
        String[] a230 = getResources().getStringArray(R.array.answers_230);
        String[] a231 = getResources().getStringArray(R.array.answers_231);
        String[] a232= getResources().getStringArray(R.array.answers_232);
        String[] a233 = getResources().getStringArray(R.array.answers_233);
        String[] a234 = getResources().getStringArray(R.array.answers_234);
        String[] a235 = getResources().getStringArray(R.array.answers_235);
        String[] a236 = getResources().getStringArray(R.array.answers_236);
        String[] a237 = getResources().getStringArray(R.array.answers_237);
        String[] a238 = getResources().getStringArray(R.array.answers_238);
        String[] a239 = getResources().getStringArray(R.array.answers_239);
        String[] a240 = getResources().getStringArray(R.array.answers_240);
        String[] a241 = getResources().getStringArray(R.array.answers_241);
        String[] a242 = getResources().getStringArray(R.array.answers_242);
        String[] a243 = getResources().getStringArray(R.array.answers_243);
        String[] a244 = getResources().getStringArray(R.array.answers_244);
        String[] a245 = getResources().getStringArray(R.array.answers_245);
        String[] a246 = getResources().getStringArray(R.array.answers_246);
        String[] a247 = getResources().getStringArray(R.array.answers_247);
        String[] a248 = getResources().getStringArray(R.array.answers_248);
        String[] a249 = getResources().getStringArray(R.array.answers_249);
        String[] a250 = getResources().getStringArray(R.array.answers_250);
        String[] a251 = getResources().getStringArray(R.array.answers_251);
        String[] a252 = getResources().getStringArray(R.array.answers_252);
        String[] a253 = getResources().getStringArray(R.array.answers_253);
        String[] a254 = getResources().getStringArray(R.array.answers_254);
        String[] a255 = getResources().getStringArray(R.array.answers_255);
        String[] a256 = getResources().getStringArray(R.array.answers_256);
        String[] a257 = getResources().getStringArray(R.array.answers_257);
        String[] a258 = getResources().getStringArray(R.array.answers_258);
        String[] a259 = getResources().getStringArray(R.array.answers_259);
        String[] a260 = getResources().getStringArray(R.array.answers_260);
        String[] a261 = getResources().getStringArray(R.array.answers_261);
        String[] a262 = getResources().getStringArray(R.array.answers_262);
        String[] a263 = getResources().getStringArray(R.array.answers_263);
        String[] a264 = getResources().getStringArray(R.array.answers_264);
        String[] a265 = getResources().getStringArray(R.array.answers_265);
        String[] a266 = getResources().getStringArray(R.array.answers_266);
        String[] a267 = getResources().getStringArray(R.array.answers_267);

        answers.add(a1);
        answers.add(a2);
        answers.add(a3);
        answers.add(a4);
        answers.add(a5);
        answers.add(a6);
        answers.add(a7);
        answers.add(a8);
        answers.add(a9);
        answers.add(a10);
        answers.add(a11);
        answers.add(a12);
        answers.add(a13);
        answers.add(a14);
        answers.add(a15);
        answers.add(a16);
        answers.add(a17);
        answers.add(a18);
        answers.add(a19);
        answers.add(a20);
        answers.add(a21);
        answers.add(a22);
        answers.add(a23);
        answers.add(a24);
        answers.add(a25);
        answers.add(a26);
        answers.add(a27);
        answers.add(a28);
        answers.add(a29);
        answers.add(a30);
        answers.add(a31);
        answers.add(a32);
        answers.add(a33);
        answers.add(a34);
        answers.add(a35);
        answers.add(a36);
        answers.add(a37);
        answers.add(a38);
        answers.add(a39);
        answers.add(a40);
        answers.add(a41);
        answers.add(a42);
        answers.add(a43);
        answers.add(a44);
        answers.add(a45);
        answers.add(a46);
        answers.add(a47);
        answers.add(a48);
        answers.add(a49);
        answers.add(a50);
        answers.add(a51);
        answers.add(a52);
        answers.add(a53);
        answers.add(a54);
        answers.add(a55);
        answers.add(a56);
        answers.add(a57);
        answers.add(a58);
        answers.add(a59);
        answers.add(a60);
        answers.add(a61);
        answers.add(a62);
        answers.add(a63);
        answers.add(a64);
        answers.add(a65);
        answers.add(a66);
        answers.add(a67);
        answers.add(a68);
        answers.add(a69);
        answers.add(a70);
        answers.add(a71);
        answers.add(a72);
        answers.add(a73);
        answers.add(a74);
        answers.add(a75);
        answers.add(a76);
        answers.add(a77);
        answers.add(a78);
        answers.add(a79);
        answers.add(a80);
        answers.add(a81);
        answers.add(a82);
        answers.add(a83);
        answers.add(a84);
        answers.add(a85);
        answers.add(a86);
        answers.add(a87);
        answers.add(a88);
        answers.add(a89);
        answers.add(a90);
        answers.add(a91);
        answers.add(a92);
        answers.add(a93);
        answers.add(a94);
        answers.add(a95);
        answers.add(a96);
        answers.add(a97);
        answers.add(a98);
        answers.add(a99);
        answers.add(a100);
        answers.add(a101);
        answers.add(a102);
        answers.add(a103);
        answers.add(a104);
        answers.add(a105);
        answers.add(a106);
        answers.add(a107);
        answers.add(a108);
        answers.add(a109);
        answers.add(a110);
        answers.add(a111);
        answers.add(a112);
        answers.add(a113);
        answers.add(a114);
        answers.add(a115);
        answers.add(a116);
        answers.add(a117);
        answers.add(a118);
        answers.add(a119);
        answers.add(a120);
        answers.add(a121);
        answers.add(a122);
        answers.add(a123);
        answers.add(a124);
        answers.add(a125);
        answers.add(a126);
        answers.add(a127);
        answers.add(a128);
        answers.add(a129);
        answers.add(a130);
        answers.add(a131);
        answers.add(a132);
        answers.add(a133);
        answers.add(a134);
        answers.add(a135);
        answers.add(a136);
        answers.add(a137);
        answers.add(a138);
        answers.add(a139);
        answers.add(a140);
        answers.add(a141);
        answers.add(a142);
        answers.add(a143);
        answers.add(a144);
        answers.add(a145);
        answers.add(a146);
        answers.add(a147);
        answers.add(a148);
        answers.add(a149);
        answers.add(a150);
        answers.add(a151);
        answers.add(a152);
        answers.add(a153);
        answers.add(a154);
        answers.add(a155);
        answers.add(a156);
        answers.add(a157);
        answers.add(a158);
        answers.add(a159);
        answers.add(a160);
        answers.add(a161);
        answers.add(a162);
        answers.add(a163);
        answers.add(a164);
        answers.add(a165);
        answers.add(a166);
        answers.add(a167);
        answers.add(a168);
        answers.add(a169);
        answers.add(a170);
        answers.add(a171);
        answers.add(a172);
        answers.add(a173);
        answers.add(a174);
        answers.add(a175);
        answers.add(a176);
        answers.add(a177);
        answers.add(a178);
        answers.add(a179);
        answers.add(a180);
        answers.add(a181);
        answers.add(a182);
        answers.add(a183);
        answers.add(a184);
        answers.add(a185);
        answers.add(a186);
        answers.add(a187);
        answers.add(a188);
        answers.add(a189);
        answers.add(a190);
        answers.add(a191);
        answers.add(a192);
        answers.add(a193);
        answers.add(a194);
        answers.add(a195);
        answers.add(a196);
        answers.add(a197);
        answers.add(a198);
        answers.add(a199);
        answers.add(a200);
        answers.add(a201);
        answers.add(a202);
        answers.add(a203);
        answers.add(a204);
        answers.add(a205);
        answers.add(a206);
        answers.add(a207);
        answers.add(a208);
        answers.add(a209);
        answers.add(a210);
        answers.add(a211);
        answers.add(a212);
        answers.add(a213);
        answers.add(a214);
        answers.add(a215);
        answers.add(a216);
        answers.add(a217);
        answers.add(a218);
        answers.add(a219);
        answers.add(a220);
        answers.add(a221);
        answers.add(a222);
        answers.add(a223);
        answers.add(a224);
        answers.add(a225);
        answers.add(a226);
        answers.add(a227);
        answers.add(a228);
        answers.add(a229);
        answers.add(a230);
        answers.add(a231);
        answers.add(a232);
        answers.add(a233);
        answers.add(a234);
        answers.add(a235);
        answers.add(a236);
        answers.add(a237);
        answers.add(a238);
        answers.add(a239);
        answers.add(a240);
        answers.add(a241);
        answers.add(a242);
        answers.add(a243);
        answers.add(a244);
        answers.add(a245);
        answers.add(a246);
        answers.add(a247);
        answers.add(a248);
        answers.add(a249);
        answers.add(a250);
        answers.add(a251);
        answers.add(a252);
        answers.add(a253);
        answers.add(a254);
        answers.add(a255);
        answers.add(a256);
        answers.add(a257);
        answers.add(a258);
        answers.add(a259);
        answers.add(a260);
        answers.add(a261);
        answers.add(a262);
        answers.add(a263);
        answers.add(a264);
        answers.add(a265);
        answers.add(a266);
        answers.add(a267);

    }
}
