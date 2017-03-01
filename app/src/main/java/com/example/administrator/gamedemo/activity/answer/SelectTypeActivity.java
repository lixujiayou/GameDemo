package com.example.administrator.gamedemo.activity.answer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolBarHelper;
import com.example.administrator.gamedemo.widget.imageview.CircleImageView;
import com.example.administrator.gamedemo.widget.imageview.CircleLayout;


/**
 * Created by Administrator on 2016-03-07.
 */
public class SelectTypeActivity extends AppCompatActivity implements CircleLayout.OnItemSelectedListener, CircleLayout.OnItemClickListener {
    private TextView selectedTextView;
    private int num_ = 0;
    private Dialog ready_d;
    private boolean sound = true;
    private int myS = 0;
    private MediaPlayer readllyGo = new MediaPlayer();
    private IntentHandler ih = new IntentHandler();
    private CircleLayout circleMenu;
    private Toolbar mToolbar;

    private ObjectAnimator animatorA;
    private ObjectAnimator animatorB;
    private ObjectAnimator animatorC;
    private ObjectAnimator animatorD;
    private ObjectAnimator animatorE;
    private ObjectAnimator animatorF;

    private View mView;
    private CircleImageView circleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);

        mView = findViewById(R.id.view);
        readllyGo = MediaPlayer.create(SelectTypeActivity.this, R.raw.readygo01);
        circleMenu = (CircleLayout)findViewById(R.id.main_circle_layout);

        circleMenu.setOnItemSelectedListener(this);
        circleMenu.setOnItemClickListener(this);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        selectedTextView = (TextView)findViewById(R.id.main_selected_textView);

        circleImageView = (CircleImageView) findViewById(R.id.main_facebook_image);

        selectedTextView.setText(((CircleImageView) circleMenu.getSelectedItem()).getName());

        View dView = getLayoutInflater().inflate(R.layout.dialog_ready,null);
        ready_d = new Dialog(SelectTypeActivity.this, R.style.dialog);
        ready_d.setContentView(dView);
        ready_d.setCanceledOnTouchOutside(false);
        ImageView dIv = (ImageView) dView.findViewById(R.id.iv_dialog);

         animatorA = ObjectAnimator.ofFloat(mView, "TranslationX", -300, 300, 0);
         animatorB = ObjectAnimator.ofFloat(mView, "scaleY", 0.5f, 1.5f, 1f);
         animatorC = ObjectAnimator.ofFloat(mView, "rotation", 0, 270, 90, 180, 0);

        animatorD = ObjectAnimator.ofFloat(selectedTextView, "TranslationX", -300, 300, 0);
        animatorE = ObjectAnimator.ofFloat(selectedTextView, "scaleY", 0.5f, 1.5f, 1f);
        animatorF = ObjectAnimator.ofFloat(selectedTextView, "rotation", 0, 270, 90, 180, 0);

        selectedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                num_ = 0;
                readllyGo.start();

                ready_d.show();
/*
                AnimatorSet animatorSet3 = new AnimatorSet();
                animatorSet3.playTogether(animatorD, animatorE, animatorF);
                animatorSet3.setDuration(3*1000);
                animatorSet3.start();

                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(animatorA, animatorB, animatorC);
                animatorSet2.setDuration(3*950);
                animatorSet2.start();
                animatorSet2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ready_d.dismiss();
                        Intent sIntent = new Intent(SelectTypeActivity.this, Starting.class);
                        sIntent.putExtra("type",myS);
                        sIntent.putExtra("yinliang",sound);
                        startActivity(sIntent);
                        overridePendingTransition(R.anim.block_move_right, R.anim.small_2_big);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });*/

                //overridePendingTransition(inAnim, outAnim);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (num_ <= 3) {
                            try {
                                Thread.sleep(500);
                                ih.sendEmptyMessage(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });





        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        mToolbar.setTitle("选择答题范围");

        /*把 toolbar 设置到Activity 中*/
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_sound_n:
                        sound = true;
                        break;
                    case R.id.action_sound_y:
                        sound = false;
                        break;
                }
                invalidateOptionsMenu();
                return true;
            }
        });


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SelectTypeActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(View view, int position, long id, String name) {
        selectedTextView.setText(name);
        myS = position;
    }

    @Override
    public void onItemClick(View view, int position, long id, String name) {
        Toast.makeText(getApplicationContext(),name, Toast.LENGTH_SHORT).show();
    }
    class IntentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            num_+=1;
            if(num_ ==1) {

                //Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.start_);
                Intent sIntent = new Intent(SelectTypeActivity.this, Starting.class);
                sIntent.putExtra("type",myS);
                sIntent.putExtra("yinliang",sound);
                startActivity(sIntent);
                overridePendingTransition(R.anim.block_move_right, R.anim.small_2_big);
            }
            else if(num_ == 2) {
                ready_d.dismiss();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_sound, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
      if(sound){
          menu.findItem(R.id.action_sound_n).setVisible(false);
          menu.findItem(R.id.action_sound_y).setVisible(true);
          readllyGo.setVolume(1,1);
      }else{
          menu.findItem(R.id.action_sound_n).setVisible(true);
          menu.findItem(R.id.action_sound_y).setVisible(false);
          readllyGo.setVolume(0,0);
      }
        return super.onPrepareOptionsMenu(menu);
    }

}
