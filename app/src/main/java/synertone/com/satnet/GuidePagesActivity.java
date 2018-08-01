package synertone.com.satnet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class GuidePagesActivity extends BaseActivity implements OnPageChangeListener {

    private ViewPager viewPager;
    private ViewPagerAdapter vpAdapter;
    private ArrayList<View> views;
    private int currPosition = 0;
    // 引导图片资源
    private static final int[] pics = {R.drawable.welcome1, R.drawable.welcome2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_pages);
        initView();
        initData();
        initEvent();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        views = new ArrayList<>();
        viewPager = findViewById(R.id.viewpager);
        vpAdapter = new ViewPagerAdapter(views);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        // 初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ScaleType.FIT_XY);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }
        // 设置数据
        viewPager.setAdapter(vpAdapter);
    }

    private void initEvent() {
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * 当滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当当前页面被滑动时调用
     */

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    /**
     * 当新的页面被选中时调用
     */

    @Override
    public void onPageSelected(int position) {
        currPosition = position;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (currPosition == pics.length - 1 && MotionEvent.ACTION_UP == ev.getAction()) {
                // 滑动到最后一页时
                Intent intent = new Intent(GuidePagesActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }
     class ViewPagerAdapter extends PagerAdapter {

        //界面列表
        private ArrayList<View> views;

        // 图片是否轮播
        private boolean isImageViewCycle = false;

        public ViewPagerAdapter (ArrayList<View> views){
            this.views = views;
        }

        /**
         * 获得当前界面数
         */
        @Override
        public int getCount() {
            if (views == null){
                return 0;
            }
            if (isImageViewCycle){
                return Integer.MAX_VALUE;
            } else if (views != null) {
                return views.size();
            }
            return 0;
        }

        /**
         * 初始化position位置的界面
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (isImageViewCycle && views.size() > 1){
                position = position % views.size();
            }
            View view = views.get(position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent parent = view.getParent();
            if (parent != null){
                ViewGroup vg = (ViewGroup) parent;
                vg.removeView(view);
            }
            container.addView(view);
            return view;
        }

        /**
         * 判断是否由对象生成界面
         */
        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return (view == arg1);
        }


    }


}
