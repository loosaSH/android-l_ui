package net.loosash.l_ui.widget;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.loosash.l_ui.R;
import net.loosash.l_ui.utils.Utils;

/**
 * Created by solie_h on 2018/2/27.
 */

public class BroadcastView extends FrameLayout {


    private String mText;
    private @DrawableRes
    int mImageRes;
    private int mTextSize;
    private int mTextColor;
    private float mImageWidth;
    private float mImageHeight;
    private boolean isRepeat;
    private boolean cancelable;
    private int mBackground;

    public BroadcastView(@NonNull Context context) {
        this(context, null);
    }

    public BroadcastView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BroadcastView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final Context context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BroadcastView);


        cancelable = a.getBoolean(R.styleable.BroadcastView_lCancelable, false);
        if (cancelable) {
            setCancelImage();
        }

        if (a.hasValue(R.styleable.BroadcastView_lText)) {
            mText = a.getString(R.styleable.BroadcastView_lText);
            mTextSize = a.getDimensionPixelSize(R.styleable.BroadcastView_lTextSize, 13);
            mTextColor = a.getColor(R.styleable.BroadcastView_lTextColor, ContextCompat.getColor(context, R.color.l_color_black));

        }


        if (a.hasValue(R.styleable.BroadcastView_lImageRes)) {
            mImageRes = a.getResourceId(R.styleable.BroadcastView_lImageRes, R.drawable.ic_launcher_round);
            mImageWidth = a.getDimension(R.styleable.BroadcastView_lImageWidth, mTextSize * 2);
            mImageHeight = a.getDimension(R.styleable.BroadcastView_lImageHeight, mTextSize * 2);
        }
        setChildView();

        isRepeat = a.getBoolean(R.styleable.BroadcastView_lRepeat, false);

        // TODO: 2018/2/27 使用ColorStateList
        mBackground = a.getColor(R.styleable.BroadcastView_lBackground, ContextCompat.getColor(context, R.color.l_color_ligth_yellow));
        a.recycle();

        setBackgroundColor(mBackground);

        invalidate();
    }

    private void setChildView() {
        setImageView();
        setTextView();
        if (cancelable){
            setCancelImage();
        }
    }

    private void setCancelImage() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = Utils.dip2px(getContext(), 15);
        layoutParams.height = Utils.dip2px(getContext(), 15);
        layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        layoutParams.setMargins(Utils.dip2px(getContext(), 5), 0, 15, 0);
        ImageView imageView = new ImageView(getContext());
        // TODO: 2018/2/27 改为
        imageView.setImageResource(mImageRes);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(layoutParams);
        addView(imageView, layoutParams);
    }

    private void setImageView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = (int) mImageWidth;
        layoutParams.height = (int) mImageHeight;
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.setMargins(Utils.dip2px(getContext(), 5), 0, 0, 0);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(mImageRes);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(layoutParams);
        addView(imageView, layoutParams);
    }

    private void setTextView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginRight = 15;
        if (cancelable) {
            marginRight = Utils.dip2px(getContext(), 15) + 15;
        }
        layoutParams.setMargins(Utils.dip2px(getContext(), 10) + (int) mImageWidth, 0, marginRight, 0);
        layoutParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        MarqueeTextView textView = new MarqueeTextView(getContext());
        textView.setText("但是非常注意的是我们的LayoutTransition是在OnCreate中设置的，也就是说是在LinearLayout创建时就给它定义好控件的入场动画和出场动画的");
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSingleLine();
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.requestFocus();
        textView.setTextSize(Utils.px2sp(getContext(), mTextSize));
        textView.setTextColor(mTextColor);
        textView.setLayoutParams(layoutParams);
        addView(textView, layoutParams);
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility == View.VISIBLE) {
            LayoutTransition layoutTransition = new LayoutTransition();
            ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "translationY", 0, getHeight());
            layoutTransition.setAnimator(LayoutTransition.APPEARING, animIn);
            ((ViewGroup) getParent()).setLayoutTransition(layoutTransition);
        } else {
            LayoutTransition layoutTransition = new LayoutTransition();
            ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "translationY", getHeight(), 0);
            layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, animOut);
            ((ViewGroup) getParent()).setLayoutTransition(layoutTransition);
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    private class MarqueeTextView extends TextView {

        public MarqueeTextView(Context context) {
            super(context);
        }

        public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean isFocused() {
            return true;
        }
    }


}
