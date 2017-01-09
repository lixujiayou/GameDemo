package com.example.administrator.gamedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.model.NoteInfo;
import com.example.administrator.gamedemo.utils.PaperView;
import com.example.administrator.gamedemo.utils.UIHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Henry on 2015/5/20.
 */
//public class RoutinesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
public class RoutinesAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RoutinesAdapter";
    public static final String BOOK_INFO = "BOOK_INFO";
    public static final String BOOK_NAME = "BOOK_NAME";
    public static final String BOOK_CONTENT = "BOOK_CONTENT";
    public static final String BOOK_COLOR_INDEX = "BOOK_COLOR_INDEX";
    public static final String BOOK_ALARM_TIME = "BOOK_ALARM_TIME";
    private static final int TYPE_ROUTINES_HEADER = 0;
    private static final int TYPE_ROUTINE = 1;
    private static final int MIN_ITEM_COUNT = 1;
    private static final int MAX_ITEM_ANIMATION_DELAY = 500;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private Context context;
    private int cellHeight;
    private int cellWidth;
    private long routinesHeaderAnimationStartTime = 0;
    private boolean lockedAnimations = false;

    private int lastAnimatedItem = 0;
    private int itemsCount = 0;
    private List<NoteInfo> mDataset;

    public RoutinesAdapter(Context context, List<NoteInfo> objects) {
        this.context = context;
        this.cellHeight = (int) (UIHelper.getScreenWidthPixels(context) / 2 * 1.2);
        this.cellWidth = UIHelper.getScreenWidthPixels(context) / 2;
        this.mDataset = objects;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ROUTINES_HEADER;
        } else {
            return TYPE_ROUTINE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ROUTINES_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_routines_header, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            return new RoutinesHeaderViewHolder(view);
        } else if (viewType == TYPE_ROUTINE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_routine, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.height = cellHeight;
            layoutParams.width = cellWidth;
            layoutParams.setFullSpan(false);
            view.setLayoutParams(layoutParams);
            return new RoutineViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_ROUTINES_HEADER) {
            bindRoutinesHeader((RoutinesHeaderViewHolder) holder);
        } else if (viewType == TYPE_ROUTINE) {
            bindRoutine((RoutineViewHolder) holder, position);
        }
    }

    private void bindRoutinesHeader(final RoutinesHeaderViewHolder holder) {
        holder.routinesHeader.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.routinesHeader.getViewTreeObserver().removeOnPreDrawListener(this);
                animateRoutinesHeader(holder);
                return false;
            }
        });
    }

    private void bindRoutine(final RoutineViewHolder holder, final int position) {

        animateRoutine(holder);
        String item = mDataset.get(position - 1).getTitle();

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.ic_trash));
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(holder.swipeLayout);
                mItemManger.closeAllItems();

                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onItemDeleteClick(holder.deleteButton, pos);
            }
        });

        if (mOnItemClickListener != null) {
            holder.textViewData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.textViewData, pos);
                    Log.d(TAG, "onItemClick() returned: " + pos);
                }
            });
        }

        //设置随机颜色
        int[] colorBook = context.getResources().getIntArray(R.array.style_book_color);
        int colorIndex = mDataset.get(position - 1).getNoteColor();
        holder.swipeLayout.setBackgroundColor(colorBook[colorIndex]);
//        holder.paperView.setColor(colorBook[colorIndex]);

//        holder.textViewPosition.setText(position + "");
        holder.textViewData.setText(item);
        mItemManger.bindView(holder.itemView, position);
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void animateRoutinesHeader(RoutinesHeaderViewHolder holder) {
        if (!lockedAnimations) {
            routinesHeaderAnimationStartTime = System.currentTimeMillis();

            holder.routinesHeader.setTranslationY(-holder.routinesHeader.getHeight());
            holder.routinesHeader.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
        }
    }

    private void animateRoutine(RoutineViewHolder holder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == holder.getPosition()) {
                setLockedAnimations(true);
            }
        }

        long animationDelay = routinesHeaderAnimationStartTime + MAX_ITEM_ANIMATION_DELAY - System.currentTimeMillis();

        if (routinesHeaderAnimationStartTime == 0) {
            animationDelay = holder.getPosition() * 30 + MAX_ITEM_ANIMATION_DELAY;
        } else if (animationDelay < 0) {
            animationDelay = holder.getPosition() * 30;
        } else {
            animationDelay += holder.getPosition() * 30;
        }

        holder.swipeLayout.setScaleY(0);
        holder.swipeLayout.setScaleX(0);
        holder.swipeLayout.animate().scaleY(1).scaleX(1).setDuration(200).setInterpolator(INTERPOLATOR).setStartDelay(animationDelay).start();
    }

    @Override
    public int getItemCount() {
        return MIN_ITEM_COUNT + mDataset.size() + itemsCount;
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    static class RoutinesHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.routinesHeader)
        View routinesHeader;

        public RoutinesHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        /*        @InjectView(R.id.position)
                TextView textViewPosition;*/
        @BindView(R.id.swipe)
        SwipeLayout swipeLayout;
        @BindView(R.id.ic_trash)
        ImageView deleteButton;
        @BindView(R.id.text_data)
        TextView textViewData;
        @BindView(R.id.paperView)
        PaperView paperView;

        public RoutineViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    public NoteInfo getItemData(int position) {
        return mDataset.get(position);
    }

    //itemClick interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemDeleteClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
