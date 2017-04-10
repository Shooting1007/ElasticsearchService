package com.qishon.es.service;

import com.qishon.es.pojo.AggregationParam;
import com.qishon.es.pojo.Pagination;
import com.qishon.es.pojo.QueryParam;
import com.qishon.es.pojo.SortParam;

/**
 * Created by shuting.wu on 2017/3/13.
 */
public interface ISearchService {

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param returnFields
     * @return
     * @author shuting.wu
     * @date 2017/4/7 15:31
    **/
    public String commonQuery(String[] indices, String[] types, String[] returnFields);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param returnFields
     * @param queryParams
     * @return
     * @author shuting.wu
     * @date 2017/4/7 15:39
    **/
    public String commonQuery(String[] indices, String[] types, String[] returnFields, QueryParam[] queryParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @return
     * @author shuting.wu
     * @date 2017/3/22 18:52
     **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination,String[] returnFields);


    /**
     * @Descrption
     * @param indices
     * @param types
     * @param queryParams
     * @param pagination
     *
     * @param returnFields
     * @return
     * @author shuting.wu
     * @date 2017/3/20 15:28
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination,String[] returnFields, QueryParam[] queryParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @param queryParams
     * @param aggregationParams
     * @return
     * @author shuting.wu
     * @date 2017/3/24 17:41
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields,  QueryParam[] queryParams, AggregationParam[] aggregationParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @param queryParams
     * @param sortParams
     * @return
     * @author shuting.wu
     * @date 2017/3/29 13:58
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields,  QueryParam[] queryParams, SortParam[] sortParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @param sortParams
     * @return
     * @author shuting.wu
     * @date 2017/3/29 13:58
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields,  SortParam[] sortParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @param queryParams
     * @param aggregationParams
     * @param sortParams
     * @return
     * @author shuting.wu
     * @date 2017/3/29 14:57
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggregationParam[] aggregationParams, SortParam[] sortParams);

    /**
     * @Descrption
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @param queryParams
     * @param aggregationParams
     * @param sortParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/3/29 16:19
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggregationParam[] aggregationParams, SortParam[] sortParams,String[] highLightFields);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @param queryParams
     * @param sortParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/3/29 16:21
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams,  SortParam[] sortParams,String[] highLightFields);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @param queryParams
     * @param aggregationParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/3/29 16:20
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggregationParam[] aggregationParams,String[] highLightFields);
    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     *
     * @param queryParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/3/29 16:20
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, String[] highLightFields);


    /*public String aggregate();

    public String popularKeywords();

    public String suggestKeywords();

    public String similarKeywords();*/

}
