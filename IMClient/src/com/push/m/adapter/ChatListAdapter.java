package com.push.m.adapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.push.m.Constants;
import com.push.m.chat.IMUtil;
import com.push.m.chat.IMUtil.IMUploadCallback;
import com.push.m.model.ChatHistory;
import com.push.m.model.MessageModel;
import com.push.m.model.MessageType;
import com.push.m.utils.TimeFormat;
import com.push.m.widget.ActionSheet;
import com.push.m.widget.ActionSheet.ActionSheetListener;
import com.push.m.R;

public class ChatListAdapter extends BaseAdapter {
	private static final String TAG = "IMMessage-ChatListAdapter";
	private Context mContext;
	private LayoutInflater mInflater; 
	private List<ChatHistory> mData;
	private String selfAliasName;
	private ChatListAdapter adapter;
	private RefreshHandler refreshHandler;
    
    public ChatListAdapter(Context context, List<ChatHistory> data) {  
        // TODO Auto-generated constructor stub  
    	mContext = context;
        mInflater = LayoutInflater.from(context);  
        mData = data;
        adapter = this;
        refreshHandler = new RefreshHandler(ChatListAdapter.this);
        
        selfAliasName = IMUtil.getStringSharedPreferences(mContext, "IM", "aliasName", "");
    }  
      
    @Override  
    public int getCount() {  
        // TODO Auto-generated method stub  
        return mData.size();  
    }  
      
    @Override  
    public ChatHistory getItem(int position) {  
        return mData.get(position);  
    }  
      
    @Override  
    public long getItemId(int position) {  
    	return position;
    }  
    
    /**
	 * 获取item类型数
	 */
	public int getViewTypeCount() {
        return 4;
    }

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		ChatHistory message = getItem(position); 
		if (message == null) {
			return -1;
		}
		MessageType msgType = MessageType.valueOf(message.getMsgType());
    	MessageType direct = MessageType.valueOf(message.getDirect());
		if (msgType.equals(MessageType.TEXT)) {
			if (direct.equals(MessageType.SEND)) {
				return 0;
			} else if(direct.equals(MessageType.RECV)) {
				return 1;
			}
		}
		if (msgType.equals(MessageType.AUDIO)) {
			if (direct.equals(MessageType.SEND)) {
				return 2;
			} else if(direct.equals(MessageType.RECV)) {
				return 3;
			}
		}
		return -1;// invalid
	}
	
	private View createViewByMessage(ChatHistory message, int position) {
		MessageType msgType = MessageType.valueOf(message.getMsgType());
    	MessageType direct = MessageType.valueOf(message.getDirect());
		if (msgType.equals(MessageType.TEXT)) {
			if (direct.equals(MessageType.SEND)) {
				return mInflater.inflate(R.layout.item_send_text, null);
			} else if(direct.equals(MessageType.RECV)) {
				return mInflater.inflate(R.layout.item_recv_text, null);
			}
		}
		if (msgType.equals(MessageType.AUDIO)) {
			if (direct.equals(MessageType.SEND)) {
				return mInflater.inflate(R.layout.item_send_audio, null);
			} else if(direct.equals(MessageType.RECV)) {
				return mInflater.inflate(R.layout.item_recv_audio, null);
			}
		}
		return mInflater.inflate(R.layout.item_send_text, null);
	}
      
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) { 
    	final int mPosition = position;
    	ViewHolder viewHolder = null; 
    	
    	final ChatHistory message = mData.get(position);
    	MessageType msgType = MessageType.valueOf(message.getMsgType());
    	MessageType direct = MessageType.valueOf(message.getDirect());
    	String textMsg = message.getTextMsg();
    	final String filePath = message.getFileUrl();
    	String duration = message.getDuration();
    	
        if(convertView == null){  
        	viewHolder = new ViewHolder();  
        	convertView = createViewByMessage(message, position);
        	try {
        		if (msgType.equals(MessageType.TEXT)) {
        			if (direct.equals(MessageType.SEND)) {
        				viewHolder.tvSendMsg = (TextView) convertView.findViewById(R.id.tvSendMsg);
        				viewHolder.ivSending = (ImageView) convertView.findViewById(R.id.ivSending);
        				viewHolder.ivReSend = (ImageView) convertView.findViewById(R.id.ivReSend);
        				viewHolder.ivMeHeader = (ImageView) convertView.findViewById(R.id.ivMeHeader);
        			} else if(direct.equals(MessageType.RECV)) {
        				viewHolder.tvRecvMsg = (TextView) convertView.findViewById(R.id.tvRecvMsg);
        				viewHolder.ivFriendHeader = (ImageView) convertView.findViewById(R.id.ivFriendHeader);
        			}
        		}
        		if (msgType.equals(MessageType.AUDIO)) {
        			if (direct.equals(MessageType.SEND)) {
        				viewHolder.tvSendMsg = (TextView) convertView.findViewById(R.id.tvSendMsg);
        				viewHolder.ivSending = (ImageView) convertView.findViewById(R.id.ivSending);
        				viewHolder.ivReSend = (ImageView) convertView.findViewById(R.id.ivReSend);
        				viewHolder.ivSendVolumn = (ImageView) convertView.findViewById(R.id.ivSendVolumn);
        				viewHolder.tvSendDuration = (TextView) convertView.findViewById(R.id.tvSendDuration);
        				viewHolder.ivMeHeader = (ImageView) convertView.findViewById(R.id.ivMeHeader);
        			} else if(direct.equals(MessageType.RECV)) {
        				viewHolder.tvRecvMsg = (TextView) convertView.findViewById(R.id.tvRecvMsg);
        				viewHolder.ivRecvVolumn = (ImageView) convertView.findViewById(R.id.ivRecvVolumn);
        				viewHolder.tvRecvDuration = (TextView) convertView.findViewById(R.id.tvRecvDuration);
        				viewHolder.ivVoiceUnread = (ImageView) convertView.findViewById(R.id.ivVoiceUnread);
        				viewHolder.ivFriendHeader = (ImageView) convertView.findViewById(R.id.ivFriendHeader);
        			}
        		}
        	} catch(Exception e) {
        		
        	}
			convertView.setTag(viewHolder);  
        } else {  
        	viewHolder = (ViewHolder) convertView.getTag();
        }  
        
        OnLongClickListener longClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
            	mContext.setTheme(R.style.ActionSheetStyleIOS7);
    			ActionSheet.createBuilder(mContext, ((FragmentActivity)mContext).getSupportFragmentManager())
    					.setTitle("菜单").setCancelButtonTitle("取消")
    					.setOtherButtonTitles("删除")
    					.setCancelableOnTouchOutside(true)
    					.setListener(new ActionSheetListener() {

    						@Override
    						public void onOtherButtonClick(ActionSheet actionSheet,
    								int index) {
    							if (index == 0) {
    								ChatHistory deleteItem = mData.get(position);
    								IMUtil.deleteChat(deleteItem);
    								mData.remove(position);
    								adapter.notifyDataSetChanged();
    							}
    						}

    						@Override
    						public void onDismiss(ActionSheet actionSheet,
    								boolean isCancel) {

    						}
    					}).show();
				return true;
            }
        };
        
        if(msgType.equals(MessageType.TEXT)) {
    		if(direct.equals(MessageType.SEND)) {
    	    	viewHolder.tvSendMsg.setText(textMsg);
    			int msgStatus = (Integer)message.getMsgStatus();
    			final Animation sendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
                LinearInterpolator lin = new LinearInterpolator();
                sendingAnim.setInterpolator(lin);
                switch (msgStatus) {
                    case Constants.MSG_STATUS_SUCCESS:
                        if (sendingAnim != null) {
                        	viewHolder.ivSending.clearAnimation();
                        	viewHolder.ivSending.setVisibility(View.GONE);
                        }
                        viewHolder.ivReSend.setVisibility(View.GONE);
                        break;
                    case Constants.MSG_STATUS_FAIL:
                    	if (sendingAnim != null) {
                    		viewHolder.ivSending.clearAnimation();
                    		viewHolder.ivSending.setVisibility(View.GONE);
                        }
                    	viewHolder.ivReSend.setVisibility(View.VISIBLE);
                    	viewHolder.ivReSend.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								MessageModel model = new MessageModel();
								model.setData(message);
								model.setMsgType(MessageType.TEXT);
								IMUtil.sendMsg(model, adapter, mPosition, new SendMsgCallback() {
									
									@Override
									public void notifyDataSetChanged() {
								    	refreshHandler.sendEmptyMessage(1);
									}
								});
							}
						});
                        break;
                    case Constants.MSG_STATUS_SENDING:
                    	viewHolder.ivSending.setVisibility(View.VISIBLE);
                    	viewHolder.ivSending.startAnimation(sendingAnim);
                    	viewHolder.ivReSend.setVisibility(View.GONE);
                        break;
                }
                viewHolder.tvSendMsg.setOnLongClickListener(longClickListener);
    		} else if(direct.equals(MessageType.RECV)){
    			viewHolder.tvRecvMsg.setText(textMsg);
    			viewHolder.tvRecvMsg.setOnLongClickListener(longClickListener);
    		}
    	} else if(msgType.equals(MessageType.AUDIO)) {
    		if(direct.equals(MessageType.SEND)) {
    			viewHolder.tvSendDuration.setText(duration + "''");
    			int msgStatus = message.getMsgStatus();
    			final Animation sendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
                LinearInterpolator lin = new LinearInterpolator();
                sendingAnim.setInterpolator(lin);
                switch (msgStatus) {
                    case Constants.MSG_STATUS_SUCCESS:
                        if (sendingAnim != null) {
                        	viewHolder.ivSending.clearAnimation();
                        	viewHolder.ivSending.setVisibility(View.GONE);
                        }
                        break;
                    case Constants.MSG_STATUS_FAIL:
                    	if (sendingAnim != null) {
                    		viewHolder.ivSending.clearAnimation();
                    		viewHolder.ivSending.setVisibility(View.GONE);
                        }
            			viewHolder.ivReSend.setVisibility(View.VISIBLE);
                    	viewHolder.ivReSend.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
								IMUtil.upload(filePath, new IMUploadCallback() {
									
									@Override
									public void cbSuccess(String fileUrl) {
										Date date = new Date();
										MessageModel model = new MessageModel();
										model.setData(message);
										model.setMsgType(MessageType.AUDIO);
										
										IMUtil.sendMsg(model, adapter, mData.size()-1, new SendMsgCallback() {
											
											@Override
											public void notifyDataSetChanged() {
										    	refreshHandler.sendEmptyMessage(1);
											}
										});
									}

									@Override
									public void cbFail(HttpException error, String msg) {
										message.setMsgStatus(Constants.MSG_STATUS_FAIL);
										refreshHandler.sendEmptyMessage(0);
									}
								});
								
							}
						});
                        break;
                    case Constants.MSG_STATUS_SENDING:
                    	viewHolder.ivSending.setVisibility(View.VISIBLE);
                    	viewHolder.ivSending.startAnimation(sendingAnim);
                        break;
                }
                viewHolder.tvSendMsg.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					try {
    						final MediaPlayer player = new MediaPlayer();
    						player.setDataSource(filePath);
    						if(!player.isPlaying()) {
    							player.prepare(); 
    							player.start();
    						} else {
    							player.stop();
    						}
    						
    						player.setOnCompletionListener(new OnCompletionListener() {//播放完成
    							
    							@Override
    							public void onCompletion(MediaPlayer mp) {
    								player.release();
    							}
    						});
    					} catch (IllegalArgumentException e) {
    						Log.e(TAG, e.getMessage());
    					} catch (IllegalStateException e) {
    						Log.e(TAG, e.getMessage());
    					} catch (IOException e) {
    						Log.e(TAG, e.getMessage());
    					}
    				}
    			});
                viewHolder.tvSendMsg.setOnLongClickListener(longClickListener);
    		} else if(direct.equals(MessageType.RECV)){
    			viewHolder.tvRecvDuration.setText(duration + "''");
    			if(message.getMsgStatus() == Constants.MSG_STATUS_READ) {
    				viewHolder.ivVoiceUnread.setVisibility(View.GONE);
    			} else {
    				viewHolder.ivVoiceUnread.setVisibility(View.VISIBLE);
    			}
    			viewHolder.tvRecvMsg.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					try {
    						final MediaPlayer player = new MediaPlayer();
    						player.setDataSource(filePath);
    						if(!player.isPlaying()) {
    							player.prepare(); 
    							player.start();
    							message.setMsgStatus(Constants.MSG_STATUS_READ);
    							IMUtil.updateChat(message);
    							notifyDataSetChanged();
    						} else {
    							player.stop();
    						}
    						
    						player.setOnCompletionListener(new OnCompletionListener() {//播放完成
    							
    							@Override
    							public void onCompletion(MediaPlayer mp) {
    								player.release();
    							}
    						});
    					} catch (IllegalArgumentException e) {
    						Log.e(TAG, e.getMessage());
    					} catch (IllegalStateException e) {
    						Log.e(TAG, e.getMessage());
    					} catch (IOException e) {
    						Log.e(TAG, e.getMessage());
    					}
    				}
    			}); 
    			viewHolder.tvRecvMsg.setOnLongClickListener(longClickListener);
    		}
    	}
        
        //显示时间
        TextView msgTime = (TextView) convertView
                .findViewById(R.id.sendTimeTxt);
        long nowDate = message.getTime();
        if (position != 0) {
            long lastDate = mData.get(position - 1).getTime();
            // 如果两条消息之间的间隔超过十分钟则显示时间
            if (nowDate - lastDate > 600000) {
                TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                msgTime.setText(timeFormat.getDetailTime());
                msgTime.setVisibility(View.VISIBLE);
            } else {
                msgTime.setVisibility(View.GONE);
            }
        } else {
            TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
            msgTime.setText(timeFormat.getDetailTime());
        }
        
        return convertView;  
    }  
      
    class ViewHolder{ 
    	TextView tvRecvMsg;
    	TextView tvSendMsg;
    	TextView tvRecvDuration;
    	TextView tvSendDuration;
    	ImageView ivVoiceUnread;
    	ImageView ivSending;
    	ImageView ivReSend;
    	ImageView ivRecvVolumn;
    	ImageView ivSendVolumn;
    	ImageView ivFriendHeader;
    	ImageView ivMeHeader;
    }
    
    public interface SendMsgCallback {
    	public void notifyDataSetChanged();
    }
    
    public RefreshHandler getRefreshHandler() {
    	return refreshHandler;
    }

    public class RefreshHandler extends Handler {
        // WeakReference to the outer class's instance.
        private final WeakReference<ChatListAdapter> mOuter;
        public RefreshHandler(ChatListAdapter adapter) {
            mOuter = new WeakReference<ChatListAdapter>(adapter);
        }
 
        @Override
        public void handleMessage(Message message) {
        	ChatListAdapter outer = mOuter.get();
            if (outer != null) {
            	super.handleMessage(message);
    			outer.notifyDataSetChanged();
            }
        }
    }
    
}
