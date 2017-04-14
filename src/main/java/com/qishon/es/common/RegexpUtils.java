package com.qishon.es.common;/**
 * Created by shuting.wu on 2017/4/13.
 */

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shuting.wu
 * @date 2017-04-13 12:47
 **/
public class RegexpUtils {

    /**
     *
     * @param line
     * @param patternType
     * @return
     * @throws Exception
     * @author shuting.wu
     * @date 2017/4/13 12:49
     */
    public static String[] getArrayByPattern(String line, String patternType) throws Exception {
        // 创建 Pattern 对象
        String pattern = PropertiesUtil.getStringByKey(patternType);
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        ArrayList<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group().toString());
        }
        if(list.size() > 0) {
            return list.toArray(new String[list.size()]);
        }
        return null;
    }

}
