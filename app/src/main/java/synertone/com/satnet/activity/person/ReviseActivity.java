package synertone.com.satnet.activity.person;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import synertone.com.satnet.utils.XTHttpUtil;
import synertone.com.satnet.view.ToastUtil;

import static synertone.com.satnet.utils.JsonUtil.string2MD5;

/*个人中心密码修改*/
public class ReviseActivity extends BaseActivity implements OnClickListener {
	private Button mReviseSavePasswork,mReviseCancelPasswork;
	private EditText mReviseOldPasswork, mReviseNewPasswork, mReviseRetSetPass;
	private String mPwdToken,mResult;
	private RelativeLayout rl_top_bar;
	private TextView mTittle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.revise_password_activity);
		initView();
	}
	
	private void initView() {
		rl_top_bar= findViewById(R.id.rl_top_bar);
		mTittle= findViewById(R.id.tv_bar_title);
		mTittle.setText(R.string.modify_pwd);
		mReviseSavePasswork = findViewById(R.id.revise_password_btnsave);
		mReviseCancelPasswork = findViewById(R.id.revise_password_cancel);
		mReviseOldPasswork = findViewById(R.id.revise_oldpassword);
		mReviseNewPasswork = findViewById(R.id.revise_newpassword);
		mReviseRetSetPass = findViewById(R.id.revise_resetpassword);
		mReviseSavePasswork.setOnClickListener(this);
		if(SatnetApplication.accountModel!=null) {
			mPwdToken = SatnetApplication.accountModel.getSessionToken();
		}
		initEvent();
	}

   //获取文本的方法
	private String oldPassWorkStr,newPassWorkStr,retSetPassStr;
	private void getTextPassStr() {
		oldPassWorkStr=mReviseOldPasswork.getText().toString();
		newPassWorkStr=mReviseNewPasswork.getText().toString();
		retSetPassStr=mReviseRetSetPass.getText().toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.revise_password_btnsave:
			getTextPassStr();
		if (!"".equals(oldPassWorkStr)) {
				if (newPassWorkStr.length()==8&&retSetPassStr.length()==8&&oldPassWorkStr.length()==8){
					if (newPassWorkStr.equals(oldPassWorkStr)) {
						ToastUtil.doToast("原始密码与新密码不能相同");
					}else if (newPassWorkStr.equals(retSetPassStr)) {
						postPassWork(oldPassWorkStr, newPassWorkStr,retSetPassStr);
					}else {
						ToastUtil.doToast("新密码与确认密码不一致,请重新输入");
						
						return;
					}
				}else{
					ToastUtil.doToast("原始密码与新密码的长度为8,且为数字!");
					return;
				}
			}else {
				ToastUtil.doToast("原始密码不能为空");
				return;
			}
			break;
			case R.id.revise_password_cancel:
				Intent mIntent = new Intent(ReviseActivity.this, PersonalActivity.class);
				startActivity(mIntent);
				finish();
				break;
		}
	}
	private void initEvent() {
		rl_top_bar.setOnTouchListener(new ComBackTouchListener());
	}

	private void postPassWork(final String oldPassWorkStr, final String newPassWorkStr, final String retSetPassStr) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("oldPasswd",string2MD5(oldPassWorkStr) );
			jsonObject.put("newPasswd1", string2MD5(newPassWorkStr));
			jsonObject.put("newPasswd2", string2MD5(retSetPassStr));
			jsonObject.put("sessionToken", mPwdToken);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestParams params = new RequestParams(XTHttpUtil.POST_MOREADV_MODIPASS);
		params.setAsJsonContent(true);
		params.setBodyContent(jsonObject.toString());
		x.http().post(params, new MyCallback(mContext, false) {

			@Override
			public void onSuccess(String result) {
				try {
					//使用JSONObject给response转换编码
					JSONObject jsonObject = new JSONObject(result);
					mResult = jsonObject.getString("result");
					if(mResult.equals("0")){
						Intent intent = new Intent(ReviseActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
						ToastUtil.doToast("修改成功");
					}else if(mResult.equals("1")){
						ToastUtil.doToast("会话不存在");
					}else if(mResult.equals("2")){
						ToastUtil.doToast("密码错误");
					}else if(mResult.equals("3")){
						ToastUtil.doToast("格式不符");
					}else if(mResult.equals("4")){
						ToastUtil.doToast("网关方向失败");
				}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtil.doToast("修改失败");
			}
		});
	}
}
