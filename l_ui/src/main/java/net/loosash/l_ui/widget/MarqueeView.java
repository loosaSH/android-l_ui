package net.loosash.l_ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import net.loosash.l_ui.R;
import net.loosash.l_ui.res.Resource;
import net.loosash.l_ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solie_h on 2018/2/9.
 */

public class MarqueeView extends ViewFlipper {

    // 默认字号
    private static final int DEFFULT_TEXT_SIZE = 13;
    // 默认文字颜色
    private static final @ColorRes
    int DEFFULT_TEXT_COLOR = Resource.Color.BLACK;
    // 默认文字是否单行
    private static final boolean DEFFULT_TEXT_SIGNLE_LINE = true;
    // 默认文字布局方向
    private static final int DEFULT_TEXT_GRAVITY = Gravity.LEFT | Gravity.CENTER_VERTICAL;
    // 默认播放间隔时间  毫秒单位
    private static final int DEFULT_INTERVAL = 10000;
    // 默认进入退出动画  毫秒单位
    private static final int DEFULT_ANIM_DURATION = 8000;
    // 默认不使用用户资源动画
    private static final boolean DEFULT_NO_USES_CUSTOMER_ANIM_RES = false;

    // 默认左对齐
    private static final int GRAVITY_LEFT = 0;
    private static final int GRAVITY_CENTER = 1;
    private static final int GRAVITY_RIGHT = 2;

    // 默认从下到上
    private static final int DIRECTION_BOTTOM_TO_TOP = 0;
    private static final int DIRECTION_TOP_TO_BOTTOM = 1;
    private static final int DIRECTION_RIGHT_TO_LEFT = 2;
    private static final int DIRECTION_LEFT_TO_RIGHT = 3;

    private int mTextSize;
    private int mTextSizePX;
    private int mTextColor;
    private boolean isSingleLine;
    private int mGravity;
    private int mInterval;
    private int mAnimDuration;
    private int mDirection;
    private boolean useCustomerAnim;


    private @AnimRes
    int inAnimResId;
    private @AnimRes
    int outAnimResId;

    private boolean isAnimStart = false;
    private boolean hasSetAnimDuration;
    private int position;
    private List<? extends CharSequence> notices = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs == null)
            return;
        final Context context = getContext();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView);

//        mTextSize = a.getInteger(R.styleable.MarqueeView_lTextSize, DEFFULT_TEXT_SIZE);
//        mTextSize = Utils.dip2px(context, mTextSize);
//        Log.i("xx","mTextSize:"+mTextSize);


        mTextSize = (int) a.getDimension(R.styleable.MarqueeView_lTextSize, DEFFULT_TEXT_SIZE);
        mTextSizePX = Utils.sp2px(context, DEFFULT_TEXT_SIZE);


        mTextColor = a.getColor(R.styleable.MarqueeView_lTextColor, ContextCompat.getColor(context, R.color.l_color_black));
        isSingleLine = a.getBoolean(R.styleable.MarqueeView_lSingleLine, DEFFULT_TEXT_SIGNLE_LINE);
        mGravity = a.getInt(R.styleable.MarqueeView_lGravity, DEFULT_TEXT_GRAVITY);
        switch (mGravity) {
            case GRAVITY_LEFT:
                mGravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case GRAVITY_CENTER:
                mGravity = Gravity.CENTER;
                break;
            case GRAVITY_RIGHT:
                mGravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
            default:
                mGravity = DEFULT_TEXT_GRAVITY;
        }
        mInterval = a.getInteger(R.styleable.MarqueeView_lInterval, DEFULT_INTERVAL);

        useCustomerAnim = a.getBoolean(R.styleable.MarqueeView_useCustomerAnimRes, DEFULT_NO_USES_CUSTOMER_ANIM_RES);

        // 若在xml中设置app:useCustomerAnimRes="true"
        // 则使用android:inAnimator="@anim/XXX",android:outAnimator="@anim/XXX"
        // 默认false，不支持使用inAnimator，outAnimator,根据用户输入方向，动画时间控制动画
        if (!useCustomerAnim) {
            hasSetAnimDuration = a.hasValue(R.styleable.MarqueeView_lAnimDuration);
            mAnimDuration = a.getInteger(R.styleable.MarqueeView_lAnimDuration, DEFULT_ANIM_DURATION);
            boolean hasDirection = a.hasValue(R.styleable.MarqueeView_lDirection);
            if (hasDirection) {
                mDirection = a.getInteger(R.styleable.MarqueeView_lDirection, DIRECTION_BOTTOM_TO_TOP);
                switch (mDirection) {
                    case DIRECTION_BOTTOM_TO_TOP:
                        inAnimResId = R.anim.anim_marquee_bottom_in;
                        outAnimResId = R.anim.anim_marquee_top_out;
                        break;
                    case DIRECTION_TOP_TO_BOTTOM:
                        inAnimResId = R.anim.anim_marquee_top_in;
                        outAnimResId = R.anim.anim_marquee_bottom_out;
                        break;
                    case DIRECTION_RIGHT_TO_LEFT:
                        inAnimResId = R.anim.anim_marquee_right_in;
                        outAnimResId = R.anim.anim_marquee_left_out;
                        break;
                    case DIRECTION_LEFT_TO_RIGHT:
                        inAnimResId = R.anim.anim_marquee_left_in;
                        outAnimResId = R.anim.anim_marquee_right_out;
                        break;
                    default:
                        inAnimResId = R.anim.anim_marquee_bottom_in;
                        outAnimResId = R.anim.anim_marquee_top_out;
                }
            }
        }


        a.recycle();

        setFlipInterval(mInterval);
    }

    /**
     * 根据字符串，启动翻页公告
     *
     * @param notice 字符串
     */
    public void startWithText(String notice) {
        startWithText(notice, inAnimResId, outAnimResId);
    }

    /**
     * 根据字符串，启动翻页公告
     *
     * @param notice       字符串
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    @SuppressWarnings("deprecation")
    public void startWithText(final String notice, final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        if (TextUtils.isEmpty(notice)) return;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                startWithFixedWidth(notice, inAnimResId, outAnimResID);
            }
        });
    }

    /**
     * 根据字符串和宽度，启动翻页公告
     *
     * @param notice 字符串
     */
    private void startWithFixedWidth(String notice, @AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        int noticeLength = notice.length();
//        int width = Utils.px2dip(getContext(), getWidth());
        int width = getWidth();
        if (width == 0) {
            throw new RuntimeException("Please set the width of MarqueeView !");
        }

        int limit = width / mTextSizePX;
        List list = new ArrayList();

        if (noticeLength <= limit) {
            list.add(notice);
        } else {
            int size = noticeLength / limit + (noticeLength % limit != 0 ? 1 : 0);
            for (int i = 0; i < size; i++) {
                int startIndex = i * limit;
                int endIndex = ((i + 1) * limit >= noticeLength ? noticeLength : (i + 1) * limit);
                list.add(notice.substring(startIndex, endIndex));
            }
        }

        if (notices == null) notices = new ArrayList<>();
        notices.clear();
        notices.addAll(list);
        postStart(inAnimResId, outAnimResID);
    }

    private void postStart(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        post(new Runnable() {
            @Override
            public void run() {
                start(inAnimResId, outAnimResID);
            }
        });
    }

    private void start(final @AnimRes int inAnimResId, final @AnimRes int outAnimResID) {
        removeAllViews();
        clearAnimation();

        position = 0;
        addView(createTextView(notices.get(position)));

        if (notices.size() > 1) {
            if (!useCustomerAnim)
                setInAndOutAnimation(inAnimResId, outAnimResID);
            startFlipping();
        }

        if (getInAnimation() != null) {
            getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (isAnimStart) {
                        animation.cancel();
                    }
                    isAnimStart = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    position++;
                    if (position >= notices.size()) {
                        position = 0;
                    }
                    View view = createTextView(notices.get(position));
                    if (view.getParent() == null) {
                        addView(view);
                    }
                    isAnimStart = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private android.widget.TextView createTextView(CharSequence text) {
        android.widget.TextView textView = (android.widget.TextView) getChildAt((getDisplayedChild() + 1) % 3);
        if (textView == null) {
            textView = new android.widget.TextView(getContext());
            textView.setGravity(mGravity);
            textView.setTextColor(mTextColor);
            textView.setTextSize(mTextSize);
            textView.setSingleLine(isSingleLine);
        }
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getPosition(), (android.widget.TextView) v);
                }
            }
        });
        textView.setText(text);
        textView.setTag(position);
        return textView;
    }

    public int getPosition() {
        return (int) getCurrentView().getTag();
    }

    public List<? extends CharSequence> getNotices() {
        return notices;
    }

    public void setNotices(List<? extends CharSequence> notices) {
        this.notices = notices;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, TextView textView);
    }

    /**
     * 设置进入动画和离开动画
     *
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    private void setInAndOutAnimation(@AnimRes int inAnimResId, @AnimRes int outAnimResID) {
        Animation inAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
        if (hasSetAnimDuration) inAnim.setDuration(mAnimDuration);
        setInAnimation(inAnim);

        Animation outAnim = AnimationUtils.loadAnimation(getContext(), outAnimResID);
        if (hasSetAnimDuration) outAnim.setDuration(mAnimDuration);
        setOutAnimation(outAnim);
    }

}
