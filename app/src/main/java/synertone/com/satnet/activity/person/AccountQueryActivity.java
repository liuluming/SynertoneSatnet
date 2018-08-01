package synertone.com.satnet.activity.person;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import synertone.com.satnet.R;
import synertone.com.satnet.fragment.CurrentQueryFragment;
import synertone.com.satnet.fragment.HistoryAccountFragment;

public class AccountQueryActivity extends FragmentActivity {
	private ViewPager viewPager;// 翻转页viewpager
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mFragments = new ArrayList<Fragment>();
	private LinearLayout[] bottonMenu;
	private ImageView mIMGlishi, mIMGshishi;
	private TextView mTlishi, mTshishi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.persern_activity_main);
        initView();
        initLayout();
        bottonMenu = getMenuButtonObject();
        setBottonEvent();
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return mFragments.size();
			}
			@Override
			public Fragment getItem(int arg0) {
				return mFragments.get(arg0);
			}
		};
		initViewPager();
		viewPager.setAdapter(mAdapter);
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			private int currentIndex=0;

			@Override
			public void onPageSelected(int position) {
				resetTabBtn();
				switch (position) {
				case 0:
					mIMGshishi
					.setImageResource(R.drawable.person_pic_check_found);
			mTshishi.setTextColor(Color.parseColor("#ffffff"));
					viewPager.setCurrentItem(0);
					break;
				case 1:
					mIMGlishi
					.setImageResource(R.drawable.person_pic_check_main);
			mTlishi.setTextColor(Color.parseColor("#ffffff"));
					break;
				default:
					mIMGshishi
					.setImageResource(R.drawable.person_pic_check_found);
			mTshishi.setTextColor(Color.parseColor("#ffffff"));
					break;
				}
				currentIndex = position;
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
    }
    /**
     * 设置底部菜单点击事件
     */
    private void setBottonEvent() {
    	//实时
    			bottonMenu[0].setOnClickListener(new View.OnClickListener() {
    				public void onClick(View v) {
    					resetTabBtn();
    					mIMGshishi.setImageResource(R.drawable.person_pic_check_found);
    					mTshishi.setTextColor(Color.parseColor("#ffffff"));
    					viewPager.setCurrentItem(0);
    				}
    			});
    	// 历史
		bottonMenu[1].setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resetTabBtn();
				mIMGlishi.setImageResource(R.drawable.person_pic_check_main);
				mTlishi.setTextColor(Color.parseColor("#ffffff"));
				viewPager.setCurrentItem(1);
			}
		});
		
		
	}
    /*
	 * 重置图片按钮颜色
	 */
	protected void resetTabBtn() {
		mIMGlishi.setImageResource(R.drawable.person_pic_main);
		mIMGshishi.setImageResource(R.drawable.person_pic_found);
		mTlishi.setTextColor(Color.rgb(56,56,56));
		mTshishi.setTextColor(Color.rgb(56,56,56));
	}
	/**
     * 初始化菜单事件
     */
    public void initLayout(){
    	CurrentQueryFragment crrqry = new CurrentQueryFragment();
    	HistoryAccountFragment hisqry = new HistoryAccountFragment();
    	mFragments.add(crrqry);
    	mFragments.add(hisqry);
    }
    /**
     * 初始化控件
     */
    private void initView() {
		mIMGshishi = findViewById(R.id.MyBottemSHISHIImg);
		mIMGlishi = findViewById(R.id.MyBottemLishiImg);
		mTshishi = findViewById(R.id.MyBottemSHISHITxt);
		mTlishi = findViewById(R.id.MyBottemLishiTxt);
		viewPager = findViewById(R.id.id_viewpager);
    }
    /**
     * 获取底部菜单事件
     * @return
     */
    private LinearLayout[] getMenuButtonObject() {
		LinearLayout[] lv_bottonMenu = new LinearLayout[2];
		lv_bottonMenu[0] = findViewById(R.id.MyBottemshishiBtn);
		lv_bottonMenu[1] = findViewById(R.id.MyBottemLishiBtn);
		return lv_bottonMenu;
	}
    /**
     * 初始化
     * 
     */
   public void initViewPager(){
	   resetTabBtn();
		mIMGshishi.setImageResource(R.drawable.person_pic_check_found);
	   mTshishi.setTextColor(Color.parseColor("#ffffff"));
		viewPager.setCurrentItem(0);
   }
	public void OnAccountFinish(View v) {
		finish();

	}
}
