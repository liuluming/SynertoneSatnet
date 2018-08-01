package synertone.com.satnet.model;

import java.util.List;

/**
 * Created by snt1206 on 2017/2/18.
 */

public class OrderMessage {
   String customerName;
    List<OrderSubscription> subscription;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<OrderSubscription> getSubscription() {
        return subscription;
    }

    public void setSubscription(List<OrderSubscription> subscription) {
        this.subscription = subscription;
    }
}
