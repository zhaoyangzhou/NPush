package com.push.m;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.push.m.activity.FriendListActivity;
import com.push.m.chat.IMUtil;
import com.push.m.R;

public class MainActivity extends Activity {
	private EditText etUsername;
	private Button btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		etUsername = (EditText) findViewById(R.id.etUsername);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String deviceId = IMUtil.getDeviceUUID(MainActivity.this);
				String name = etUsername.getText().toString();
				String[] tags = {name, "teacher"};
				IMUtil.setTagsAndAliasName(tags, name);
				
				Intent intent = new Intent(MainActivity.this, FriendListActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
