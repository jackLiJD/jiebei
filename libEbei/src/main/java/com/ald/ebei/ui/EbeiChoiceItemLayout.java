package com.ald.ebei.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.ald.ebei.R;

/**
 * Created by ywd on 2018/11/18.
 */

public class EbeiChoiceItemLayout extends LinearLayout implements Checkable {
    private boolean mChecked;

    public EbeiChoiceItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        setBackgroundResource(checked ? R.drawable.shape_home_choice_period_select : R.drawable.shape_home_choice_period_unselect);
    }

    @Override
    public boolean isChecked() {
        return true;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
