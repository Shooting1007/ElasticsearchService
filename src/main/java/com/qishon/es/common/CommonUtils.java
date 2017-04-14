package com.qishon.es.common;/**
 * Created by shuting.wu on 2017/4/11.
 */

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shuting.wu
 * @date 2017-04-11 17:31
 **/
public class CommonUtils {

    /**
     * @Descrption 获取客户端IP
     * @param request
     * @return
     * @author shuting.wu
     * @date 2017/4/12 11:45
    **/
    public static String getIpAddr(HttpServletRequest request) {
        String strClientIp = request.getHeader("x-forwarded-for");
        if(strClientIp == null || strClientIp.length() == 0 ||"unknown".equalsIgnoreCase(strClientIp))
        {
            strClientIp = request.getRemoteAddr();
        }else{
            String[] ipList = strClientIp.split(",");
            for(String strIp:ipList)
            {
                if(!("unknown".equalsIgnoreCase(strIp)))
                {
                    strClientIp = strIp;
                    break;
                }
            }
        }
        return strClientIp;
    }


}
