package synertone.com.satnet.model;



/**
 * Created by snt1206 on 2017/2/20.
 */

public class QueryHistoryModel {
   HistoryMessage message;
    String result;
    String errCode;

    public HistoryMessage getMessage() {
        return message;
    }

    public void setMessage(HistoryMessage message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
