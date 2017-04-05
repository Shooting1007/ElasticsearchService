package wst.prj.es.common;/**
 * Created by shuting.wu on 2017/4/5.
 */

import net.sf.json.JSONArray;

import java.util.List;

/**
 * @author shuting.wu
 * @date 2017-04-05 13:53
 **/
public class JsonUtil {
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
}
