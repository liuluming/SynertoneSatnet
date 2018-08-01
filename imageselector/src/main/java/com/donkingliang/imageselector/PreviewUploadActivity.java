package com.donkingliang.imageselector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.donkingliang.imageselector.adapter.ImagePagerAdapter;
import com.donkingliang.imageselector.constant.Constants;
import com.donkingliang.imageselector.entry.Image;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.donkingliang.imageselector.view.MyViewPager;
import com.litesuits.common.utils.RoutePath;
import com.litesuits.common.utils.TipUtils;
import com.litesuits.common.view.BaseNiceDialog;
import com.litesuits.common.view.NiceDialog;
import com.litesuits.common.view.ViewConvertListener;
import com.litesuits.common.view.ViewHolder;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;
import static com.donkingliang.imageselector.ImageUploadActivity.completedAction;

public class PreviewUploadActivity extends AppCompatActivity {

    private MyViewPager vpImage;
    private TextView tvIndicator;
    private TextView tvConfirm;
    private FrameLayout btnConfirm;
    private TextView tvSelect;
    private RelativeLayout rlTopBar;
    private RelativeLayout rlBottomBar;

    //tempImages和tempSelectImages用于图片列表数据的页面传输。
    //之所以不要Intent传输这两个图片列表，因为要保证两位页面操作的是同一个列表数据，同时可以避免数据量大时，
    // 用Intent传输发生的错误问题。
    private static ArrayList<Image> tempImages;
    private static ArrayList<Image> tempSelectImages;

    private ArrayList<Image> mImages;
    private ArrayList<Image> mSelectImages;
    private boolean isShowBar = true;
    private boolean isConfirm = false;
    private boolean isSingle;
    private int mMaxCount;

    private BitmapDrawable mSelectDrawable;
    private BitmapDrawable mUnSelectDrawable;
    private static ImageSelectorUtils.UploadListener uploadListener;
    private CompetedReceiver competedReceiver;
    private  boolean chooseAble=true;
    private static ImageUploadActivity imageUploadActivity;
    private ImageUploadReceiver errReceiver;

    public static void openActivity(Activity activity, ArrayList<Image> images,
                                    ArrayList<Image> selectImages, boolean isSingle,
                                    int maxSelectCount, int position) {
        tempImages = images;
        tempSelectImages = selectImages;
        Intent intent = new Intent(activity, PreviewUploadActivity.class);
        intent.putExtra(Constants.MAX_SELECT_COUNT, maxSelectCount);
        intent.putExtra(Constants.IS_SINGLE, isSingle);
        intent.putExtra(Constants.POSITION, position);
        activity.startActivityForResult(intent, Constants.RESULT_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_upload);

        setStatusBarVisible(true);
        mImages = tempImages;
        tempImages = null;
        mSelectImages = tempSelectImages;
        tempSelectImages = null;

        Intent intent = getIntent();
        mMaxCount = intent.getIntExtra(Constants.MAX_SELECT_COUNT, 0);
        isSingle = intent.getBooleanExtra(Constants.IS_SINGLE, false);

        Resources resources = getResources();
        Bitmap selectBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_image_select);
        mSelectDrawable = new BitmapDrawable(resources, selectBitmap);
        mSelectDrawable.setBounds(0, 0, selectBitmap.getWidth(), selectBitmap.getHeight());

        Bitmap unSelectBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_image_un_select);
        mUnSelectDrawable = new BitmapDrawable(resources, unSelectBitmap);
        mUnSelectDrawable.setBounds(0, 0, unSelectBitmap.getWidth(), unSelectBitmap.getHeight());

        setStatusBarColor();
        initView();
        initListener();
        initViewPager();

        tvIndicator.setText(1 + "/" + mImages.size());
        changeSelect(mImages.get(0));
        vpImage.setCurrentItem(intent.getIntExtra(Constants.POSITION, 0));
        initBroadCast();
    }

    private void initBroadCast() {
        // 注册广播
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(completedAction);
        competedReceiver=new CompetedReceiver();
        registerReceiver(competedReceiver, iFilter);

        errReceiver=new ImageUploadReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(UploadService.getActionBroadcast());
        registerReceiver(errReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(competedReceiver!=null){
            unregisterReceiver(competedReceiver);
        }
        if(errReceiver!=null){
            unregisterReceiver(errReceiver);
        }
    }

    private class CompetedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(completedAction.equals(action)){
                mSelectImages.clear();
                for(Image image:mImages){
                    changeSelect(image);
                }
                chooseAble=true;
                imageUploadActivity.getmAdapter().setChooseAble(chooseAble);
                //Toast
                // .makeText(context,"ddd",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initView() {
        vpImage = findViewById(R.id.vp_image);
        tvIndicator = findViewById(R.id.tv_indicator);
        tvConfirm = findViewById(R.id.tv_confirm);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvSelect = findViewById(R.id.tv_select);
        rlTopBar = findViewById(R.id.rl_top_bar);
        rlBottomBar = findViewById(R.id.rl_bottom_bar);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlTopBar.getLayoutParams();
        lp.topMargin = getStatusBarHeight(this);
        rlTopBar.setLayoutParams(lp);
    }

    private void initListener() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chooseAble){
                    Toast.makeText(PreviewUploadActivity.this,"上传中，请稍后操作！",Toast.LENGTH_SHORT).show();
                    return;
                }
                //isConfirm = true;
                //mAdapter.setChooseAble(false);
                if(uploadListener!=null){
                    List<String> images=new ArrayList<>();
                    for(Image image:mSelectImages){
                        images.add(image.getPath());
                    }
                    uploadListener.load(images);
                }
                chooseAble=false;
                imageUploadActivity.getmAdapter().setChooseAble(chooseAble);
                //finish();
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chooseAble){
                    Toast.makeText(PreviewUploadActivity.this,"上传中，请稍后操作！",Toast.LENGTH_SHORT).show();
                    return;
                }
                clickSelect();
            }
        });
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, mImages);
        vpImage.setAdapter(adapter);
        adapter.setOnItemClickListener(new ImagePagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Image image) {
                if (isShowBar) {
                    hideBar();
                } else {
                    showBar();
                }
            }
        });
        vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tvIndicator.setText(position + 1 + "/" + mImages.size());
                changeSelect(mImages.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#373c3d"));
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 显示和隐藏状态栏
     *
     * @param show
     */
    private void setStatusBarVisible(boolean show) {
        if (show) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    /**
     * 显示头部和尾部栏
     */
    private void showBar() {
        isShowBar = true;
        setStatusBarVisible(true);
        //添加延时，保证StatusBar完全显示后再进行动画。
        rlTopBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rlTopBar != null) {
                    ObjectAnimator animator = ofFloat(rlTopBar, "translationY",
                            rlTopBar.getTranslationY(), 0).setDuration(300);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            if (rlTopBar != null) {
                                rlTopBar.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    animator.start();
                    ofFloat(rlBottomBar, "translationY", rlBottomBar.getTranslationY(), 0)
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
        ObjectAnimator animator = ObjectAnimator.ofFloat(rlTopBar, "translationY",
                0, -rlTopBar.getHeight()).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (rlTopBar != null) {
                    rlTopBar.setVisibility(View.GONE);
                    //添加延时，保证rlTopBar完全隐藏后再隐藏StatusBar。
                    rlTopBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setStatusBarVisible(false);
                        }
                    }, 5);
                }
            }
        });
        animator.start();
        ofFloat(rlBottomBar, "translationY", 0, rlBottomBar.getHeight())
                .setDuration(300).start();
    }

    private void clickSelect() {
        int position = vpImage.getCurrentItem();
        if (mImages != null && mImages.size() > position) {
            Image image = mImages.get(position);
            if (mSelectImages.contains(image)) {
                mSelectImages.remove(image);
            } else if (isSingle) {
                mSelectImages.clear();
                mSelectImages.add(image);
            } else if (mMaxCount <= 0 || mSelectImages.size() < mMaxCount) {
                mSelectImages.add(image);
            }
            changeSelect(image);
            if(mSelectImages.size() ==mMaxCount&&!mSelectImages.contains(image)){
                Toast.makeText(this,"最多只能选取"+mMaxCount+"张图片",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changeSelect(Image image) {
        tvSelect.setCompoundDrawables(mSelectImages.contains(image) ?
                mSelectDrawable : mUnSelectDrawable, null, null, null);
        setSelectImageCount(mSelectImages.size());
    }

    private void setSelectImageCount(int count) {
        if (count == 0) {
            btnConfirm.setEnabled(false);
            tvConfirm.setText(getString(R.string.upload));
        } else {
            btnConfirm.setEnabled(true);
            if (isSingle) {
                tvConfirm.setText(getString(R.string.upload));
            } else if (mMaxCount > 0) {
                tvConfirm.setText(getString(R.string.upload)+"(" + count + "/" + mMaxCount + ")");
            } else {
                tvConfirm.setText(getString(R.string.upload)+"(" + count + ")");
            }
        }
    }

    @Override
    public void finish() {
        //Activity关闭时，通过Intent把用户的操作(确定/返回)传给ImageSelectActivity。
        Intent intent = new Intent();
        intent.putExtra(Constants.IS_CONFIRM, isConfirm);
        setResult(Constants.RESULT_CODE, intent);
        super.finish();
    }

    public static void openActivity(Activity activity, ArrayList<Image> images, ArrayList<Image> selectImages, boolean isSingle, int maxSelectCount, int position, ImageSelectorUtils.UploadListener listener) {
        tempImages = images;
        tempSelectImages = selectImages;
        Intent intent = new Intent(activity, PreviewUploadActivity.class);
        intent.putExtra(Constants.MAX_SELECT_COUNT, maxSelectCount);
        intent.putExtra(Constants.IS_SINGLE, isSingle);
        intent.putExtra(Constants.POSITION, position);
        uploadListener=listener;
        imageUploadActivity= (ImageUploadActivity) activity;
        activity.startActivityForResult(intent, Constants.RESULT_CODE);

    }
    class ImageUploadReceiver extends UploadServiceBroadcastReceiver {
        @Override
        public void onProgress(Context context, UploadInfo uploadInfo) {
            super.onProgress(context, uploadInfo);
        }

        @Override
        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
            super.onError(context, uploadInfo, serverResponse, exception);
            showMyDialog(TipUtils.getFormatError(exception.getMessage()));
        }

        @Override
        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
            super.onCompleted(context, uploadInfo, serverResponse);
        }

        @Override
        public void onCancelled(Context context, UploadInfo uploadInfo) {
            super.onCancelled(context, uploadInfo);
        }

        private void showMyDialog(final String formatError) {
            NiceDialog.init()
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            holder.setText(R.id.dialog_tv, formatError);
                            holder.setOnClickListener(R.id.dialog_tv_sure, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ARouter.getInstance().build(RoutePath.FTPSettingActivity).navigation();
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
    }

}