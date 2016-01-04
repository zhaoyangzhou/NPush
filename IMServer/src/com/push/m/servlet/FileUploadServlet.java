package com.push.m.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.push.m.utils.PropertyUtil;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(FileUploadServlet.class); 

	private String tempFileFolder; // 临时文件存放目录
	private String audioFileFolder; // 音频文件目录

	/**
	 * Constructor of the object.
	 */
	public FileUploadServlet() {
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
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String fileName = writeToFile(request);

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(fileName);
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// 获取项目所在目录
		String contentPath = getServletContext().getRealPath("/");
		
		this.tempFileFolder = contentPath + PropertyUtil.getValue("config", "tempFileFolder");
		this.audioFileFolder = contentPath + PropertyUtil.getValue("config", "audioFileFolder");
	}

	/**
	 * 将上传的数据流存入临时文件
	 * 
	 * @param fileSourcel
	 *            上传流
	 * @param tempFile
	 *            临时文件
	 * @throws IOException
	 */
	private String writeToFile(HttpServletRequest request) throws IOException {
		//获取网络访问路径
		String path = request.getContextPath();
		StringBuffer fileUrl = new StringBuffer();
		fileUrl.append(request.getScheme()).append("://").append(request.getServerName()).
			append(":").append(request.getServerPort()).append(path).append("/files/audio/");
		
		File audioDir = new File(this.audioFileFolder);
		if (!audioDir.exists()) {
			audioDir.mkdirs();
		}

		File tempDir = new File(this.tempFileFolder);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		try {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Set factory constraints
			factory.setSizeThreshold(4096); // 设置缓冲区大小，这里是4kb
			factory.setRepository(tempDir);// 设置缓冲区目录

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Set overall request size constraint
			upload.setSizeMax(4194304); // 设置最大文件尺寸，这里是4MB

			List<FileItem> items = upload.parseRequest(request);// 得到所有的文件
			Iterator<FileItem> i = items.iterator();
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				String fileName = fi.getName();
				if (fileName != null) {
					File fullFile = new File(fi.getName());
					File savedFile = new File(this.audioFileFolder, fullFile.getName());
					fi.write(savedFile);
					fileUrl.append(fileName);
				}
			}
			logger.info("===文件已上传===");
		} catch (Exception e) {
			// 可以跳转出错页面
			logger.error("错误原因：" + e.getMessage());
		}
		return fileUrl.toString();
	}

}
