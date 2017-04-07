package wst.prj.es.common;/**
 * Created by shuting.wu on 2017/4/5.
 */

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * @author shuting.wu
 * @date 2017-04-05 13:53
 **/
public class StringUtil {
    public static ObjectMapper objectMapper;

    public static String arrayToString(List<Object> list) {
        StringBuffer result = null;
        for(Object o: list) {
            if(result != null) {
                result.append(" " + o.toString());
            } else{
                result = new StringBuffer(o.toString());
            }

        }
        return result.toString();
    }

    /**
     * @Descrption jsonStr转对象
     * @param jsonStr
     * @param valueTypeRef
     * @return
     * @author shuting.wu
     * @date 2017/4/6 14:48
    **/
    public static <T> T jsonStrToList(String jsonStr, TypeReference<T> valueTypeRef) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        try {
            return objectMapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @Descrption
     * @param jsonStr
     * @param clazz
     * @return
     * @author shuting.wu
     * @date 2017/4/7 17:38
    **/
    public static <T> T jsonStrToBean(String jsonStr,Class<T> clazz) throws Exception{
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
