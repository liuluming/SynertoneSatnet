package com.litesuits.common.utils;

public class TipUtils {
    public static String getFormatError(String message) {
        if(message!=null){
            if(message.contains("with username")){
                return "用户名或密码输入错误！";
            }
            if(message.contains("Connection refused")){
                return "连接被拒绝，请检查WIFI或端口！";
            }
            if(message.contains("connect timed out")||message.contains("No route to host")){
                return "连接超时，请检查WIFI或主机域名！";
            }
            if(message.contains("Network is unreachable")){
                return "网络不可用，请检查网络！";
            }
            if(message.contains("Unable to resolve host")){
                return "主机名错误，请检查主机名！";
            }
            if(message.contains("Software caused connection abort")){
                return "连接异常，请检查WIFI设置！";
            }
        }
        return "未知异常！";
    }
}
