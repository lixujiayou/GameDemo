package com.example.administrator.gamedemo.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.fragment.AnswerFragment;
import com.example.administrator.gamedemo.fragment.MineFragment;
import com.example.administrator.gamedemo.fragment.ShareFragment;
import com.example.administrator.gamedemo.model.bean.version;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseFragmentActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class MainActivity extends BaseFragmentActivity {
    private static int REQUESTPERMISSION = 110 ;
    private final static int WRITE_EXTERNAL_STORAGE_CODE = 102;

    private static final int UPDATECODE = 41;
    private static final int UPDATEPROGRESS = 42;
    private static final int UPDATEINSTALL = 44;
    private static final int NOUPDATECODE = 99;
    @BindView(R.id.ll_main_answer)
    LinearLayout ll_answer;
    @BindView(R.id.ll_main_share)
    LinearLayout ll_share;
    @BindView(R.id.ll_main_mine)
    LinearLayout ll_mine;
    @BindView(R.id.main_bottome)
    LinearLayout mainBottome;
    @BindView(R.id.tv_answer)
    TextView tv_answer;
    @BindView(R.id.tv_share)
    TextView tv_share;
    @BindView(R.id.tv_mine)
    TextView tv_mine;
    @BindView(R.id.framelayout)
    FrameLayout framelayout;
    @BindView(R.id.icon_m_answer)
    ImageView iconMAnswer;
    @BindView(R.id.icon_m_share)
    ImageView iconMShare;
    @BindView(R.id.icon_m_mine)
    ImageView iconMMine;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    private List<Fragment> list_fragmet = new ArrayList<>();

    private FragmentManager fragmentManager;

    private AnswerFragment answerFragment;
    private ShareFragment shareFragment;
    private MineFragment mineFragment;

    private int versionCode;//当前版本号
    private String apkUrl = "";//更新下载路径
    private DownLoadCompleteReceiver ompleteReceiver;
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initViews() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();


        hideFragments(transaction);

        if (answerFragment == null) {
            answerFragment = new AnswerFragment();
            transaction.add(R.id.framelayout, AnswerFragment.getInstance());
        } else {
            transaction.show(answerFragment);
        }
        transaction.commit();

        ompleteReceiver = new DownLoadCompleteReceiver();
        /** register download success broadcast **/
        registerReceiver(ompleteReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void initData() {
        try {
            versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        BmobQuery<version> allbike_temp = new BmobQuery<>();
        allbike_temp.order("-createdAt");
        allbike_temp.findObjects(new FindListener<version>() {
            @Override
            public void done(List<version> list, BmobException e) {
                int v_n = list.get(0).getVersion_num();
                BmobFile bApk = list.get(0).getApk();
                apkUrl = bApk.getUrl();
                if(v_n > versionCode){
                    TiShiUpdate();
                }
            }
        });
    }


    private void OnTabSelected(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (index) {
            case 0:
                hideFragments(transaction);
                if (answerFragment == null) {
                    answerFragment = AnswerFragment.getInstance();
                    transaction.add(R.id.framelayout, answerFragment);
                } else {
                    transaction.show(answerFragment);
                }
                break;
            case 1:
                hideFragments(transaction);
                if (shareFragment == null) {
                    shareFragment = new ShareFragment();
                    transaction.add(R.id.framelayout, shareFragment);
                } else {
                    transaction.show(shareFragment);
                }
                break;
            case 2:
                hideFragments(transaction);
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.framelayout, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }


    private void hideFragments(FragmentTransaction transaction) {
        if (answerFragment != null) {
            transaction.hide(answerFragment);
        }
        if (shareFragment != null) {
            transaction.hide(shareFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }


  /*  @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        // 动态设置ToolBar状态
        switch (vp.getCurrentItem()) {
            case 0:
                setSupportActionBar(AnswerFragment.getInstance().toolbar);
                menu.findItem(R.id.action_share).setVisible(false);
                menu.findItem(R.id.action_message).setVisible(false);
                break;
            case 1:
                setSupportActionBar(ShareFragment.getInstance().toolbar);
                menu.findItem(R.id.action_share).setVisible(true);
                menu.findItem(R.id.action_message).setVisible(false);
                break;
            case 2:
             //   setSupportActionBar(MineFragment.getInstance().toolbar);
                menu.findItem(R.id.action_share).setVisible(false);
                menu.findItem(R.id.action_message).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }*/


    private void changePage(int postion) {
        switch (postion) {
            case 0:
                tv_answer.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                tv_share.setTextColor(ContextCompat.getColor(this, R.color.textcolor_m));
                tv_mine.setTextColor(ContextCompat.getColor(this, R.color.textcolor_m));
                iconMAnswer.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_answer_p));
                iconMShare.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_share));
                iconMMine.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_mine));
                break;
            case 1:
                tv_answer.setTextColor(ContextCompat.getColor(this, R.color.textcolor_m));
                tv_share.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                tv_mine.setTextColor(ContextCompat.getColor(this, R.color.textcolor_m));
                iconMAnswer.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_answer));
                iconMShare.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_share_p));
                iconMMine.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_mine));
                break;
            case 2:
                tv_answer.setTextColor(ContextCompat.getColor(this, R.color.textcolor_m));
                tv_share.setTextColor(ContextCompat.getColor(this, R.color.textcolor_m));
                tv_mine.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                iconMAnswer.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_answer));
                iconMShare.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_share));
                iconMMine.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.icon_mine_p));
                break;
        }
    }


    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil3.showToast(MainActivity.this, "再按一次退出圣经问答");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick({R.id.ll_main_answer, R.id.ll_main_mine, R.id.main_bottome})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_main_answer:
                changePage(0);
                OnTabSelected(0);
                break;
            case R.id.ll_main_mine:
                changePage(2);
                OnTabSelected(2);
                break;
            case R.id.main_bottome:
                changePage(1);
                OnTabSelected(1);
                break;
        }
    }

    /**
     * 提示用户更新
     */
    private void TiShiUpdate(){
        new AlertDialog.Builder(this)
                .setTitle("更新提醒:")
                .setMessage("《圣经问答APP》有新版本，请更新。")
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this
                                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                    , REQUESTPERMISSION);
                            ToastUtil3.showToast(MainActivity.this, "若取消权限，会导致部分功能无法使用");
                        }else{
                            if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(MainActivity.this
                                        , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                        , WRITE_EXTERNAL_STORAGE_CODE);
                                ToastUtil3.showToast(MainActivity.this, "若取消权限，会导致部分功能无法使用");
                            }else{
                                downAPK();
                                ToastUtil3.showToast(MainActivity.this, "正在下载，请注意上方提示栏");
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    /**
     * 下载新版本应用
     */
    private void downAPK(){
        ToastUtil3.showToast(this, "开始下载，请注意通知栏");
        File dir = new File(Constants.FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(Constants.FILE_PATH,"autoapk.apk");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        //设置在什么网络情况下进行下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //设置通知栏标题

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("圣经问答");
        request.setAllowedNetworkTypes((DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI));
        request.setAllowedOverRoaming(true);
        request.setDescription("");
        request.setAllowedOverRoaming(false);
        //设置文件存放目录
        request.setDestinationInExternalPublicDir("bibleAsk", "/"+Constants.FILENAME);
        DownloadManager downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downManager.enqueue(request);


    }
    /**
     * 安装新版本应用
     */
    private void installApp() {
        File appFile = new File(Constants.FILE_NAME);
        if(!appFile.exists()) {
            return;
        }
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        this.startActivity(intent);
    }
    /**
     * 下载广播接收
     */
    private class DownLoadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                //long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                installApp();
            }else if(intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)){
            }
        }
    }
    /**
     * 权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTPERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this
                                , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                , WRITE_EXTERNAL_STORAGE_CODE);
                        ToastUtil3.showToast(MainActivity.this, "请同意权限");
                    }else{
                        ToastUtil3.showToast(MainActivity.this, "正在下载，请注意上方提示栏");
                        downAPK();
                    }
                } else {
                    //拒绝权限
                    ToastUtil3.showToast(MainActivity.this, "由于您取消权限，下载失败");
                }
            }
        }

        if (requestCode == WRITE_EXTERNAL_STORAGE_CODE) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtil3.showToast(MainActivity.this, "正在下载，请注意上方提示栏");
                    downAPK();
                } else {
                    ToastUtil3.showToast(MainActivity.this, "由于您取消权限，下载失败");
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(ompleteReceiver);


    }
}
