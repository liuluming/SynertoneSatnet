package com.synertone.ftpmoudle;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.request.target.ViewTarget;
import com.donkingliang.imageselector.view.MyViewPager;
import com.github.chrisbanes.photoview.PhotoView;
import com.litesuits.common.utils.RoutePath;
import com.synertone.ftpmoudle.adapter.PreviewImagePagerAdapter;
import com.synertone.ftpmoudle.model.FTPPictureModel;
import com.litesuits.common.utils.GsonUtils;
import com.synertone.ftpmoudle.netFtp.AsyncSavePic;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.animation.ObjectAnimator.ofFloat;

@RuntimePermissions()
@Route(path= RoutePath.SinglePicPreviewActivity)
public class SinglePicPreviewActivity extends FTPBaseActivity {
    @BindView(R2.id.btn_back)
    FrameLayout btnBack;
    @BindView(R2.id.tv_pic_name)
    TextView tvPicName;
    @BindView(R2.id.rl_top_bar)
    RelativeLayout rlTopBar;
    @BindView(R2.id.vp_image)
    MyViewPager vpImage;
    @BindView(R2.id.iv_download)
    ImageView ivDownload;
    @BindView(R2.id.rl_bottom_bar)
    RelativeLayout rlBottomBar;
    @BindView(R2.id.ll_download)
    LinearLayout llDownload;
    @BindView(R2.id.tv_indicator)
    TextView tvIndicator;
    @Autowired
    ArrayList<FTPPictureModel> ftpModelList;
    @Autowired
    FTPPictureModel ftpPictureModel;
    private boolean isShowBar = false;
    private PreviewImagePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pic_preview);
        ARouter.getInstance().inject(this);
        initView();
        initViewPager();
        initEvent();
    }

    private void initViewPager() {
        adapter = new PreviewImagePagerAdapter(this, ftpModelList);
        vpImage.setAdapter(adapter);
        vpImage.setCurrentItem(ftpModelList.indexOf(ftpPictureModel));
        tvPicName.setText(ftpPictureModel.getName());
        tvIndicator.setText((ftpModelList.indexOf(ftpPictureModel)+1) + "/" + ftpModelList.size());
    }
    private void initEvent() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinglePicPreviewActivityPermissionsDispatcher.saveBitmapToSDadWithPermissionCheck(SinglePicPreviewActivity.this);
            }
        });
        vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tvPicName.setText(ftpModelList.get(position).getName());
                tvIndicator.setText(position + 1 + "/" + ftpModelList.size());
                ftpPictureModel=ftpModelList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        adapter.setOnItemClickListener(new PreviewImagePagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FTPPictureModel image) {
                if (isShowBar) {
                    hideBar();
                } else {
                    showBar();
                }
            }
        });
    }

    private void initView() {
        rlTopBar.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rlTopBar.getLayoutParams();
                lp.topMargin = -rlTopBar.getHeight();
                rlTopBar.setLayoutParams(lp);
                FrameLayout.LayoutParams lbp = (FrameLayout.LayoutParams) rlBottomBar.getLayoutParams();
                lbp.bottomMargin = -rlBottomBar.getHeight();
                rlBottomBar.setLayoutParams(lbp);
            }
        });

    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    protected void saveBitmapToSDad() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(getApplicationContext(), "SD卡不存在或状态异常，不能写入", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap;
        FileOutputStream fos = null;
        PhotoView currentImageView = adapter.getPrimaryItem();
        if (currentImageView != null) {
            Drawable drawable = currentImageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                 bitmap = bitmapDrawable.getBitmap();
                // 首先保存图片
                File appDir = new File(Environment.getExternalStorageDirectory(), "/ftpPictures/" + ftpPictureModel.getDate().replaceAll("-", ""));
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                String fileName = ftpPictureModel.getName();
                File file = new File(appDir, fileName);
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    Toast.makeText(SinglePicPreviewActivity.this, "图片保存成功！"+"路径："+appDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                  try {
                      if(fos!=null){
                          fos.close();
                      }
                  }catch (Exception e){
                      e.printStackTrace();
                  }

                }
            }else{
                AsyncSavePic asyncSavePic=new AsyncSavePic(this);
                asyncSavePic.execute(ftpPictureModel);
            }
        } else {
            Toast.makeText(this, "图片加载中，请稍后！", Toast.LENGTH_SHORT).show();
        }
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForSDcard(final PermissionRequest request) {
        request.proceed();
    }

    /**
     * 显示头部和尾部栏
     */
    private void showBar() {
        isShowBar = true;
        tvIndicator.setVisibility(View.GONE);
        rlTopBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rlTopBar != null) {
                    ofFloat(rlTopBar, "translationY", -rlTopBar.getHeight(), rlTopBar.getHeight())
                            .setDuration(300).start();
                    ofFloat(rlBottomBar, "translationY", rlBottomBar.getHeight(), -rlBottomBar.getHeight())
                            .setDuration(300).start();
                }
            }
        }, 100);
    }

    /**
     * 隐藏头部和尾部栏
     */
    private void hideBar() {
        isShowBar = false;
        tvIndicator.setVisibility(View.VISIBLE);
        ofFloat(rlTopBar, "translationY", rlTopBar.getHeight(), -rlTopBar.getHeight())
                .setDuration(300).start();
        ofFloat(rlBottomBar, "translationY", -rlBottomBar.getHeight(), rlBottomBar.getHeight())
                .setDuration(300).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        SinglePicPreviewActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
