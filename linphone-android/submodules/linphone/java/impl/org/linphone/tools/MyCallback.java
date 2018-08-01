package org.linphone.tools;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.xutils.common.Callback;

/**
 * Created by snt1231 on 2017/3/28.
 */

public abstract  class MyCallback implements Callback.CacheCallback<String> {
    private KProgressHUD hud;
    private Context mContext;

    public MyCallback(Context mContext,boolean isShow) {
        this.mContext = mContext;
        if(isShow){
            showDialog();
        }
    }

    private void showDialog() {
        hud = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        hud.setCancellable(false);
        hud.show();
    }
    private void dismissDialog(){
        if(hud!=null){
            hud.dismiss();
        }
    }
    @Override
    public  void onSuccess(String result) {
        dismissDialog();
    }

    @Override
    public  void onError(Throwable ex, boolean isOnCallback){
        dismissDialog();
    }

    @Override
    public  void onCancelled(CancelledException cex){
        dismissDialog();
    }

    @Override
    public void onFinished() {
        dismissDialog();
    }

    @Override
    public boolean onCache(String result) {
        return false;
    }
}
