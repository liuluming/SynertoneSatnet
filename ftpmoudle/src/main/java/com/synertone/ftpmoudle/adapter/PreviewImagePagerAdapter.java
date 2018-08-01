package com.synertone.ftpmoudle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.donkingliang.imageselector.utils.ImageUtil;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.synertone.ftpmoudle.model.FTPPictureModel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class PreviewImagePagerAdapter extends PagerAdapter {
    private Context mContext;
    List<FTPPictureModel> mImgList;
    private OnItemClickListener mListener;
    private PhotoView mCurrentView;
    public PreviewImagePagerAdapter(Context context, List<FTPPictureModel> imgList) {
        this.mContext = context;
        mImgList = imgList;
    }



    @Override
    public int getCount() {
        return mImgList == null ? 0 : mImgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof PhotoView) {
            PhotoView view = (PhotoView) object;
            view.setImageDrawable(null);
            container.removeView(view);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final  PhotoView currentView = new PhotoView(mContext);
        currentView.setAdjustViewBounds(true);
        final FTPPictureModel image = mImgList.get(position);
        container.addView(currentView);
        if(image.getUrl().endsWith(".gif")){
            Glide.with(mContext).load(image.getUrl()).into(currentView);
        }else{
            Glide.with(mContext).asBitmap()
                    .load(image.getUrl()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    int bw = resource.getWidth();
                    int bh = resource.getHeight();
                    if (bw > 8192 || bh > 8192) {
                        Bitmap bitmap = ImageUtil.zoomBitmap(resource, 8192, 8192);
                        setBitmap(currentView, bitmap);
                    } else {
                        setBitmap(currentView, resource);
                    }
                }
            });
        }

        currentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position, image);
                }
            }
        });
        return currentView;
    }



    private void setBitmap(PhotoView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        if (bitmap != null) {
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            int vw = imageView.getWidth();
            int vh = imageView.getHeight();
            if (bw != 0 && bh != 0 && vw != 0 && vh != 0) {
                if (1.0f * bh / bw > 1.0f * vh / vw) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    float offset = (1.0f * bh * vw / bw - vh) / 2;
                    adjustOffset(imageView, offset);
                } else {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mListener = l;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, FTPPictureModel image);
    }

    private void adjustOffset(PhotoView view, float offset) {
        PhotoViewAttacher attacher = view.getAttacher();
        try {
            Field field = PhotoViewAttacher.class.getDeclaredField("mBaseMatrix");
            field.setAccessible(true);
            Matrix matrix = (Matrix) field.get(attacher);
            matrix.postTranslate(0, offset);
            Method method = PhotoViewAttacher.class.getDeclaredMethod("resetMatrix");
            method.setAccessible(true);
            method.invoke(attacher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (PhotoView) object;
    }

    public PhotoView getPrimaryItem() {
        return mCurrentView;

    }
}
