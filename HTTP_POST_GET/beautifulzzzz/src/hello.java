import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class hello extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public hello() {
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
	/*
	 * 以Get方式访问页面时执行该函数 执行doGet前会先执行getLastModified,如果浏览器发现getLastModified返回数值
	 * 与上次访问返回数值相同，则认为该文档没有更新，浏览器执行缓存而不执行doGet 如果返回-1则认为是实时更新的，总是执行该函数
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.log("执行 doGet 方法...");
		this.execute(request, response);
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
	 *             if an error occurred 执行前不会执行getLastModified
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.log("执行 doPost 方法...");
		this.execute(request, response);
	}

	/**
	 * 返回该Servlet生成文档的更新时间。对Get方法有效 返回的时间为相对于1970年1月1日08:00:00的毫秒数
	 * 如果返回-1表示实时更新。默认为-1
	 */
	@Override
	public long getLastModified(HttpServletRequest request) {
		this.log("执行 getLastModified 方法...");
		return -1;
	}

	// 执行方法
	private void execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setCharacterEncoding("UTF-8");// 设置request和response编码，两个都要注意
		request.setCharacterEncoding("UTF-8");
		String requestURI = request.getRequestURI();// 访问Servlet的URI
		String method = request.getMethod();// 访问Servlet的方式Get或Post
		// 获得用户提交的所有param
		Map<String, String> map = request.getParameterMap();
		for (String key : map.keySet()) {
			System.out.println(key + "+" + request.getParameter(key));
		}

		response.setContentType("text/html");// 设置文档类型为HTML类型
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
		out.println("<HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println(" <BODY>");
		out.println("	以" + method + " 方式访问该页面。提取的param参数为：<br/>");
		for (String key : map.keySet()) {
			out.println("	" + key + "+" + request.getParameter(key) + "<br/>");
		}

		out.println("  </BODY>");
		out.println("</HTML>");
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
		// Put your code here
	}

}
