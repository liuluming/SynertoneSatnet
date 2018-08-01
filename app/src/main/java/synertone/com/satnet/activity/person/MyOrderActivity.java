package synertone.com.satnet.activity.person;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.tools.MyCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import synertone.com.satnet.BaseActivity;
import synertone.com.satnet.R;
import synertone.com.satnet.SatnetApplication;
import synertone.com.satnet.adapter.MyOrderAdapter;
import synertone.com.satnet.model.MyOrderModel;
import synertone.com.satnet.model.OrderSubscription;
import synertone.com.satnet.utils.XTHttpUtil;
import synertone.com.satnet.view.ToastUtil;

/**
 * Created y snt1206 on 2017/2/14.
 */

public class MyOrderActivity extends BaseActivity {
    private List<OrderSubscription> mOrderList;
    private ListView lv_my_order;
    private String mOrderToken,mCode,mResult;
    private TextView mClientName,mTittle;
    private MyOrderAdapter mOrderAdapter;
    private Gson gson;
    private RelativeLayout rl_top_bar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_my_order);
        lv_my_order = findViewById(R.id.lv_my_order);
        gson = new Gson();
        initView();
    }

    private void initView() {
        rl_top_bar= findViewById(R.id.rl_top_bar);
        mTittle= findViewById(R.id.tv_bar_title);
        mTittle.setText(getString(R.string.my_order));
        mClientName= findViewById(R.id.client_name_lv);
        if(SatnetApplication.accountModel.getSessionToken()!=null) {
            mOrderToken = SatnetApplication.accountModel.getSessionToken();
        }
        mOrderList = new ArrayList<OrderSubscription>();
        mOrderAdapter = new MyOrderAdapter(this, mOrderList);
        lv_my_order.setAdapter(mOrderAdapter);
        if(mOrderToken!=null) {
            myOrderRequestPost();
        }
        initEvent();
    }

    private void initEvent() {
        rl_top_bar.setOnTouchListener(new ComBackTouchListener());
    }
    private void myOrderRequestPost() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionToken", mOrderToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(XTHttpUtil.POST_MY_ORDER);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        x.http().post(params, new MyCallback(mContext, true) {

            @Override
            public void onSuccess(String result) {
                MyOrderModel myOrderModel = gson.fromJson(result, MyOrderModel.class);
                mCode = myOrderModel.getErrCode();
                mResult = myOrderModel.getResult();
                if (mResult.equals("0")) {
                    if (mCode.equals("0")) {
                        ToastUtil.doToast( "查询成功");
                        mClientName.setText(myOrderModel.getMessage().getCustomerName());
                        orderUserMessages(myOrderModel.getMessage().getSubscription());
                    }else if (mCode.equals("1")) {
                        ToastUtil.doToast( "用户不存在");
                    } else if (mCode.equals("2")) {
                        ToastUtil.doToast( "无账单");
                    } else if (mCode.equals("99")) {
                        ToastUtil.doToast( "未知原因");
                    }
                }else if (mResult.equals("1")) {
                    ToastUtil.doToast( "查询失败");
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.doToast( "查询失败");
            }
        });
    }
    private void orderUserMessages(List<OrderSubscription> orderInformations) {
        mOrderList.clear();
        for (OrderSubscription information : orderInformations) {
            mOrderList.add(information);
        }
        mOrderAdapter.notifyDataSetChanged();

    }
}

