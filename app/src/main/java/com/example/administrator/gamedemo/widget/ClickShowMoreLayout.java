package com.example.administrator.gamedemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.UIHelper;


/**
 * Created by lixu on 2016/12/12.
 * 点击展开更多
 */
public class ClickShowMoreLayout extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "ClickShowMoreLayout";


    public static final int CLOSE = 0;
    public static final int OPEN = 1;

    private int preState;

    public TextView mTextView;
    private TextView mClickToShow;

    private int textColor;
    private int textSize;

    private int showLine;
    private String clickText;

    private boolean hasMore;
    private boolean hasGetLineCount = false;


    private static final SparseIntArray TEXT_STATE = new SparseIntArray();

    public ClickShowMoreLayout(Context context) {
        this(context, null);
    }

    public ClickShowMoreLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClickShowMoreLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClickShowMoreLayout);
        textColor = a.getColor(R.styleable.ClickShowMoreLayout_text_color, 0xff1a1a1a);
        textSize = a.getDimensionPixelSize(R.styleable.ClickShowMoreLayout_text_size, 15);
        showLine = a.getInt(R.styleable.ClickShowMoreLayout_show_line, 8);
        clickText = a.getString(R.styleable.ClickShowMoreLayout_click_text);
        if (TextUtils.isEmpty(clickText)) clickText = "全文";
        a.recycle();

        initView(context);

    }

    private void initView(Context context) {
        mTextView = new TextView(context);
        mClickToShow = new TextView(context);

        mTextView.setLineSpacing(0f,1.2f);
        mTextView.setTextSize(textSize);
        mTextView.setTextColor(textColor);
        mTextView.setMaxLines(showLine);
        mClickToShow.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_tx_show_more));
        mClickToShow.setTextSize(textSize);
        mClickToShow.setTextColor(ContextCompat.getColor(context,R.color.nick));
        mClickToShow.setText(clickText);

        //设置文字超链接
        mTextView.setAutoLinkMask(Linkify.ALL);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mClickToShow.setAutoLinkMask(Linkify.ALL);
        mClickToShow.setMovementMethod(LinkMovementMethod.getInstance());

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                         ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIHelper.dipToPx(10f);
        mClickToShow.setLayoutParams(params);
        mClickToShow.setOnClickListener(this);

        setOrientation(VERTICAL);
        addView(mTextView);
        addView(mClickToShow);
    }

    @Override
    public void onClick(View v) {
        boolean needOpen = TextUtils.equals(((TextView) v).getText().toString(), clickText);
        setState(needOpen ? OPEN : CLOSE);
    }


    public void setState(int state) {
        switch (state) {
            case CLOSE:
                mTextView.setMaxLines(showLine);
                mClickToShow.setText(clickText);
                break;
            case OPEN:
                mTextView.setMaxLines(Integer.MAX_VALUE);
                mClickToShow.setText("收起");
                break;
        }
        TEXT_STATE.put(getText().toString().hashCode(), state);
    }

    public void setText(String str) {
        if (hasGetLineCount) {
            restoreState(str);
            mTextView.setText(str);
        } else {
            mTextView.setText(str);
            mTextView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (!hasGetLineCount) {
                        hasMore = mTextView.getLineCount() > showLine;
                        hasGetLineCount = true;
                    }
                    mClickToShow.setVisibility(hasMore ? VISIBLE : GONE);
                    mTextView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            setState(CLOSE);
        }
    }

    private void restoreState(String str) {
        try {
            int state = CLOSE;
            int holderState = TEXT_STATE.get(str.hashCode(), -1);
            if (holderState == -1) {
                TEXT_STATE.put(str.hashCode(), state);
            } else {
                state = holderState;
            }
            setState(state);
        }catch (Exception e){}
    }

    public CharSequence getText() {
        return mTextView.getText();
    }

}
