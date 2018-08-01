package synertone.com.satnet.activity.person;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.tools.MyCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import synertone.com.satnet.BaseActivity;
import synertone.com.satnet.LoginActivity;
import synertone.com.satnet.R;
import synertone.com.satnet.SatnetApplication;
import synertone.com.satnet.utils.SharedPreferenceManager;
import synertone.com.satnet.utils.UpdateManager;
import synertone.com.satnet.utils.XTHttpUtil;
import synertone.com.satnet.view.ToastUtil;

public class PersonalActivity extends BaseActivity implements OnClickListener{
	protected static final String TAG = "PersonalActivity";
	private LinearLayout mRevise_password, mQuit,liuliangchaxun,my_order,zhangdanchaxun,mVersionUpdate;
	private TextView mVersionNum,mTittle,mStatus;
	private static String VERSION_1;
	private String mExitToken,mGetUser,mResult,mCode;
	UpdateManager manager = new UpdateManager(PersonalActivity.this);
	public static String AppVerSion;
	private RelativeLayout rl_top_bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_activity);
		initView();
		if (!(TextUtils.isEmpty(VERSION_1))) {
			mVersionNum.setText(VERSION_1);
		}

		try {
			mVersionNum.setText(getVersionName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initEvent();
	}

	private void initEvent() {
		rl_top_bar.setOnTouchListener(new ComBackTouchListener());
	}


	@Override
	protected void onResume() {
		super.onResume();
		queryTokenStatusPost();
	}

	private void initView() {
		mStatus= findViewById(R.id.tv_status);
		rl_top_bar= findViewById(R.id.rl_top_bar);
		mTittle= findViewById(R.id.tv_bar_title);
		mTittle.setText(R.string.person_center);
		mQuit = findViewById(R.id.ll_quit);
		mRevise_password = findViewById(R.id.change_pwd);
		mVersionNum = findViewById(R.id.personal_version_number);
		mVersionUpdate= findViewById(R.id.ll_version_update);
        liuliangchaxun= findViewById(R.id.liuliangchaxun);
		zhangdanchaxun= findViewById(R.id.zhangdanchaxun);
		my_order= findViewById(R.id.my_order);
		liuliangchaxun.setOnClickListener(this);
		zhangdanchaxun.setOnClickListener(this);
		mRevise_password.setOnClickListener(this);
		mQuit.setOnClickListener(this);
		my_order.setOnClickListener(this);
		mVersionUpdate.setOnClickListener(this);
		if(SatnetApplication.accountModel!=null){
			mGetUser=SatnetApplication.accountModel.getSubscriberId() ;
			mExitToken= SatnetApplication.accountModel.getSessionToken();
		}
	}

	private Intent mIntent;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.change_pwd:
				mIntent = new Intent(this, ReviseActivity.class);
				startActivity(mIntent);
				break;
			case R.id.zhangdanchaxun:
				mIntent = new Intent(this, AccountQueryActivity.class);
				startActivity(mIntent);
				break;
			case R.id.liuliangchaxun:
				mIntent = new Intent(this, QueryFlowActivity.class);
				startActivity(mIntent);
				break;
			case R.id.my_order:
				mIntent = new Intent(this, MyOrderActivity.class);
				startActivity(mIntent);
				break;
			case R.id.ll_quit:
				showDialog();
				break;
			case R.id.ll_version_update:
				//postVerSionQuery();
				break;
		}
	}

	// 版本开始升级请求
	private void postVerSionStart() {
		RequestParams params = new RequestParams(XTHttpUtil.GET_PEOPLE_UPSYS_START); // 网址(请替换成实际的网址)

		x.http().get(params,new MyCallback(mContext,true) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
			    ToastUtil.doToast("请求失败");
			}

			@Override
			public void onSuccess(String result) {
				ToastUtil.doToast("请求成功");
				JSONObject jsonObject= null;
				try {
					jsonObject = new JSONObject(result);
					String mUrl = jsonObject.getString("uri");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void showDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(PersonalActivity.this)
				.create();
		dialog.show();
		dialog.getWindow().setContentView(R.layout.dialog_base);
		TextView tv = dialog.getWindow().findViewById(
				R.id.dialog_tv);
		tv.setText("是否退出当前账号");
		dialog.getWindow().findViewById(R.id.dialog_tv_cencel)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		dialog.findViewById(R.id.dialog_tv_sure).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						exitRequestWithPost();
					}
				});

	}
	private void exitVoip() {

	}

	// 查看最新版本接口
	private void postVerSionQuery() {
		RequestParams params = new RequestParams(XTHttpUtil.GET_PEOPLE_UPSYS_CHECK); // 网址(请替换成实际的网址)

		x.http().get(params,new MyCallback(mContext,true) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtil.doToast( "请求失败");
			}

			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject= null;
				try {
					jsonObject = new JSONObject(result);
					String mCode = jsonObject.getString("code");
					if(mCode.equals("0")){
						ToastUtil.doToast( "查询成功");
					}else{
						ToastUtil.doToast( "查询失败");
					}
					String msg = jsonObject.getString("msg");
					AppVerSion = jsonObject.getString("ver");
					if (!(AppVerSion.equals(mVersionNum.getText()))) {
						// 这里来检测版本是否需要更新
						manager = new UpdateManager(mContext);
						postVerSionStart();
						manager.checkUpdateInfo();

					} else {
						ToastUtil.doToast( "已经是最新版本");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*// 点击退出个人中心页面
	public void onPersonFinish(View v) {
		finish();

	}*/

	/**
	 * //得到当前程序版本名
	 * @author snt1179
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		int versionCode=0;
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			Log.e(TAG, "当前程序版本号---versionName----》"+versionName);
			versionCode = pi.versionCode;
			Log.e(TAG, "当前版本号----versionCode---》"+versionCode);
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}
	/**
	 * @author snt1179
	 * 获取当前应用版本号
	 * @return
	 * @throws Exception
	 */
	private String getVersionName() throws Exception
	{
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
		String version = packInfo.versionName;
		Log.e(TAG, "当前应用版本号----version---》"+version);
	        /*获取当前系统的android版本号*/
		int currentapiVersion=android.os.Build.VERSION.SDK_INT;
		Log.e(TAG, "当前系统版本号-----currentapiVersion--》"+currentapiVersion);
		return version;
	}
	private void exitRequestWithPost() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("subscriberId", mGetUser);
			jsonObject.put("sessionToken", mExitToken);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestParams params = new RequestParams(XTHttpUtil.POST_LOGIN_OUT);
		params.setAsJsonContent(true);
		params.setBodyContent(jsonObject.toString());
		x.http().post(params, new MyCallback(mContext, false) {

			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					mCode = jsonObject.getString("errCode");
					mResult = jsonObject.getString("result");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (mResult.equals("0")) {
					if (mCode.equals("0")) {
						SharedPreferenceManager.saveString(PersonalActivity.this, "mToken", null);
						ToastUtil.doToast( "下线成功");
						Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
						startActivity(intent);
						exitVoip();
						finish();
					} else if (mCode.equals("1")) {
						ToastUtil.doToast( "用户不存在");
					} else if (mCode.equals("2")) {
						ToastUtil.doToast( "无账单");
					} else if (mCode.equals("99")) {
						ToastUtil.doToast( "未知原因");
					}
				} else if (mResult.equals("1")) {
					ToastUtil.doToast( "下线失败");
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtil.doToast( "下线失败");
			}
		});
	}
	private void queryTokenStatusPost() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("sessionToken", mExitToken);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestParams params = new RequestParams(XTHttpUtil.POST_TOKEN_STATUS);
		params.setAsJsonContent(true);
		params.setBodyContent(jsonObject.toString());
		x.http().post(params, new MyCallback(mContext, false) {

			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					mResult = jsonObject.getString("result");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (mResult.equals("0")) {
					mStatus.setText(R.string.online);
					mStatus.setTextColor(Color.parseColor("#00cd00"));
				} else  {
					mStatus.setText(R.string.offline);
					mStatus.setTextColor(Color.parseColor("#ff0000"));
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mStatus.setText(R.string.offline);
				mStatus.setTextColor(Color.parseColor("#ff0000"));
			}
		});
	}
}