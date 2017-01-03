package com.example.administrator.gamedemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.core.MyApplication;
import com.example.administrator.gamedemo.utils.base_image.RoundedImageView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * Created by lixu on 2016/11/1.
 * <p>
 * 图片加载
 */

public enum ImageLoadMnanger {
    INSTANCE;

    public void clearMemory() {
        Glide.get(MyApplication.getAppContext()).clearMemory();
    }

    public void loadImage(ImageView imageView, String imgUrl) {
        loadImageByNormalConfig(imageView, imgUrl)
                .into(imageView);
    }
    //加载封面专用
    public void loadImageToCover(ImageView imageView, String imgUrl) {
        loadImageToC(imageView, imgUrl)
                .into(imageView);
    }

    public void loadImageForRv(RoundedImageView imageView, String imgUrl) {
        loadImageByNormalConfig(imageView, imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

                .into(imageView);
    }

//    .placeholder(new ColorDrawable(0xffc6c6c6)) //占位

    public void loadImage(ImageView imageView, String imgUrl, int width, int height) {
        loadImageByNormalConfig(imageView, imgUrl).placeholder(new ColorDrawable(0xffc6c6c6))
                                                  .override(width, height)
                                                  .into(imageView);
    }


    public void loadImageDontAnimate(ImageView imageView, String imgUrl) {
        loadImageByNormalConfig(imageView, imgUrl).dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }


//    public void loadRoundImage(ImageView imageView,String imgUrl){
//        Glide.with(fragment)
//                .load(imgUrl)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .bitmapTransform(new RoundedCornersTransformation(getImageContext(imageView),12,0, RoundedCornersTransformation.CornerType.ALL))
//                .placeholder(R.drawable.ic_loading_small)
//                .error(R.drawable.ic_loading_no)
//                .thumbnail(0.2f)
//                .into(imageView)
//        ;
//    }
    public void loadRoundImage(ImageView imageView, String imgUrl){
        Glide.with(getImageContext(imageView))
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new RoundedCornersTransformation(getImageContext(imageView),8,0, RoundedCornersTransformation.CornerType.ALL))
                .placeholder(R.drawable.ic_loading_small)
                .error(R.drawable.ic_loading_no)
                .thumbnail(0.2f)
                .into(imageView)
        ;
    }
 public void loadCicleImage(Fragment fragment,ImageView imageView,String imgUrl){
        Glide.with(fragment)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new CropCircleTransformation(getImageContext(imageView)))
                .placeholder(R.drawable.ic_loading_small)
                .error(R.drawable.ic_loading_no)
                .thumbnail(0.2f)
                .into(imageView)
        ;
    }

 public void loadNomalImage(Fragment fragment,ImageView imageView,String imgUrl){
        Glide.with(fragment)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_loading_small)
                .error(R.drawable.ic_loading_no)
                .thumbnail(0.2f)
                .into(imageView)
        ;
    }


    private BitmapRequestBuilder loadImageByNormalConfig(ImageView imageView, String url) {

        return Glide.with(getImageContext(imageView))
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.ic_loading_small)
                .error(R.drawable.ic_loading_no)
                .thumbnail(0.5f)
                ;
    }

    private BitmapRequestBuilder loadImageToC(ImageView imageView, String url) {

        return Glide.with(getImageContext(imageView))
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.img_huoer)
                .error(R.drawable.img_huoer)
                .thumbnail(0.5f)
                ;
    }


    public void steDimImage (Fragment fragment, String imgUrl, final ImageView imageView){
        Glide.with(fragment)
                .load(imgUrl)
                .asBitmap()
                .placeholder(R.drawable.ic_loading_small)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(Constants.doBlur(resource,10,false));
                    }
                });

    }


    private Context getImageContext(@Nullable ImageView imageView) {
        if (imageView == null) {
            return MyApplication.getAppContext();
        }
        return imageView.getContext();
    }



}
