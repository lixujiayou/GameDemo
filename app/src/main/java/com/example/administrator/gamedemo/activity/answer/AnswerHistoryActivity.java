package com.example.administrator.gamedemo.activity.answer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.AnswerHistory;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.database.Sq;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @auther lixu
 * Created by lixu on 2017/1/10 0010.
 * 答题历史记录
 */
public class AnswerHistoryActivity extends BaseActivity{


    private SQLiteDatabase db;
    private Sq s;

    @BindView(R.id.lv_history)
    ListView lv_history;

    @BindView(R.id.rl_hint)
    RelativeLayout rl_hint;

    private HistoryAdapter historyAdapter;
    private List<AnswerHistory> mAnswerHistories = new ArrayList<>();

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_history);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("时间轴");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        setSupportActionBar(mToolbar);
        s = new Sq(AnswerHistoryActivity.this, "USER1.db", null, 1);
        db = s.getWritableDatabase();
        lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ErroTopicActivity.INTENT_HISTORY,mAnswerHistories.get(i));
                Intent gIntent = new Intent(AnswerHistoryActivity.this,ErroTopicActivity.class);
                gIntent.putExtras(bundle);
                startActivityForResult(gIntent,1);
            }
        });
    }

    @Override
    public void initData() {
        historyAdapter = new HistoryAdapter(mAnswerHistories);
        lv_history.setAdapter(historyAdapter);
        mAnswerHistories.clear();
        mAnswerHistories.addAll(getAllscore());

        if(mAnswerHistories.size() == 0){
            if(isLogin()){
                queryData();
            }
        }else{
            rl_hint.setVisibility(View.GONE);
            lv_history.setVisibility(View.VISIBLE);
            historyAdapter.notifyDataSetChanged();
        }

        Logger.d("查询数据"+mAnswerHistories.size());

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_sync:
                        if(isLogin()){
                            showDiaLog();
                        }else{
                            Intent lINtent = new Intent(AnswerHistoryActivity.this, LoginActivity.class);
                            startActivityForResult(lINtent,1);
                        }
                        break;
                    case R.id.action_clean:
                        showCleanDialog();
                        break;
                }
                return true;
            }
        });
    }

    private void showDiaLog() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("是否将当前记录备份到云端并同步数据？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            syncData();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            ToastUtil3.showToast(AnswerHistoryActivity.this, "程序异常,请稍后重试");
        }
    }
    /**
     * 同步数据
     */
    List<BmobObject> mAnswerHistoriesTemp = new ArrayList<>();
    private void syncData(){
        mAnswerHistoriesTemp.clear();
        showProgressBarDialog(AnswerHistoryActivity.this);
        if(mAnswerHistories.size() == 0){
            //直接下载数据
            queryData();
        }else{
            for(AnswerHistory answerHistory : mAnswerHistories){
                if(!answerHistory.getIs_upload().equals(Constants.SYNC_OK)){
                    answerHistory.setAuther(cUser);
                    answerHistory.setIs_upload(Constants.SYNC_OK);
                    mAnswerHistoriesTemp.add(answerHistory);
                }
            }

            if(mAnswerHistoriesTemp.size() == 0){
                dimssProgressDialog();
                ToastUtil3.showToast(AnswerHistoryActivity.this,"未发现新增记录，无需同步");
                return;
            }
            new BmobBatch().insertBatch(mAnswerHistoriesTemp).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> o, BmobException e) {
                    if(e==null){
                        queryData();
                    }else{
                        dimssProgressDialog();
                        ToastUtil3.showToast(AnswerHistoryActivity.this,"同步异常，请检查网络并重试");
                        Logger.d("同步异常"+s.toString());
                    }
                }
            });
        }
    }

    private void queryData(){
        if(pDialog==null || !pDialog.isShowing()){
            showProgressBarDialog(AnswerHistoryActivity.this);
        }
        BmobQuery<AnswerHistory> bmobQuery = new BmobQuery<>();
        bmobQuery.include(AnswerHistory.HistoryFields.AUTHER);
        bmobQuery.order("-createdAt");
        //bmobQuery.addWhereRelatedTo(AnswerHistory.HistoryFields.AUTHER, new BmobPointer(Constants.getInstance().getUser()));
        bmobQuery.addWhereEqualTo(AnswerHistory.HistoryFields.AUTHER,new BmobPointer(cUser));
        bmobQuery.findObjects(new FindListener<AnswerHistory>() {
            @Override
            public void done(List<AnswerHistory> list, BmobException e) {
                dimssProgressDialog();
                if(e == null) {
                    if (!ToolUtil.isListEmpty(list)) {
                        mAnswerHistoriesTemp.clear();
                        mAnswerHistories.clear();
                        mAnswerHistories.addAll(list);
                        rl_hint.setVisibility(View.GONE);
                        lv_history.setVisibility(View.VISIBLE);
                        historyAdapter.notifyDataSetChanged();

                        db.delete("user1", null, null);
                        for(AnswerHistory answerHistory : list) {
                            ContentValues cv = new ContentValues();
                            cv.put("username", answerHistory.getTime());
                            cv.put("pwd", String.valueOf(answerHistory.getScore()));
                            cv.put("name_h", answerHistory.getResult());
                            cv.put("is_upload", Constants.SYNC_OK);
                            cv.put("type_",answerHistory.getType());
                            db.insert("user1", null, cv);
                        }
                    } else {
                        rl_hint.setVisibility(View.VISIBLE);
                        lv_history.setVisibility(View.GONE);
                    }
                }else{
                    ToastUtil3.showToast(AnswerHistoryActivity.this, "初始化数据失败，请检查网络并重试");
                    Logger.d("初始化数据失败" + s.toString());
                }
            }
        });
    }

    private void showCleanDialog(){
        try {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("此操作会删除您所有的历史信息，是否继续？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cleanData();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            ToastUtil3.showToast(AnswerHistoryActivity.this, "程序异常,请稍后重试");
        }
    }

    private void cleanData(){
        if(pDialog==null || !pDialog.isShowing()){
            showProgressBarDialog(AnswerHistoryActivity.this);
        }
        BmobQuery<AnswerHistory> bmobQuery = new BmobQuery<>();
        bmobQuery.include(AnswerHistory.HistoryFields.AUTHER);
        bmobQuery.order("-createdAt");
        bmobQuery.addWhereEqualTo(AnswerHistory.HistoryFields.AUTHER,new BmobPointer(cUser));
        bmobQuery.findObjects(new FindListener<AnswerHistory>() {
            @Override
            public void done(List<AnswerHistory> list, BmobException e) {
                List<BmobObject> list_temp = new ArrayList<BmobObject>();
                list_temp.addAll(list);
                if(e == null) {
                    if (!ToolUtil.isListEmpty(list)) {
                        new BmobBatch().deleteBatch(list_temp).doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> o, BmobException e) {
                                dimssProgressDialog();
                                if(e==null){
                                    db.delete("user1", null, null);
                                    mAnswerHistories.clear();
                                    mAnswerHistories.addAll(getAllscore());
                                    historyAdapter.notifyDataSetChanged();
                                    rl_hint.setVisibility(View.GONE);
                                    lv_history.setVisibility(View.VISIBLE);
                                    ToastUtil3.showToast(AnswerHistoryActivity.this,"清理成功");
                                }else{
                                    ToastUtil3.showToast(AnswerHistoryActivity.this,"清理失败");
                                    Logger.d("清理失败" + s.toString());
                                }
                            }
                        });
                    } else {
                        dimssProgressDialog();
                        rl_hint.setVisibility(View.VISIBLE);
                        lv_history.setVisibility(View.GONE);
                        db.delete("user1", null, null);
                        mAnswerHistories.clear();
                        mAnswerHistories.addAll(getAllscore());
                        historyAdapter.notifyDataSetChanged();
                        rl_hint.setVisibility(View.GONE);
                        lv_history.setVisibility(View.VISIBLE);
                        ToastUtil3.showToast(AnswerHistoryActivity.this,"清理成功");
                    }
                }else{
                    dimssProgressDialog();
                    ToastUtil3.showToast(AnswerHistoryActivity.this, "初始化数据失败，请检查网络并重试");
                    Logger.d("初始化数据失败" + s.toString());
                }
            }
        });
    }

    public List<AnswerHistory> getAllscore(){
        List<AnswerHistory> str = new ArrayList<>();
//        AnswerHistory mi2 = new AnswerHistory();
        Cursor cursor = db.query("user1", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            AnswerHistory mi = new AnswerHistory();
            mi.setScore(Integer.parseInt(cursor.getString(cursor.getColumnIndex("pwd"))));
            mi.setTime(cursor.getString(cursor.getColumnIndex("username")));
            mi.setResult(cursor.getString(cursor.getColumnIndex("name_h")));
            mi.setIs_upload(cursor.getString(cursor.getColumnIndex("is_upload")));
            mi.setType(cursor.getString(cursor.getColumnIndex("type_")));
            mi.setErro(cursor.getString(cursor.getColumnIndex("erro_")));
            mi.setErroSelect(cursor.getString(cursor.getColumnIndex("erro_select")));
//            Logger.d("Score = "+  cursor.getString(cursor.getColumnIndex("pwd"))
//                    +"Time = "+cursor.getString(cursor.getColumnIndex("username"))
//            +"Result= "+cursor.getString(cursor.getColumnIndex("name_h"))
//            +"Is_upload = "+cursor.getString(cursor.getColumnIndex("is_upload"))
//            +"Type = "+cursor.getString(cursor.getColumnIndex("type_")));
            Students stu = new Students();
            stu.setObjectId(cursor.getString(cursor.getColumnIndex("user_id")));
            str.add(mi);
        }
        return str;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }

    class HistoryAdapter extends BaseAdapter{
        private List<AnswerHistory> mAnswerHistories;
        private HistoryViewHolder historyViewHolder;
        public HistoryAdapter(List<AnswerHistory> answerHistories){
            this.mAnswerHistories = answerHistories;
        }

        @Override
        public int getCount() {
            return mAnswerHistories.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null){
                historyViewHolder = new HistoryViewHolder();
                view = getLayoutInflater().inflate(R.layout.item_history,null);
                historyViewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
                historyViewHolder.tv_result = (TextView) view.findViewById(R.id.tv_result);
                historyViewHolder.tv_score = (TextView) view.findViewById(R.id.tv_score);
                historyViewHolder.tv_type = (TextView) view.findViewById(R.id.tv_type);
                view.setTag(historyViewHolder);
            }else{
                historyViewHolder = (HistoryViewHolder) view.getTag();
            }

            historyViewHolder.tv_time.setText(mAnswerHistories.get(i).getTime());
            historyViewHolder.tv_result.setText(mAnswerHistories.get(i).getResult());
            historyViewHolder.tv_score.setText(String.valueOf(mAnswerHistories.get(i).getScore()));
            historyViewHolder.tv_type.setText(mAnswerHistories.get(i).getType());
            return view;
        }
    }

    class HistoryViewHolder{
        TextView tv_time;
        TextView tv_result;
        TextView tv_type;
        TextView tv_score;
    }
    private SweetAlertDialog pDialog;
    public void showProgressBarDialog(final Activity mContext){
        try {

                pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText("正在连接服务器，请稍等");
                pDialog.setCancelable(false);
                pDialog.show();

        }catch (Exception e){
            Logger.d("ProgressBarDialog的上下文找不到啦！");
        }
    }

    /**
     * 取消进度框
     */
    public void dimssProgressDialog(){
        if(pDialog == null){
            return;
        }
        pDialog.dismiss();
    }
}
