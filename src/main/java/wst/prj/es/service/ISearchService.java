package wst.prj.es.service;

import wst.prj.es.pojo.AggregationParam;
import wst.prj.es.pojo.Pagination;
import wst.prj.es.pojo.QueryParam;
import wst.prj.es.pojo.SortParam;

/**
 * Created by shuting.wu on 2017/3/13.
 */
public interface ISearchService {

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @return
     * @author shuting.wu
     * @date 2017/3/22 18:52
     **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination,String[] returnFields,String objectName);


    /**
     * @Descrption
     * @param indices
     * @param types
     * @param queryParams
     * @param pagination
     * @param objectName
     * @param returnFields
     * @return
     * @author shuting.wu
     * @date 2017/3/20 15:28
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination,String[] returnFields,String objectName, QueryParam[] queryParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param aggregationParams
     * @return
     * @author shuting.wu
     * @date 2017/3/24 17:41
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, AggregationParam[] aggregationParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param sortParams
     * @return
     * @author shuting.wu
     * @date 2017/3/29 13:58
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, SortParam[] sortParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param sortParams
     * @return
     * @author shuting.wu
     * @date 2017/3/29 13:58
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, SortParam[] sortParams);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param aggregationParams
     * @param sortParams
     * @return
     * @author shuting.wu
     * @date 2017/3/29 14:57
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName,QueryParam[] queryParams, AggregationParam[] aggregationParams, SortParam[] sortParams);

    /**
     * @Descrption
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param aggregationParams
     * @param sortParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/3/29 16:19
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName,QueryParam[] queryParams, AggregationParam[] aggregationParams, SortParam[] sortParams,String[] highLightFields);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param sortParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/3/29 16:21
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName,QueryParam[] queryParams,  SortParam[] sortParams,String[] highLightFields);

    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param aggregationParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/3/29 16:20
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName,QueryParam[] queryParams, AggregationParam[] aggregationParams,String[] highLightFields);
    /**
     * @Descrption
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param highLightFields
     * @return
     * @author shuting.wu
     * @date 2017/3/29 16:20
    **/
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName,QueryParam[] queryParams, String[] highLightFields);


    /*public String aggregate();

    public String popularKeywords();

    public String suggestKeywords();

    public String similarKeywords();*/

}
