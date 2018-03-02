package net.loosash.l_ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import net.loosash.l_ui.R;
import net.loosash.l_ui.drawable.BorderShape;
import net.loosash.l_ui.res.Resource;


/**
 * Created by solie_h on 2017/12/15.
 */

public class TextView extends android.support.v7.widget.AppCompatTextView {

    public final static int BORDER_LEFT = 0x0001;
    public final static int BORDER_RIGHT = 0x0010;
    public final static int BORDER_TOP = 0x0100;
    public final static int BORDER_BOTTOM = 0x1000;

    public final static int BORDER_ALL = BORDER_LEFT | BORDER_RIGHT | BORDER_TOP | BORDER_BOTTOM;

    private int mBorder;
    private int mBorderColor;
    private int mBorderSize;
    private Drawable mBorderDrawable;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, R.style.Loosa_Widget_TextView);

    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null)
            return;

        final Context context = getContext();
        final float density = getResources().getDisplayMetrics().density;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView);
        int border = a.getInt(R.styleable.TextView_lBorder, -1);
        int borderSize = a.getDimensionPixelOffset(R.styleable.TextView_lBorderSize, (int) density);
        int borderColor = a.getColor(R.styleable.TextView_lBorderColor, Resource.Color.GREY);
        String fontFile = a.getString(R.styleable.TextView_lFont);
        a.recycle();

        setBorder(border, borderSize, borderColor);

        if (!this.isInEditMode() && fontFile != null && fontFile.length() > 0) {
            Typeface typeface = getFont(context, fontFile);
            if (typeface != null)
                setTypeface(typeface);
        }

    }

    /**
     * 设置边框
     *
     * @param border
     * @param borderSize
     * @param borderColor
     */
    private void setBorder(int border, int borderSize, int borderColor) {
        this.mBorder = border;
        this.mBorderSize = borderSize;
        this.mBorderColor = borderColor;

        if (mBorder == -1 || mBorder == 0) {
            mBorderDrawable = null;
        } else {
            RectF borderRect;
            if ((border & BORDER_ALL) == BORDER_ALL) {
                borderRect = new RectF(borderSize, borderSize, borderSize, borderSize);
            } else {
                int l = 0, t = 0, r = 0, b = 0;
                if ((border & BORDER_LEFT) == BORDER_LEFT)
                    l = borderSize;
                if ((border & BORDER_TOP) == BORDER_TOP)
                    t = borderSize;
                if ((border & BORDER_RIGHT) == BORDER_RIGHT)
                    r = borderSize;
                if ((border & BORDER_BOTTOM) == BORDER_BOTTOM)
                    b = borderSize;
                borderRect = new RectF(l, t, r, b);
            }

            if (mBorderDrawable == null) {
                ShapeDrawable drawable = new ShapeDrawable(new BorderShape(borderRect));
                Paint paint = drawable.getPaint();
                paint.setColor(borderColor);
                drawable.setCallback(this);
                mBorderDrawable = drawable;
            } else {
                ShapeDrawable drawable = (ShapeDrawable) mBorderDrawable;
                Paint paint = drawable.getPaint();
                paint.setColor(borderColor);
                BorderShape shape = (BorderShape) drawable.getShape();
                shape.setBorder(borderRect);
            }
        }
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = mBorderDrawable;
        if (drawable != null && mBorderSize > 0 && mBorderColor != 0)
            drawable.draw(canvas);
        super.onDraw(canvas);
    }

    public int getBorder() {
        return mBorder;
    }

    public int getBorderSize() {
        return mBorderSize;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Drawable drawable = mBorderDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        Drawable drawable = mBorderDrawable;
        return (drawable != null && who == drawable) || super.verifyDrawable(who);
    }

    /**
     * 获取字体文件 typeface
     *
     * @param context
     * @param fontFile
     * @return
     */
    public static Typeface getFont(Context context, String fontFile) {
        String fontPath = "fonts/" + fontFile;
        try {
            return Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception e) {
            Log.e("Loosa Ui", "Font file at " + fontPath + " cannot be found or the file is " +
                    "not a valid font file. Please be sure that library assets are included " +
                    "to project. If not, copy assets/fonts folder of the library to your " +
                    "projects assets folder.");
            return null;
        }
    }

}
