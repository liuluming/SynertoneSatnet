package synertone.com.satnet.activity.person;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.tools.MyCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.math.BigDecimal;

import synertone.com.satnet.BaseActivity;
import synertone.com.satnet.R;
import synertone.com.satnet.SatnetApplication;
import synertone.com.satnet.model.QueryCurrentModel;
import synertone.com.satnet.utils.MoneyUtil;
import synertone.com.satnet.utils.XTHttpUtil;
import synertone.com.satnet.view.ToastUtil;

import static synertone.com.satnet.utils.MoneyUtil.ceil;


/**
 * Created by snt1206 on 2017/2/13.
 */

public class QueryFlowActivity extends BaseActivity {
    private String mFlowToken, mCode, mResult;
    private Gson gson;
    private TextView mTittle, total_flow_num, usable_flow_num, main_total_flow_num, main_usable_flow_num, packet_total_num, food_fp_has_rest, packet_usable_num;
    private ProgressBar pb_progressbar, pb_progressbar1, pb_progressbar2, pb_progressbar3;
    private RelativeLayout rl_top_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_query_flow);
        if (SatnetApplication.accountModel != null) {
            mFlowToken = SatnetApplication.accountModel.getSessionToken();
        }
        initView();
        gson = new Gson();
        if (mFlowToken != null) {
            myFlowRequestPost();
        }
    }

    private void initView() {
        rl_top_bar = findViewById(R.id.rl_top_bar);
        mTittle = findViewById(R.id.tv_bar_title);
        mTittle.setText(R.string.query_flow);
        total_flow_num = findViewById(R.id.total_flow_num);
        usable_flow_num = findViewById(R.id.usable_flow_num);
        main_total_flow_num = findViewById(R.id.main_total_flow_num);
        main_usable_flow_num = findViewById(R.id.main_usable_flow_num);
        packet_total_num = findViewById(R.id.packet_total_num);
        food_fp_has_rest = findViewById(R.id.food_fp_has_rest);
        packet_usable_num = findViewById(R.id.packet_usable_num);
        pb_progressbar = findViewById(R.id.pb_progressbar);
        pb_progressbar1 = findViewById(R.id.pb_progressbar1);
        pb_progressbar2 = findViewById(R.id.pb_progressbar2);
        pb_progressbar3 = findViewById(R.id.pb_progressbar3);
        initEvent();
    }

    private void initEvent() {
        rl_top_bar.setOnTouchListener(new ComBackTouchListener());
    }

    private void myFlowRequestPost() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionToken", mFlowToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(XTHttpUtil.POST_QUERY_FLOW);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        x.http().post(params, new MyCallback(mContext, true) {

            @Override
            public void onSuccess(String result) {
                ToastUtil.doToast(result);
                if (result != null) {
                    QueryCurrentModel queryCurrent = gson.fromJson(result, QueryCurrentModel.class);
                    mCode = queryCurrent.getErrCode();
                    mResult = queryCurrent.getResult();
                    //(data.message.dataPlanTraffic/1000).toFixed(2) + "M";
                    if (mResult.equals("0")) {
                        if (mCode.equals("0")) {
                            ToastUtil.doToast("查询成功");
                            String traAndFeeTotaltra = new BigDecimal(ceil(Double.parseDouble(queryCurrent.getMessage().getTraAndFeeTotaltraffic()), 2)).divide(new BigDecimal(1000)).toString();
                            String traAndFeeTotal;
                            if (traAndFeeTotaltra.contains(",")) {
                                traAndFeeTotal = traAndFeeTotaltra.replaceAll("[,]", "");
                            } else {
                                traAndFeeTotal = traAndFeeTotaltra;
                            }
                            total_flow_num.setText(MoneyUtil.decimalFormat.format(ceil(Double.parseDouble(traAndFeeTotal), 2)) + "M");
                            String usedTraAndFeeTotaltra = new BigDecimal(ceil(Double.parseDouble(queryCurrent.getMessage().getUsedTraAndFeeTotaltraffic()), 2)).divide(new BigDecimal(1000)).toString();
                            BigDecimal traAndFeeTotalBg = new BigDecimal(traAndFeeTotal);
                            BigDecimal usedTraAndFeeTotaltraBg = new BigDecimal(usedTraAndFeeTotaltra);
                            String usableFlowNum = traAndFeeTotalBg.subtract(usedTraAndFeeTotaltraBg).toString();
                            String usableFlow;
                            if (usableFlowNum.contains(",")) {
                                usableFlow = usableFlowNum.replaceAll("[,]", "");
                            } else {
                                usableFlow = usableFlowNum;
                            }
                            usable_flow_num.setText(MoneyUtil.decimalFormat.format(ceil(Double.parseDouble(usableFlow), 2)) + "M");
                            String planTrafficTotal = new BigDecimal(MoneyUtil.formatString(queryCurrent.getMessage().getDataPlanTrafficTotal(), 2)).divide(new BigDecimal(1000)).toString();
                            String trafficTotal;
                            if (planTrafficTotal.contains(",")) {
                                trafficTotal = planTrafficTotal.replaceAll("[,]", "");
                            } else {
                                trafficTotal = planTrafficTotal;
                            }
                            main_total_flow_num.setText(MoneyUtil.decimalFormat.format(ceil(Double.parseDouble(trafficTotal), 2)) + "M");
                            String planTraffic = new BigDecimal(ceil(Double.parseDouble(queryCurrent.getMessage().getDataPlanTraffic()), 2)).divide(new BigDecimal(1000)).toString();
                            BigDecimal planTrafficTotalBg = new BigDecimal(planTrafficTotal);
                            BigDecimal mainUsableFlowNumBg = new BigDecimal(planTraffic);
                            String mainUsableFlowNum = planTrafficTotalBg.subtract(mainUsableFlowNumBg).toString();
                            String mainUsableFlow;
                            if (mainUsableFlowNum.contains(",")) {
                                mainUsableFlow = mainUsableFlowNum.replaceAll("[,]", "");
                            } else {
                                mainUsableFlow = mainUsableFlowNum;
                            }
                            main_usable_flow_num.setText(MoneyUtil.decimalFormat.format(ceil(Double.parseDouble(mainUsableFlow), 2)) + "M");
                            double extendTraffic = Double.parseDouble(queryCurrent.getMessage().getExtendTraffic());
                            String extendTra = new BigDecimal(ceil(extendTraffic, 2)).divide(new BigDecimal(1000)).toString();
                            food_fp_has_rest.setText(MoneyUtil.decimalFormat.format(ceil(Double.parseDouble(extendTra), 2)) + "M");
                            String packTrafficTotal = new BigDecimal(ceil(Double.parseDouble(queryCurrent.getMessage().getTrafficPackTrafficTotal()), 2)).divide(new BigDecimal(1000)).toString();
                            String packTrafficTo;
                            if (packTrafficTotal.contains(",")) {
                                packTrafficTo = packTrafficTotal.replaceAll("[,]", "");
                            } else {
                                packTrafficTo = packTrafficTotal;
                            }
                            double packetTotalNum=MoneyUtil.ceil(Double.parseDouble(packTrafficTo), 2);
                            packet_total_num.setText((MoneyUtil.decimalFormat.format(packetTotalNum)) + "M");
                            String trafficPackTra = new BigDecimal(MoneyUtil.ceil(Double.parseDouble(queryCurrent.getMessage().getTrafficPackTraffic()), 2)).divide(new BigDecimal(1000)).toString();
                            BigDecimal packTrafficTotalBg = new BigDecimal(packTrafficTotal);
                            BigDecimal trafficPackTraBg = new BigDecimal(trafficPackTra);
                            String trafficPackNum = packTrafficTotalBg.subtract(trafficPackTraBg).toString();
                            packet_usable_num.setText(MoneyUtil.decimalFormat.format(MoneyUtil.ceil(Double.parseDouble(trafficPackNum), 2)) + "M");
                            pb_progressbar.setProgress((int) (((Double.parseDouble(traAndFeeTotal) - Double.parseDouble(usedTraAndFeeTotaltra)) / Double.parseDouble(traAndFeeTotal)) * 100));
                            pb_progressbar1.setProgress((int) (((Double.parseDouble(trafficTotal) - Double.parseDouble(planTraffic)) / Double.parseDouble(trafficTotal)) * 100));
                            pb_progressbar2.setProgress((int) (((Double.parseDouble(packTrafficTo) - Double.parseDouble(trafficPackTra)) / Double.parseDouble(packTrafficTo)) * 100));
                            if(extendTra.equals("0.00")){
                                pb_progressbar3.setProgress(0);
                            }else {
                                pb_progressbar3.setProgress(100);
                            }
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
                } else {
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