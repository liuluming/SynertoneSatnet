package com.synertone.ftpmoudle;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.donkingliang.imageselector.utils.StringUtils;
import com.litesuits.common.utils.RoutePath;
import com.litesuits.common.utils.TipUtils;
import com.litesuits.common.view.BaseNiceDialog;
import com.litesuits.common.view.NiceDialog;
import com.litesuits.common.view.ViewConvertListener;
import com.litesuits.common.view.ViewHolder;
import com.synertone.ftpmoudle.adapter.MySection;
import com.synertone.ftpmoudle.adapter.RecyclerViewSpacesItemDecoration;
import com.synertone.ftpmoudle.adapter.SectionAdapter;
import com.synertone.ftpmoudle.model.FTPPictureModel;
import com.synertone.ftpmoudle.model.FTPUserModel;
import com.litesuits.common.utils.GsonUtils;
import com.synertone.ftpmoudle.utils.SharedPreferenceManager;
import com.synertone.ftpmoudle.views.CustomLoadMoreView;

import net.gotev.uploadservice.UploadService;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
@Route(path = RoutePath.FTPPictureBrowseActivity)
public class FTPPictureBrowseActivity extends FTPBaseActivity{
    private static final int ERROR_MESSAGE = 0;
    private static final int GO_TO_SETTING = 1;
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.tv_bar_title)
    TextView tvBarTitle;
    @BindView(R2.id.rl_top_bar)
    RelativeLayout rlTopBar;
    @BindView(R2.id.rv_gridView)
    RecyclerView rvGridView;
    @BindView(R2.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    private SectionAdapter sectionAdapter;
    private List<MySection> mySectionList;
    private int dayNum=1;
    private int daySize=2;
    private View notDataView;
    private TreeMap<String,List<FTPPictureModel>> picTreeMap;
    private  final String ftpServicePath ="/";
    private ArrayList<FTPPictureModel> ftpPictureModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftppicture_browse);
        initView();
        initData();
        initEvent();
        ftpPictureModelList=new ArrayList<>();
        AsyncGetFPTUrls asyncGetFPTUrls = new AsyncGetFPTUrls();
        String ftpuser = SharedPreferenceManager.getString(this, "ftpuser");
        if (StringUtils.isNotEmptyString(ftpuser)) {
            FTPUserModel ftpUserModel = GsonUtils.fromJson(ftpuser, FTPUserModel.class);
            asyncGetFPTUrls.execute(ftpUserModel);
        }

    }
    private void initData() {
        mySectionList=new ArrayList<>();
        sectionAdapter = new SectionAdapter(this, R.layout.item_section_content, R.layout.def_section_head, mySectionList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        rvGridView.setLayoutManager(gridLayoutManager);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 10);//top间距

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 10);//底部间距

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 10);//左间距

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 10);//右间距
        rvGridView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        sectionAdapter.setLoadMoreView(new CustomLoadMoreView());
        rvGridView.setAdapter(sectionAdapter);
        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) rvGridView.getParent(), false);
    }
    private void initView() {
        swipeLayout.setEnabled(false);
        tvBarTitle.setText("图片预览");
    }

    private void initEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sectionAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(picTreeMap==null){
                   return;
                }
                loadMore();
            }

            public void loadMore() {
                Set<Map.Entry<String, List<FTPPictureModel>>> entries = picTreeMap.entrySet();
                Iterator<Map.Entry<String, List<FTPPictureModel>>> iterator = entries.iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, List<FTPPictureModel>> next = iterator.next();
                    List<FTPPictureModel> ftpPictureModels1 = next.getValue();
                    Collections.sort(ftpPictureModels1);
                    ftpPictureModelList.addAll(ftpPictureModels1);
                    MySection headSection=new MySection(true,ftpPictureModels1.get(0).getDate(),false);
                    mySectionList.add(headSection);
                    for(FTPPictureModel fp:ftpPictureModels1){
                        MySection section=new MySection(fp);
                        mySectionList.add(section);
                    }
                    iterator.remove();
                    if(dayNum==daySize){
                        dayNum=1;
                        break;
                    }
                    dayNum++;
                }
                if(!iterator.hasNext()){
                    sectionAdapter.setEnableLoadMore(false);
                }
                 sectionAdapter.loadMoreComplete();
            }
        }, rvGridView);
        sectionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MySection item = (MySection) adapter.getItem(position);
                FTPPictureModel ftpPictureModel = item.t;
             ARouter.getInstance().build(RoutePath.SinglePicPreviewActivity)
                     .withParcelable("ftpPictureModel",ftpPictureModel)
                     .withObject("ftpModelList",ftpPictureModelList).navigation(FTPPictureBrowseActivity.this);
            }
        });
    }
    Handler mH=new Handler(){
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             int what = msg.what;
             if(what==ERROR_MESSAGE){
                String message= (String) msg.obj;
                 TextView tv_content = notDataView.findViewById(R.id.tv_content);
                 tv_content.setText(TipUtils.getFormatError(message));
                 sectionAdapter.setEmptyView(notDataView);
                 showMyDialog(TipUtils.getFormatError(message));
             }
         }
     };
    class AsyncGetFPTUrls extends AsyncTask<FTPUserModel, Void, TreeMap<String,List<FTPPictureModel>>> {
        private FTPClient ftpClient;
        public static final String DEFAULT_SECURE_SOCKET_PROTOCOL = "TLS";
        private TreeMap<String,List<FTPPictureModel>> picLTreeMap=new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });

        @Override
        protected TreeMap<String,List<FTPPictureModel>> doInBackground(FTPUserModel... ftpUserModels) {
            if (ftpUserModels != null && ftpUserModels.length > 0) {
                FTPUserModel ftpUserModel = ftpUserModels[0];
                try {
                    configFTP(ftpUserModel);
                    // this is needed to calculate the total bytes and the uploaded bytes, because if the
                    // request fails, the upload method will be called again
                    // (until max retries is reached) to retry the upload, so it's necessary to
                    // know at which status we left, to be able to properly notify firther progress.
                    downloadImageUrls(ftpServicePath);
                    // Broadcast completion only if the user has not cancelled the operation.
                    ftpClient.logout();
                } catch (Exception e) {
                    e.printStackTrace();
                    mH.sendMessage(mH.obtainMessage(ERROR_MESSAGE,e.getMessage()));
                } finally {
                    if (ftpClient.isConnected()) {
                        try {
                            ftpClient.disconnect();
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                    ftpClient = null;
                }
            }
            return picLTreeMap;
        }

        private void configFTP(FTPUserModel ftpUserModel) throws Exception {
            if (ftpUserModel.isUseSSL()) {
                String secureProtocol = ftpUserModel.getSecureSocketProtocol();
                if (secureProtocol == null || secureProtocol.isEmpty())
                    secureProtocol =DEFAULT_SECURE_SOCKET_PROTOCOL;
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

        private void downloadImageUrls(String pathName) throws IOException {
            if (pathName.startsWith("/") && pathName.endsWith("/")) {
                //更换目录到当前目录
                ftpClient.changeWorkingDirectory(pathName);
                FTPFile[] files = ftpClient.mlistDir();
                if(files==null||files.length==0){
                    files=ftpClient.listFiles();
                }
                for (FTPFile file : files) {
                    if (file.isFile()) {
                        String[] split = pathName.split("/");
                        StringBuffer sb = new StringBuffer("");
                        for(int i=0;i<split.length;i++){
                            String path = split[i];
                            if(path.length()==8){
                                try{
                                    long i1 = Long.parseLong(path);
                                        sb.append(path);
                                        sb.insert(4, "-");
                                        sb.insert(7, "-");
                                        break;
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }

                        FTPPictureModel ftpPictureModel = new FTPPictureModel(file.getTimestamp(),file.getName(), sb.toString(), pathName + file.getName());
                        List<FTPPictureModel> ftpPictureModels = picLTreeMap.get(ftpPictureModel.getDate());
                        if(ftpPictureModels==null){
                            ftpPictureModels=new ArrayList<>();
                            picLTreeMap.put(ftpPictureModel.getDate(),ftpPictureModels);
                        }
                        ftpPictureModels.add(ftpPictureModel);
                    } else if (file.isDirectory()) {
                        // 需要加此判断。否则，ftp默认将‘项目文件所在目录之下的目录（./）’与‘项目文件所在目录向上一级目录下的目录（../）’都纳入递归，这样下去就陷入一个死循环了。需将其过滤掉。
                        if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                            downloadImageUrls(pathName + file.getName() + "/");
                        }
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(TreeMap<String,List<FTPPictureModel>> treeMap) {
            super.onPostExecute(treeMap);
            if(treeMap==null||treeMap.size()==0){
                //sectionAdapter.setEmptyView(notDataView);
                return;
            }
            picTreeMap=treeMap;
            Set<Map.Entry<String, List<FTPPictureModel>>> entries = treeMap.entrySet();
            Iterator<Map.Entry<String, List<FTPPictureModel>>> iterator = entries.iterator();
            while (iterator.hasNext()){
                Map.Entry<String, List<FTPPictureModel>> next = iterator.next();
                List<FTPPictureModel> ftpPictureModels1 = next.getValue();
                Collections.sort(ftpPictureModels1);
                ftpPictureModelList.addAll(ftpPictureModels1);
                MySection headSection=new MySection(true,ftpPictureModels1.get(0).getDate(),false);
                mySectionList.add(headSection);
                for(FTPPictureModel fp:ftpPictureModels1){
                    MySection section=new MySection(fp);
                    mySectionList.add(section);
                }
                iterator.remove();
                if(dayNum==daySize){
                    dayNum=1;
                    break;
                }
                dayNum++;
            }
            sectionAdapter.notifyDataSetChanged();
        }
    }
    private void showMyDialog(final String formatError) {
        NiceDialog.init()
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setText(R.id.dialog_tv,formatError);
                        holder.setOnClickListener(R.id.dialog_tv_sure, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ARouter.getInstance().build(RoutePath.FTPSettingActivity).navigation(FTPPictureBrowseActivity.this
                                ,GO_TO_SETTING);
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
                    AsyncGetFPTUrls asyncGetFPTUrls = new AsyncGetFPTUrls();
                    String ftpuser = SharedPreferenceManager.getString(this, "ftpuser");
                    if (StringUtils.isNotEmptyString(ftpuser)) {
                        FTPUserModel ftpUserModel = GsonUtils.fromJson(ftpuser, FTPUserModel.class);
                        asyncGetFPTUrls.execute(ftpUserModel);
                    }
                }
                break;
                default:
                    break;

        }

    }
}
