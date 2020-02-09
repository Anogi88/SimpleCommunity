package com.example.stateswitch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 数据获取失败视图
 */
public class DefaultErrorView extends LinearLayout implements StateErrorInterface {

	private TextView mTextHint;

	public DefaultErrorView(Context context) {
		this(context, null);
	}

	public DefaultErrorView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DefaultErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_default_error, this, true);
		mTextHint = findViewById(R.id.text_error_hit);
	}

	@NonNull
	@Override
	public View getView() {
		return this;
	}

	@Override
	public void setPromptMessage(String msg) {
		mTextHint.setText(msg);
	}

	@Override
	public void setOnRetryListener(OnRetryListener listener) {
	}

	@Override
	public void visible(boolean visible) {

	}
}
