package synertone.com.satnet.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import synertone.com.satnet.SatnetApplication;

/**
 * Created by snt1231 on 2017/3/27.
 */

public class FontTextView extends AppCompatTextView {


    public FontTextView(Context context) {
        this(context,null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        this.setTypeface(SatnetApplication.fontXiti);
    }
}
