package synertone.com.satnet.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016-5-26.
 */
public class MoneyUtil {
  /**
   *
   * @param money can't be a String
   * @return
   */
  public  static  String format(Object money){
    if(money instanceof String){
      return (String) money;
    }
    return  new DecimalFormat("##0.0").format(money);
  }
  /**
   *
   * @param money can't be a String
   * @return
   */
  public  static  String format2Decimal(Object money){
    if(money instanceof String){
      return (String) money;
    }
    return  new DecimalFormat("##0.00").format(money);
  }

  /**
   * 格式字符串形式的金额 四舍五入方式
   * @param money 金额值
   * @param scale 保留小数位数
   * @return
   */
  public static String formatString(String money,int scale){
    if(money==null||"".equals(money)){
      return "";
    }
    BigDecimal bigDecimal=new BigDecimal(money);
    bigDecimal=bigDecimal.setScale(scale,BigDecimal.ROUND_HALF_UP);
    return bigDecimal.toString();
  }
  /**
   * @Author snt1170
   * @Title 转换double类型的数据,解决BigDecimal误差问题
   * @param data   原始数据
   * @param digits 需要保留的小数据位数
   * @return 进一法到指定小数位数的数字
   */
  public static double ceil(double data, int digits){
    BigDecimal bd = new BigDecimal(data);

    //中间单位
    BigDecimal unit = new BigDecimal(Double.valueOf("10000000000"));

    BigDecimal tun = bd.multiply(unit);

    //转换后的数据
    double data_new = tun.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();

    bd = new BigDecimal(data_new);

    //还原数据
    bd = bd.divide(unit);

    //返回目标结果
    return bd.setScale(digits, BigDecimal.ROUND_UP).doubleValue();
  }
  public static DecimalFormat decimalFormat  = new DecimalFormat("#,##0.00");
}
