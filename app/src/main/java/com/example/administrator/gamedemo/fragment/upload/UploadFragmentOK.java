package com.example.administrator.gamedemo.fragment.upload;

import com.example.administrator.gamedemo.core.Constants;


/**
 * Created by Administrator on 2016/12/8 0008.
 *
 * @author lixu
 * 审核通过的上传答题
 */

public class UploadFragmentOK extends UploadFragment{
    public UploadFragmentOK() {
    }

    public static UploadFragmentOK getInstance() {
        return answerFragmentHolder.instance;
    }

    public static class answerFragmentHolder {
        public static final UploadFragmentOK instance = new UploadFragmentOK();
    }

    @Override
    protected void setmType() {
        mType = Constants.UPLOAD_OK;
    }

}
