package synertone.com.satnet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import synertone.com.satnet.R;
import synertone.com.satnet.model.OrderSubscription;

import static synertone.com.satnet.utils.JsonUtil.decimalFormat;


/**
 * Created by snt1206 on 2017/2/15.
 */

public class MyOrderAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<OrderSubscription> mOrderList;
    private Boolean isMode;
    public MyOrderAdapter(Context context, List<OrderSubscription> orderList) {
        this.mInflater = LayoutInflater.from(context);
        this.mOrderList=orderList;
        isMode=true;
    }

    @Override
    public int getCount() {
        return mOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.my_order_item, null);
            holder.tv_meal_name = convertView.findViewById(R.id.tv_meal_name);
            holder.tv_product_price = convertView.findViewById(R.id.tv_product_price);
            holder.tv_product_flow = convertView.findViewById(R.id.tv_product_flow);
            holder.tv_extra_price = convertView.findViewById(R.id.tv_extra_price);
            holder.tv_current_time = convertView.findViewById(R.id.tv_current_time);
            holder.tv_expiry_date = convertView.findViewById(R.id.tv_expiry_date);
            holder.tv_server_name = convertView.findViewById(R.id.tv_server_name);
            holder.ll_order_information = convertView.findViewById(R.id.ll_order_information);
            holder.ll_flow_meal = convertView.findViewById(R.id.ll_flow_meal);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_meal_name.setText(mOrderList.get(position).getProductName());
        String mProductAmount=decimalFormat.format(Double.parseDouble(mOrderList.get(position).getProductAmount()));
        holder.tv_product_price.setText(mProductAmount);
        String mProductTraffic=decimalFormat.format(Double.parseDouble(mOrderList.get(position).getProductTraffic()));
        holder.tv_product_flow.setText(mProductTraffic);
        if(mOrderList.get(position).getExtUnitPrice()!=null) {
            holder.tv_extra_price.setText(mOrderList.get(position).getExtUnitPrice() + "元/MB");
        }else{
            holder.tv_extra_price.setText("--");
        }
        holder.tv_current_time.setText(mOrderList.get(position).getValidTime());
        holder.tv_expiry_date.setText( mOrderList.get(position).getInvalidTime());
        holder.tv_server_name.setText(mOrderList.get(position).getServiceName());

        final LinearLayout ll_order_information = holder.ll_order_information;
        holder.ll_flow_meal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isMode) {
                    ll_order_information.setVisibility(View.VISIBLE);
                    isMode=false;
                }else {
                    ll_order_information.setVisibility(View.GONE);
                    isMode=true;
                }
            /*    if(isMode) {
                    ll_order_information.setVisibility(View.VISIBLE);
                }else {
                    ll_order_information.setVisibility(View.GONE);
                }
                isMode=!isMode;*/
              notifyDataSetChanged();
            }
        });


        return convertView;
    }

    public final class ViewHolder{
        public TextView tv_meal_name;
        public TextView tv_product_price;
        public TextView tv_current_time;
        public TextView tv_expiry_date;
        public TextView tv_product_flow;
        public TextView tv_extra_price;
        public TextView tv_server_name;
        public LinearLayout ll_flow_meal,ll_order_information;
    }
}

