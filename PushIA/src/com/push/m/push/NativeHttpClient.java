package com.push.m.push;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.push.m.push.model.Message;
import com.push.m.util.PropertyUtil;
import com.push.m.util.StringUtil;

public class NativeHttpClient {
	private static final Logger logger = Logger.getLogger(NativeHttpClient.class);
	
	private HttpClientCallback callback;

	/**
	 * HttpClient连接SSL
	 */
	private void initSSL() {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			FileInputStream instream = new FileInputStream(new File(
					"d:\\tomcat.keystore"));
			try {
				// 加载keyStore d:\\tomcat.keystore
				trustStore.load(instream, "123456".toCharArray());
			} catch (CertificateException e) {
				e.printStackTrace();
			} finally {
				try {
					instream.close();
				} catch (Exception ignore) {
				}
			}
			// 穿件Socket工厂,将trustStore注入
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			// 创建Scheme
			Scheme sch = new Scheme("https", 8443, socketFactory);
			// 注册Scheme
			httpclient.getConnectionManager().getSchemeRegistry().register(sch);
			// 创建http请求(get方式)
			HttpGet httpget = new HttpGet(
					"https://localhost:8443/myDemo/Ajax/serivceJ.action");
			logger.info("executing request" + httpget.getRequestLine());
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			logger.info(response.getStatusLine());
			if (entity != null) {
				logger.info("Response content length: "
						+ entity.getContentLength());
				String ss = EntityUtils.toString(entity);
				logger.info(ss);
				EntityUtils.consume(entity);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 发送post请求
	 */
	public <T> void doPost(String appKey, T t, HttpClientCallback callback) {
		// 创建默认的httpClient实例.
		CloseableHttpClient httpClient = HttpClients.createDefault();   
		CloseableHttpResponse response = null;
		// 创建httppost
		HttpPost httpPost = new HttpPost(PropertyUtil.getValue("config",
				"requestUrl"));
		// 创建参数队列
		List<NameValuePair> formParams = parseParams(appKey, t);
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
			httpPost.setEntity(uefEntity);
			logger.info("executing request " + httpPost.getURI());
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			callback.complete(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, "UTF-8"));
		} catch (ClientProtocolException e) {
			callback.complete(1, e.getMessage());
		} catch (UnsupportedEncodingException e) {
			callback.complete(1, e.getMessage());
		} catch (IOException e) {
			callback.complete(1, e.getMessage());
		} finally {
			// 关闭连接,释放资源
			try {
				response.close();
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送get请求
	 */
	public <T> void doGet(String appKey, T t, HttpClientCallback callback) {

		CloseableHttpClient httpClient = HttpClients.createDefault();  
		CloseableHttpResponse response = null;

		try {
			// 创建httpget.
			HttpGet httpGet = new HttpGet(PropertyUtil.getValue("config",
					"requestUrl"));
			logger.info("executing request " + httpGet.getURI());
			// 执行get请求.
			response = httpClient.execute(httpGet);
			// 获取响应实体
			HttpEntity entity = response.getEntity();
			callback.complete(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, "UTF-8"));
		} catch (ClientProtocolException e) {
			callback.complete(1, e.getMessage());
		} catch (ParseException e) {
			callback.complete(1, e.getMessage());
		} catch (IOException e) {
			callback.complete(1, e.getMessage());
		} finally {
			// 关闭连接,释放资源
			try {
				response.close();
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private <T> List<NameValuePair> parseParams(String appKey, T t) {
		Gson gson = new Gson();
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		if(appKey != null) {
			formParams.add(new BasicNameValuePair("appKey", encode(appKey)));
		}
		if (t instanceof Map) {
			Map map = (Map) t;
			Set keySet = map.keySet();
			for (Object obj : keySet) {
				String key = (String) obj;
				formParams.add(new BasicNameValuePair(key, encode((String) map
						.get(key))));
			}
		} else if (t instanceof Message) {
			Message message = (Message) t;
			long timeToLive = message.getTimeToLive();
			String title = message.getTitle();
			String msgContent = message.getMsgContent();
			String method = message.getMethod();
			String tag = message.getTag();
			String aliasName = message.getAliasName();
			String extras = StringUtil.mapToJson(message.getExtras());
			if(method != null) {
				formParams.add(new BasicNameValuePair("method", encode(method)));
			}
			if(title != null) {
				formParams.add(new BasicNameValuePair("title", encode(title)));
			}
			if(msgContent != null) {
				formParams.add(new BasicNameValuePair("msgContent", encode(msgContent)));
			}
			if(extras != null) {
				formParams.add(new BasicNameValuePair("extras", encode(extras)));
			}
			if(aliasName != null) {
				formParams.add(new BasicNameValuePair("aliasName", encode(aliasName)));
			} else if(tag != null){
				formParams.add(new BasicNameValuePair("tag", encode(tag)));
			}
			formParams.add(new BasicNameValuePair("timeToLive", encode(String.valueOf(timeToLive))));
		}
		return formParams;
	}
	
	/**
	 * <p>Method ：encode
	 * <p>Description : 预留加密算法
	 *
	 * @param content
	 * @return 
	 */
	private String encode(String content) {
		return content;
	}
	
	/**
	 * <p>Method ：decode
	 * <p>Description : 预留解密算法
	 *
	 * @param content
	 * @return 
	 */
	private String decode(String content) {
		return content;
	}
	
	public interface HttpClientCallback {
		public void complete(int code, String result);
	}
}
