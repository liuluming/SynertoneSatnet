package synertone.com.satnet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneService;
import org.linphone.tools.MyCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import synertone.com.satnet.model.AccountModel;
import synertone.com.satnet.utils.BitmapUtil;
import synertone.com.satnet.utils.SharedPreferenceManager;
import synertone.com.satnet.utils.XTHttpUtil;

import static android.content.Intent.ACTION_MAIN;

/**
 * Created by snt1231 on 2017/3/27.
 */

public class StartActivity extends BaseActivity{
    private boolean firstLoad;
    private ImageView iv_app_start;
    private String mResult;
    private String mToken;
    private Handler mHandler;
    private StartActivity.ServiceWaitThread mServiceThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Hack to avoid to draw twice LinphoneActivity on tablets
        if (getResources().getBoolean(org.linphone.R.bool.orientation_portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        initData();
        mHandler = new Handler();
        if (LinphoneService.isReady()) {
            onServiceReady();
        } else {
            // start linphone as background
            startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
            mServiceThread = new StartActivity.ServiceWaitThread();
            mServiceThread.start();
        }
    }

    private void goToLoginActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }

    private void initData() {
        Bitmap bitmap= BitmapUtil.readBitMap(mContext,R.drawable.iv_app_start);
        iv_app_start.setImageBitmap(bitmap);
    }

    private void initView() {
        iv_app_start= findViewById(R.id.iv_app_start);
    }

    private void queryTokenStatusPost() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionToken", mToken);
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
                    goToHomeActivity();
                } else  {
                    goToLoginActivity();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                goToLoginActivity();
            }
        });
    }

    private void goToHomeActivity() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AccountModel accountModel=new AccountModel();
                accountModel.setSessionToken(mToken);
                accountModel.setSubscriberId( SharedPreferenceManager.getString(mContext, "subscriberId"));
                accountModel.setmStrPassW( SharedPreferenceManager.getString(mContext, "passwd"));
                SatnetApplication.accountModel=accountModel;
                Intent intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        },1500);


    }
    private void handSatnetGoto() {
        firstLoad= SharedPreferenceManager.getBoolean(mContext,"firstLoad");
        mToken = SharedPreferenceManager.getString(mContext, "mToken");
        if(!firstLoad){
            if(mToken==null){
                goToLoginActivity();
            }else{
                queryTokenStatusPost();
            }
        }else{
            // 程序第一次安装，跳转到引导界面
            SharedPreferenceManager.saveBoolean(mContext,"firstLoad",false);
            Intent intent = new Intent(getApplicationContext(), GuidePagesActivity.class);
            startActivity(intent);
        }
    }

    private class ServiceWaitThread extends Thread {
        public void run() {
            while (!LinphoneService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onServiceReady();
                }
            });
            mServiceThread = null;
        }
    }
    protected void onServiceReady() {
        handSatnetGoto();
    }


}
