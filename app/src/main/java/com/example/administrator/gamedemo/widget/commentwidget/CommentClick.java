package com.example.administrator.gamedemo.widget.commentwidget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.UIHelper;
import com.example.administrator.gamedemo.widget.span.ClickableSpanEx;


/**
 * Created by 大灯泡 on 2016/2/23.
 * 评论点击事件
 */
public class CommentClick extends ClickableSpanEx {
    private Context mContext;
    private int textSize;
    private Students mUserInfo;

    private CommentClick() {
    }

    private CommentClick(Builder builder) {
        super(builder.color, builder.clickEventColor);
        mContext = builder.mContext;
        mUserInfo = builder.mUserInfo;
        this.textSize = builder.textSize;
    }

    @Override
    public void onClick(View widget) {
        if (mUserInfo != null)
            Toast.makeText(mContext, "当前用户名是： " + mUserInfo.getNick_name() + "   它的ID是： " + mUserInfo.getObjectId(),
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTextSize(textSize);
    }

    public static class Builder {
        private int color;
        private Context mContext;
        private int textSize = 16;
        private Students mUserInfo;
        private int clickEventColor;

        public Builder(Context context, @NonNull Students info) {
            mContext = context;
            mUserInfo = info;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = UIHelper.sp2px(textSize);
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setClickEventColor(int color) {
            this.clickEventColor = color;
            return this;
        }

        public CommentClick build() {
            return new CommentClick(this);
        }
    }
}
