package com.push.m.widget;

import com.push.m.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <p>Class       : com.push.mobile.view.FilterBarView
 * <p>Description: 自定义只带返回按钮的标题控件
 *
 */
public class HeaderOnlyBackButtonView extends LinearLayout {
	private Context mContext;
	private HeaderCallback callback;
	
	private LinearLayout btnBack;
	private TextView tvTitle;
	
	public HeaderOnlyBackButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	public HeaderOnlyBackButtonView(Context context) {
		super(context);
		this.init(context);
	}
	
	private void init(Context context) {
		this.mContext = context;
		View convertView = LayoutInflater.from(context).inflate(R.layout.header_only_back_button, this);
		btnBack = (LinearLayout)convertView.findViewById(R.id.btnBack);
		tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
	}
	
	public void setCallback(HeaderCallback callback) {
		this.callback = callback;
		//设置标题
		callback.setTitle(tvTitle);
		//返回按钮
		callback.goBack(btnBack);
	}

	public interface HeaderCallback {
		/**
		 * <p>Method ：goBack
		 * <p>Description : 返回
		 *
		 * @param btnBack 
		 */
		public void goBack(LinearLayout btnBack);
		/**
		 * <p>Method ：setTitle
		 * <p>Description : 设置标题
		 *
		 * @param tvTitle 
		 */
		public void setTitle(TextView tvTitle);
	}
}
