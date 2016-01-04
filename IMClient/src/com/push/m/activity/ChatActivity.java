package com.push.m.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.push.m.Constants;
import com.push.m.NotificationBuilder;
import com.push.m.adapter.ChatListAdapter;
import com.push.m.adapter.ChatListAdapter.SendMsgCallback;
import com.push.m.chat.IMUtil;
import com.push.m.chat.IMUtil.IMDownloadCallback;
import com.push.m.chat.IMUtil.IMUploadCallback;
import com.push.m.model.AudioMessage;
import com.push.m.model.ChatHistory;
import com.push.m.model.MessageModel;
import com.push.m.model.MessageType;
import com.push.m.model.TextMessage;
import com.push.m.receiver.IMReceiver;
import com.push.m.record.AudioRecorder;
import com.push.m.record.RecordButton;
import com.push.m.record.RecordButton.RecordListener;
import com.push.m.widget.HeaderOnlyBackButtonView;
import com.push.m.widget.HeaderOnlyBackButtonView.HeaderCallback;
import com.push.m.R;

public class ChatActivity extends FragmentActivity {

	public static final int REQUEST_CODE_CONTEXT_MENU = 1;
	public static final int RESULT_CODE_DELETE = 1;
	
	@ViewInject(R.id.header) private HeaderOnlyBackButtonView header;
	@ViewInject(R.id.swipeRefresh) private SwipeRefreshLayout swipeRefresh;
	@ViewInject(R.id.mChatListView) private ListView mChatListView;
	@ViewInject(R.id.btnRecord) private RecordButton btnRecord;
	@ViewInject(R.id.etMsg) private EditText etMsg;
	@ViewInject(R.id.btnAddFile) private ImageButton btnAddFile;
	@ViewInject(R.id.btnSendMsg) private Button btnSendMsg;
	private String selfDeviceId, selfName, friendDeviceId, friendName, friendAliasName, selfAliasName;
	
	private List<ChatHistory> mData = new ArrayList<ChatHistory>();
	private ChatListAdapter adapter;
	private View mLoadingMessage;
	
	private BroadcastReceiver messageReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_chat);
		ViewUtils.inject(this);
		init();
		queryChatHistory();
		initReceiver();
	}

	/**
	 * <p>Method ：init
	 * <p>Description : 初始化
	 * 
	 * @see com.push.mobile.base.BaseActivity#init()
	 */
	public void init() {
		selfDeviceId = IMUtil.getStringSharedPreferences(this, "IM", "selfDeviceId", "");
		selfName = selfAliasName = IMUtil.getStringSharedPreferences(this, "IM", "aliasName", "我");
		friendName = getIntent().getStringExtra("friendName");
		friendDeviceId = getIntent().getStringExtra("friendDeviceId");
		friendAliasName = getIntent().getStringExtra("friendAliasName");
		
		//设置标题和返回按钮事件
		header.setCallback(new HeaderCallback() {
			
			@Override
			public void setTitle(TextView tvTitle) {
				tvTitle.setText(friendName);
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
		
		etMsg.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					showSoftInput();
				} else {
					dismissSoftInput();
				}
				
			}
		});
		
		etMsg.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length() > 0) {
					btnAddFile.setVisibility(View.GONE);
					btnSendMsg.setVisibility(View.VISIBLE);
				} else {
					btnAddFile.setVisibility(View.VISIBLE);
					btnSendMsg.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnRecord.setAudioRecord(new AudioRecorder());
		btnRecord.setRecordListener(new RecordListener() {
			
			@Override
			public void recordEnd(final String filePath, final float duration) {
				final Date date = new Date();
				final  AudioMessage message = new AudioMessage();
				message.setSendAliasName(selfAliasName);
				message.setSendDeviceId(selfDeviceId);
				message.setRecvAliasName(friendAliasName);
				message.setRecvDeviceId(friendDeviceId);
				message.setDuration(duration);
				message.setTime(date.getTime());
				message.setFileUrl(filePath);
				message.setDirect(MessageType.SEND);
				final ChatHistory chat = IMUtil.saveChat(message, Constants.MSG_STATUS_SENDING);
				mData.add(chat);
				adapter.notifyDataSetChanged();
				
				//上传音频文件
				IMUtil.upload(filePath, new IMUploadCallback() {
					
					@Override
					public void cbSuccess(String fileUrl) {
						//发送语音消息
						message.setFileUrl(fileUrl);
						MessageModel model = new MessageModel();
						model.setData(message);
						model.setMsgType(MessageType.AUDIO);
						
						IMUtil.sendMsg(model, adapter, mData.size()-1, new SendMsgCallback() {
							
							@Override
							public void notifyDataSetChanged() {
								chat.setMsgStatus(Constants.MSG_STATUS_SUCCESS);
								IMUtil.updateChat(chat);
								adapter.getRefreshHandler().sendEmptyMessage(1);
							}
						});
					}

					@Override
					public void cbFail(HttpException error, String msg) {
						chat.setMsgStatus(Constants.MSG_STATUS_FAIL);
						IMUtil.updateChat(chat);
						adapter.getRefreshHandler().sendEmptyMessage(0);
					}
				});
			}
		});
		
		adapter = new ChatListAdapter(ChatActivity.this, mData);
		mChatListView.setAdapter(adapter);
		
		// 设置下拉刷新时的颜色值,颜色值需要定义在xml中
		swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                 android.R.color.holo_orange_light, android.R.color.holo_red_light);
		// 设置下拉刷新监听器
		swipeRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				swipeRefresh.postDelayed(new Runnable() {

					@Override
					public void run() {
						queryChatHistory();
					}
				}, 1000);
			}
		});
		
		mChatListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	dismissSoftInput();
                return false;
            }
        });
	}
	
	private void queryChatHistory() {
		mData.clear();
		mData.addAll(IMUtil.findChatHistory(friendAliasName));
		//列表自动定位到最后一行
		mChatListView.setSelection(mChatListView.getAdapter().getCount()-1);
		swipeRefresh.setRefreshing(false);
	}
	
	@Override
	public void onStart() {
		IMReceiver.IS_BACKGROUND = false;
		IMReceiver.CHAT_VIEW_VISIBLE = true;
		dismissSoftInput();
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
		IMReceiver.IS_BACKGROUND = true;
		IMReceiver.CHAT_VIEW_VISIBLE = true;
		dismissSoftInput();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		IMReceiver.IS_BACKGROUND = true;
		IMReceiver.CHAT_VIEW_VISIBLE = false;
		header = null;
		unregisterReceiver(messageReceiver);
		super.onDestroy();
	}
	
	@OnClick({R.id.switchVoice, R.id.btnSendMsg, R.id.btnAddFile})
	public void onclick(View v) {
		if(v.getId() == R.id.switchVoice) {
			dismissSoftInput();
			ImageButton switchImage = (ImageButton) v;
			if(etMsg.isShown()) {
				switchImage.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn_normal);
				etMsg.setVisibility(View.GONE);
				btnRecord.setVisibility(View.VISIBLE);
			} else {
				switchImage.setBackgroundResource(R.drawable.chatting_setmode_voice_btn_normal);
				etMsg.setVisibility(View.VISIBLE);
				btnRecord.setVisibility(View.GONE);
			}
		} else if(v.getId() == R.id.btnAddFile) {
			
		} else if(v.getId() == R.id.btnSendMsg) {
			String textMsg = etMsg.getText().toString();
			etMsg.setText("");
			Date date = new Date();
			
			MessageModel model = new MessageModel();
			TextMessage message = new TextMessage();
			message.setSendAliasName(selfAliasName);
			message.setSendDeviceId(selfDeviceId);
			message.setRecvAliasName(friendAliasName);
			message.setRecvDeviceId(friendDeviceId);
			message.setTextMsg(textMsg);
			message.setTime(date.getTime());
			message.setDirect(MessageType.SEND);
			model.setData(message);
			model.setMsgType(MessageType.TEXT);
			
			ChatHistory chat = IMUtil.saveChat(message, Constants.MSG_STATUS_SENDING);
			mData.add(chat);
			adapter.notifyDataSetChanged();
			
			IMUtil.sendMsg(model, adapter, mData.size()-1, null);
		}
	}
	
	/**
	 * <p>Method ：initReceiver
	 * <p>Description : 监听即时消息
	 * 
	 */
	public void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.MESSAGE_RECEIVE_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		messageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(Constants.MESSAGE_RECEIVE_ACTION)) {
			        MessageModel model = (MessageModel)intent.getSerializableExtra("model");
			        
			        if(model.getMsgType().equals(MessageType.TEXT)) {
			        	TextMessage message = (TextMessage)model.getData();
			        	message.setDirect(MessageType.RECV);
			        	String textMsg = message.getTextMsg();
			        	Map extras = new HashMap();
			        	extras.put("friendName", message.getSenderName());
			        	extras.put("friendDeviceId", message.getSendDeviceId());
			        	ChatHistory chat = IMUtil.saveChat(message, Constants.MSG_STATUS_READ);
						mData.add(chat);
						adapter.notifyDataSetChanged();
			        } else if(model.getMsgType().equals(MessageType.AUDIO)) {
			        	final AudioMessage message = (AudioMessage)model.getData();
			        	message.setDirect(MessageType.RECV);
			        	IMUtil.download(message.getFileUrl(), new IMDownloadCallback() {
							
							@Override
							public void cbSuccess(String filePath) {
								ChatHistory chat = IMUtil.saveChat(message, Constants.MSG_STATUS_UNREAD);
								mData.add(chat);
								adapter.notifyDataSetChanged();
							}
						});
			        }
			        
				}
			}
		};
		registerReceiver(messageReceiver, filter);
	}
	
	private void showSoftInput() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                InputMethodManager imm = ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE));
                imm.showSoftInputFromInputMethod(etMsg.getWindowToken(), 0);
            }
        }
    }

    public void dismissSoftInput() {
        //隐藏软键盘
        InputMethodManager imm = ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

	@Override
    public void onLowMemory() {
    	System.gc();
        super.onLowMemory();
    }
}
