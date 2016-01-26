package cn.singull.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.singull.utils.UIUtils;

public class HttpHelper {
	/**
	 * get请求
	 * 
	 * @param uri
	 * @return
	 */
	public static String get(String uri) {
		String json = null;
		HttpGet get = new HttpGet(uri);
		try {
			HttpResponse response = new DefaultHttpClient().execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				json = EntityUtils.toString(entity);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * post请求
	 * @param uri
	 * @param list
	 * @return
	 */
	public static String post(String uri, List<BasicNameValuePair> list) {
		String json = null;
		HttpPost post = new HttpPost(uri);
		try {
			post.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				json = EntityUtils.toString(entity);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 获取本机IP地址
	 * 
	 * @param
	 * @return
	 */
	public static String getLocalID() {
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> inet = nif.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (inet.hasMoreElements()) {
					InetAddress ip = inet.nextElement();
					if (!ip.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ip
							.getHostAddress())) {
						ipaddress = ip.getHostAddress();
						System.out.println(ipaddress);
						return ipaddress;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return "error";
		}
		return "error";
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		// 获取代表联网状态的NetWorkInfo对象
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取当前的网络连接是否可用
		if (manager.getActiveNetworkInfo() != null) {
			return manager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	/**
	 * 判断是否连接wifi
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifiNetworkInfo.isConnected() ;

	}
	/**
	 * Base64Encoder base64加密
	 * 
	 * @param str
	 * @return
	 */
	public static String base64Encoder(String str) {
		return base64Encoder(str.getBytes());
	}

	/**
	 * Base64Encoder base64加密
	 * 
	 * @param bytes
	 * @return
	 */
	public static String base64Encoder(byte[] bytes) {
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	/**
	 * Base64Decoder base64解密
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] base64Decoder(String str) {
		return base64Decoder(str.getBytes());
	}

	/**
	 * Basse64Decoder base64解密
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] base64Decoder(byte[] bytes) {
		return Base64.decode(bytes, Base64.DEFAULT);
	}

	/**
	 * URLEncoder URL加密
	 * 
	 * @param str
	 * @return
	 */
	public static String urlEncoder(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	/**
	 * URLEncoder URL加密
	 * 
	 * @param bytes
	 * @return
	 */
	public static String urlEncoder(byte[] bytes) {
		return urlEncoder(bytes.toString());
	}

	/**
	 * URLDecoder URL解密
	 * 
	 * @param str
	 * @return
	 */
	public static String urlDecoder(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	/**
	 * URLDecoder URL解密
	 * 
	 * @param bytes
	 * @return
	 */
	public static String urlDecoder(byte[] bytes) {
		return urlDecoder(bytes.toString());
	}

	/**
	 * MD5加密
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		StringBuffer buf = new StringBuffer("");
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte b[] = md5.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 获取系统时间currentTimeMillis() (php的格林威治时间单位是秒)
	 * 
	 * @param
	 * @return
	 */
	public static long getDateCurrentTimeMillis() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * Unicode转中文
	 * 
	 * @param str
	 * @return
	 */
	public static String reconvert(String str) {
		char[] c = str.toCharArray();
		String resultStr = "";
		for (int i = 0; i < c.length; i++)
			resultStr += String.valueOf(c[i]);
		return resultStr;
	}

	/**
	 * 中文转Unicode
	 * 
	 * @param str
	 * @return
	 */
	public static String conver(String str) {
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			String temp = "";
			int strInt = str.charAt(i);
			if (strInt > 127) {
				temp += "\\u" + Integer.toHexString(strInt);
			} else {
				temp = String.valueOf(str.charAt(i));
			}

			result += temp;
		}
		return result;
	}

	/**
	 * map转json串
	 * 
	 * @param map
	 * @return
	 */
	public static String simpleMapToJsonStr(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return "null";
		}
		String jsonStr = "{";
		Set<?> keySet = map.keySet();
		for (Object key : keySet) {
			jsonStr += "\"" + key + "\":\"" + map.get(key) + "\",";
		}
		jsonStr = jsonStr.substring(1, jsonStr.length() - 2);
		jsonStr += "}";
		return jsonStr;
	}

	/**
	 * json串转map
	 * 
	 * @param str
	 * @return
	 */
	public static Map jsonStrToSimpleMap(String str) {
		String sb = str.substring(1, str.length() - 1);
		String[] name = sb.split("\\\",\\\"");
		String[] nn = null;
		Map map = new HashMap();
		for (int i = 0; i < name.length; i++) {
			nn = name[i].split("\\\":\\\"");
			map.put(nn[0], nn[1]);
		}
		return map;
	}

	/**
	 * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法变量
	 */
	static double x_pi = Math.PI * 3000.0 / 180.0;

	/**
	 * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法
	 * 
	 * @param gcj_lat
	 * @param gcj_lon
	 * @return
	 */
	public static double[] bd_encrypt(double gcj_lat, double gcj_lon) {
		double x = gcj_lon, y = gcj_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		return new double[] { bd_lat, bd_lon };
	}

	/**
	 * 百度坐标系 (BD-09) 与火星坐标系 (GCJ-02) 的转换算法变量
	 * 
	 * @param bd_lat
	 * @param bd_lon
	 * @return
	 */
	public static double[] bd_decrypt(double bd_lat, double bd_lon) {
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gg_lon = z * Math.cos(theta);
		double gg_lat = z * Math.sin(theta);
		return new double[] { gg_lat, gg_lon };
	}

	/**
	 * 图片转字符串base64加密
	 * 
	 * @param path
	 * @return
	 */
	public static String image2String(String path) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		bitmap = UIUtils.bitmapSample(path,1920,1920, Bitmap.Config.ARGB_8888);
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
			System.gc();
		}
		String str = HttpHelper.base64Encoder(bStream.toByteArray());
		try {
			fis.close();
			bStream.close();
			bStream = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 文件目录转换为json
	 * @param file
	 * @return
	 */
	public static String filesToJson(File file){
		JSONObject object = new JSONObject();
		List<String> files = new ArrayList<>();
		for(File f :file.listFiles()){
			if(f.isDirectory()){
				files.add(filesToJson(f));
			}
			else{
				files.add(f.getName());
			}
		}
		try {
			object.put(file.getName(), files);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
}
