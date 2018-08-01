package synertone.com.satnet.model;

/**
 * Created by snt1231 on 2017/3/31.
 */

public class AccountModel {
    private String subscriberId;
    private String mStrPassW;
    private String sessionToken;

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getmStrPassW() {
        return mStrPassW;
    }

    public void setmStrPassW(String mStrPassW) {
        this.mStrPassW = mStrPassW;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }


}
