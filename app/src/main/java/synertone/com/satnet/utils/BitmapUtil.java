package synertone.com.satnet.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by snt1231 on 2017/3/27.
 */

public class BitmapUtil {
    /**
     * 2.  * 以最省内存的方式读取本地资源的图片
     * 3.  * @param context
     * 4.  * @param resId
     * 5.  * @return
     * 6.
     */
    public static Bitmap readBitMap(Context context, int resId) {
        InputStream is=null;
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            opt.inJustDecodeBounds = false;
            //获取资源图片
             is = context.getResources().openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, opt);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
          if(is!=null){
              try {
                  is.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        }
       return null;
    }
}
