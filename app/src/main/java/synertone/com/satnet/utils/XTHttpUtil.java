package synertone.com.satnet.utils;

public class XTHttpUtil {

	private final static String TAG = "XTHttpUtil";
	// 总的
	public final static String XTADDRESS = "http://192.168.80.1:8005/api";// http請求地址
	/* 登录 */
	public final static String POST_LOGIN_ADDRESS = "http://192.168.80.1:9005/ucms/auth";
	/* 我的订购 */
	public final static String POST_MY_ORDER = "http://192.168.80.1:9005/ucms/order/query";
	/* 实时账单查询 */
	public final static String POST_QUERY_ACCOUNT_ADDRESS = "http://192.168.80.1:9005/ucms/rtbill/query";
	/* 历史账单查询 */
	public final static String POST_QUERY_HISTORY_ADDRESS = "http://192.168.80.1:9005/ucms/hisbill/query";
	/* 流量查询 */
	public final static String POST_QUERY_FLOW= "http://192.168.80.1:9005/ucms/rtbill/query";
	/* 主动下线*/
	public final static String POST_LOGIN_OUT = "http://192.168.80.1:9005/ucms/logout";
	/* 查询当前鉴权状态*/
	public final static String POST_TOKEN_STATUS = "http://192.168.80.1:9005/ucms/token/status";
	//密码修改
	public final static String POST_MOREADV_MODIPASS="http://192.168.80.1:9005/ucms/modifpass";
	//查看最新版本
    public final static String GET_PEOPLE_UPSYS_CHECK=XTADDRESS+"/people/upsys/check";
	//开始升级
    public final static String GET_PEOPLE_UPSYS_START=XTADDRESS+"/people/upsys/start";

    
}
