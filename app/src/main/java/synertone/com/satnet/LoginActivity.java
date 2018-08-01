package synertone.com.satnet;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.tools.MyCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import synertone.com.satnet.model.AccountModel;
import synertone.com.satnet.utils.JsonUtil;
import synertone.com.satnet.utils.SharedPreferenceManager;
import synertone.com.satnet.utils.XTHttpUtil;
import synertone.com.satnet.view.ToastUtil;

import static synertone.com.satnet.R.id.btn_login;
import static synertone.com.satnet.utils.JsonUtil.string2MD5;

public class LoginActivity extends BaseActivity {
    private EditText mEdUser, mEdPassW;
    private String mStrUser, mStrPassW;
    private Button mLoginButton;
    private String mToken, mGetUser, mGetPassWord,mCode;
    private CheckBox mCheckButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
        initData();
    }

    private void initData() {
        mGetUser = SharedPreferenceManager.getString(LoginActivity.this, "subscriberId");
        mGetPassWord = SharedPreferenceManager.getString(LoginActivity.this, "passwd");
        boolean isNotChecked = SharedPreferenceManager.getBoolean(mContext, "isNotChecked");
        if(isNotChecked){
            mEdUser.setText("");
            mEdPassW.setText("");
            mCheckButton.setChecked(false);
        }else{
            mEdUser.setText(mGetUser);
            mEdPassW.setText(mGetPassWord);
            mCheckButton.setChecked(true);
        }
    }

    private void initView() {
        mCheckButton = findViewById(R.id.remember);
        mEdUser = findViewById(R.id.login_ed_user);
        mEdPassW = findViewById(R.id.login_ed_password);
        mLoginButton = findViewById(btn_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
        // 用户名改变的时候清空密码输入框
        mEdUser.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mEdPassW.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // 点击登录
    public void onLogin() {
        mStrUser = mEdUser.getText().toString();
        mStrPassW = mEdPassW.getText().toString();
        SharedPreferenceManager.saveString(LoginActivity.this, "subscriberId", mStrUser);
        SharedPreferenceManager.saveString(LoginActivity.this, "passwd", mStrPassW);
        if(mCheckButton.isChecked()){
            SharedPreferenceManager.saveBoolean(LoginActivity.this, "isNotChecked", false);
        }else{
            SharedPreferenceManager.saveBoolean(LoginActivity.this, "isNotChecked", true);
        }
        if (!TextUtils.isEmpty(mStrUser)) {
            if (!TextUtils.isEmpty(mStrPassW)) {
              // handlerSuccessLogin(mStrUser, mStrPassW);
              loginHttp(mStrUser, mStrPassW);
            } else {
                ToastUtil.doToast("密码不能为空，请输入密码");
                mEdPassW.requestFocus();
            }
        } else {
            ToastUtil.doToast("用户名不能为空，请输入用户名");
            mEdUser.requestFocus();
        }
    }

    private void loginHttp(final String mStrUser, final String mStrPassW) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("subscriberId", mStrUser);
            jsonObject.put("passwd", string2MD5(mStrPassW));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(XTHttpUtil.POST_LOGIN_ADDRESS);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        x.http().post(params, new MyCallback(mContext, true) {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(result);
                    mCode = jsonObject.optString("result");
                    mToken = jsonObject.optString("sessionToken",null);
                    if (mCode.equals("0") || mCode.equals("3")) {
                        handlerSuccessLogin(mStrUser, mStrPassW);
                        ToastUtil.doToast("登录成功");
                    } else if (mCode.equals("1")) {
                        ToastUtil.doToast("用户不存在");
                    } else if (mCode.equals("2")) {
                        ToastUtil.doToast("用户名或密码错误");
                    } else if (mCode.equals("4")) {
                        ToastUtil.doToast("鉴权超时");
                    } else if (mCode.equals("5")) {
                        ToastUtil.doToast("未知错误");
                    } else if (mCode.equals("6")) {
                        ToastUtil.doToast("服务器正忙");
                    }else if (mCode.equals("8")) {
                        ToastUtil.doToast("内部错误");
                    } else if (mCode.equals("7")) {
                        handlerSuccessLogin(mStrUser, mStrPassW);
                        ToastUtil.doToast("您已登录，请勿重复登录！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (!JsonUtil.ping()) {
                    ToastUtil.doToast("网络不可用,请检查设备");
                }else{
                    ToastUtil.doToast("连接服务器失败");
                }
            }
        });
    }

    private void handlerSuccessLogin(String mStrUser, String mStrPassW) {
        initAccountData(mStrUser, mStrPassW, mToken);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void initAccountData(String mStrUser, String mStrPassW,String mToken) {
        AccountModel accountModel=new AccountModel();
        if(mToken==null){
            mToken=SharedPreferenceManager.getString(mContext,"mToken");
        }else{
            SharedPreferenceManager.saveString(mContext,"mToken",mToken);
        }
        accountModel.setSessionToken(mToken);
        accountModel.setSubscriberId(mStrUser);
        accountModel.setmStrPassW(mStrPassW);
        SatnetApplication.accountModel=accountModel;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();

    }

    @Override
    protected void onResume () {
        super.onResume();
    }

    @Override
    protected void onStop () {
        super.onStop();

    }

    @Override
    protected void onPause () {
        super.onPause();
    }

}
