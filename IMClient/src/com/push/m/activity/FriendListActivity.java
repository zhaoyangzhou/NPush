package com.push.m.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.push.m.Constants;
import com.push.m.adapter.FriendListAdapter;
import com.push.m.chat.IMUtil;
import com.push.m.chat.IMUtil.IMCallback;
import com.push.m.model.AliasDevice;
import com.push.m.model.ChatHistory;
import com.push.m.model.MessageModel;
import com.push.m.model.Terminal;
import com.push.m.service.PushService;
import com.push.m.widget.ActionSheet;
import com.push.m.widget.HeaderHasOtherButtonView;
import com.push.m.widget.ActionSheet.ActionSheetListener;
import com.push.m.widget.HeaderHasOtherButtonView.HeaderCallback;
import com.push.m.R;

public class FriendListActivity extends FragmentActivity implements OnItemLongClickListener {
	@ViewInject(R.id.header) private HeaderHasOtherButtonView header;
	
	@ViewInject(R.id.lvFriends) private ListView lvFriends;
	private List<Map> list = new ArrayList<Map>();
	
	private Gson gson = new Gson();
	
	private FriendListAdapter adapter;
	
	private BroadcastReceiver onlineReceiver;
	
	//应用退出
	private long mExitTime;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_friend_list);
		ViewUtils.inject(this);
		initReceiver();
		init();
		queryList();
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
				tvTitle.setText("朋友列表");
			}
			
			@Override
			public void goBack(LinearLayout btnBack) {
				// TODO Auto-generated method stub
				btnBack.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						PushService.isConnSucc = false;
						finish();
					}
				});
			}

			@Override
			public void otherButton(TextView btnOther) {
				btnOther.setText("刷新");
				
				btnOther.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						queryList();
					}
				});
			}
		});
		
		adapter = new FriendListAdapter(FriendListActivity.this, list, R.layout.item_friend);
		lvFriends.setAdapter(adapter);
		
		lvFriends.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		setTheme(R.style.ActionSheetStyleIOS7);
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setTitle("菜单").setCancelButtonTitle("取消")
				.setOtherButtonTitles("删除")
				.setCancelableOnTouchOutside(true)
				.setListener(new ActionSheetListener() {

					@Override
					public void onOtherButtonClick(ActionSheet actionSheet,
							int index) {
						if (index == 0) {
							Map map = (Map) adapter.getItem(position);
							IMUtil.deleteAllChat((String) map.get("alias_name"));
						}
					}

					@Override
					public void onDismiss(ActionSheet actionSheet,
							boolean isCancel) {

					}
				}).show();
		return true;
	}
	
	private void queryList() {
		IMUtil.getTerminalList(new IMCallback() {
			
			@Override
			public void cbQueryTerminals(String result) {
				if(result != "") {
					Terminal terminal = IMUtil.getTerminal();
					List<Map> dataList = gson.fromJson(result, List.class);
					if(dataList == null || dataList.size() == 0) {
						return;
					}
					//将自己从好友列表中移除
					for(Map map : dataList) {
						if(map.get("alias_name").equals(terminal.getAliasName())) {
							dataList.remove(map);
							break;
						}
					}
					
					list.clear();
					list.addAll(dataList);
					adapter.notifyDataSetChanged();
					
					lvFriends.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							Map map = list.get(position);
							String deviceId = (String)map.get("device_id");
							String friendName = (String)map.get("user_name");
							String friendAliasName = (String)map.get("alias_name");
							Intent intent = new Intent(FriendListActivity.this, ChatActivity.class);
							intent.putExtra("friendDeviceId", deviceId);
							intent.putExtra("friendName", friendName);
							intent.putExtra("friendAliasName", friendAliasName);
							startActivity(intent);
						}
					});
				}
			}
		});
	}
	
	/**
	 * <p>Method ：initReceiver
	 * <p>Description : 监听好友上线状态
	 * 
	 */
	public void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ONLINE_RECEIVE_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		onlineReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(Constants.ONLINE_RECEIVE_ACTION)) {
			        MessageModel model = (MessageModel)intent.getSerializableExtra("model");
			        AliasDevice device = (AliasDevice)model.getData();
			        for(Map deviceMap : list) {
			        	if(deviceMap.get("alias_name").equals(device.getAliasName())) {
			        		deviceMap.put("device_id", device.getDeviceId());
			        		deviceMap.put("online", device.getOnline());
			        		adapter.notifyDataSetChanged();
			        		break;
			        	}
			        }
				}
			}
		};
		registerReceiver(onlineReceiver, filter);
	}
	
	/**
	 * <p>Method ：onKeyDown
	 * <p>Description : 物理按键监听事件
	 *
	 * @param keyCode
	 * @param event
	 * @return 
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				IMUtil.stopPush();
				System.exit(0);
			}
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * <p>Method ：onStop
	 * <p>Description : 销毁操作，进行垃圾回收
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(onlineReceiver);
		super.onDestroy();
	}
}
