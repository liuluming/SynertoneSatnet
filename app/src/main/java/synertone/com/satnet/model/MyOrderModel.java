package synertone.com.satnet.model;

/**
 * Created by snt1206 on 2017/2/18.
 */

public class MyOrderModel {
    OrderMessage message;
    String result;
    String errCode;
    public void setMessage(OrderMessage message) {
        this.message = message;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public OrderMessage getMessage() {
        return message;
    }

    public String getResult() {
        return result;
    }

    public String getErrCode() {
        return errCode;
    }
}
