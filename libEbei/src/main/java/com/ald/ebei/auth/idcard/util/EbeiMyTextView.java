package com.ald.ebei.auth.idcard.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class EbeiMyTextView extends TextView {
	public EbeiMyTextView(Context context) {
		super(context);
	}

	public EbeiMyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 倾斜度45,上下左右居中
		canvas.rotate(90, getMeasuredWidth() * 10 / 25, getMeasuredHeight() * 10 / 25);
		super.onDraw(canvas);
	}
}
