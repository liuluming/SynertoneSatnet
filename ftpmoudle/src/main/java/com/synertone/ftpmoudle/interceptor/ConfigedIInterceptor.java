package com.synertone.ftpmoudle.interceptor;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.donkingliang.imageselector.utils.StringUtils;
import com.litesuits.common.utils.RoutePath;
import com.litesuits.common.view.BaseNiceDialog;
import com.litesuits.common.view.NiceDialog;
import com.litesuits.common.view.ViewConvertListener;
import com.litesuits.common.view.ViewHolder;
import com.synertone.ftpmoudle.FTPMainActivity;
import com.synertone.ftpmoudle.R;
import com.synertone.ftpmoudle.utils.SharedPreferenceManager;
@Interceptor(priority = 4)
public class ConfigedIInterceptor implements IInterceptor {
    private Context context;
    private String path;

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        path = postcard.getPath();
        if(RoutePath.FTPPictureBrowseActivity.equals(path)){
            String ftpuser = SharedPreferenceManager.getString(context, "ftpuser");
            if(StringUtils.isEmptyString(ftpuser)){
                callback.onInterrupt(null);
                showMyDialog();
            }else{
                callback.onContinue(postcard);
            }
        }else{
            callback.onContinue(postcard);
        }

    }

    @Override
    public void init(Context context) {
        this.context= context;
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
                                ARouter.getInstance().build(RoutePath.FTPSettingActivity)
                                        .withString("routePath",path).navigation(context);
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
                .show(((FragmentActivity)FTPMainActivity.getThis()).getSupportFragmentManager());

    }
}
