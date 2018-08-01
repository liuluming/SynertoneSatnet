package com.synertone.ftpmoudle.netFtp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.donkingliang.imageselector.utils.StringUtils;
import com.synertone.ftpmoudle.model.FTPPictureModel;
import com.synertone.ftpmoudle.model.FTPUserModel;
import com.litesuits.common.utils.GsonUtils;
import com.synertone.ftpmoudle.utils.SharedPreferenceManager;

import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.ftp.FTPUploadTaskParameters;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class AsyncSavePic extends AsyncTask<FTPPictureModel,Void,Void> {
   private FTPUserModel ftpUserModel;
   private FTPClient ftpClient;
    private InputStream inputStream;
    private FTPPictureModel ftpPictureModel;
    private FileOutputStream fos;
    private Context mContext;
    private File appDir;

    public AsyncSavePic(Context context) {
        this.mContext=context;
        String ftpuser = SharedPreferenceManager.getString(context, "ftpuser");
        if(StringUtils.isNotEmptyString(ftpuser)){
            ftpUserModel = GsonUtils.fromJson(ftpuser, FTPUserModel.class);
        }
    }

    @Override
    protected Void doInBackground(FTPPictureModel... ftpPictureModels) {
        ftpPictureModel=ftpPictureModels[0];
        if (ftpUserModel != null) {
            try {
                configFTPClient();
                String ftpUrl = ftpPictureModel.getUrl();
                inputStream= ftpClient.retrieveFileStream(ftpUrl.substring(4));
                 appDir = new File(Environment.getExternalStorageDirectory(), "/ftpPictures/" + ftpPictureModel.getDate().replaceAll("-", ""));
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                String fileName = ftpPictureModel.getName();
                File file = new File(appDir, fileName);
                fos = new FileOutputStream(file);
                byte[] tempbytes = new byte[100];
                int byteread;
                while ((byteread = inputStream.read(tempbytes)) != -1) {
                    fos.write(tempbytes, 0, byteread);
                }

                inputStream.close();
                fos.close();
                ftpClient.completePendingCommand();
                ftpClient.logout();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (Exception exc) {
                    }
                }
                ftpClient = null;

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void file) {
        super.onPostExecute(file);
        Toast.makeText(mContext, "图片保存成功！"+"路径："+appDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
    private void configFTPClient() throws Exception {
        if (ftpUserModel.isUseSSL()) {
            String secureProtocol = ftpUserModel.getSecureSocketProtocol();

            if (secureProtocol == null || secureProtocol.isEmpty())
                secureProtocol = FTPUploadTaskParameters.DEFAULT_SECURE_SOCKET_PROTOCOL;
            ftpClient = new FTPSClient(secureProtocol, ftpUserModel.isImplicitSecurity());
        } else {
            ftpClient = new FTPClient();
        }
        ftpClient.setBufferSize(UploadService.BUFFER_SIZE);
        ftpClient.setDefaultTimeout(ftpUserModel.getConnectTimeout());
        ftpClient.setConnectTimeout(ftpUserModel.getConnectTimeout());
        ftpClient.setAutodetectUTF8(true);
        ftpClient.connect(ftpUserModel.getIpAddress(), Integer.parseInt(ftpUserModel.getIpPort()));

        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            throw new Exception("Can't connect to " + ftpUserModel.getIpAddress()
                    + ":" + ftpUserModel.getIpPort()
                    + ". The server response is: " + ftpClient.getReplyString());
        }
        if (!ftpClient.login(ftpUserModel.getUserName(), ftpUserModel.getUserPassword())) {
            throw new Exception("Error while performing login on " + ftpUserModel.getIpAddress()
                    + ":" + ftpUserModel.getIpPort()
                    + " with username: " + ftpUserModel.getUserName()
                    + ". Check your credentials and try again.");
        }
        // to prevent the socket timeout on the control socket during file transfer,
        // set the control keep alive timeout to a half of the socket timeout
        int controlKeepAliveTimeout = ftpUserModel.getSocketTimeout() / 2 / 1000;
        ftpClient.setSoTimeout(ftpUserModel.getSocketTimeout());
        ftpClient.setControlKeepAliveTimeout(controlKeepAliveTimeout);
        ftpClient.setControlKeepAliveReplyTimeout(controlKeepAliveTimeout * 1000);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setFileTransferMode(ftpUserModel.isCompressedFileTransfer() ?
                FTP.COMPRESSED_TRANSFER_MODE : FTP.STREAM_TRANSFER_MODE);
    }
}
