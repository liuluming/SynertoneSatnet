package synertone.com.satnet.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.tools.MyCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.math.BigDecimal;
import java.util.Calendar;

import synertone.com.satnet.R;
import synertone.com.satnet.SatnetApplication;
import synertone.com.satnet.activity.person.AccountQueryActivity;
import synertone.com.satnet.model.QueryCurrentModel;
import synertone.com.satnet.utils.MoneyUtil;
import synertone.com.satnet.utils.XTHttpUtil;
import synertone.com.satnet.view.ToastUtil;

import static synertone.com.satnet.utils.JsonUtil.decimalFormat;

public class CurrentQueryFragment extends Fragment {
    private AccountQueryActivity mActivity;
    private View view;
    private TextView mClientName, mBillTotle,
            mBillInfo, mLiuBillInfo,
            mBillInfoTv, mFlowUseInfo,
            mFlowUseInfoTv, mOutflowUseTv,
            mAccountRemainTv, mAccountCycleTv;
    private Gson gson;
    private String mAccountToken, mCode, mResult, mYear;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AccountQueryActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.person_fragment_current, container, false);
        if(SatnetApplication.accountModel!=null) {
            mAccountToken = SatnetApplication.accountModel.getSessionToken();
        }
        gson = new Gson();
        initView();
        if (mAccountToken != null) {
            accountRequestPost();
        }
        return view;
    }

    public void initView() {
        Calendar c = Calendar.getInstance();
        mYear = String.valueOf(c.get(Calendar.YEAR));
        mClientName = view.findViewById(R.id.clint_name_lv);
        mBillTotle = view.findViewById(R.id.month_bill_totle_lv);
        mBillInfo = view.findViewById(R.id.bill_info_lv);
        mLiuBillInfo = view.findViewById(R.id.liulb_bill_info_lv);
        mBillInfoTv = view.findViewById(R.id.bill_info_tv);
        mFlowUseInfo = view.findViewById(R.id.flow_use_info_tv);
        mFlowUseInfoTv = view.findViewById(R.id.liu_flow_use_info_lv);
        mOutflowUseTv = view.findViewById(R.id.flow_use_out_info);
        mAccountRemainTv = view.findViewById(R.id.accountting_remainning_lv);
        mAccountCycleTv = view.findViewById(R.id.accountting_cycle_lv);

    }
    private void accountRequestPost() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionToken", mAccountToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(XTHttpUtil.POST_QUERY_ACCOUNT_ADDRESS);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        x.http().post(params, new MyCallback(mActivity, true) {

            @Override
            public void onSuccess(String result) {
                QueryCurrentModel queryCurrent = gson.fromJson(result, QueryCurrentModel.class);
                mCode = queryCurrent.getErrCode();
                mResult = queryCurrent.getResult();
                if (mResult.equals("0")) {
                    if (mCode.equals("0")) {
                        ToastUtil.doToast("查询成功");
                        mClientName.setText(queryCurrent.getMessage().getCustomerName());
                        String mAccountBalance=decimalFormat.format(Double.parseDouble(queryCurrent.getMessage().getAccountBalance()));
                        mAccountRemainTv.setText(mAccountBalance + "元");//账户余额信息
                        mAccountCycleTv.setText((mYear + "." + queryCurrent.getMessage().getChargeBeginTime()) + "-" + (mYear + "." + queryCurrent.getMessage().getChargeEndTime()));//计费周期
                        mBillTotle.setText(queryCurrent.getMessage().getTotalFee() + "元");//当月总扣费
                        mBillInfo.setText(queryCurrent.getMessage().getDataPlanFee() + "元");//套餐扣费信息
                        mLiuBillInfo.setText(queryCurrent.getMessage().getTrafficPackFee() + "元");//流量包扣费详细信息
                        mBillInfoTv.setText(queryCurrent.getMessage().getExtendTrafficFee() + "元");//套餐外流量扣费信息
                        double mFlowUseInfo = Double.parseDouble(queryCurrent.getMessage().getDataPlanTraffic());
                        String mFlowInfo = new BigDecimal(MoneyUtil.ceil(mFlowUseInfo, 2)).divide(new BigDecimal(1000)).toString();
                        CurrentQueryFragment.this.mFlowUseInfo.setText(MoneyUtil.decimalFormat.format(MoneyUtil.ceil(Double.parseDouble(mFlowInfo), 2)) + "M"); //包含套餐流量使用情况
                        double mLiuFlowUse = Double.parseDouble(queryCurrent.getMessage().getTrafficPackTraffic());
                        String mLiuUse = new BigDecimal(MoneyUtil.ceil(mLiuFlowUse, 2)).divide(new BigDecimal(1000)).toString();
                        mFlowUseInfoTv.setText(MoneyUtil.decimalFormat.format(MoneyUtil.ceil(Double.parseDouble(mLiuUse), 2)) + "M");//包含流量包使用情况
                        double mExtendFlowUseInfo = Double.parseDouble(queryCurrent.getMessage().getExtendTraffic());
                        String mExtendUseInfo = new BigDecimal(MoneyUtil.ceil(mExtendFlowUseInfo, 2)).divide(new BigDecimal(1000)).toString();
                        mOutflowUseTv.setText(MoneyUtil.decimalFormat.format(MoneyUtil.ceil(Double.parseDouble(mExtendUseInfo), 2)) + "M");//包含套餐外流量使用情况
                    } else if (mCode.equals("1")) {
                        ToastUtil.doToast("用户不存在");
                    } else if (mCode.equals("2")) {
                        ToastUtil.doToast("无账单");
                    } else if (mCode.equals("99")) {
                        ToastUtil.doToast("未知原因");
                    }
                } else if (mResult.equals("1")) {
                    ToastUtil.doToast("暂无数据，查询失败");
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.doToast("查询失败");
            }
        });
    }
}
