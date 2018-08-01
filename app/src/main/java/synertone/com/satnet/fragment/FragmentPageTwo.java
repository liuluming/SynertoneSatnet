package synertone.com.satnet.fragment;

import android.app.Activity;
import android.os.Bundle;
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
import synertone.com.satnet.model.QueryHistoryModel;
import synertone.com.satnet.utils.MoneyUtil;
import synertone.com.satnet.utils.XTHttpUtil;
import synertone.com.satnet.view.ToastUtil;

public class FragmentPageTwo extends BaseFragment{

	private View view;
	private TextView mClientName, mBillTotle, mBillInfo,
			mLiuBillInfo, mBillInfoTv,mBillDate;
	private String mPageTwoToken,mCode,mResult,mYear,mMonth;
	private AccountQueryActivity mActivity;
	private Gson gson;
	private boolean isPrepared;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (AccountQueryActivity) activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if(SatnetApplication.accountModel!=null) {
			mPageTwoToken = SatnetApplication.accountModel.getSessionToken();
		}
		gson=new Gson();
		view = inflater.inflate(R.layout.persern_fragment_2, container,false);
		initView();
		isPrepared = true;
		lazyLoad();
		return view;
	}

	private void initView() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1 ;
		if(month<=2){
			mYear= String.valueOf(year-1);
			mMonth= String.valueOf(month-2+12);
		}else{
			mYear= String.valueOf(year);
			mMonth= String.valueOf(month-2);
		}
		mClientName = view.findViewById(R.id.clint_name_lv);
		mBillTotle = view.findViewById(R.id.month_bill_totle_lv);
		mBillInfo = view.findViewById(R.id.bill_info_lv);
		mLiuBillInfo = view.findViewById(R.id.liulb_bill_info_lv);
		mBillInfoTv = view.findViewById(R.id.bill_info_tv);
		mBillDate = view.findViewById(R.id.tv_bill_date);
		mBillDate.setText(mYear+"年"+mMonth+"月账单");
	}
	@Override
	protected void lazyLoad() {
		if(!isPrepared || !isVisible) {
			return;
		}else{
			if (mPageTwoToken != null) {
				pageTwoRequestPost();
			}
		}
	}
	private void pageTwoRequestPost() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("sessionToken", mPageTwoToken);
			jsonObject.put("year", mYear);
			jsonObject.put("month", mMonth);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestParams params = new RequestParams(XTHttpUtil.POST_QUERY_HISTORY_ADDRESS);
		params.setAsJsonContent(true);
		params.setBodyContent(jsonObject.toString());
		x.http().post(params, new MyCallback(mActivity, true) {

			@Override
			public void onSuccess(String result) {
				QueryHistoryModel queryHistory = gson.fromJson(result, QueryHistoryModel.class);
				mCode = queryHistory.getErrCode();
				mResult = queryHistory.getResult();
				if (mResult.equals("0")) {
					if (mCode.equals("0")) {
						ToastUtil.doToast("查询成功");
						mClientName.setText(queryHistory.getMessage().getCustomerName());
						double mTotalFee = Double.parseDouble(queryHistory.getMessage().getTotalFee());
						String mBillTotleFee = new BigDecimal(MoneyUtil.ceil(mTotalFee, 2)).toString();
						mBillTotle.setText(MoneyUtil.decimalFormat.format(MoneyUtil.ceil(Double.parseDouble(mBillTotleFee), 2))+"元");//当月总扣费
						double mBillFee = Double.parseDouble(queryHistory.getMessage().getDataPlanFee());
						String mBillInfoFee = new BigDecimal(MoneyUtil.ceil(mBillFee, 2)).toString();
						mBillInfo.setText(MoneyUtil.decimalFormat.format(MoneyUtil.ceil(Double.parseDouble(mBillInfoFee), 2))+"元");//套餐扣费信息
						double mLiuBill = Double.parseDouble(queryHistory.getMessage().getTrafficPackFee());
						String mLiuBillInfoFee = new BigDecimal(MoneyUtil.ceil(mLiuBill, 2)).toString();
						mLiuBillInfo.setText(MoneyUtil.decimalFormat.format(MoneyUtil.ceil(Double.parseDouble(mLiuBillInfoFee), 2))+"元");//流量包扣费信息
						double mBillTv = Double.parseDouble(queryHistory.getMessage().getExtendTrafficFee());
						String mBillInfoTvFee = new BigDecimal(MoneyUtil.ceil(mBillTv, 2)).toString();
						mBillInfoTv.setText(MoneyUtil.decimalFormat.format(MoneyUtil.ceil(Double.parseDouble(mBillInfoTvFee), 2))+"元");//套餐外流量扣费信息
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