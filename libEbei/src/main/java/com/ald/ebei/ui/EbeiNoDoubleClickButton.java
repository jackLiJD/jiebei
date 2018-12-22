package com.ald.ebei.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/18 20:59
 * 描述：不允许短时间内双击响应
 * 修订历史：
 */
public class EbeiNoDoubleClickButton extends android.support.v7.widget.AppCompatButton {
    private long previousTime;

    public EbeiNoDoubleClickButton(Context context) {
        super(context);
    }

    public EbeiNoDoubleClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EbeiNoDoubleClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param event
     *         touch事件
     *
     * @return 是否消耗点击事件
     * true - 消耗点击事件
     * false - 不消耗点击事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                long currentTime = System.currentTimeMillis();
                if (currentTime - previousTime < 1000) {
                    return true;
                }
                previousTime = currentTime;
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
