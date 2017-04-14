package com.qishon.es.service;

import com.qishon.es.pojo.AggParam;
import com.qishon.es.pojo.Pagination;
import com.qishon.es.pojo.QueryParam;
import com.qishon.es.pojo.SortParam;

/**
 * Created by shuting.wu on 2017/3/13.
 */
public interface ISearchService {

    /**
     *
     * @param origin
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param queryParams
     * @param aggParams
     * @param sortParams
     * @param hightLightFields
     * @return
     * @author shuting.wu
     * @date 2017/4/12 21:08
     */
    public String commonQuery(String origin,String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] hightLightFields);

    /**
     *
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param queryParams
     * @param aggParams
     * @param sortParams
     * @param hightLightFields
     * @return
     * @author shuting.wu
     * @date 2017/4/12 21:08
     */
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] hightLightFields);

    /**
     *
     * @param indices
     * @param types
     * @param value
     * @param size
     * @author shuting.wu
     * @date 2017/4/11 17:23
     */
    //public String phraseQuery(String[] indices, String[] types,String value,int size);

    /*public String aggregate();

    public String popularKeywords();

    public String suggestKeywords();

    public String similarKeywords();*/

}
