package synertone.com.satnet.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * Created by tao.j on 2016/8/26.
 */
public class ProgressDialogUtils {

    private static final String TAG = "ProgressDialogUtils";
    private static ProgressDialog mConnectProgress;
    private static Context sContext;

    public static void showProgressDialog(Context context, int resId) {
        String msg = context.getString(resId);
        showProgressDialog(context, msg);
    }

    public static void showProgressDialog(Context context, String msg) {
        Log.d(TAG, "context ="+context+", sContext = "+sContext +", equal:"+(sContext == context));
        if (sContext != context) {
            sContext = context;
            mConnectProgress = new ProgressDialog(context);
            mConnectProgress.setIndeterminate(true);
            mConnectProgress.setCancelable(false);
        }

        mConnectProgress.setMessage(msg);
        mConnectProgress.show();
    }

    public static void dismissProgressDialog(){
        if(mConnectProgress != null && mConnectProgress.isShowing()){
            mConnectProgress.dismiss();
        }
    }

}
