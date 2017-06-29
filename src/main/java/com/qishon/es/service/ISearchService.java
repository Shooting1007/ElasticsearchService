package com.qishon.es.service;

import com.qishon.es.pojo.*;

/**
 * Created by shuting.wu on 2017/3/13.
 */
public interface ISearchService {

    /**
     * @param origin
     * @param pagination
     * @param returnFields
     * @param queryParams
     * @param aggParams
     * @param sortParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/4/12 21:08
     */
    public String commonQuery(String origin, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] highLightFields);


    /**
     * @param origin
     * @param pagination
     * @param returnFields
     * @param queryParams
     * @param aggParams
     * @param sortParams
     * @param highLightFields
     * @param highLightTags
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/4/17 10:24
     **/
    public String commonQuery(String origin, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] highLightFields, String highLightTags);


    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param queryParams
     * @param aggParams
     * @param sortParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/4/12 21:08
     */
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] highLightFields);

}
