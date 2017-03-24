package wst.prj.es.common;/**
 * Created by shuting.wu on 2017/3/14.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author shuting.wu
 * @date 2017-03-2017/3/14 11:56
 **/
public class PropertiesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);
    private static ResourceBundle resource = ResourceBundle.getBundle("elasticsearch");

    public static String getStringByKey(String propertyKey) throws Exception {
        String propertyValue = null;
        try {
            propertyValue = resource.getString(propertyKey);
        } catch (MissingResourceException e1) {
            LOGGER.info("properties NotFound:" + propertyKey);
        }
        return propertyValue;
    }

    public static int getIntegerByKey(String propertyKey) {
        int propertyValue = Integer.MIN_VALUE;
        try {
            propertyValue = Integer.parseInt(resource.getString(propertyKey));
        } catch (MissingResourceException e1) {
            LOGGER.info("properties NotFound:" + propertyKey);
        }
        return propertyValue;
    }

    public static Map<String, Object> getPropertiesByPrefix(String prefix) {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        Iterator<String> iterator = resource.keySet().iterator();
        String propertyKey = null;
        while (iterator.hasNext()) {
            propertyKey = iterator.next();
            if (propertyKey.startsWith(prefix)) {
                propertyValues.put(propertyKey.replaceFirst(prefix,""), resource.getString(propertyKey));
            }
        }
        return propertyValues;
    }
}