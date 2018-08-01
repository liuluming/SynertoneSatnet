package com.litesuits.common.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.litesuits.common.R;
import com.litesuits.common.adapter.CommonAdapter;

public class NiceSpinner<T> {
    private PopupWindow mPopupWindow;
    private ListView mListView;
    private static final int DEFAULT_ELEVATION = 16;
    private int mWidth;
    public NiceSpinner(Context context) {
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(DEFAULT_ELEVATION);
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.spinner_drawable));
        } else {
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drop_down_shadow));
        }
        ListView listView = new ListView(context);
        mPopupWindow = popupWindow;
        mListView = listView;
        mPopupWindow.setContentView(mListView);
    }

    public void setAdapter(CommonAdapter<T> commonAdapter) {
        mListView.setAdapter(commonAdapter);
    }

    public void showTo(View view) {
        measurePopUpDimension(view);
        mPopupWindow.showAsDropDown(view);
    }

    private void measurePopUpDimension(View view) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        mListView.measure(widthSpec, heightSpec);
        mPopupWindow.setWidth(mWidth==0?mListView.getMeasuredWidth():mWidth);
        mPopupWindow.setHeight(mListView.getMeasuredHeight());
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }
}
