package synertone.com.satnet.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import synertone.com.satnet.R;


/**
 * 广告图片自动轮播控件</br>
 * 
 * <pre>
 *   集合ViewPager和指示器的一个轮播控件，主要用于一般常见的广告图片轮播，具有自动轮播和手动轮播功能 
 *   使用：只需在xml文件中使用{@code <com.minking.imagecycleview.ImageCycleView/>} ，
 *   然后在页面中调用  {@link #setImageResources(ArrayList, ImageCycleViewListener) }即可!
 *   
 *   另外提供{@link #startImageCycle() } \ {@link #pushImageCycle() }两种方法，用于在Activity不可见之时节省资源；
 *   因为自动轮播需要进行控制，有利于内存管理
 * </pre>
 * 
 * @author minking
 */
public class AmwellImageCycleView extends LinearLayout {

	/**
	 * 上下文
	 */
	private Context mContext;

	/**
	 * 图片轮播视图
	 */
	private ViewPager mAdvPager = null;

	/**
	 * 滚动图片视图适配器
	 */
	private ImageCycleAdapter mAdvAdapter;

	/**
	 * 图片轮播指示器控件
	 */
	private ViewGroup mGroup;

	/**
	 * 图片轮播指示器-个图
	 */
	private ImageView mImageView = null;

	/**
	 * 滚动图片指示器-视图列表
	 */
	private ImageView[] mImageViews = null;

	/**
	 * 图片滚动当前图片下标
	 */
	private int mImageIndex = 0;

	/**
	 * 手机密度
	 */
	private float mScale;

	private int imageCount;
	private static final int MIN_SIZE = 12;

	/**
	 * @param context
	 */
	public AmwellImageCycleView(Context context) {
		this(context, null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AmwellImageCycleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
		initEvent();

	}

	/**
	 * @param context
	 */
	private void initData(Context context) {
		mContext = context;
		mScale = context.getResources().getDisplayMetrics().density;
		View view = LayoutInflater.from(context).inflate(
				R.layout.ad_cycle_view, this);
		mAdvPager = view.findViewById(R.id.adv_pager);
		// 滚动图片右下指示器视图
		mGroup = findViewById(R.id.viewGroup);
		/* addView(view) */
	}
  /*public void setIndicatorLayout(boolean isNewUser){
		RelativeLayout.LayoutParams params;
		if(isNewUser){
			params= (RelativeLayout.LayoutParams) mGroup.getLayoutParams();
			params.topMargin= (int) (ScreenUtil.getHight(mContext)* MainFragmentActivity.BANNER_RATIO)- DensityUtils.dip2px(mContext,45);
		}else{
			params= (RelativeLayout.LayoutParams) mGroup.getLayoutParams();
			params.topMargin= (int) (ScreenUtil.getHight(mContext)* MainFragmentActivity.BANNER_RATIO)- DensityUtils.dip2px(mContext,25);
		}
		mGroup.setLayoutParams(params);
	}*/
	/**
	 * 设置事件
	 */
	private void initEvent() {
		mAdvPager.setOnPageChangeListener(new GuidePageChangeListener());
		mAdvPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					// 开始图片滚动
					startImageCycle();
					break;
				default:
					// 停止图片滚动
					pushImageCycle();
					break;
				}
				return false;
			}
		});
	}

	/**
	 * 装填图片数据
	 * 
	 * @param imageUrlList
	 * @param imageCycleViewListener
	 */
	public void setImageResources(ArrayList<String> imageUrlList,
			ImageCycleViewListener imageCycleViewListener) {
		int[] imageIds = new int[] { R.drawable.default_banner1,R.drawable.default_banner1 };
		setImageCount(imageUrlList,imageIds);
		setBannerIndicator();
		initViewPager(imageUrlList, imageCycleViewListener, imageIds);
	}

	/**
	 * 设置广告栏广告个数
	 * 
	 * @param imageUrlList
	 * @param imageIds 
	 * @param imageIds
	 */
	private void setImageCount(ArrayList<String> imageUrlList, int[] imageIds) {
		// 图片广告数量

		if (isNotEmptyUrlList(imageUrlList)) {
			imageCount = imageUrlList.size();
		} else {
			imageCount = imageIds.length;
		}
	}

	/**
	 * @param imageUrlList
	 * @param imageCycleViewListener
	 * @param imageIds
	 */
	private void initViewPager(ArrayList<String> imageUrlList,
			ImageCycleViewListener imageCycleViewListener, int[] imageIds) {
		if (isNotEmptyUrlList(imageUrlList)) {
			mAdvAdapter = new ImageCycleAdapter(mContext, imageUrlList,
					imageCycleViewListener);
		} else {
			mAdvAdapter = new ImageCycleAdapter(mContext, imageIds,
					imageCycleViewListener);
		}

		mAdvPager.setAdapter(mAdvAdapter);
		mAdvPager.setCurrentItem(8000);//使viewpager 无限左滑动
		/*
		 * if (imageUrlList.size() > 1) {// 使viewpager 无限左滑动
		 * mAdvPager.setCurrentItem((Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE
		 * / 2) % imageUrlList.size());// 设置选中为中间/图片为和第0张一样 } else {
		 * mAdvPager.setCurrentItem((Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE
		 * / 2) % imageIds.length);// 设置选中为中间/图片为和第0张一样 }
		 */
		/* startImageTimerTask(); */
	}

	private boolean isNotEmptyUrlList(ArrayList<String> imageUrlList) {
		return imageUrlList!=null&&imageUrlList.size() > 0;
	}

	/**
	 * 设置Banner指示器个数
	 */
	private void setBannerIndicator() {
		// 清除所有子视图
		mGroup.removeAllViews();
		mImageViews = new ImageView[imageCount];
		if (mImageViews.length > 1) {
			for (int i = 0; i < imageCount; i++) {
				mImageView = new ImageView(mContext);
//				int imageParams = (int) (mScale * 20 + 0.5f);// XP与DP转换，适应不同分辨率
				int imagePadding = (int) (mScale * 5 + 0.5f);
//				mImageView.setLayoutParams(new LayoutParams(imageParams,
//						imageParams));
				mImageView.setPadding(imagePadding, imagePadding, imagePadding,
						imagePadding);
				mImageViews[i] = mImageView;
				if (i == 0) {
					mImageViews[i]
							.setImageResource(R.drawable.banner_dian_focus);
				} else {
					mImageViews[i]
							.setImageResource(R.drawable.banner_dian_blur);
				}
				mGroup.addView(mImageViews[i]);
			}
		}
	}

	/**
	 * 开始轮播(手动控制自动轮播与否，便于资源控制)
	 */
	public void startImageCycle() {
		if(imageCount>=2){
			startImageTimerTask();
		}
		
	}

	/**
	 * 暂停轮播——用于节省资源
	 */
	public void pushImageCycle() {
		stopImageTimerTask();
	}

	/**
	 * 开始图片滚动任务
	 */
	private void startImageTimerTask() {
		pushImageCycle();
		// 图片每3秒滚动一次
		mHandler.postDelayed(mImageTimerTask, 4000);
	}

	/**
	 * 停止图片滚动任务
	 */
	private void stopImageTimerTask() {
		mHandler.removeCallbacks(mImageTimerTask);
	}

	private Handler mHandler = new Handler();

	/**
	 * 图片自动轮播Task
	 */
	private Runnable mImageTimerTask = new Runnable() {

		@Override
		public void run() {
			mAdvPager.setCurrentItem(mAdvPager.getCurrentItem() + 1);
		}
	};

	/**
	 * 轮播图片状态监听器
	 * 
	 * @author minking
	 */
	private final class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE)
				startImageCycle(); // 开始下次计时
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {
			// 设置当前显示的图片下标
			mImageIndex = index % imageCount;
			/* mImageIndex = index; */
			// 设置图片滚动指示器背景
			if (imageCount > 1) {
				mImageViews[mImageIndex]
						.setImageResource(R.drawable.banner_dian_focus);
				for (int i = 0; i < mImageViews.length; i++) {
					if (mImageIndex != i) {
						mImageViews[i]
								.setImageResource(R.drawable.banner_dian_blur);
					}
				}
			}

		}

	}

	private class ImageCycleAdapter extends PagerAdapter {

		/**
		 * 图片视图缓存列表
		 */
		/* private ArrayList<ImageView> mImageViewCacheList; */

		/**
		 * 图片资源列表
		 */
		private ArrayList<String> mAdList = new ArrayList<String>();
		/**
		 * 默认图片资源列表
		 */
		private int[] imageIds = new int[] {};

		/**
		 * 广告图片点击监听器
		 */
		private ImageCycleViewListener mImageCycleViewListener;

		private Context mContext;

		public ImageCycleAdapter(Context context, ArrayList<String> adList,
                                 ImageCycleViewListener imageCycleViewListener) {
			mContext = context;
			mAdList = adList;
			mImageCycleViewListener = imageCycleViewListener;
		}

		public ImageCycleAdapter(Context context, int[] imageIds,
                                 ImageCycleViewListener imageCycleViewListener) {
			mContext = context;
			this.imageIds = imageIds;
			mImageCycleViewListener = imageCycleViewListener;
		}

		@Override
		public int getCount() {
			if (mAdList.size() == 1 || imageIds.length == 1) {// 一张图片时不用流动
				return 1;
			}// 使viewpager 无限右滑动
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {

			if (mAdList.size() > 0) {
				String imageUrl = mAdList.get(position % mAdList.size());
				ImageView imageView;
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				// 设置图片点击监听
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mImageCycleViewListener.onImageClick(
								position % mAdList.size(), v, 0);
					}
				});
				imageView.setTag(imageUrl);
				container.addView(imageView);
				mImageCycleViewListener.displayImage(imageUrl, imageView);
				return imageView;
			} else {
				int imageId = imageIds[position % imageIds.length];
				ImageView imageView;
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				imageView.setImageResource(imageId);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				// 设置图片点击监听
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mImageCycleViewListener.onImageClick(position
								% imageIds.length, v, 1);
					}
				});
				imageView.setTag(imageId);
				container.addView(imageView);
				return imageView;
			}

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ImageView view = (ImageView) object;
			container.removeView(view);
		}

	}

	/**
	 * 轮播控件的监听事件
	 * 
	 * @author minking
	 */
	public interface ImageCycleViewListener {

		/**
		 * 加载图片资源
		 * 
		 * @param imageURL
		 * @param imageView
		 */
		void displayImage(String imageURL, ImageView imageView);

		/**
		 * 单击图片事件
		 * 
		 * @param position
		 * @param imageView
		 */
		void onImageClick(int position, View imageView, int type);
	}

}
