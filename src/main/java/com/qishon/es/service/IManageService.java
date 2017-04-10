package com.qishon.es.service;

import com.qishon.es.pojo.ElasticsearchClientBak;

/**
 * Created by shuting.wu on 2017/3/13.
 */
public interface IManageService {

    /**
     * @Descrption
     * @param client
     * @param indexName
     * @return long 成功返回版本号
     * @author shuting.wu
     * @date 2017/3/13 19:00
    **/
    public long createIndex(ElasticsearchClientBak client, String indexName, String typeName) throws Exception;

    /**
     * @Descrption
     * @param client
     * @param indexName
     * @param type
     * @return
     * @author shuting.wu
     * @date 2017/3/13 19:01
    **/
    public void createMapping(ElasticsearchClientBak client, String indexName, String type);

    /**
     * @Descrption
     * @param client
     * @param indexName
     * @return
     * @author shuting.wu
     * @date 2017/3/13 19:02
    **/
    public void deleteIndex(ElasticsearchClientBak client, String indexName) throws Exception;;

    /**
     * @Descrption
     * @param indexFrom
     * @param typeFrom
     * @param indexTo
     * @param typeTo
     * @return
     * @author shuting.wu
     * @date 2017/3/13 20:55
    **/
    public void transferData(ElasticsearchClientBak client, String indexFrom, String typeFrom, String indexTo, String typeTo) ;

}
