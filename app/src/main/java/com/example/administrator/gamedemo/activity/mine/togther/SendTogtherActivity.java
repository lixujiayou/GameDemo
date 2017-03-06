package com.example.administrator.gamedemo.activity.mine.togther;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.request.AddTogtherRequest;
import com.example.administrator.gamedemo.widget.request.OnResponseListener;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.example.administrator.gamedemo.widget.request.TogtherRequest;
import com.lidong.photopicker.ImageCaptureManager;
import com.lidong.photopicker.PhotoPickerActivity;
import com.lidong.photopicker.PhotoPreviewActivity;
import com.lidong.photopicker.SelectModel;
import com.lidong.photopicker.intent.PhotoPickerIntent;
import com.lidong.photopicker.intent.PhotoPreviewIntent;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Administrator on 2016/12/26 0026.
 * 发表一起
 */
public class SendTogtherActivity extends BaseActivity {


    private static final int REQUEST_CAMERA_CODE = 10;
    private static final int REQUEST_PREVIEW_CODE = 20;

    @BindView(R.id.gridView)
    GridView gridView;

    @BindView(R.id.et_togther)
    EditText et_togther;

    private ArrayList<String> imagePaths = new ArrayList<>();
    private ImageCaptureManager captureManager; // 相机拍照处理类

    private GridAdapter gridAdapter;
    private String depp;

    private String TAG = SendTogtherActivity.class.getSimpleName();
    private SweetAlertDialog pDialog;
    private SweetAlertDialog eDialog;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_togther_write);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("一起说");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_send_whrite:
                        String text = et_togther.getText().toString();
                        if(text.trim() == null || text.length() == 0){
                            ToastUtil3.showToast(SendTogtherActivity.this,"请填写发布内容");
                        }else {
                            if(text.length() > 500){
                                ToastUtil3.showToast(SendTogtherActivity.this,"字数已超出限制500字，请修改后发布");
                            }else {
                                commitTogther(text);
                            }
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);

        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imgs = (String) parent.getItemAtPosition(position);
                if ("000000".equals(imgs) ){
                    PhotoPickerIntent intent = new PhotoPickerIntent(SendTogtherActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(6); // 最多选择照片数量，默认为6
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                }else{
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(SendTogtherActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            }
        });

        imagePaths.add("000000");
        gridAdapter = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                depp =textView.getText().toString().trim()!=null?textView.getText().toString().trim():"woowoeo";
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
////                        FileUploadManager.uploadMany(imagePaths, depp);
////                        FileUploadManager.upload(imagePaths,depp);
//                    }
//                }.start();
//            }
//        });
    }

    @Override
    public void initData() {
    }

    private void commitTogther(String togtherText) {

        showProgressBarDialog(SendTogtherActivity.this);
        AddTogtherRequest addTogtherRequest = new AddTogtherRequest();
        addTogtherRequest.setAuthId(Constants.getInstance().getUser(SendTogtherActivity.this).getObjectId());
        addTogtherRequest.addText(togtherText);
        if(imagePaths.contains("000000")) {
            imagePaths.remove("000000");
        }
        if(imagePaths != null || imagePaths.size() !=0 ) {
            for (String picUrl : imagePaths) {
                addTogtherRequest.addPicture(picUrl);
            }
        }

        addTogtherRequest.setOnResponseListener(new OnResponseListener<String>() {
            @Override
            public void onStart(int requestType) {
            }

            @Override
            public void onSuccess(String response, int requestType) {
                dimssProgressDialog();
                ToastUtil3.showToast(SendTogtherActivity.this,"发布成功");
                Logger.d(response+requestType);
                setResult(3);
                finish();
            }

            @Override
            public void onError(BmobException e, int requestType) {
                dimssProgressDialog();
                showErroDialog(SendTogtherActivity.this,"发布失败","请检查网络并重试");
                Logger.d(e.toString()+requestType);
            }

            @Override
            public void onProgress(int pro) {
                setProgressDialogText("已上传%"+pro);
            }
        });
        addTogtherRequest.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_send_whrite, menu);
        return true;
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;
        private LayoutInflater inflater;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
            if (listUrls.size() == 7) {
                listUrls.remove(listUrls.size() - 1);
            }
            inflater = LayoutInflater.from(SendTogtherActivity.this);
        }

        public int getCount() {
            return listUrls.size();
        }

        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_image, parent, false);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String path = listUrls.get(position);
            if (path.equals("000000")) {
                holder.image.setImageResource(R.drawable.add_image);
            } else {
                Glide.with(SendTogtherActivity.this)
                        .load(path)
                        .placeholder(R.drawable.ic_loading_small)
                        .error(R.drawable.ic_loading_small)
                        .centerCrop()
                        .crossFade()
                        .into(holder.image);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView image;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    Log.d(TAG, "list: " + "list = [" + list.size());
                    loadAdpater(list);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    ArrayList<String> ListExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                    Log.d(TAG, "ListExtra: " + "ListExtra = [" + ListExtra.size());
                    loadAdpater(ListExtra);
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths){
        if (imagePaths!=null&& imagePaths.size()>0){
            imagePaths.clear();
        }
        if (paths.contains("000000")){
            paths.remove("000000");
        }
        paths.add("000000");
        imagePaths.addAll(paths);
        gridAdapter  = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);
        try{
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void showProgressBarDialog(final Activity mContext){
        try {
                pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText("正在提交数据，请稍等");
                pDialog.setCancelable(true);
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

    /**
     * 设置进度
     * @param pro
     */
    public void setProgressDialogText(String pro){
        if(pDialog == null){
            return;
        }
        pDialog.setTitleText(pro);
    }

    /**
     * 错误的dialog
     * @param mContext
     * @param title
     * @param message
     */
    public void showErroDialog(final Activity mContext,String title,String message){
        try {
            if(mContext.hasWindowFocus()) {
                eDialog = new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE);
                eDialog.setTitleText(title);
                eDialog.setContentText(message);
                eDialog.setConfirmText("知道啦");
                eDialog.show();
            }
        }catch (Exception e){
            Logger.d("ErroDialog的上下文找不到啦！");
        }
    }


}
