package com.push.m.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.entity.FileUploadEntity;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.MultipartEntity.CallBackInfo;
import com.lidroid.xutils.http.client.multipart.content.ContentBody;
import com.lidroid.xutils.http.client.multipart.content.FileBody;

public class XUtil {
	/**PostCallback postCB: 自定义的HTTP请求回调接口*/
	private PostCallbackIA postCBIA;
	private static HttpUtils HTTP = null;
	
	private HttpUtils getSingleInstance() {
		if(HTTP == null) {
			HTTP = new HttpUtils();
			HttpParams params = new BasicHttpParams();  
	        ConnManagerParams.setTimeout(params, 20000);  
	        HttpConnectionParams.setSoTimeout(params, 20000);  
	        HttpConnectionParams.setConnectionTimeout(params, 20000);
		}
		return HTTP;
	}
	/**
	 * <p>Method ：sendRequestByPost
	 * <p>Description : 以POST方式发送请求，带加载动画
	 *
	 * @param ctx 上下文
	 * @param url 请求地址
	 * @param paramsMap 请求参数
	 * @param postCBIA 回调接口
	 */
	public void sendRequestByPost(final Context ctx, final String url, final Map<String, Object> paramsMap, final PostCallbackIA postCBIA) {
		RequestParams requestParams = new RequestParams();
		//构造请求参数
		if(paramsMap != null) {
			Set<String> keys = paramsMap.keySet();
			for(String key : keys) {
				requestParams.addBodyParameter(key, (String)paramsMap.get(key));
			}
		}
		
		getSingleInstance().send(HttpRequest.HttpMethod.POST,
		    url,
		    requestParams,
		    new RequestCallBack<String>() {

		        @Override
		        public void onStart() {
		        	
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		            if (isUploading) {
			            
		            }
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	postCBIA.onSuccess(responseInfo);
		        }
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	postCBIA.onFailure(error, msg);
		        }
		});
	}
	
	/**
	 * <p>Method ：download
	 * <p>Description : 方法功能描述
	 *
	 * @param ctx 上下文
	 * @param url 文件地址
	 * @param localPath 本地存储路径 
	 * @param postCBIA 回调接口
	 */
	public void download(final Context ctx, final String url, final String localPath, final PostCallbackIA postCBIA) {
		HttpHandler handler = getSingleInstance().download(url,
			localPath,
		    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
		    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
		    new RequestCallBack<File>() {

		        @Override
		        public void onStart() {
		            Toast.makeText(ctx, "下载中，请稍后...", Toast.LENGTH_SHORT);
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		            
		        }

		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	postCBIA.onSuccess(responseInfo);
		        }


		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	postCBIA.onFailure(error, msg);
		        }
		});

	}
	
	public void upload(final Context ctx, final String url, final String localPath, final PostCallbackIA postCBIA) {
		//HttpUtils http = new HttpUtils(60 * 1000);  
		RequestParams params = new RequestParams();  
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("file", new FileBody(new File(localPath)));
		params.setBodyEntity(entity);
		getSingleInstance().send(HttpRequest.HttpMethod.POST,
			url,
		    params,
		    new RequestCallBack<String>() {

		        @Override
		        public void onStart() {
		            //testTextView.setText("conn...");
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		            /*if (isUploading) {
		                testTextView.setText("upload: " + current + "/" + total);
		            } else {
		                testTextView.setText("reply: " + current + "/" + total);
		            }*/
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	postCBIA.onSuccess(responseInfo);
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	postCBIA.onFailure(error, msg);
		        }
		});
	}
	
	public interface PostCallbackIA {
		/**
		 * @param responseInfo 返回结果
		 */
		public <T>void onSuccess(ResponseInfo<T> responseInfo);
		/**
		 * @param error 异常信息
		 * @param msg 异常信息
		 */
		public void onFailure(HttpException error, String msg);
	}
}
