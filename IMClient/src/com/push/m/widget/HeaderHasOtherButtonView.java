package com.push.m.widget;

import com.push.m.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * <p>Class       : com.push.mobile.view.HeaderHasOtherButtonView
 * <p>Description: 自定义右侧带其它按钮的标题控件
 *
 */
public class HeaderHasOtherButtonView extends LinearLayout {
	private Context mContext;
	private HeaderCallback callback;
	
	private LinearLayout btnBack;
	private TextView btnOther;
	private TextView tvTitle;
	
	public HeaderHasOtherButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	public HeaderHasOtherButtonView(Context context) {
		super(context);
		this.init(context);
	}
	
	private void init(Context context) {
		this.mContext = context;
		View convertView = LayoutInflater.from(context).inflate(R.layout.header_has_other_button, this);
		btnBack = (LinearLayout)convertView.findViewById(R.id.btnBack);
		tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
		btnOther = (TextView)convertView.findViewById(R.id.btnOther);
	}
	
	public void setCallback(HeaderCallback callback) {
		this.callback = callback;
		//设置标题
		callback.setTitle(tvTitle);
		//返回按钮
		callback.goBack(btnBack);
		//右侧按钮
		callback.otherButton(btnOther);
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
		 * <p>Method ：otherButton
		 * <p>Description : 点击右侧按钮
		 *
		 * @param btnOther 
		 */
		public void otherButton(TextView btnOther);
		/**
		 * <p>Method ：setTitle
		 * <p>Description : 设置标题
		 *
		 * @param tvTitle 
		 */
		public void setTitle(TextView tvTitle);
	}
}
