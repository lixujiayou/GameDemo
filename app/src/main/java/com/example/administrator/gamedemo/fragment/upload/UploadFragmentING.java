package com.example.administrator.gamedemo.fragment.upload;


import com.example.administrator.gamedemo.core.Constants;


/**
 * Created by Administrator on 2016/12/8 0008.
 *
 * @author lixu
 * 审核中的上传答题
 */

public class UploadFragmentING extends UploadFragment{

    public UploadFragmentING() {
    }

    public static UploadFragmentING getInstance() {
        return answerFragmentHolder.instance;
    }

    @Override
    protected void setmType() {
        mType = Constants.UPLOAD_ING;
    }

    public static class answerFragmentHolder {
        public static final UploadFragmentING instance = new UploadFragmentING();
    }

}
