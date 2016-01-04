package com.push.m.activity;

import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.push.m.widget.HeaderOnlyBackButtonView;
import com.push.m.widget.HeaderOnlyBackButtonView.HeaderCallback;
import com.push.m.R;

public class PushMessageDetailActivity extends Activity {
	@ViewInject(R.id.header) private HeaderOnlyBackButtonView header;
	@ViewInject(R.id.tvMsg) private TextView tvMsg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_push_message_detail);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * <p>Method ：init
	 * <p>Description : 初始化
	 * 
	 * @see com.push.mobile.base.BaseActivity#init()
	 */
	public void init() {
		//设置标题和返回按钮事件
		header.setCallback(new HeaderCallback() {
			
			@Override
			public void setTitle(TextView tvTitle) {
				tvTitle.setText("推送详情");
			}
			
			@Override
			public void goBack(LinearLayout btnBack) {
				// TODO Auto-generated method stub
				btnBack.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						finish();
					}
				});
			}
		});
		
		Bundle bundel = getIntent().getExtras();
		Set keySet = bundel.keySet();
		for(Object obj : keySet) {
			String key = (String) obj;
			tvMsg.append(key + ": " + bundel.getString(key) + "\n");
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * <p>Method ：onStop
	 * <p>Description : 销毁操作，进行垃圾回收
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
