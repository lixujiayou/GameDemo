package com.example.administrator.gamedemo.activity.answer;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.ToolBarHelper;
import com.example.administrator.gamedemo.widget.imageview.CircleImageView;
import com.example.administrator.gamedemo.widget.imageview.CircleLayout;


/**
 * Created by Administrator on 2016-03-07.
 */
public class SelectTypeActivity extends AppCompatActivity implements CircleLayout.OnItemSelectedListener, CircleLayout.OnItemClickListener {
    TextView selectedTextView;
    int num_ = 0;
    Dialog ready_d;
    boolean sound;
    int myS = 0;
    MediaPlayer readllyGo = new MediaPlayer();
    IntentHandler ih = new IntentHandler();
    CircleLayout circleMenu;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);
        Intent pIntent = getIntent();
        sound =  pIntent.getExtras().getBoolean("yinliang");
        readllyGo = MediaPlayer.create(SelectTypeActivity.this, R.raw.readygo01);
        if(sound){
            readllyGo.setVolume(1,1);
        }else{
            readllyGo.setVolume(0,0);
        }
         circleMenu = (CircleLayout)findViewById(R.id.main_circle_layout);
        circleMenu.setOnItemSelectedListener(this);
        circleMenu.setOnItemClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        selectedTextView = (TextView)findViewById(R.id.main_selected_textView);
        selectedTextView.setText(((CircleImageView) circleMenu.getSelectedItem()).getName());
        selectedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_ = 0;
                readllyGo.start();
                ready_d = new Dialog(SelectTypeActivity.this, R.style.dialog);
                ready_d.setContentView(R.layout.dialog_ready);
                ready_d.setCanceledOnTouchOutside(false);
                ready_d.show();
                //  iv_readllygo.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (num_<=3) {
                            try {
                                Thread.sleep(800);
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
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    public void onCreateCustomToolBar(Toolbar toolbar){
        toolbar.setContentInsetsRelative(0, 0);
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
            else if(num_ ==2) {
                ready_d.dismiss();
            }
        }
    }
}
