package com.example.administrator.gamedemo.fragment.upload;


import com.example.administrator.gamedemo.core.Constants;

/**
 * Created by Administrator on 2016/12/8 0008.
 *
 * @author lixu
 * 审核失败的上传答题
 */

public class UploadFragmentNO extends UploadFragment{

    public UploadFragmentNO() {
    }

    public static UploadFragmentNO getInstance() {
        return answerFragmentHolder.instance;
    }

    public static class answerFragmentHolder {
        public static final UploadFragmentNO instance = new UploadFragmentNO();
    }

    @Override
    protected void setmType() {
        mType = Constants.UPLOAD_NO;
    }
}
