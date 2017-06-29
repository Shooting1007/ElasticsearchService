package com.qishon.es.common;/**
 * Created by shuting.wu on 2017/4/5.
 */

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * @author shuting.wu
 * @date 2017-04-05 13:53
 **/
public class JsonStrUtils {
    public static ObjectMapper objectMapper;

    public static String arrayToString(List<Object> list) throws Exception {
        StringBuffer result = null;
        for (Object o : list) {
            if (result != null) {
                result.append(" " + o.toString());
            } else {
                result = new StringBuffer(o.toString());
            }

        }
        return result.toString();
    }

    /**
     * @param jsonStr
     * @param valueTypeRef
     * @return
     * @Descrption jsonStr转对象
     * @author shuting.wu
     * @date 2017/4/6 14:48
     **/
    public static <T> T jsonStrToList(String jsonStr, TypeReference<T> valueTypeRef) throws Exception {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        if (jsonStr == null || jsonStr == "") {
            return null;
        }
        return objectMapper.readValue(jsonStr, valueTypeRef);

    }

    /**
     * @param jsonStr
     * @param clazz
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/4/7 17:38
     **/
    public static <T> T jsonStrToBean(String jsonStr, Class<T> clazz) throws Exception {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        if (jsonStr == null || jsonStr == "") {
            return null;
        }
        return objectMapper.readValue(jsonStr, clazz);
    }

    /**
     * 对象转字符串
     * @param o
     * @return
     * @throws Exception
     */
    public static String objectToStr(Object o) throws Exception {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper.writeValueAsString(o);
    }

}
