package synertone.com.satnet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import org.xutils.common.util.DensityUtil;

/**
 * Created by snt1231 on 2017/3/27.
 */

public class BaseActivity extends FragmentActivity{
    public Context mContext;
    public SatnetApplication application;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext=this;
        application= (SatnetApplication) getApplication();
        application.addAct(this);
    }


    public class ComBackTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float downX=event.getX();
            float downY = event.getY();
            if(event.getAction()==MotionEvent.ACTION_UP){
                int maxX=DensityUtil.dip2px(130);
                int maxY=DensityUtil.dip2px(80);
                if(downX<=maxX &&downY<maxY){
                    application.removeAct((Activity) mContext);
                    finish();
                }
            }
            return false;
        }
    }


}
