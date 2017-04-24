package com.example.administrator.gamedemo.activity.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.adapter.RoutinesAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.NoteInfo;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.RevealBackgroundView;
import com.example.administrator.gamedemo.widget.database.BookDatabaseUtil;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @auther lixu
 * Created by lixu on 2017/1/9 0009.
 * 笔记展示页面
 */
public class NotesListActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener{

    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    public static final int UPDATE_BOOK = 0;
    public static final int NEW_BOOK = 1;
    public static final int RENEW_BOOK = 2;
    public static final int DELETE_BOOK = 3;

    @BindView(R.id.revealBackgroundView)
    RevealBackgroundView revealBackgroundView;
    @BindView(R.id.itemRecyclerView)
    RecyclerView routinesRecyclerView;
    @BindView(R.id.addBtn)
    FloatingActionButton addRoutinesItemBtn;

    private RoutinesAdapter routinesAdapter;
    private boolean pendingIntro;
    private ArrayList<NoteInfo> mDataSet;
    private int itemPosition = 1;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_notes);
        ButterKnife.bind(this);
        setupDatabase();
        setupGridLayout();
        setupRevealBackground(savedInstanceState);
        initAddItemBtn(addRoutinesItemBtn);
    }

    @Override
    public void initViews() {
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);

    }

    @Override
    public void initData() {

    }

    private void setupDatabase() {
        if(isLogin()){
            mDataSet = BookDatabaseUtil.getInstance(NotesListActivity.this).queryBookInfos();
        if (mDataSet == null ) {
            BmobQuery<NoteInfo> bmobQuery = new BmobQuery<>();
            bmobQuery.addWhereEqualTo(NoteInfo.NotesFields.AUTHOR,new BmobPointer(cUser));
            //bmobQuery.setLimit(10);
            boolean isCache = bmobQuery.hasCachedResult(NoteInfo.class);
            if (isCache) {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            } else {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            }
            bmobQuery.findObjects(new FindListener<NoteInfo>() {
                @Override
                public void done(List<NoteInfo> list, BmobException e) {
                    if(e == null){
                        mDataSet = (ArrayList<NoteInfo>) BookDatabaseUtil.getInstance(NotesListActivity.this).setBookInfos(list);
                    }else{
                        mDataSet = new ArrayList<>();
                    }
                }
            });
        }
        }else{
            mDataSet = BookDatabaseUtil.getInstance(NotesListActivity.this).queryBookInfos();
        }
    }

    private void setupGridLayout() {

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        routinesRecyclerView.setLayoutManager(layoutManager);
        routinesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                routinesAdapter.setLockedAnimations(true);
            }
        });
        /*        RecyclerViewScrollManager scrollManager = new RecyclerViewScrollManager();
                scrollManager.attach(routinesRecyclerView);
                scrollManager.addView(addRoutinesItemBtn, RecyclerViewScrollManager.Direction.DOWN); //下滑动画*/
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        revealBackgroundView.setFillPaintColor(ContextCompat.getColor(NotesListActivity.this,R.color.colorPrimary));
        revealBackgroundView.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            revealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    revealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    revealBackgroundView.startFromLocation(new int[]{0, 0});
                    return true;
                }
            });
        } else {
            revealBackgroundView.setToFinishedFrame();
            routinesAdapter.setLockedAnimations(true);
        }
    }

    private void initAddItemBtn(final FloatingActionButton imageButton) {
        imageButton.setImageResource(R.drawable.ic_add_white_36dp);
        //初始化隐藏Button
        imageButton.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageButton.getViewTreeObserver().removeOnPreDrawListener(this);
                pendingIntro = true;
                imageButton.setTranslationY(2 * imageButton.getHeight());
                return true;
            }
        });
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            routinesRecyclerView.setVisibility(View.VISIBLE);

            routinesAdapter = new RoutinesAdapter(this, mDataSet);
            routinesRecyclerView.setAdapter(routinesAdapter);
            routinesRecyclerView.setItemAnimator(new DefaultItemAnimator());
            //item点击
            routinesAdapter.setOnItemClickListener(new RoutinesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position == 0) return;
                    itemPosition = position;
                    NoteInfo getData = routinesAdapter.getItemData(position - 1);
                    Intent intent = new Intent(NotesListActivity.this, WriteNoteActivity.class);
                    intent.putExtra(WriteNoteActivity.INTENT_ISCHANGE,true);
                    intent.putExtra(RoutinesAdapter.BOOK_NAME, getData.getTitle());
                    intent.putExtra(RoutinesAdapter.BOOK_CONTENT, getData.getContent());
                    intent.putExtra(RoutinesAdapter.BOOK_COLOR_INDEX, (int) getData.getNoteColor());
                    intent.putExtra(RoutinesAdapter.BOOK_ALARM_TIME, getData.getCreatedAt());
                    startActivityForResult(intent, UPDATE_BOOK);
                }

                @Override
                public void onItemDeleteClick(View view, int position) {
                    deleteItem(position - 1, true);
                }
            });

            if (pendingIntro) {
                startIntroAnimation();
            }
        } else {
            routinesRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private void startIntroAnimation() {
        addRoutinesItemBtn.animate().translationY(0).setStartDelay(300).setDuration(400).setInterpolator(new OvershootInterpolator(1.0f)).start();
    }

    @OnClick(R.id.addBtn)
    public void onAddClick() {
        itemPosition = Integer.MAX_VALUE;
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(NotesListActivity.this, WriteNoteActivity.class);
        intent.putExtra(WriteNoteActivity.INTENT_ISCHANGE,false);
        intent.putExtra(RoutinesAdapter.BOOK_NAME, getResources().getString(R.string.text_book));
        intent.putExtra(RoutinesAdapter.BOOK_COLOR_INDEX, new Random().nextInt(4));
        intent.putExtra(RoutinesAdapter.BOOK_ALARM_TIME, DATE_FORMAT.format(calendar.getTime()));
        startActivityForResult(intent, NEW_BOOK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name;
        String content;
        int colorIndex;
        String time = "";

        switch (requestCode) {
            case UPDATE_BOOK:
                if (resultCode == RENEW_BOOK) {
                    name = data.getStringExtra(RoutinesAdapter.BOOK_NAME);
                    content = data.getStringExtra(RoutinesAdapter.BOOK_CONTENT);
                    colorIndex = data.getIntExtra(RoutinesAdapter.BOOK_COLOR_INDEX, new Random().nextInt(4));
                    time = data.getStringExtra(RoutinesAdapter.BOOK_ALARM_TIME);
                    updateItem(itemPosition - 1, name,colorIndex,content, time);
                } else if (resultCode == DELETE_BOOK) {
                    deleteItem(itemPosition - 1, false);
                }
                break;
            case NEW_BOOK:
                if (resultCode == RENEW_BOOK) {
                    name = data.getStringExtra(RoutinesAdapter.BOOK_NAME);
                    content = data.getStringExtra(RoutinesAdapter.BOOK_CONTENT);
                    colorIndex = data.getIntExtra(RoutinesAdapter.BOOK_COLOR_INDEX, new Random().nextInt(4));
                    time = data.getStringExtra(RoutinesAdapter.BOOK_ALARM_TIME);
                    addItem(name,content, colorIndex, time);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //addItem,deleteItem,updateItem 的position从0开始
    //添加
    private void addItem(String name,String content, int colorIndex, String time) {
        final NoteInfo bookInfo = new NoteInfo();
        bookInfo.setAuthor(Constants.getInstance().getUser());
        bookInfo.setTitle(name);
        bookInfo.setContent(content);
        bookInfo.setNoteColor(colorIndex);
        bookInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
            if(e == null ){
                ToastUtil3.showToast(NotesListActivity.this,"编辑成功");
                BookDatabaseUtil.getInstance(NotesListActivity.this).insertBookInfo(bookInfo);
            }else{
                Snackbar.make(addRoutinesItemBtn,"新增失败,已为您新增到本地，请检查网络并点击该笔记重新提交", Snackbar.LENGTH_INDEFINITE).setAction("Error", null).show();
                BookDatabaseUtil.getInstance(NotesListActivity.this).insertBookInfo(bookInfo, bookInfo, true);
            }
            }
        });

        mDataSet.add(0, bookInfo);
        routinesAdapter.notifyDataSetChanged();
    }

    //删除
    private void deleteItem(int position, boolean setAnimator) {
        final NoteInfo bookInfo = mDataSet.get(position);
        bookInfo.delete(bookInfo.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    BookDatabaseUtil.getInstance(NotesListActivity.this).deleteBookInfo(bookInfo);
                }else{
                    BookDatabaseUtil.getInstance(NotesListActivity.this).deleteBookInfo(bookInfo, true);
                }
            }
        });

        int size = mDataSet.size();
        if (size > 0 && position < size) {
            mDataSet.remove(position);
            if (setAnimator) {
                routinesAdapter.notifyItemRemoved(position + 1);
                routinesAdapter.notifyItemRangeChanged(position + 1, mDataSet.size());
            } else {
                routinesAdapter.notifyDataSetChanged();
            }
        }
    }

    //更新
    private void updateItem(final int position, String name,int colorIndex,String content, String time) {
        final NoteInfo bookInfo = mDataSet.get(position);
        final NoteInfo bookInfoOld = new NoteInfo(
                  bookInfo.getAuthor()
                , bookInfo.getTitle()
                , bookInfo.getContent()
                , bookInfo.getNoteColor()
                , bookInfo.getNoteCreateTime());
        bookInfo.setAuthor(Constants.getInstance().getUser());
        bookInfo.setTitle(name);
        bookInfo.setContent(content);
        bookInfo.setNoteColor(colorIndex);
        bookInfo.setNoteCreateTime(time);
        if (bookInfo.getObjectId() == null) {
            Logger.d( "into : test id = null");
            bookInfo.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e == null){
                        ToastUtil3.showToast(NotesListActivity.this,"更新成功");
                        BookDatabaseUtil.getInstance(NotesListActivity.this).insertBookInfo(bookInfo, bookInfoOld, true);
                    }else{

                        Snackbar.make(addRoutinesItemBtn,"更新失败,已将本地笔记更新，请检查网络并点击该笔记重新提交", Snackbar.LENGTH_INDEFINITE).setAction("Error", null).show();
                        BookDatabaseUtil.getInstance(NotesListActivity.this).insertBookInfo(bookInfo, bookInfoOld, true); //无效invalid ObjectId
                    }
                }
            });
        } else {
            //服务器

            bookInfo.update(bookInfo.getObjectId(),new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        BookDatabaseUtil.getInstance(NotesListActivity.this).insertBookInfo(bookInfo);
                    }else{
                        BookDatabaseUtil.getInstance(NotesListActivity.this).insertBookInfo(bookInfo, bookInfoOld, true); //无效invalid ObjectId
                    }
                }
            });
        }

        int size = mDataSet.size();
        if (position < size) {
            mDataSet.set(position, bookInfo);
            routinesAdapter.notifyDataSetChanged();
        }

    }

}
