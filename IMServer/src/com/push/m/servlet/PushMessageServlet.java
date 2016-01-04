package com.push.m.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.push.m.chat.IMUtil;
import com.push.m.db.DBUtil;
import com.push.m.model.PushMessage;
import com.push.m.model.TextMessage;

public class PushMessageServlet extends HttpServlet {
	
	private Gson gson = new Gson();
	
	/**
	 * Constructor of the object.
	 */
	public PushMessageServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); 
		response.setContentType("text/json");
		String method = request.getParameter("method");
		if(method == null) {
			return;
		}
		if(method.equals("findAllDevice")) {//列出终端列表
			List<Map> list = IMUtil.findAllDevice();
			PrintWriter out = response.getWriter();
			out.println(gson.toJson(list));
			out.flush();
			out.close();
		} else if(method.equals("findAllTags")) {//列出标签
			List<Map> list = IMUtil.findAllTags();
			PrintWriter out = response.getWriter();
			out.println(gson.toJson(list));
			out.flush();
			out.close();
		} else if(method.equals("send")) {//服务端推送消息
			Date date = new Date();
			String appKey = request.getParameter("appKey");
			String tag = request.getParameter("tag");
			String title = request.getParameter("title");
			String msgContent  = request.getParameter("msgContent");
			String extras = request.getParameter("extras");
			
			//TODO: 判断AppKey合法性，通过AppKey可以区分是否为调用API的请求
			if(appKey != null) {
				
			}
			
			//发送的消息实体
			PushMessage message = new PushMessage();
			message.setTitle(title);
			message.setMsgContent(msgContent);
			message.setExtras(extras);
			message.setTime(date.getTime());
			message.setTag(tag);
			
			IMUtil.pushMessageByTag(message);
			
		} else if(method.equals("cancel")) {//服务端取消已推送但还未送达的消息 msgStatus=0
			String cancelTag = request.getParameter("cancelTag");
			String startTime  = request.getParameter("startTime");
			String endTime  = request.getParameter("endTime");
			int count = IMUtil.cancelMessage(cancelTag, startTime, endTime);
			PrintWriter out = response.getWriter();
			out.println(gson.toJson(count));
			out.flush();
			out.close();
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
