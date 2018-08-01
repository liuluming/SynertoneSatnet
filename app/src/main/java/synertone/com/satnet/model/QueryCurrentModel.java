package synertone.com.satnet.model;



/**
 * Created by snt1206 on 2017/2/20.
 */

public class QueryCurrentModel {
   CurrentMessage message;
    String result;
    String errCode;

    public CurrentMessage getMessage() {
        return message;
    }

    public void setMessage(CurrentMessage message) {
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
