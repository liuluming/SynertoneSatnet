package synertone.com.satnet.model;

/**
 * Created by snt1206 on 2017/2/18.
 */

public class OrderSubscription {
    String invalidTime;
    String validTime;
    String isMaster;
    String productName;
    String productAmount;
    String serviceName;
    String ExtUnitPrice;
    String productTraffic;


    public String getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(String invalidTime) {
        this.invalidTime = invalidTime;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(String isMaster) {
        this.isMaster = isMaster;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(String productAmount) {
        this.productAmount = productAmount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getProductTraffic() {
        return productTraffic;
    }

    public void setProductTraffic(String productTraffic) {
        this.productTraffic = productTraffic;
    }

    public String getExtUnitPrice() {
        return ExtUnitPrice;
    }

    public void setExtUnitPrice(String extUnitPrice) {
        ExtUnitPrice = extUnitPrice;
    }
}
