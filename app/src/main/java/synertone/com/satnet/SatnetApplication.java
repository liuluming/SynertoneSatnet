package synertone.com.satnet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.synertone.ftpmoudle.BuildConfig;

import net.gotev.uploadservice.UploadService;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import synertone.com.satnet.model.AccountModel;
import synertone.com.satnet.utils.CrashHandler;

/**
 * Created by snt1231 on 2017/3/27.
 */

public class SatnetApplication extends Application {
    private List<Activity> actList;
    public static AccountModel accountModel;
    public static Typeface fontXiti;//表示细体字体
    //public static Typeface fontPutu;//表示常规字体
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ARouter.init(SatnetApplication.this);
            }
        }).start();
        actList = new ArrayList<>();
        x.Ext.init(this);
        initFontType();
        CrashHandler crashHandler=CrashHandler.getInstance();
        crashHandler.init(this);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.BACKOFF_MULTIPLIER = 2;
    }

    private void initFontType() {
        fontXiti = Typeface.createFromAsset(getAssets(),
                "fonts/xiti.otf");
      /*  fontPutu = Typeface.createFromAsset(getAssets(),
                "fonts/changgui.otf");*/
    }

    public void addAct(Activity act) {
        this.actList.add(act);
    }

    public void removeAct(Activity act) {
        this.actList.remove(act);
    }
    public void finishAllAct() {
        for (Activity act : actList) {
            if (act != null) {
                act.finish();
            }
        }
        actList.clear();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
