package wst.prj.es.service.impl;
/**
 * Created by shuting.wu on 2017/3/13.
 */

import net.sf.json.JSONArray;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wst.prj.es.common.SearchOperator;
import wst.prj.es.common.SearchType;
import wst.prj.es.pojo.AggregationParam;
import wst.prj.es.pojo.ElasticClient;
import wst.prj.es.pojo.Pagination;
import wst.prj.es.pojo.QueryParam;
import wst.prj.es.service.ISearchService;

import java.util.*;

/**
 * @author shuting.wu
 * @date 2017-03-13 11:50
 **/
public class SearchServiceImpl implements ISearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);


    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/22 18:52
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName) {
        return query(indices, types, pagination, returnFields, objectName, null,null);
    }


    /**
     * @param indices
     * @param types
     * @param queryParams
     * @param pagination
     * @param objectName
     * @param returnFields
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/20 15:28
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams) {
        return query(indices, types, pagination, returnFields, objectName, queryParams,null);
    }

    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param aggregationParams
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/24 17:41
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, AggregationParam[] aggregationParams) {
        return query(indices, types, pagination, returnFields, objectName, queryParams,aggregationParams);
    }

    /**
     * @param indices
     * @param types
     * @param queryParams
     * @param aggregationParams
     * @return
     * @Descrption 基础查询
     * @author shuting.wu
     * @date 2017/3/20 11:31
     **/

    private String query(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, AggregationParam[] aggregationParams) {
        long begin = new Date().getTime();
        //转换查询参数

        Map<String, Object> builders = null;
        QueryBuilder queryBuilder = null;
        FilterBuilder filterBuilder = null;
        if (null != queryParams && queryParams.length > 0) {
            builders = parseQuery(queryParams);
            queryBuilder = (QueryBuilder) builders.get("query");
            filterBuilder = (FilterBuilder) builders.get("filter");
        }

        //查询
        SearchRequestBuilder srb = new SearchRequestBuilder(ElasticClient.getTransportClient());
        srb.setIndices(indices).setTypes(types).addFields(returnFields);
        //设置分页
        srb.setSize(pagination.getPageSize());
        srb.setFrom(pagination.getPageSize() * (pagination.getPageNo() - 1));
        //设置查询条件
        if(queryBuilder != null) {
            srb.setQuery(queryBuilder);
        }
        if(filterBuilder != null) {
            srb.setPostFilter(filterBuilder);
        }

        SearchResponse scrollResp = srb.execute().actionGet();
        long totalCount = scrollResp.getHits().getTotalHits();
        long took = scrollResp.getTookInMillis();
        //页数判断，当总条数除以每页条数，有余数的话，总页数加1。
        Integer totalPage = (int) Math.ceil((double) totalCount / (double) pagination.getPageSize());
        pagination.setPageCount(scrollResp.getHits().getHits().length);
        pagination.setTotalCount(totalCount);
        pagination.setTotalPage(totalPage);

        return this.jsonResultByFields(scrollResp.getHits().getHits(), objectName, pagination, took, begin);
    }

    /**
     * @Descrption 参数转换为查询对象
     * @param queryParams
     * @return Map包含两个对象： query，filter
     * @author shuting.wu
     * @date 2017/3/24 16:22
    **/
    private Map<String, Object> parseQuery(QueryParam[] queryParams) {
        BoolQueryBuilder boolQueryBuilder = null;
        BoolFilterBuilder boolFilterBuilder = null;
        QueryBuilder queryBuilder = null;
        FilterBuilder filterBuilder = null;
        SearchOperator searchOperate = null;
        SearchType searchType = null;
        Object value = null;
        String field = null;
        for (QueryParam queryParam : queryParams) {
            filterBuilder = null;
            queryBuilder = null;
            field = queryParam.getQueryField();
            searchType = queryParam.getType();
            value = queryParam.getValue();
            switch (searchType) {
                case MATCH:
                    queryBuilder = QueryBuilders.matchQuery(field, value)
                            .analyzer(queryParam.getAnalyzer());
                    break;
                case PHASE:
                    queryBuilder = QueryBuilders.matchQuery(field, value)
                            .type(MatchQueryBuilder.Type.PHRASE)
                            .analyzer(queryParam.getAnalyzer());
                    break;
                case PREFIX:
                    queryBuilder = QueryBuilders.matchQuery(field, value)
                            .type(MatchQueryBuilder.Type.PHRASE_PREFIX)
                            .analyzer(queryParam.getAnalyzer());
                    break;
                case TERM:
                    filterBuilder = FilterBuilders.termFilter(field,value);
                    //queryBuilder = QueryBuilders.termQuery(field, value);
                    break;
                case RANGE:
                    filterBuilder = FilterBuilders.rangeFilter(field)
                            .from(queryParam.getRange().getLower())
                            .to(queryParam.getRange().getUpper())
                            .includeLower(queryParam.getRange().isIncludeLower())
                            .includeUpper(queryParam.getRange().isIncludeUpper());
                    break;
                default:
                    break;
            }
            if (queryParam.isNested()) {
                if (field.indexOf(".") == -1) {
                    continue;
                }
                if(queryBuilder != null) {
                    queryBuilder = QueryBuilders.nestedQuery(field.substring(0, field.lastIndexOf(".")), queryBuilder);
                }
                if(filterBuilder != null) {
                    filterBuilder = FilterBuilders.nestedFilter(field.substring(0, field.lastIndexOf(".")), filterBuilder);
                }
            }

            searchOperate = queryParam.getOperator();
            if (null != queryBuilder && null == boolQueryBuilder) {
                boolQueryBuilder = new BoolQueryBuilder();
            }
            if (null != filterBuilder && null == boolFilterBuilder) {
                boolFilterBuilder = new BoolFilterBuilder();
            }
            switch (searchOperate) {
                case AND:
                    if (null != queryBuilder) {
                        boolQueryBuilder.must(queryBuilder);
                    }
                    if (null != filterBuilder) {
                        boolFilterBuilder.must(filterBuilder);
                    }
                    break;
                case OR:
                    if (null != queryBuilder) {
                        boolQueryBuilder.should(queryBuilder);
                    }
                    if (null != filterBuilder) {
                        boolFilterBuilder.should(filterBuilder);
                    }
                    break;
                case NOT:
                    if (null != queryBuilder) {
                        boolQueryBuilder.mustNot(queryBuilder);
                    }
                    if (null != filterBuilder) {
                        boolFilterBuilder.mustNot(filterBuilder);
                    }
                    break;
                default:
                    break;
            }

        }
        Map<String, Object> builders = new HashMap<String, Object>();
        builders.put("query",boolQueryBuilder);
        builders.put("filter",boolFilterBuilder);
        return builders;
    }

    private void parseAggregation(AggregationParam[] aggParams,AggregationBuilder parentAggBuilder) {
        AggregationBuilder aggbuilder = null;
        for(AggregationParam aggParam:aggParams) {
            switch (aggParam.getType()) {
                case TERM:
                    aggbuilder = AggregationBuilders.terms(aggParam.getAggName()).field(aggParam.getField());
                    break;
                case NESTED:
                    aggbuilder = AggregationBuilders.nested(aggParam.getAggName()).path(aggParam.getField());
                    break;
                default:
                    break;
            }
            if(aggParam.getNestedAggs() != null && aggParam.getNestedAggs().size() > 0) {
                aggbuilder = parseAggregation(aggParam.getNestedAggs(),parentAggBuilder);
            }
        }
    }

    /**
     * @param searchHits
     * @return Json格式的String字符串
     * @Descrption 从source中获取结果，未设置返回的fields参数
     * @author shuting.wu
     * @date 2017/3/21 14:04
     **/
    private String jsonResultBySource(SearchHit[] searchHits) {
        StringBuffer result = null;
        for (SearchHit searchHit : searchHits) {
            if (null != result) {
                result.append(",");
            } else {
                result = new StringBuffer();
            }
            result.append(searchHit.getSourceAsString());
        }
        return result.toString();
    }

    private String jsonResultByFields(SearchHit[] searchHits, String objectName, Pagination pagination, long took, long begin) {
        StringBuffer result = new StringBuffer("{");
        Iterator<Map.Entry<String, SearchHitField>> fieldIterable = null;
        List<Object> resultList = new ArrayList<Object>();
        Map.Entry<String, SearchHitField> fieldEntry = null;
        SearchHitField searchHitField = null;
        Map<String, Object> entryMap = null;
        for (SearchHit searchHit : searchHits) {
            entryMap = null;
            fieldIterable = searchHit.getFields().entrySet().iterator();
            while (fieldIterable.hasNext()) {
                fieldEntry = fieldIterable.next();
                searchHitField = fieldEntry.getValue();
                //entryMap = (Map<String, Object>) this.entryToMapObject(searchHitField.getName(), searchHitField.getValues(), entryMap, 0);
                entryMap = this.entryToMap(searchHitField.getName(), searchHitField.getValues(), entryMap, false);
            }
            resultList.add(entryMap);
        }
        String paginationStr = "\"pagination\":{\"pageNo\":\"" + pagination.getPageNo() + "\","
                + "\"pageSize\":\"" + pagination.getPageSize() + "\","
                + "\"pageCount\":\"" + pagination.getPageCount() + "\","
                + "\"totalCount\":\"" + pagination.getTotalCount() + "\","
                + "\"totalPage\":\"" + pagination.getTotalPage() + "\""
                + "}";
        result.append(paginationStr);
        result.append(",\"took\":" + took + ",\"tooks\":" + (new Date().getTime() - begin));
        if (null != resultList) {
            result.append(",\"" + objectName + "\":" + JSONArray.fromObject(resultList).toString() + "");
        } else {
            result.append(",\"" + objectName + "\":[{}]");
        }
        result.append("}");
        System.out.print("#######################################   搜索结果  #############################################" + "\n");
        System.out.print(result + "\n");
        return result.toString();
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @param entryMap
     * @param isList
     * @return
     * @Descrption 自上而下生成结果集
     * @author shuting.wu
     * @date 2017/3/22 14:19
     **/
    private Map<String, Object> entryToMap(String fieldName, Object fieldValue, Map<String, Object> entryMap, boolean isList) {
        String key = fieldName;
        String key2 = null;
        Object entryValue = null;
        if (fieldName.indexOf(".") != -1) {
            key = fieldName.substring(0, fieldName.indexOf("."));
            key2 = fieldName.substring(fieldName.indexOf(".") + 1, fieldName.length());
            if (key2.indexOf(".") == -1) {
                isList = true;
            }
        }
        //判断是否存在节点
        if (null == entryMap) {
            entryMap = new HashMap<String, Object>();
        } else if (entryMap.containsKey(key)) {
            entryValue = entryMap.get(key);
        }

        if (isList) {
            //TODO 数组根节点
            List<Map<String, Object>> entryList = null;
            Map<String, Object> _map = null;
            int i = 0;
            if (null != entryValue) {
                entryList = (ArrayList<Map<String, Object>>) entryValue;
            } else {
                entryList = new ArrayList<Map<String, Object>>();
            }
            for (Object o : (List) fieldValue) {
                if (entryList.size() >= i + 1) {
                    entryList.get(i).put(key2, o);
                } else {
                    _map = new HashMap<String, Object>();
                    _map.put(key2, o);
                    entryList.add(_map);
                }
                i++;
            }
            entryMap.put(key, entryList);
        } else if (null != key2) {
            //TODO 数组父节点
            Map<String, Object> fieldMap = null;
            if (null != entryValue) {
                fieldMap = (Map<String, Object>) entryValue;
            }
            fieldMap = entryToMap(key2, fieldValue, fieldMap, isList);
            entryMap.put(key, fieldMap);
        } else {
            //TODO 非数组
            entryMap.put(key, ((List) fieldValue).get(0));
        }

        return entryMap;
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @return Obect
     * @Descrption 自下而上生成结果集（存在bug:多字段子集会被覆盖，暂未修复）
     * @author shuting.wu
     * @date 2017/3/21 19:16
     **/
    private Object entryToMapObject(String fieldName, Object fieldValue, Map<String, Object> parentMap, int isList) {
        Object object = new Object();
        List<Map<String, Object>> list = null;
        if (fieldName.indexOf(".") > 0) {
            String key = fieldName.substring(fieldName.lastIndexOf(".") + 1, fieldName.length());
            if (fieldValue instanceof Collection) {
                list = new ArrayList<>();
                Map<String, Object> _map = null;
                for (Object o : (ArrayList<Object>) fieldValue) {
                    _map = new HashMap<String, Object>();
                    _map.put(key, o);
                    list.add(_map);
                }
            }
            String parentField = fieldName.substring(0, fieldName.lastIndexOf("."));
            parentMap = (Map<String, Object>) entryToMapObject(parentField, list, parentMap, 1);
        } else {
            if (null == parentMap) {
                parentMap = new HashMap<String, Object>();
            }
            if (isList == 0) {
                parentMap.put(fieldName, ((ArrayList<Object>) fieldValue).get(0));
            } else {
                parentMap.put(fieldName, fieldValue);
            }
        }
        object = parentMap;
        return object;
    }

}
