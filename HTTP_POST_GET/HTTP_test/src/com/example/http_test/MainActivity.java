package com.example.http_test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button but1, but2, but3, but4;
	TextView tvw;

	private final static String BASE_URL = "http://192.168.1.100:8080//beautifulzzzz//servlet//hello";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		but1 = (Button) this.findViewById(R.id.button1);
		but2 = (Button) this.findViewById(R.id.button2);
		but3 = (Button) this.findViewById(R.id.button3);
		but4 = (Button) this.findViewById(R.id.button4);
		tvw = (TextView) this.findViewById(R.id.textView1);
		but1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Quest(1).start();
			}
		});
		but2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Quest(2).start();
			}
		});
		but3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Quest(3).start();
			}
		});
		but4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Quest(4).start();
			}
		});
	}

	/***
	 * 收集Message消息用于更新TextView,因为layout更新不能放线程中，所以要单独拿出来
	 */
	Handler registerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == -1) {
				return;
			}
			if (msg.what == 1) {
				tvw.setText(msg.getData().getSerializable("get").toString());
				return;
			}
		}
	};

	/***
	 * 
	 * @author LiTao 因为http要单独放在一个线程里，不然会导致阻塞，我们有4种请求方法，所以用kind表示不同的请求线程
	 *         然后分别调用不同的请求函数Fun函数进行请求
	 *         此外在Quest里也不能实现TextView更新，所以要用Message将获得的getStr返回
	 */
	public class Quest extends Thread {
		private int kind = 0;

		Quest(int kind) {
			this.kind = kind;
		}

		public void run() {
			String getStr = "";
			Message message = new Message();
			try {
				switch (kind) {
				case 1:
					getStr = Func1();
					message.what = 1;
					break;
				case 2:
					getStr = Func2();
					message.what = 1;
					break;
				case 3:
					getStr = Func3();
					message.what = 1;
					break;
				case 4:
					getStr = Func4();
					message.what = 1;
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = -1;
			}
			Bundle bundle = new Bundle();
			bundle.putSerializable("get", getStr);
			message.setData(bundle);
			registerHandler.sendMessage(message);
		}
	}
	
	/***
	 * 用HttpURLConnection发送Get请求，返回请求字符
	 * @return
	 * @throws IOException
	 */
	public String Func1() throws IOException{
		// 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
		String MyURL=BASE_URL+ "?name=" + URLEncoder.encode("beautifulzzzz", "utf-8")
        		+"&password=12345678";//(好像这里中文不行)
		URL getUrl = new URL(MyURL);
		// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
        // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) getUrl.openConnection();
        
		// 设置连接属性
		conn.setConnectTimeout(30000);// 设置连接超时时长，单位毫秒
              
        // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到服务器
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));// 取得输入流，并使用Reader读取
        String result = "";
		String line = "";
		while ((line = reader.readLine()) != null) {
			result = result + line+"\n";
		}
		System.out.println(result);
		reader.close();
		conn.disconnect();
		return result;
	}

	/***
	 * 用HttpURLConnection发送post请求，返回请求字符
	 * @return
	 * @throws IOException
	 */
 	public String Func2() throws IOException {
		URL url = new URL(BASE_URL);
		// 此处的urlConnection对象实际上是根据URL的
		// 请求协议(此处是http)生成的URLConnection类
		// 的子类HttpURLConnection,故此处最好将其转化
		// 为HttpURLConnection类型的对象,以便用到
		// HttpURLConnection更多的API.如下:
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// 设置连接属性
		conn.setDoOutput(true);// 使用 URL 连接进行输出
		conn.setDoInput(true);// 使用 URL 连接进行输入
		conn.setUseCaches(false);// POST请求不能用缓存
		conn.setConnectTimeout(30000);// 设置连接超时时长，单位毫秒
        conn.setInstanceFollowRedirects(true);// URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
        // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
        // 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode
        // 进行编码
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
		conn.setRequestMethod("POST");// 设置请求方式，POST or
										// GET，注意：如果请求地址为一个servlet地址的话必须设置成POST方式

		OutputStream outStrm = conn.getOutputStream();// 此处getOutputStream会隐含的进行connect
		DataOutputStream out = new DataOutputStream(outStrm);
		 // 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
        String content = "name=" + URLEncoder.encode("李某人", "utf-8")
        		+"&password="+ URLEncoder.encode("12345678", "utf-8");
        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
        out.writeBytes(content); 
        out.flush();
        out.close(); // flush and close
        
		// 调用HttpURLConnection连接对象的getInputStream()函数,
		// 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
		InputStream inStrm = conn.getInputStream(); // <===注意，实际发送请求的代码段就在这里
		// 上边的httpConn.getInputStream()方法已调用,本次HTTP请求已结束,再向对象输出流的输出已无意义，
		// 既使对象输出流没有调用close()方法，下边的操作也不会向对象输出流写入任何数据.
		// 因此，要重新发送数据时需要重新创建连接、重新设参数、重新创建流对象、重新写数据、
		// 重新发送数据(至于是否不用重新这些操作需要再研究)
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(inStrm));
		String result = "";
		String line = "";
		while ((line = reader.readLine()) != null) {
			result = result + line+"\n";
		}
		System.out.println(result);
		reader.close();
	    conn.disconnect();
		return result;
	}

	/***
	 * 使用Http的GET请求返回服务器返回结果字符串
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String Func3() throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(BASE_URL + "?name=beautifulzzzz"
				+ "&password=1234");
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 连接超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		// 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				30000);
		HttpResponse httpResp = httpClient.execute(httpGet);
		String response = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
		System.out.println(response);
		if (response == null)
			response = "";
		return response;
	}

	/***
	 * 使用Http的POST请求返回服务器返回结果字符串
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String Func4() throws ClientProtocolException, IOException {
		// 將用戶名、密碼和imei封裝到list中，待http發送post請求給服務器
		NameValuePair pair1 = new BasicNameValuePair("user_name", "涛");
		NameValuePair pair2 = new BasicNameValuePair("user_password",
				"Deddd344");
		List<NameValuePair> pairList = new ArrayList<NameValuePair>();
		pairList.add(pair1);
		pairList.add(pair2);
		HttpPost httpPost = new HttpPost(BASE_URL);
		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList,
				HTTP.UTF_8);
		// 将请求体内容加入请求中
		httpPost.setEntity(requestHttpEntity);
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 连接超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		// 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				30000);

		HttpResponse httpResp = httpClient.execute(httpPost);
		String response = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
		System.out.println(response);
		if (response == null)
			response = "";
		return response;
	}
}
