package synertone.com.satnet.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import synertone.com.satnet.R;
import synertone.com.satnet.activity.person.AccountQueryActivity;

public class HistoryAccountFragment extends BaseFragment implements View.OnClickListener {
    private AccountQueryActivity mActivity;
    private View view;
    private ViewPager mPaper1;
    private FragmentPagerAdapter mAdapter1;
    private List<Fragment> mFragments1 = new ArrayList<Fragment>();
    private TextView mOne, mTwo, mThree, mFour, mFive;
    private boolean isPrepared;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AccountQueryActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.persern_activity_home_main, container, false);
        initLayout();
        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        } else {
            initAdapterData();
        }
    }

    private void initAdapterData() {
        mAdapter1 = new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments1.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments1.get(arg0);
            }
        };
        mPaper1.setAdapter(mAdapter1);
        mPaper1.setCurrentItem(0);
        mOne.setTextColor(Color.rgb(87, 153, 8));
        mTwo.setTextColor(Color.parseColor("#000000"));
        mThree.setTextColor(Color.parseColor("#000000"));
        mFour.setTextColor(Color.parseColor("#000000"));
        mFive.setTextColor(Color.parseColor("#000000"));
        mPaper1.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int currentIndex;

            @Override
            public void onPageSelected(int position) {
                resetColor();
                switch (position) {
                    case 0:
                        mOne.setTextColor(Color.rgb(87, 153, 8));
                        mPaper1.setCurrentItem(0);
                        break;
                    case 1:
                        mTwo.setTextColor(Color.rgb(87, 153, 8));
                        mPaper1.setCurrentItem(1);
                        break;
                    case 2:
                        mThree.setTextColor(Color.rgb(87, 153, 8));
                        mPaper1.setCurrentItem(2);
                        break;
                    case 3:
                        mFour.setTextColor(Color.rgb(87, 153, 8));
                        mPaper1.setCurrentItem(3);
                        break;
                    case 4:
                        mFive.setTextColor(Color.rgb(87, 153, 8));
                        mPaper1.setCurrentItem(4);
                        break;
                    default:
                        mOne.setTextColor(Color.rgb(87, 153, 8));
                        mPaper1.setCurrentItem(0);
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
     * 初始化控件
     */
    public void initLayout() {
        mOne = view.findViewById(R.id.tv_youngfootable);
        mTwo = view.findViewById(R.id.tv_midfield);
        mThree = view.findViewById(R.id.tv_forward);
        mFour = view.findViewById(R.id.tv_guard);
        mFive = view.findViewById(R.id.tv_8);
        mPaper1 = view.findViewById(R.id.view_pager);
        mOne.setOnClickListener(this);
        mTwo.setOnClickListener(this);
        mThree.setOnClickListener(this);
        mFour.setOnClickListener(this);
        mFive.setOnClickListener(this);
        FragmentPageOne f1 = new FragmentPageOne();
        FragmentPageTwo f2 = new FragmentPageTwo();
        FragmentPageThree f3 = new FragmentPageThree();
        FragmentPageFour f4 = new FragmentPageFour();
        FragmentPageFive f5 = new FragmentPageFive();
        mFragments1.add(f1);
        mFragments1.add(f2);
        mFragments1.add(f3);
        mFragments1.add(f4);
        mFragments1.add(f5);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        if (month == 1) {
            mOne.setText(String.valueOf(month - 1 + 12) + "月");
            mTwo.setText(String.valueOf(month - 2 + 12) + "月");
            mThree.setText(String.valueOf(month - 3 + 12) + "月");
            mFour.setText(String.valueOf(month - 4 + 12) + "月");
            mFive.setText(String.valueOf(month - 5 + 12) + "月");
        } else if (month == 2) {
            mOne.setText(String.valueOf(month - 1) + "月");
            mTwo.setText(String.valueOf(month - 2 + 12) + "月");
            mThree.setText(String.valueOf(month - 3 + 12) + "月");
            mFour.setText(String.valueOf(month - 4 + 12) + "月");
            mFive.setText(String.valueOf(month - 5 + 12) + "月");
        } else if (month == 3) {
            mOne.setText(String.valueOf(month - 1) + "月");
            mTwo.setText(String.valueOf(month - 2) + "月");
            mThree.setText(String.valueOf(month - 3 + 12) + "月");
            mFour.setText(String.valueOf(month - 4 + 12) + "月");
            mFive.setText(String.valueOf(month - 5 + 12) + "月");
        } else if (month == 4) {
            mOne.setText(String.valueOf(month - 1) + "月");
            mTwo.setText(String.valueOf(month - 2) + "月");
            mThree.setText(String.valueOf(month - 3) + "月");
            mFour.setText(String.valueOf(month - 4 + 12) + "月");
            mFive.setText(String.valueOf(month - 5 + 12) + "月");
        } else if (month == 5) {
            mOne.setText(String.valueOf(month - 1) + "月");
            mTwo.setText(String.valueOf(month - 2) + "月");
            mThree.setText(String.valueOf(month - 3) + "月");
            mFour.setText(String.valueOf(month - 4) + "月");
            mFive.setText(String.valueOf(month - 5 + 12) + "月");
        } else {
            mOne.setText(String.valueOf(month - 1) + "月");
            mTwo.setText(String.valueOf(month - 2) + "月");
            mThree.setText(String.valueOf(month - 3) + "月");
            mFour.setText(String.valueOf(month - 4) + "月");
            mFive.setText(String.valueOf(month - 5) + "月");
        }
    }

    public void resetColor() {
        mOne.setTextColor(Color.rgb(56, 56, 56));
        mTwo.setTextColor(Color.rgb(56, 56, 56));
        mThree.setTextColor(Color.rgb(56, 56, 56));
        mFour.setTextColor(Color.rgb(56, 56, 56));
        mFive.setTextColor(Color.rgb(56, 56, 56));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_youngfootable:
                resetColor();
                mOne.setTextColor(Color.rgb(87, 153, 8));
                mPaper1.setCurrentItem(0);
                break;
            case R.id.tv_midfield:
                resetColor();
                mTwo.setTextColor(Color.rgb(87, 153, 8));
                mPaper1.setCurrentItem(1);
                break;
            case R.id.tv_forward:
                resetColor();
                mThree.setTextColor(Color.rgb(87, 153, 8));
                mPaper1.setCurrentItem(2);
                break;
            case R.id.tv_guard:
                resetColor();
                mFour.setTextColor(Color.rgb(87, 153, 8));
                mPaper1.setCurrentItem(3);
                break;
            case R.id.tv_8:
                resetColor();
                mFive.setTextColor(Color.rgb(87, 153, 8));
                mPaper1.setCurrentItem(4);
                break;
            default:
                resetColor();
                mOne.setTextColor(Color.rgb(87, 153, 8));
                mPaper1.setCurrentItem(0);
                break;
        }
    }

    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        public List<Activity> mListViews;

        public MyPagerAdapter(List<Activity> mListViews) {
            this.mListViews = mListViews;
        }


        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }


        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
}
