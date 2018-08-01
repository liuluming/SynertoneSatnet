package synertone.com.satnet.utils;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import synertone.com.satnet.R;
import synertone.com.satnet.activity.person.PersonalActivity;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */

public class UpdateManager extends Service implements android.view.View.OnClickListener {
	private Context mContext;
	// 提示语
	private String updateMsg = "有最新的软件包哦，亲快下载吧~";

	// 返回的安装包url
	private String apkUrl = "http://app.synertone.net:8008/xtxw0504.apk";

	private Dialog noticeDialog;

	private Dialog downloadDialog;
	/* 下载包安装路径 */
	private static final String savePath = "/sdcard/updatedemo/";
	private TextView mTv_pro;
	private static final String saveFileName = savePath
			+ "UpdateDemoRelease.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;

	protected static final String TAG = "UpdateManager";
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private int progress;
	private Thread downLoadThread;
	private boolean interceptFlag = false;
	private NotificationManager notificationManager;
	private Notification notification;

	public UpdateManager() {
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

				switch (msg.what) {

				case DOWN_UPDATE:
					int result = (int) (count * 100 / length);
					mProgress.setProgress(result);
					mTv_pro.setText(result + "%");
					Log.d(TAG, result + "%");
					break;
				case DOWN_OVER:
					// 下载完成通知安装
					 mHandler.post(new Runnable() {
			                public void run() {
			                	downloadDialog.cancel();
			                	installApk();

			                }
			            });
					break;

				}

			super.handleMessage(msg);
		}
    };

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	//
	// 外部接口让主Activity调用
	public void checkUpdateInfo() {
		showNoticeDialog();
	}

	TextView tView;
	Button mDown,mQuxiao;//表示现在下载和取消下载
	/**
	 * 显示软件更新对话框
	 */
	public void showNoticeDialog() {

		noticeDialog=new Dialog(mContext, R.style.sj_dialog);
		View view= LayoutInflater.from(mContext).inflate(R.layout.shengji_tip_layout, null);
		noticeDialog.setContentView(view);
		tView= view.findViewById(R.id.getbanben_number);
		mDown= view.findViewById(R.id.xianzaixiazai);
		mQuxiao= view.findViewById(R.id.shaohougengxin);
		tView.setText("协同星网V"+ PersonalActivity.AppVerSion);
		mDown.setOnClickListener(this);
		mQuxiao.setOnClickListener(this);
		noticeDialog.show();
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新中...");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mTv_pro = v.findViewById(R.id.tv_progress);
		mProgress = v.findViewById(R.id.update_progress);
		
		
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
				
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();
	}

	public void down_file() throws IOException {
		// 下载函数
		// filename = url.substring(url.lastIndexOf("/") + 1);
		// 获取文件名
		URL myURL = new URL(apkUrl);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		length = conn.getContentLength();// 根据响应获取文件大小
		if (this.length <= 0)
			throw new RuntimeException("无法获知文件大小 ");
		if (is == null)
			throw new RuntimeException("stream is null");
		FileOutputStream fos = new FileOutputStream(saveFileName);
		// 把数据存入路径+文件名
		byte buf[] = new byte[1024];
		count = 0;
		sendMsg(0);
		do {
			// 循环读取
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
			count += numread;

			sendMsg(1);// 更新进度条
		} while (true);
		sendMsg(2);// 通知下载完成
		try {
			is.close();
		} catch (Exception ex) {
			Log.e("tag", "error: " + ex.getMessage(), ex);
		}

	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	long length = 0;
	long count = 0;
	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 * @date 2012-4-26
	 * @blog http://blog.92coding.com
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				count=0;
				// sendMsg(0);
				byte buf[] = new byte[1024*10];

				do {
					int numread = is.read(buf);
					if (numread == -1) {
						break;
					}
					fos.write(buf, 0, numread);
					count += numread;
					//progress = (int) (count / length);
					// 更新进度
					sendMsg(DOWN_UPDATE);
				
			
				} while (!interceptFlag);// 点击取消就停止下载.
				sendMsg(DOWN_OVER);// 通知下载完成
				  fos.flush();    
                  if (fos != null) {    
                      fos.close();    
                  }    
				// 关闭流
				fos.close();
				is.close();
				conn.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		mHandler.sendMessage(msg);
	}

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.xianzaixiazai:
			showDownloadDialog();
			noticeDialog.dismiss();
			break;
		case R.id.shaohougengxin:
			noticeDialog.dismiss();
			break;
		default:
			break;
		}
	}

}