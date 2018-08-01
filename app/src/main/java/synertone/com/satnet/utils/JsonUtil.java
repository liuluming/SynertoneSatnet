package synertone.com.satnet.utils;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.List;

public class JsonUtil {

	/**
	 * 将json字符串转化成对象集合
	 * 
	 * @param json
	 * @return
	 */
	public static <T> List<T> jsonToArray(String json, Type listType) {
		try {
			Gson gs = new Gson();
			List<T> ls = gs.fromJson(json, listType);
			return ls;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将json字符串转化成对象
	 * 
	 * @param json
	 * @param c
	 * @return
	 */
	public static <T> T jsonToEntity(String json, Class<T> c) {
		try {
			Gson gs = new Gson();
			Object obj = gs.fromJson(json, c);
			return (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * MD5加密
	 * @param str
	 * @return
	 */
	public static String string2MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	/* @author sichard
* @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
* @return
*/
	public static final boolean ping() {

		String result = null;
		try {
			String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
			// 读取ping的内容，可以不加
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer stringBuffer = new StringBuffer();
			String content = "";
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
			}
			Log.d("------ping-----", "result content : " + stringBuffer.toString());
			// ping的状态
			int status = p.waitFor();
			if (status == 0) {
				result = "success";
				return true;
			} else {
				result = "failed";
			}
		} catch (IOException e) {
			result = "IOException";
		} catch (InterruptedException e) {
			result = "InterruptedException";
		} finally {
			Log.d("----result---", "result = " + result);
		}
		return false;
	}
	public static DecimalFormat decimalFormat  = new DecimalFormat("#,##0.00");
}
