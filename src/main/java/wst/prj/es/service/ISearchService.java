package wst.prj.es.service;

import wst.prj.es.pojo.AggregationParam;
import wst.prj.es.pojo.Pagination;
import wst.prj.es.pojo.QueryParam;

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


    /*public String aggregate();

    public String popularKeywords();

    public String similarKeywords();*/


}