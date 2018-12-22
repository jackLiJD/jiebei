package com.ald.ebei.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class EbeiInputMethodLinearLayout extends LinearLayout {

    private OnSizeChangeListener onSizeChangeListener;

    public EbeiInputMethodLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EbeiInputMethodLinearLayout(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (oldh != 0) {
            if (oldh > h && onSizeChangeListener != null) {
                onSizeChangeListener.onSizeSmaller();
            } else if (oldh < h && onSizeChangeListener != null) {
                onSizeChangeListener.onSizeLarger();
            }
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener) {
        this.onSizeChangeListener = onSizeChangeListener;
    }

    public interface OnSizeChangeListener {

        public void onSizeLarger();

        public void onSizeSmaller();
    }
}
