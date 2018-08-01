package com.synertone.ftpmoudle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.donkingliang.imageselector.utils.StringUtils;
import com.litesuits.common.utils.RoutePath;
import com.litesuits.common.view.BaseNiceDialog;
import com.litesuits.common.view.NiceDialog;
import com.litesuits.common.view.ViewConvertListener;
import com.litesuits.common.view.ViewHolder;
import com.synertone.ftpmoudle.model.FTPUserModel;
import com.litesuits.common.utils.GsonUtils;
import com.synertone.ftpmoudle.utils.SharedPreferenceManager;

import net.gotev.uploadservice.ftp.FTPUploadRequest;
import net.gotev.uploadservice.ftp.UnixPermissions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

public class FTPMainActivity extends FTPBaseActivity {
    private static final int GO_TO_SETTING = 1;
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.tv_bar_title)
    TextView tvBarTitle;
    @BindView(R2.id.ll_settings)
    LinearLayout llSettings;
    @BindView(R2.id.ll_picture_see)
    LinearLayout llPictureSee;
    @BindView(R2.id.ll_picture_upload)
    LinearLayout llPictureUpload;
    private  final String ftpServicePath ="/";
    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_main);
        activity = this;
        initView();
        initEvent();
    }
    public static Activity getThis() {
        return activity;
    }
    private void initEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FTPMainActivity.this, FTPSettingActivity.class);
                startActivity(intent);
            }
        });
        llPictureSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ARouter.getInstance().build(RoutePath.FTPPictureBrowseActivity).navigation(FTPMainActivity.this);
            }
        });
        llPictureUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ftpuser = SharedPreferenceManager.getString(FTPMainActivity.this, "ftpuser");
                if(StringUtils.isEmptyString(ftpuser)){
                    showMyDialog();
                    return;
                }
                goToPicUpLoad();
            }
        });
    }

    private void goToPicUpLoad() {
        ImageSelectorUtils.openPhotoUpload(FTPMainActivity.this,12,new ImageSelectorUtils.UploadListener(){
            @Override
            public void load(List<String> images) {
                final String uploadId = UUID.randomUUID().toString();
                String ftpuser = SharedPreferenceManager.getString(FTPMainActivity.this, "ftpuser");
                if(StringUtils.isEmptyString(ftpuser)){
                    showMyDialog();
                    return;
                }
                FTPUserModel ftpUserModel = GsonUtils.fromJson(ftpuser, FTPUserModel.class);
                final FTPUploadRequest request = new FTPUploadRequest(FTPMainActivity.this, uploadId, ftpUserModel.getIpAddress(), Integer.parseInt(ftpUserModel.getIpPort()))
                        .setMaxRetries(FTPUploadRequest.MAX_RETRIES)
                        .setNotificationConfig(getNotificationConfig(uploadId, R.string.ftp_upload))
                        .setUsernameAndPassword(ftpUserModel.getUserName(), ftpUserModel.getUserPassword())
                        .setCreatedDirectoriesPermissions(new UnixPermissions("777"))
                        .setSocketTimeout(5000)
                        .setConnectTimeout(5000);
                for(String localPath:images){
                    String remotePath = generateRemotePath(localPath);
                    try {
                        request.addFileToUpload(localPath, remotePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                request.startUpload();
            }
        });
    }

    private void initView() {
        tvBarTitle.setText(R.string.picture_manager);
    }
    private String generateRemotePath(String subtitle) {
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        String[] split = subtitle.split("/");
        return ftpServicePath+sf.format(time)+"/"+split[split.length-1];
    }
    private void showMyDialog() {
        NiceDialog.init()
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setText(R.id.dialog_tv,"参数设置不正确,请先设置参数");
                        holder.setOnClickListener(R.id.dialog_tv_sure, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance().build(RoutePath.FTPSettingActivity).navigation(FTPMainActivity.this,GO_TO_SETTING);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.dialog_tv_cencel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setMargin(30)
                .show(getSupportFragmentManager());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GO_TO_SETTING:
                if(resultCode==RESULT_OK){
                    goToPicUpLoad();
                }
                break;
                default:
                    break;
        }
    }
}
