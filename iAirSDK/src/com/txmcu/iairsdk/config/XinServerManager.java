package com.txmcu.iairsdk.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.app.Activity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.txmcu.iairsdk.config.iAirConstants;


/**
 * APP和服务器沟通的协议，采用HTTP POST + JSON格式交互。
 * 
 * @author Administrator
 *
 */
public class XinServerManager {

	static String TAG = "XinServerManager";

	public abstract interface onSuccess {

		// Method descriptor #4 ()V
		public abstract void run(JSONObject response) throws JSONException;
	}
	/**
	 * 创建一个新的HTTP连接
	 * @return
	 */
	static private AsyncHttpClient getHttpClient() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);
		return client;
	}


	/**
	 * 解�??JSAON??��?????�???��??
	 * @param obj
	 * @param key
	 * @return
	 */
	private static float getJsonDouble(JSONObject obj, String key) {
		try {
			float ret = (float) obj.getDouble(key);
			return ret;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
/**
 * 解析JSON格式的整数
 * @param obj
 * @param key
 * @return
 */
	private static int getJsonInt(JSONObject obj, String key) {
		try {
			int ret = (int) obj.getInt(key);
			return ret;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
/**
 * 解析JSON 格式的字符串
 * @param obj
 * @param key
 * @return
 */
	private static String getJsonString(JSONObject obj, String key) {
		try {
			String ret = obj.getString(key);
			return ret;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * 提交一个请求给服务器。这个是所有发送消息都调用的函数，
	 * 是最低层的调用函数。
	 * @param activity
	 * @param r
	 * @param post_params
	 * @param postUrlString
	 */
	private static void postHttpBase(final Activity activity,
			final onSuccess r, final RequestParams post_params,
			final String postUrlString) {

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {

					AsyncHttpClient client = getHttpClient();
					client.post(postUrlString, post_params,
							new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(String response) {
									System.out.println(response);
									//iAirUtil.toastMessage(activity, response);

									try {
										JSONObject jsonObject = new JSONObject(
												response);
										if (r != null) {
											r.run(jsonObject);
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();

									}

									// r.run(response);
								}
							});

				} catch (Exception e) {
					Log.d(TAG, e.toString());
				}
			}
		});
	}


//	A1.1.8 【检查小新是否存在接口】（该步骤为1.1.5的下一步）更新小新的基础信息
//	1) 请求：http://112.124.58.144/android/checkxiaoxin_exist 
//	2) form数据：userid=xxx&sn=xxx
//	3) 返回：
//	A.成功：{"ret":"Ok"}
//	B.失败：{"ret":"Fail"}
	static public void checkxiaoxin_exist(final Activity activity,
			final String userid,
			final String sn,
			final onSuccess r) {

		RequestParams post_params = new RequestParams();
		post_params.put("userid", userid);
		post_params.put("sn", sn);
		postHttpBase(activity, r, post_params,
				iAirConstants.checkxiaoxin_exist);
	}
	

	
}
