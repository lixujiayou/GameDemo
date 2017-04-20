package com.example.administrator.gamedemo.activity.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ClipImageActivity;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.simage.Crop;
import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by lixu on 2016/12/22 0022.
 * 个人中心
 */
public class MineCenterActivity extends BaseActivity {

    private static final int CAMERA_REQUEST_CODE = 1458;
    private static final int GALLERY_REQUEST_CODE = 1450;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;

    @BindView(R.id.ll_change_user)
    LinearLayout ll_change;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.ll_icon)
    LinearLayout llIcon;
    @BindView(R.id.atv_mobile)
    TextView atvMobile;
    @BindView(R.id.ll_mobile)
    LinearLayout llMobile;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.ll_nickname)
    LinearLayout llNickname;
    @BindView(R.id.ll_pwd)
    LinearLayout llPwd;
    @BindView(R.id.tv_note)
    TextView tvNote;
    @BindView(R.id.ll_note)
    LinearLayout llNote;

    @BindView(R.id.ll_exit)
    LinearLayout llExit;


    private File mTempDir;//修改头像 选取的图片路径
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_center);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("修改资料");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textcolor_2));
        mToolbar.setNavigationIcon(R.drawable.icon_cancle_black);

        mTempDir = new File( Environment.getExternalStorageDirectory(),"bibleAsk");
        if(!mTempDir.exists()){
            mTempDir.mkdirs();
        }
    }

    @Override
    public void initData() {
        Students students= Constants.getInstance().getUser();
        if(students != null) {

            if(students.getUser_icon() != null){
                ImageLoadMnanger.INSTANCE.loadIconImage(MineCenterActivity.this,ivIcon,students.getUser_icon().getFileUrl());
            }

            atvMobile.setText(students.getUsername());
            tvNickname.setText(students.getNick_name());
            tvNote.setText(students.getMyself_speak());
        }else{
            atvMobile.setText("请登录");
            tvNickname.setText("无");
            tvNote.setText("无");
        }
    }

    @OnClick({R.id.ll_icon, R.id.ll_mobile, R.id.ll_nickname, R.id.ll_pwd, R.id.ll_note, R.id.ll_change_user, R.id.ll_exit})
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.ll_icon:
                if(isLogin()){
                    showPhotoDialog();
                }else{
                    startLogin();
                }

                break;
            case R.id.ll_mobile:
                if(isLogin()){
                    ToastUtil3.showToast(MineCenterActivity.this,"无法修改");
                }else{
                    startLogin();
                }

                break;
            case R.id.ll_nickname:
                if(!isLogin()){
                    startLogin();
                }else {
                    Intent mIntent = new Intent(MineCenterActivity.this, AllModifyActivity.class);
                    mIntent.putExtra(AllModifyActivity.MODIFY, AllModifyActivity.MODIFY_NICKNAME);
                    startActivityForResult(mIntent, 1);
                }
                break;
            case R.id.ll_pwd:
                if(!isLogin()){
                    startLogin();
                }else {
                    Intent pIntent = new Intent(MineCenterActivity.this, AllModifyActivity.class);
                    pIntent.putExtra(AllModifyActivity.MODIFY, AllModifyActivity.MODIFY_PWD);
                    startActivityForResult(pIntent, 1);
                }
                break;
            case R.id.ll_note:
                if(!isLogin()){
                    startLogin();
                }else {
                    Intent nIntent = new Intent(MineCenterActivity.this, AllModifyActivity.class);
                    nIntent.putExtra(AllModifyActivity.MODIFY, AllModifyActivity.MODIFY_NOTE);
                    startActivityForResult(nIntent, 1);
                }
                break;
            case R.id.ll_change_user:
                if(!isLogin()){
                    startLogin();
                }else {
                    showDiaLog("即将退出当前账号，是否继续？", false);
                }
                break;
            case R.id.ll_exit:
                showDiaLog("即将退出《圣经问答APP》，是否继续？",true);
                break;
        }
    }

    private void startLogin(){
        Intent iIntent = new Intent(MineCenterActivity.this,LoginActivity.class);
        startActivityForResult(iIntent,1);
    }

    private void showDiaLog(String title, final boolean isExit) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(title)
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(isExit){
                                setResult(Constants.EXITAPP);
                                finish();
                            }else{
                                BmobUser.logOut();
                                //Constants.getInstance().getUser() = null;
                                Intent gIntent = new Intent(MineCenterActivity.this, LoginActivity.class);
                                startActivityForResult(gIntent,1);
                            }
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            ToastUtil3.showToast(MineCenterActivity.this, "程序异常,请稍后重试");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Constants.LOGIN_OK){
            initData();
        }else if(resultCode == Constants.GOLOGIN){
            Intent gIntent = new Intent(MineCenterActivity.this,LoginActivity.class);
            startActivityForResult(gIntent,1);
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:   // 调用相机拍照
                    File temp = new File(mCurrentPhotoPath);
                    //beginCrop(Uri.fromFile(temp));
                    gotoClipActivity(Uri.fromFile(temp));
                    break;
                case GALLERY_REQUEST_CODE:  // 直接从相册获取
                    gotoClipActivity(data.getData());
                   // beginCrop(data.getData());
                    break;
                case Crop.REQUEST_CROP:  // 裁剪图片结果
                    handleCrop( resultCode, data);
                    break;
                case REQUEST_CROP_PHOTO:  //剪切图片返回
                    Logger.d("返回"+data.getData().getPath());
                    handleCrop( resultCode, data);
                    break;
            }
        }
    }

    private Dialog dialog_help_2;
    View view_2;
    private String mCurrentPhotoPath; //当前选取的相片路径
    helpdialog_item_2 hi_2 = null;
    private void showPhotoDialog() {
        if(hi_2==null){
            hi_2= new helpdialog_item_2();
            view_2 = getLayoutInflater().inflate(R.layout.dialog_out_login, null);
            hi_2.tv_help1 = (TextView) view_2.findViewById(R.id.bt_help1);
            hi_2.tv_help2 = (TextView) view_2.findViewById(R.id.bt_help2);
            hi_2.tv_helpcancle = (TextView) view_2.findViewById(R.id.bt_helpcancle);
            dialog_help_2 = new Dialog(this, R.style.transparentFrameWindowStyle);
            dialog_help_2.setContentView(view_2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog_help_2.getWindow();
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = this.getWindowManager().getDefaultDisplay().getHeight();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog_help_2.onWindowAttributesChanged(wl);
            dialog_help_2.setCanceledOnTouchOutside(true);
            view_2.setTag(hi_2);
        }else{
            hi_2 = (helpdialog_item_2) view_2.getTag();
        }
        hi_2.tv_help1.setText("相册选取");
        hi_2.tv_help2.setText("相机拍照");
        hi_2.tv_help1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
                startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
                dialog_help_2.dismiss();
            }
        });
        hi_2.tv_help2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "love" + String.valueOf( System.currentTimeMillis());
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                File cropFile = new File( mTempDir, fileName);
                Uri fileUri = Uri.fromFile( cropFile);
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                mCurrentPhotoPath = fileUri.getPath();
                startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
                dialog_help_2.dismiss();
            }
        });
        hi_2.tv_helpcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_help_2.dismiss();
            }
        });
        dialog_help_2.show();
    }

    class helpdialog_item_2{
        TextView tv_help1;
        TextView tv_help2;
        TextView tv_helpcancle;
    }


    private Uri imgUrlToImageview;
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
//            imgUrlToImageview = Crop.getOutput(result);
//            upLoadImg(Crop.getOutput(result).getPath());

            imgUrlToImageview = result.getData();
            upLoadImg(result.getData().getPath());
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtil3.showToast(MineCenterActivity.this,"裁剪图片失败，请重试");
        }
    }

    /**
     * 裁剪图片
     * @param source
     */
    private void beginCrop(Uri source) {
        String fileName = "cover_" + String.valueOf( System.currentTimeMillis()+".image");
        File cropFile = new File( mTempDir, fileName);
        Uri outputUri = Uri.fromFile( cropFile);
        new Crop(source).output(outputUri).withAspect(1,1).start(this);
    }


    /**
     * 打开截图界面
     *
     * @param uri
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipImageActivity.class);
        intent.putExtra("type", 2);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    /**
     * 上传封面
     */
    private BmobFile bmobFileCover;
    private void upLoadImg(String imgUrl){
        showProgressBarDialog(MineCenterActivity.this);
        bmobFileCover = new BmobFile(new File(imgUrl));
        bmobFileCover.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    cUser.setUser_icon(bmobFileCover);
                    cUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            dimssProgressDialog();
                            if(e == null){
                                ivIcon.setImageURI(imgUrlToImageview);
                            }else{
                                ToastUtil3.showToast(MineCenterActivity.this,"上传头像失败，请检查网络并重试"+e);
                            }
                        }
                    });
                }else{
                    ToastUtil3.showToast(MineCenterActivity.this,"上传头像失败，请检查网络并重试"+e);
                    dimssProgressDialog();
                }
            }
        });

    }


    private SweetAlertDialog pDialog;
    public void showProgressBarDialog(final Activity mContext){
        try {
            pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.setTitleText("正在提交数据，请稍等");
            pDialog.setCancelable(false);
            pDialog.show();

        }catch (Exception e){
            Logger.d("ProgressBarDialog的上下文找不到啦！"+e);
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
