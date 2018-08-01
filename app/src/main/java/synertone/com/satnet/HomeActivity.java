package synertone.com.satnet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.litesuits.common.adapter.CommonAdapter;
import com.litesuits.common.adapter.CommonViewHolder;
import com.litesuits.common.utils.ScreenUtil;
import com.synertone.ftpmoudle.FTPMainActivity;

import org.linphone.BluetoothManager;
import org.linphone.CallActivity;
import org.linphone.ContactsManager;
import org.linphone.LinphoneActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneService;
import org.linphone.LinphoneUtils;
import org.linphone.assistant.RemoteProvisioningActivity;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.Version;
import org.linphone.tutorials.TutorialLauncherActivity;

import java.util.ArrayList;
import java.util.List;

import synertone.com.satnet.activity.person.PersonalActivity;
import synertone.com.satnet.model.HomeMenuModel;
import synertone.com.satnet.view.AmwellImageCycleView;
import synertone.com.satnet.view.MyGridView;

import static synertone.com.satnet.view.AmwellImageCycleView.ImageCycleViewListener;

public class HomeActivity extends BaseActivity {

    private AmwellImageCycleView amwell_ad;
    private int bannerHeight;
    private MyGridView mgv_content;
    private final String ACTION_CALL_LINPHONE  = "org.linphone.intent.action.CallLaunched";

    private Handler mHandler;
    private String addressToCall;
    private Uri uriToResolve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mHandler=new Handler();
        initView();
        setBannerHeight();
        initBanner();
        initMenu();
        initEvent();
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_CALL.equals(action)) {
                if (intent.getData() != null) {
                    addressToCall = intent.getData().toString();
                    addressToCall = addressToCall.replace("%40", "@");
                    addressToCall = addressToCall.replace("%3A", ":");
                    if (addressToCall.startsWith("sip:")) {
                        addressToCall = addressToCall.substring("sip:".length());
                    }
                }
            } else if (Intent.ACTION_VIEW.equals(action)) {
                if (LinphoneService.isReady()) {
                    addressToCall = ContactsManager.getInstance().getAddressOrNumberForAndroidContact(getContentResolver(), intent.getData());
                } else {
                    uriToResolve = intent.getData();
                }
            }
        }
    }

    private void initEvent() {
        mgv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent mIntent = new Intent(mContext, PersonalActivity.class);
                        startActivity(mIntent);
                        break;
                    case 1:
                        handleVOIP();
                        break;
                    case 2:
                         Intent intent=new Intent(HomeActivity.this, FTPMainActivity.class);
                         startActivity(intent);
                        break;
                }
            }
        });
    }

    private void handleVOIP() {
        handLinphoneGoto();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void handLinphoneGoto() {
        final Class<? extends Activity> classToStart;
        if (getResources().getBoolean(org.linphone.R.bool.show_tutorials_instead_of_app)) {
            classToStart = TutorialLauncherActivity.class;
        } else if (getResources().getBoolean(org.linphone.R.bool.display_sms_remote_provisioning_activity) && LinphonePreferences.instance().isFirstRemoteProvisioning()) {
            classToStart = RemoteProvisioningActivity.class;
        } else {
            classToStart = LinphoneActivity.class;
        }

        // We need LinphoneService to start bluetoothManager
        if (Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
            BluetoothManager.getInstance().initBluetooth();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent newIntent = new Intent(mContext, classToStart);
                Intent intent = getIntent();
                String stringFileShared = null;
                String stringUriFileShared = null;
                Uri fileUri = null;
                if (intent != null) {
                    String action = intent.getAction();
                    String type = intent.getType();
                    newIntent.setData(intent.getData());
                    if (Intent.ACTION_SEND.equals(action) && type != null) {
                        if (type.contains("text/")){
                            if(("text/plain").equals(type) && intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
                                stringFileShared = intent.getStringExtra(Intent.EXTRA_TEXT);
                                newIntent.putExtra("msgShared", stringFileShared);
                            } else if(intent.getExtras().get(Intent.EXTRA_STREAM) != null){
                                stringFileShared = (LinphoneUtils.createCvsFromString(LinphoneUtils.processContactUri(getApplicationContext(), (Uri)intent.getExtras().get(Intent.EXTRA_STREAM)))).toString();
                                newIntent.putExtra("fileShared", stringFileShared);
                            }
                        }else {
                            if(intent.getStringExtra(Intent.EXTRA_STREAM) != null){
                                stringUriFileShared = intent.getStringExtra(Intent.EXTRA_STREAM);
                            }else {
                                fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                                stringUriFileShared = LinphoneUtils.getRealPathFromURI(getBaseContext(), fileUri);
                                if(stringUriFileShared == null)
                                    if(fileUri.getPath().contains("/0/1/mediakey:/local") || fileUri.getPath().contains("/ORIGINAL/NONE/")) {
                                        stringUriFileShared = LinphoneUtils.getFilePath(getBaseContext(), fileUri);
                                    }else
                                        stringUriFileShared = fileUri.getPath();
                            }
                            newIntent.putExtra("fileShared", stringUriFileShared);
                        }
                    }else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                        if (type.startsWith("image/")) {
                            //TODO : Manage multiple files sharing
                        }
                    }else if( ACTION_CALL_LINPHONE.equals(action) && (intent.getStringExtra("NumberToCall") != null)) {
                        String numberToCall = intent.getStringExtra("NumberToCall");
                        if (CallActivity.isInstanciated()) {
                            CallActivity.instance().startIncomingCallActivity();
                        } else {
                            LinphoneManager.getInstance().newOutgoingCall(numberToCall, null);
                        }
                    }
                }
                if (uriToResolve != null) {
                    addressToCall = ContactsManager.getInstance().getAddressOrNumberForAndroidContact(getContentResolver(), uriToResolve);
                    Log.i("LinphoneLauncher", "Intent has uri to resolve : " + uriToResolve.toString());
                    uriToResolve = null;
                }
                if (addressToCall != null) {
                    newIntent.putExtra("SipUriOrNumber", addressToCall);
                    Log.i("LinphoneLauncher", "Intent has address to call : " + addressToCall);
                    addressToCall = null;
                }
                startActivity(newIntent);
                if (classToStart == LinphoneActivity.class && LinphoneActivity.isInstanciated() && (stringFileShared != null || fileUri != null)) {
                    if(stringFileShared != null) {
                        LinphoneActivity.instance().displayChat(null, stringFileShared, null);
                    }
                    else if(fileUri != null) {
                        LinphoneActivity.instance().displayChat(null, null, stringUriFileShared);
                    }
                }
                //finish();
            }
        }, 0);
    }
    private void showRegisterVoipDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_voip_login);
        final EditText et_account = dialog.getWindow().findViewById(R.id.et_account);
        final EditText et_password = dialog.getWindow().findViewById(R.id.et_password);
        dialog.getWindow().findViewById(R.id.dialog_tv_cencel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.dialog_tv_sure).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        loginVoip(et_account.getText().toString(), et_password.getText().toString());

                    }
                });
    }

    private void loginVoip(String account, String password) {
        //VoipUtils.handleVOIPLogin((Activity) mContext, getBundlecontext(), account, password);
    }

    private void initMenu() {
        List<HomeMenuModel> homeMenuModels = new ArrayList<>();
        homeMenuModels.add(new HomeMenuModel("个人中心", R.drawable.icon_gerenzhognxin));
        homeMenuModels.add(new HomeMenuModel("语音电话", R.drawable.iv_voip));
        homeMenuModels.add(new HomeMenuModel("图片管理", R.drawable.iv_pic_manager));
        CommonAdapter<HomeMenuModel> commonAdapter = new CommonAdapter<HomeMenuModel>(mContext, R.layout.home_menu_item, homeMenuModels) {
            @Override
            protected void fillItemData(CommonViewHolder viewHolder, int position, HomeMenuModel item) {
                viewHolder.setTextForTextView(R.id.tv_title, item.getTitle());
                viewHolder.setImageForView(R.id.iv_content, item.getImageResId());
            }
        };
        mgv_content.setAdapter(commonAdapter);
    }

    private void setBannerHeight() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            bannerHeight = (int) (ScreenUtil.getHight(mContext) * 0.3);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bannerHeight = (int) (ScreenUtil.getHight(mContext) * 0.4);
        }
        initHeight();
    }

    private void initBanner() {
        amwell_ad.setImageResources(null, mAdCycleViewListener);
        if (amwell_ad != null) {
            amwell_ad.startImageCycle();
        }
    }

    private void initHeight() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) amwell_ad.getLayoutParams();
        params.height = bannerHeight;
        amwell_ad.setLayoutParams(params);
    }

    private void initView() {
        amwell_ad = findViewById(R.id.amwell_ad);
        mgv_content = findViewById(R.id.mgv_content);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            application.finishAllAct();
            startActivity(new Intent()
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME));
        }
        return super.onKeyDown(keyCode, event);
    }

    private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

        @Override
        public void displayImage(String imageURL, ImageView imageView) {

        }

        @Override
        public void onImageClick(int position, View imageView, int type) {

        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setBannerHeight();
        initBanner();
    }
}
