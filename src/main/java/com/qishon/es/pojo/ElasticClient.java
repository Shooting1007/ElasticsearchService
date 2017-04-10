package com.qishon.es.pojo;/**
 * Created by shuting.wu on 2017/3/14.
 */

import com.qishon.es.common.PropertiesUtil;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shuting.wu
 * @date 2017-03-2017/3/14 11:32
 **/
public class ElasticClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClient.class);
    // 创建私有对象
    private volatile static  TransportClient client ;

    static {
        try {
            String host = PropertiesUtil.getStringByKey("elasticsearch.host");
            int port = PropertiesUtil.getIntegerByKey("elasticsearch.port");
            Map<String, Object> setAttributes = new HashMap<String, Object>();
            setAttributes = PropertiesUtil.getPropertiesByPrefix("setting.");

            if(null != setAttributes.get("cluster.name")) {
                String clusterName = setAttributes.get("cluster.name").toString();
                // 设置client.transport.sniff为true来使客户端去嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中，
                setAttributes.remove("client.transport.sniff");
                setAttributes.put("client.transport.sniff","true");
            }
            Settings settings = ImmutableSettings.settingsBuilder().put(setAttributes).build();
            Class<?> clazz = Class.forName(TransportClient.class.getName());
            Constructor<?> constructor = clazz.getDeclaredConstructor(Settings.class);
            constructor.setAccessible(true);
            client = (TransportClient) constructor.newInstance(settings);
            client.addTransportAddress(new InetSocketTransportAddress(host, port));
        } catch (Exception e) {
            LOGGER.error("elasticsearch conent fail...",e);
        }
    }

    // 取得实例
    public static synchronized TransportClient getTransportClient() {
        return client;
    }
}
