package com.synertone.ftpmoudle;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.came.viewbguilib.ButtonBgUi;
import com.donkingliang.imageselector.utils.StringUtils;
import com.litesuits.common.utils.RoutePath;
import com.synertone.ftpmoudle.model.FTPUserModel;
import com.litesuits.common.utils.GsonUtils;
import com.synertone.ftpmoudle.utils.SharedPreferenceManager;

import butterknife.BindView;

@Route(path = RoutePath.FTPSettingActivity)
public class FTPSettingActivity extends FTPBaseActivity {
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.tv_bar_title)
    TextView tvBarTitle;
    @BindView(R2.id.tv_ip_address)
    TextView tvIpAddress;
    @BindView(R2.id.et_ip_address)
    EditText etIpAddress;
    @BindView(R2.id.tv_port)
    TextView tvPort;
    @BindView(R2.id.et_ip_port)
    EditText etIpPort;
    @BindView(R2.id.tv_username)
    TextView tvUsername;
    @BindView(R2.id.et_username)
    EditText etUsername;
    @BindView(R2.id.tv_password)
    TextView tvPassword;
    @BindView(R2.id.et_password)
    EditText etPassword;
    @BindView(R2.id.btn_ok)
    ButtonBgUi btnOk;
    @BindView(R2.id.btn_cancel)
    ButtonBgUi btnCancel;
    @Autowired
    String routePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpsetting);
        ARouter.getInstance().inject(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkParams()){
                    FTPUserModel ftpUserModel=new FTPUserModel(etIpAddress.getText().toString(),
                            etIpPort.getText().toString(),etUsername.getText().toString(),
                            etPassword.getText().toString());
                    String ftpuser = GsonUtils.toJson(ftpUserModel);
                    SharedPreferenceManager.saveString(FTPSettingActivity.this,"ftpuser",ftpuser);
                    Toast.makeText(FTPSettingActivity.this,"参数设置成功！",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    if(StringUtils.isNotEmptyString(routePath)){
                        ARouter.getInstance().build(routePath).navigation();
                    }
                }else{
                    Toast.makeText(FTPSettingActivity.this,"参数不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        String ftpuser = SharedPreferenceManager.getString(this, "ftpuser");
        if(StringUtils.isNotEmptyString(ftpuser)){
            FTPUserModel ftpUserModel = GsonUtils.fromJson(ftpuser, FTPUserModel.class);
            etIpAddress.setText(ftpUserModel.getIpAddress());
            etIpAddress.setSelection(etIpAddress.getText().length());
            etIpPort.setText(ftpUserModel.getIpPort());
            etUsername.setText(ftpUserModel.getUserName());
            etPassword.setText(ftpUserModel.getUserPassword());
        }
        tvBarTitle.setText("参数设置");
    }
    private boolean checkParams() {
      return  StringUtils.isNotEmptyString(etIpAddress.getText().toString())&&
                StringUtils.isNotEmptyString(etIpPort.getText().toString())&&
                StringUtils.isNotEmptyString(etUsername.getText().toString())&&
                StringUtils.isNotEmptyString(etPassword.getText().toString());
    }
}
