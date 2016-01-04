package com.push.m.chat;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.StringUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.push.m.Constants;
import com.push.m.adapter.ChatListAdapter;
import com.push.m.adapter.ChatListAdapter.SendMsgCallback;
import com.push.m.model.AudioMessage;
import com.push.m.model.ChatHistory;
import com.push.m.model.MessageModel;
import com.push.m.model.MessageType;
import com.push.m.model.Terminal;
import com.push.m.model.TextMessage;
import com.push.m.service.PushService;
import com.push.m.utils.XUtil;
import com.push.m.utils.XUtil.PostCallbackIA;

public class IMUtil {
	private static final String TAG = "IMMessage-IMUtil";

	private static Context APPLICATION_CONTEXT;

	private static XUtil xUtil = new XUtil();
	private static DbUtils db;

	public static Context getContext() {
		return APPLICATION_CONTEXT;
	}

	/**
	 * <p>
	 * Method ：init
	 * <p>
	 * Description : 方法功能描述
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		APPLICATION_CONTEXT = context;
		try {
			DaoConfig config = new DaoConfig(context);
			config.setDbName("xUtils-demo"); // db名
			config.setDbVersion(1); // db版本
			db = DbUtils.create(config);// db还有其他的一些构造方法，比如含有更新表版本的监听器的
			db.configAllowTransaction(true);
			db.createTableIfNotExist(ChatHistory.class);
		} catch (DbException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * <p>
	 * Method ：setTagsAndAliasName
	 * <p>
	 * Description : 设置标签并启动推送服务
	 * 
	 * @param tags
	 */
	public static void setTagsAndAliasName(String[] tags, String aliasName) {
		StringBuffer tagBuf = new StringBuffer();
		for(int i = 0, len = tags.length, j = len - 1; i < len; i++) {
			tagBuf.append(tags[i]);
			if(i < j) {
				tagBuf.append(",");
			}
		}
		setStringSharedPreferences(APPLICATION_CONTEXT, "IM", "tags", tagBuf.toString());
		setBooleanSharedPreferences(APPLICATION_CONTEXT, "IM", "enablePush", true);
		setStringSharedPreferences(APPLICATION_CONTEXT, "IM", "selfDeviceId", getDeviceUUID(APPLICATION_CONTEXT));
		setStringSharedPreferences(APPLICATION_CONTEXT, "IM", "aliasName", aliasName);
		APPLICATION_CONTEXT.startService(new Intent(Constants.PUSH_SERVICE_ACTION));
	}

	/**
	 * <p>
	 * Method ：stopPush
	 * <p>
	 * Description : 停止推送服务
	 * 
	 */
	public static void stopPush() {
		setBooleanSharedPreferences(APPLICATION_CONTEXT, "IM", "enablePush", false);
		MessageModel model = new MessageModel();
		model.setData(getTerminal());
		model.setMsgType(MessageType.CONNECT);
		String json = IMUtil.toJson(model);
		PushService.getChannelFuture().channel().writeAndFlush(json);
		PushService.stop();
		APPLICATION_CONTEXT.stopService(new Intent(Constants.PUSH_SERVICE_ACTION));
	}
	
	/**
	 * <p>
	 * Method ：getTerminal
	 * <p>
	 * Description : 获取终端信息
	 * 
	 * @return
	 */
	public static Terminal getTerminal() {
		String tags = getStringSharedPreferences(APPLICATION_CONTEXT, "IM", "tags", "");
		boolean enablePush = getBooleanSharedPreferences(APPLICATION_CONTEXT, "IM", "enablePush", true);
		String deviceId = getStringSharedPreferences(APPLICATION_CONTEXT, "IM", "selfDeviceId", "");
		String aliasName = getStringSharedPreferences(APPLICATION_CONTEXT, "IM", "aliasName", "");
		Terminal terminal = new Terminal();
		terminal.setTags(tags.split(","));
		terminal.setEnablePush(enablePush);
		terminal.setDeviceId(deviceId);
		terminal.setAliasName(aliasName);
		terminal.setPlatform("android");
		return terminal;
	}

	/**
	 * <p>
	 * Method ：sendMsg
	 * <p>
	 * Description : 发送消息
	 * 
	 * @param msg
	 *            消息，MessageModel
	 * @param adapter
	 * @param position
	 *            在数据源中的索引位置
	 * @param callback
	 *            刷新列表回调方法
	 */
	public static <T> void sendMsg(T msg, final ChatListAdapter adapter,
			final int position, final SendMsgCallback callback) {
		Gson gson = new Gson();
		String json = gson.toJson(msg);
		PushService.getChannelFuture().channel().writeAndFlush(json)
				.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture f)
							throws Exception {
						ChatHistory chat = (ChatHistory) adapter
								.getItem(position);
						if (f.isSuccess()) {
							chat.setMsgStatus(Constants.MSG_STATUS_SUCCESS);
							updateChat(chat);
							if (callback != null) {
								callback.notifyDataSetChanged();
							} else {
								adapter.notifyDataSetChanged();
							}
						} else {
							chat.setMsgStatus(Constants.MSG_STATUS_FAIL);
							updateChat(chat);
							if (callback != null) {
								callback.notifyDataSetChanged();
							} else {
								adapter.notifyDataSetChanged();
							}
						}
					}
				});
	}

	public static String getTerminalList(final IMCallback callback) {
		Map params = new HashMap();
		params.put("method", "findAllDevice");
		xUtil.sendRequestByPost(APPLICATION_CONTEXT, Constants.SERVER
				+ "IMServer/servlet/PushMessageServlet", params,
				new PostCallbackIA() {

					@Override
					public <T> void onSuccess(ResponseInfo<T> responseInfo) {
						String result = (String) responseInfo.result;
						callback.cbQueryTerminals(result);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}

				});
		return null;
	}

	public static void upload(String filePath, final IMUploadCallback callback) {
		String uploadUrl = Constants.SERVER + getMetaValue("uploadUrl");
		xUtil.upload(APPLICATION_CONTEXT, uploadUrl, filePath,
				new PostCallbackIA() {

					@Override
					public <T> void onSuccess(ResponseInfo<T> responseInfo) {
						String fileName = responseInfo.result.toString();
						if (callback != null) {
							callback.cbSuccess(fileName);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if (callback != null) {
							callback.cbFail(error, msg);
						}
					}
				});
	}

	public static void download(String fileUrl,
			final IMDownloadCallback callback) {
		final String localPath = Constants.AUDIO_DIR + getCurrentDate()
				+ ".amr";
		xUtil.download(APPLICATION_CONTEXT, fileUrl, localPath,
				new PostCallbackIA() {

					@Override
					public <T> void onSuccess(ResponseInfo<T> responseInfo) {
						callback.cbSuccess(localPath);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub

					}
				});
	}

	public static MessageModel fromJson(String json, Class clazz) {
		Gson gson = new Gson();
		Type objectType = type(MessageModel.class, clazz);
		return gson.fromJson(json, objectType);
	}

	public static <T> String toJson(T clazz) {
		Gson gson = new Gson();
		Type objectType = type(MessageModel.class, clazz.getClass());
		return gson.toJson(clazz, objectType);
	}

	static ParameterizedType type(final Class raw, final Type... args) {
		return new ParameterizedType() {
			public Type getRawType() {
				return raw;
			}

			public Type[] getActualTypeArguments() {
				return args;
			}

			public Type getOwnerType() {
				return null;
			}
		};
	}

	// 以当前时间作为文件名
	public static String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	public static String getMetaValue(String metaName) {
		ApplicationInfo appInfo;
		String metaValue = null;
		try {
			appInfo = APPLICATION_CONTEXT.getPackageManager()
					.getApplicationInfo(APPLICATION_CONTEXT.getPackageName(),
							PackageManager.GET_META_DATA);
			metaValue = appInfo.metaData.getString(metaName);
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return metaValue;
	}
	
	/**
	 * <p>Method ：saveChat
	 * <p>Description : 保存聊天记录
	 *
	 * @param t
	 * @param msgStatus
	 * @return 
	 */
	public static <T> ChatHistory saveChat(T t, int msgStatus) {
		//当前登陆人别名
		String aliasName = getStringSharedPreferences(APPLICATION_CONTEXT, "IM", "aliasName", "");
		ChatHistory chat = null;
		try {
			if(t instanceof TextMessage) {
				TextMessage message = (TextMessage) t;
				chat = new ChatHistory();
				chat.setMsgStatus(msgStatus);
				chat.setMsgType(MessageType.TEXT.toString());
				chat.setRecvDeviceId(message.getRecvDeviceId());
				chat.setSendDeviceId(message.getSendDeviceId());
				chat.setTextMsg(message.getTextMsg());
				chat.setTime(message.getTime());
				chat.setRecvAliasName(message.getRecvAliasName());
				chat.setSendAliasName(message.getSendAliasName());
				chat.setDirect(message.getDirect().toString());
				chat.setUserAliasName(aliasName);
			} else if(t instanceof AudioMessage) {
				AudioMessage message = (AudioMessage) t;
				DecimalFormat decimalFormat = new DecimalFormat(".#"); 
		    	double second = Double.parseDouble(decimalFormat.format(message.getDuration()/1000)) ;
				chat = new ChatHistory();
				chat.setDuration(String.valueOf(second));
				chat.setFileUrl(message.getFileUrl());
				chat.setMsgStatus(msgStatus);
				chat.setMsgType(MessageType.AUDIO.toString());
				chat.setRecvDeviceId(message.getRecvDeviceId());
				chat.setSendDeviceId(message.getSendDeviceId());
				chat.setTime(message.getTime());
				chat.setRecvAliasName(message.getRecvAliasName());
				chat.setSendAliasName(message.getSendAliasName());
				chat.setDirect(message.getDirect().toString());
				chat.setUserAliasName(aliasName);
			}
			db.saveOrUpdate(chat);
		} catch (DbException e) {
			Log.e(TAG, e.getMessage());
		}
		return chat;
	}
	
	/**
	 * <p>Method ：updateChat
	 * <p>Description : 修改聊天记录状态
	 *
	 * @param chatBean
	 * @return 
	 */
	public static ChatHistory updateChat(ChatHistory chatBean) {
		ChatHistory chat = null;
		try {
			chat = db.findFirst(
					Selector.from(ChatHistory.class)
						.where("time", "=", chatBean.getTime())
						.and("sendAliasName", "=", chatBean.getSendAliasName())
						.and("recvAliasName", "=", chatBean.getRecvAliasName())
						.and("userAliasName", "=", chatBean.getUserAliasName()));
			if(chat != null) {
				chat.setMsgStatus(chatBean.getMsgStatus());
				db.update(chat, "msgStatus");
			}
		} catch (DbException e) {
			Log.e(TAG, e.getMessage());
		}
		return chat;
	}
	
	/**
	 * <p>Method ：deleteChat
	 * <p>Description : 删除单条聊天信息
	 *
	 */
	public static void deleteChat(ChatHistory chatBean) {
		try {
			db.delete(chatBean);
		} catch (DbException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	public static void deleteAllChat(String aliasName) {
		try {
			db.delete(ChatHistory.class, WhereBuilder.b("sendAliasName", "=", aliasName).or("recvAliasName", "=", aliasName));
		} catch (DbException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * <p>Method ：findChatHistory
	 * <p>Description : 查询历史信息
	 *
	 * @param friendAliasName 好友别名
	 * @return 
	 */
	public static List<ChatHistory> findChatHistory(String friendAliasName) {
		//当前登陆人别名
		String aliasName = getStringSharedPreferences(APPLICATION_CONTEXT, "IM", "aliasName", "");
		try {
			return db.findAll(Selector.from(ChatHistory.class)
						.where("userAliasName", "=", aliasName)
						.and(WhereBuilder.b("sendAliasName", "=", friendAliasName).or("recvAliasName", "=", friendAliasName)));
		} catch (DbException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}
	
	/**
	 * <p>Method ：jsonToMap
	 * <p>Description : Json串转Map
	 *
	 * @param json
	 * @return 
	 */
	public static Map jsonToMap(String json) {
		if(json != null) {
			Gson gson = new Gson();
			return gson.fromJson(json, Map.class);
		}
		return null;
	}
	
	/**获取设备唯一值*/
	public static String getDeviceUUID(Context context) {

		@SuppressWarnings("static-access")
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, tmPhone, androidId;

		tmDevice = "" + tm.getDeviceId();

		tmSerial = "" + tm.getSimSerialNumber();

		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

		String uniqueId = deviceUuid.toString();

		Log.d("debug", "uuid=" + uniqueId);

		return uniqueId;
	}
	
	//获取String类型的应用变量
	public static String getStringSharedPreferences(Context context,String name,String key,String defaultValue){
		SharedPreferences preferences = context.getSharedPreferences(name, 0);
		return preferences.getString(key, defaultValue == null ? "" : defaultValue);
	}
	
	//获取boolean类型的应用变量
	public static boolean getBooleanSharedPreferences(Context context,String name,String key,boolean defaultValue){
		SharedPreferences preferences = context.getSharedPreferences(name, 0);
		return preferences.getBoolean(key, defaultValue);
	}
	
	//获取int类型的应用变量
	public static int getIntegerSharedPreferences(Context context,String name,String key,int defaultValue){
		SharedPreferences preferences = context.getSharedPreferences(name, 0);
		return preferences.getInt(key, defaultValue);
	}
	
	//设置String类型的应用变量
	public static void setStringSharedPreferences(Context context,String name,String key,String value){
		SharedPreferences preferences = context.getSharedPreferences(name, 0);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(key, value == null ? "" : value);
		editor.commit();
	}
	
	//设置boolean类型的应用变量
	public static void setBooleanSharedPreferences(Context context,String name,String key,boolean value){
		SharedPreferences preferences = context.getSharedPreferences(name, 0);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	//设置int类型的应用变量
	public static void setIntegerSharedPreferences(Context context,String name,String key,int value){
		SharedPreferences preferences = context.getSharedPreferences(name, 0);
		SharedPreferences.Editor editor=preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public interface IMCallback {
		public void cbQueryTerminals(String result);
	}

	public interface IMUploadCallback {
		public void cbSuccess(String fileName);

		public void cbFail(HttpException error, String msg);
	}

	public interface IMDownloadCallback {
		public void cbSuccess(String filePath);
	}

}
