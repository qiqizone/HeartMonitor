package com.ustc.engineering.lab410.HeartMonitor.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BaseViewEx extends FrameLayout {

	public BaseViewEx(Context context) {
		this(context, null);
	}

	public BaseViewEx(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BaseViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View child = LayoutInflater.from(getContext()).inflate(getLayoutId(),
				null);
		LayoutParams chilidLp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		this.addView(child, chilidLp);
		init();
	}
	
	protected abstract void init();

	protected abstract int getLayoutId();
}
