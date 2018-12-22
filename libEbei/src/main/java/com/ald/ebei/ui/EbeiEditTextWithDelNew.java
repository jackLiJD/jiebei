package com.ald.ebei.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.ald.ebei.R;
import com.ald.ebei.config.EbeiConfig;

public class EbeiEditTextWithDelNew extends EditText {

    private final static String TAG = "EditTextWithDel";
    private Drawable imgAble;
    private Context context;
    private boolean isClearVisibility = true;

    public EbeiEditTextWithDelNew(Context context) {
        super(context);
        init(context);
    }

    public EbeiEditTextWithDelNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EbeiEditTextWithDelNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        imgAble = EbeiConfig.getContext().getResources().getDrawable(R.drawable.icon_clear);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        setDrawable();
    }

    private void setDrawable() {
        if (length() < 1) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble, null);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            if (rect.contains(eventX, eventY) && isClearVisibility) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void setClearVisibility(boolean visibility) {
        isClearVisibility = visibility;
        if (visibility) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }
}
