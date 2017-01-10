package com.example.administrator.gamedemo.activity.notes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.adapter.RoutinesAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.NoteInfo;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @auther lixu
 * Created by lixu on 2017/1/9 0009.
 * 写笔记
 */
public class WriteNoteActivity extends BaseActivity {
    public static final String INTENT_ISCHANGE = "isChange";
    public static final String INTENT_NOTE = "cNote";

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.ll_preview)
    LinearLayout llPreview;

    @BindView(R.id.ll_write)
    RelativeLayout ll_write;

    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private boolean isChange;
    private boolean isPreView;
    private NoteInfo cNote;
    private String getBookAlarmTime;
    private Intent gIntent;
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_write_note);
    }

    @Override
    public void initViews() {
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        setSupportActionBar(mToolbar);
          gIntent = getIntent();
        isChange = gIntent.getExtras().getBoolean(INTENT_ISCHANGE);
        if (isChange) {

            cNote = (NoteInfo) gIntent.getSerializableExtra(INTENT_NOTE);

                int[] colorBook = getResources().getIntArray(R.array.style_book_color);
                int getBookColorIndex = gIntent.getIntExtra(RoutinesAdapter.BOOK_COLOR_INDEX, new Random().nextInt(4));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.alpha(colorBook[getBookColorIndex]));
                }
                appBarLayout.setBackgroundColor(colorBook[getBookColorIndex]);
                isPreView = true;
                init();
                llPreview.setVisibility(View.VISIBLE);
                ll_write.setVisibility(View.GONE);

        } else {
            isPreView = false;
            collapsingToolbarLayout.setTitle("写笔记");
        }
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_send:
                        String name = etTitle.getText().toString().trim();
                        String content = etContent.getText().toString().trim();
                        if (name.isEmpty()) {
                            Snackbar.make(fab,"标题不能为空", Snackbar.LENGTH_SHORT).setAction("Error", null).show();
                            YoYo.with(Techniques.Wobble).duration(500).delay(100).playOn(etTitle);
                            break;
                        } else if(content.isEmpty()){
                            Snackbar.make(fab,"内容不能为空", Snackbar.LENGTH_SHORT).setAction("Error", null).show();
                            YoYo.with(Techniques.Wobble).duration(500).delay(100).playOn(etContent);
                        }else {
                            updateAlarmTimeView();
                           // Intent intent = new Intent();
                            gIntent.putExtra(RoutinesAdapter.BOOK_NAME, name);
                            gIntent.putExtra(RoutinesAdapter.BOOK_CONTENT, content);
                            gIntent.putExtra(RoutinesAdapter.BOOK_ALARM_TIME, getBookAlarmTime);
                            setResult(NotesListActivity.RENEW_BOOK, gIntent);
                            finish();
                        }
                        break;
                    case R.id.action_delete:
                        setResult(NotesListActivity.DELETE_BOOK, gIntent);
                        finish();
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;

    }

    @Override
    public void initData() {
    }

    private void init() {
        String name = gIntent.getStringExtra(RoutinesAdapter.BOOK_NAME);
        String content = gIntent.getStringExtra(RoutinesAdapter.BOOK_CONTENT);
        collapsingToolbarLayout.setTitle(name);
        tvContent.setText(content);
        etTitle.setText(name);
        etContent.setText(content);

    }

    @OnClick(R.id.fab)
    public void onClick() {
        if(isPreView){
            llPreview.setVisibility(View.GONE);
            ll_write.setVisibility(View.VISIBLE);
            isPreView = false;
        }else{

            llPreview.setVisibility(View.VISIBLE);
            ll_write.setVisibility(View.GONE);
            collapsingToolbarLayout.setTitle(etTitle.getText().toString());
            tvContent.setText(etContent.getText().toString());
            isPreView = true;
        }
    }

    private void updateAlarmTimeView() {
        getBookAlarmTime = Constants.StringData();
    }
}
