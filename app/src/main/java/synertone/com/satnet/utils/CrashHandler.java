package synertone.com.satnet.utils;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private UncaughtExceptionHandler defaultUEH;
    private final static boolean IS_DEBUG = true;
    private static CrashHandler INSTANCE;
    private Context mContext;
    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        StackTraceElement[] trace = ex.getStackTrace();
        StackTraceElement[] trace2 = new StackTraceElement[trace.length+3];
        System.arraycopy(trace, 0, trace2, 0, trace.length);
        trace2[trace.length+0] = new StackTraceElement("Android", "MODEL", android.os.Build.MODEL, -1);
        trace2[trace.length+1] = new StackTraceElement("Android", "VERSION", android.os.Build.VERSION.RELEASE, -1);
        trace2[trace.length+2] = new StackTraceElement("Android", "FINGERPRINT", android.os.Build.FINGERPRINT, -1);
        ex.setStackTrace(trace2);

        ex.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        e(TAG, stacktrace);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdcardPath = Environment.getExternalStorageDirectory().getPath()+"/SynertoneSatnet";
            File dir = new File(sdcardPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            writeLog(stacktrace, sdcardPath + "/synertone_crash");
        }
        defaultUEH.uncaughtException(thread, ex);
    }

    private void writeLog(String log, String name) {
        CharSequence timestamp = DateFormat.format("yyyyMMdd_kkmmss", System.currentTimeMillis());
        String filename = name + "_" + timestamp + ".log";

        try {
            FileOutputStream stream = new FileOutputStream(filename);
            OutputStreamWriter output = new OutputStreamWriter(stream);
            BufferedWriter bw = new BufferedWriter(output);

            bw.write(log);
            bw.newLine();

            bw.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void e(String tag, String msg) {
        if (IS_DEBUG) {
            Log.e(tag, msg);
        }
    }
}
