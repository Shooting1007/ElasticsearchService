package wst.prj.es.service.impl;
/**
 * Created by shuting.wu on 2017/3/13.
 */

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wst.prj.es.common.StringUtil;
import wst.prj.es.common.PropertiesUtil;
import wst.prj.es.common.SearchOperator;
import wst.prj.es.common.SearchType;
import wst.prj.es.pojo.*;
import wst.prj.es.service.ISearchService;

import java.lang.reflect.Method;
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
     * @param returnFields
     * @param objectName
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/4/7 15:31
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, String[] returnFields, String objectName) {
        return query(indices,types,null,returnFields,"fabric",null,null,null,null);
    }

    /**
     * @param indices
     * @param types
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/4/7 15:39
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, String[] returnFields, String objectName, QueryParam[] queryParams) {
        return query(indices,types,null,returnFields,"fabric",queryParams,null,null,null);
    }

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
        return query(indices, types, pagination, returnFields, objectName, null, null,null,null);
    }


    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, SortParam[] sortParams) {
        return query(indices, types, pagination, returnFields, objectName, queryParams, null,sortParams,null);
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
        return query(indices, types, pagination, returnFields, objectName, queryParams, null,null,null);
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
        return query(indices, types, pagination, returnFields, objectName, queryParams, aggregationParams,null,null);
    }

    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param sortParams
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/29 13:58
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, SortParam[] sortParams) {
        return query(indices, types, pagination, returnFields, objectName, null, null,sortParams,null);
    }

    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param aggregationParams
     * @param sortParams
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/29 14:57
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, AggregationParam[] aggregationParams, SortParam[] sortParams) {
        return query(indices, types, pagination, returnFields, objectName, queryParams, aggregationParams,sortParams,null);
    }

    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param aggregationParams
     * @param sortParams
     * @param highLightFields    @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/29 16:19
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, AggregationParam[] aggregationParams, SortParam[] sortParams, String[] highLightFields) {
        return query(indices, types, pagination, returnFields, objectName, queryParams, aggregationParams, sortParams, highLightFields);
    }

    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param sortParams
     * @param highLightFields
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/29 16:21
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, SortParam[] sortParams, String[] highLightFields) {
        return query(indices, types, pagination, returnFields, objectName, queryParams, null, sortParams, highLightFields);
    }

    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param aggregationParams
     * @param highLightFields
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/29 16:20
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, AggregationParam[] aggregationParams, String[] highLightFields) {
        return query(indices, types, pagination, returnFields, objectName, queryParams, aggregationParams, null, highLightFields);
    }

    /**
     * @param indices
     * @param types
     * @param pagination
     * @param returnFields
     * @param objectName
     * @param queryParams
     * @param highLightFields
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/29 16:20
     **/
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, String[] highLightFields) {
        return query(indices, types, pagination, returnFields, objectName, queryParams, null, null, highLightFields);
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

    private String query(String[] indices, String[] types, Pagination pagination, String[] returnFields, String objectName, QueryParam[] queryParams, AggregationParam[] aggregationParams, SortParam[] sortParams,String[] hightLightFields) {
        try {
            long begin = new Date().getTime();

            // TODO: 2017/3/29  查询
            SearchRequestBuilder srb = new SearchRequestBuilder(ElasticClient.getTransportClient());
            srb.setIndices(indices).setTypes(types).addFields(returnFields);

            // TODO: 2017/3/29 转换查询参数
            Map<String, Object> builders = null;
            QueryBuilder queryBuilder = null;
            FilterBuilder filterBuilder = null;
            if (null != queryParams && queryParams.length > 0) {
                builders = this.parseQuery(queryParams);
                queryBuilder = (QueryBuilder) builders.get("query");
                filterBuilder = (FilterBuilder) builders.get("filter");
            }
            //TODO 2017/3/29  设置查询条件
            if (queryBuilder != null) {
                srb.setQuery(queryBuilder);
            }
            if (filterBuilder != null) {
                srb.setPostFilter(filterBuilder);
            }
            //TODO 2017/3/29  设置聚合参数
            if (aggregationParams != null && aggregationParams.length > 0) {
                for (AggregationParam aggParam : aggregationParams) {
                    srb.addAggregation(this.parseAggregation(aggParam));
                }
            }
            //TODO 2017/3/29  设置高亮
            if(hightLightFields != null && hightLightFields.length > 0){
                for(String field:hightLightFields){
                    srb.addHighlightedField(field);
                }
                //srb.addHighlightedField("_source");
                //srb.addHighlightedField("_all");
                srb.setHighlighterPreTags(PropertiesUtil.getStringByKey("highlighterPreTags"));
                srb.setHighlighterPostTags(PropertiesUtil.getStringByKey("highlighterPostTags"));
            }
            //TODO 2017/3/29  设置排序
            if(sortParams != null && sortParams.length > 0) {
                for(SortParam sortParam:sortParams) {
                    srb.addSort(sortParam.getField(),sortParam.getOrder());
                }
            }
            //TODO 2017/3/29  设置分页
            if(pagination == null) {
                pagination = new Pagination();
            }
            srb.setSize(pagination.getPageSize());
            srb.setFrom(pagination.getPageSize() * (pagination.getPageNo() - 1));

            SearchResponse scrollResp = srb.execute().actionGet();

            //TODO 2017/3/29  处理分页信息
            long totalCount = scrollResp.getHits().getTotalHits();
            Integer totalPage = (int) Math.ceil((double) totalCount / (double) pagination.getPageSize());
            pagination.setPageCount(scrollResp.getHits().getHits().length);
            pagination.setTotalCount(totalCount);
            pagination.setTotalPage(totalPage);
            return this.jsonResultByFields(scrollResp, objectName, pagination, begin);
        }catch(Exception e){
            LOGGER.error(e.getMessage(),e);
            return "ERROR";
        }
    }

    /**
     * @param queryParams
     * @return Map包含两个对象： query，filter
     * @Descrption  参数转换为查询对象
     * @author shuting.wu
     * @date 2017/3/24 16:22
     **/
    private Map<String, Object> parseQuery(QueryParam[] queryParams) {
        BoolQueryBuilder boolQueryBuilder = null;
        BoolFilterBuilder boolFilterBuilder = null;
        QueryBuilder queryBuilder;
        FilterBuilder filterBuilder;
        SearchOperator searchOperate;
        SearchType searchType;
        Object value;
        String field;
        for (QueryParam queryParam : queryParams) {
            filterBuilder = null;
            queryBuilder = null;
            field = queryParam.getQueryField();
            searchType = queryParam.getType();
            value = queryParam.getValue();
            switch (searchType) {
                case MATCH:
                    queryBuilder = QueryBuilders.matchQuery(field, value)
                            .analyzer(queryParam.getAnalyzer())
                            .boost(queryParam.getBoost());
                    break;
                case PHASE:
                    queryBuilder = QueryBuilders.matchQuery(field, value)
                            .type(MatchQueryBuilder.Type.PHRASE)
                            .analyzer(queryParam.getAnalyzer())
                            .boost(queryParam.getBoost());
                    break;
                case PREFIX:
                    queryBuilder = QueryBuilders.matchQuery(field, value)
                            .type(MatchQueryBuilder.Type.PHRASE_PREFIX)
                            .analyzer(queryParam.getAnalyzer())
                            .boost(queryParam.getBoost());
                    break;
                case TERM:
                    filterBuilder = FilterBuilders.termFilter(field, value);
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
                if (queryBuilder != null) {
                    queryBuilder = QueryBuilders.nestedQuery(field.substring(0, field.lastIndexOf(".")), queryBuilder);
                }
                if (filterBuilder != null) {
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
        builders.put("query", boolQueryBuilder);
        builders.put("filter", boolFilterBuilder);
        return builders;
    }

    /**
     * @param aggParam
     * @return
     * @Descrption 参数转换为聚合对象
     * @author shuting.wu
     * @date 2017/3/26 16:34
     **/
    private AbstractAggregationBuilder parseAggregation(AggregationParam aggParam) {
        AggregationBuilder aggBuilder = null;
        MetricsAggregationBuilder metricsBuilder = null;
        switch (aggParam.getType()) {
            case GLOBAL:
                aggBuilder = AggregationBuilders.global(aggParam.getAggName());
                break;
            case TERM:
                aggBuilder = AggregationBuilders.terms(aggParam.getAggName()).field(aggParam.getField())
                        .size(aggParam.getSize());
                break;
            case NESTED:
                if (aggParam.isReverse()) {
                    aggBuilder = AggregationBuilders.reverseNested(aggParam.getAggName()).path(aggParam.getField());
                } else {
                    aggBuilder = AggregationBuilders.nested(aggParam.getAggName()).path(aggParam.getField());
                }
                break;
            case IS_NULL:
                aggBuilder = AggregationBuilders.missing(aggParam.getAggName());
                break;
            case DATE_HISTOGRAM:
                aggBuilder = AggregationBuilders.dateHistogram(aggParam.getAggName()).field(aggParam.getField());
                break;
            case METRICS_MAX:
                metricsBuilder = AggregationBuilders.max(aggParam.getAggName()).field(aggParam.getField());
                break;
            case METRICS_MIN:
                metricsBuilder = AggregationBuilders.min(aggParam.getAggName()).field(aggParam.getField());
                break;
            case METRICS_SUM:
                metricsBuilder = AggregationBuilders.sum(aggParam.getAggName()).field(aggParam.getField());
                break;
            case METRICS_AVG:
                metricsBuilder = AggregationBuilders.avg(aggParam.getAggName()).field(aggParam.getField());
                break;
            case METRICS_STATS:
                metricsBuilder = AggregationBuilders.stats(aggParam.getAggName()).field(aggParam.getField());
                break;
            default:
                break;
        }
        if (metricsBuilder != null) {
            return metricsBuilder;
        }
        // TODO: 2017/3/26 下级聚合
        if (aggParam.getNestedAggs() != null && aggParam.getNestedAggs().length > 0) {
            for (AggregationParam subAggParam : aggParam.getNestedAggs()) {
                aggBuilder = aggBuilder.subAggregation(parseAggregation(subAggParam));
            }
        }
        return aggBuilder;
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

    /**
     * @param scrollResp
     * @param objectName
     * @param pagination
     * @param begin
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/27 13:45
     **/
    private String jsonResultByFields(SearchResponse scrollResp, String objectName, Pagination pagination, long begin) {
        StringBuffer result = new StringBuffer("{");
        // TODO: 2017/3/27 时间花销封装
        long took = scrollResp.getTookInMillis();
        result.append("\"took\":" + took + ",\"tooks\":" + (new Date().getTime() - begin));
        // TODO: 2017/3/27 封装分页信息
        String paginationStr = ",\"pagination\":{\"pageNo\":\"" + pagination.getPageNo() + "\","
                + "\"pageSize\":\"" + pagination.getPageSize() + "\","
                + "\"pageCount\":\"" + pagination.getPageCount() + "\","
                + "\"totalCount\":\"" + pagination.getTotalCount() + "\","
                + "\"totalPage\":\"" + pagination.getTotalPage() + "\""
                + "}";
        result.append(paginationStr);
        // TODO: 2017/3/27 封装查询结果
        SearchHit[] searchHits = scrollResp.getHits().getHits();
        Iterator<Map.Entry<String, SearchHitField>> fieldIterable = null;
        List<Object> resultList = new ArrayList<Object>();
        Map.Entry<String, SearchHitField> fieldEntry = null;
        SearchHitField searchHitField = null;
        Map<String, Object> entryMap = null;
        for (SearchHit searchHit : searchHits) {
            entryMap = null;
            fieldIterable = searchHit.getFields().entrySet().iterator();
            System.out.print("-------------------  searchHit.score:" + searchHit.getScore() + " ------------------ \n");
            while (fieldIterable.hasNext()) {
                fieldEntry = fieldIterable.next();
                searchHitField = fieldEntry.getValue();
                entryMap = this.entryToMap(searchHitField.getName(), searchHitField.getValues(), entryMap, false);
            }
            // TODO: 2017/3/29 高亮字段结果处理
            if(!searchHit.getHighlightFields().isEmpty()) {
                Iterator<HighlightField> iterable = searchHit.getHighlightFields().values().iterator();
                HighlightField hField;
                String highLightValue;
                while (iterable.hasNext()) {
                    hField = iterable.next();
                    highLightValue = "";
                    //System.out.print("highlightfield=" + hField.getName() + "\n");
                    if(hField.getFragments() != null && hField.getFragments().length > 0) {
                        for(Text text:hField.getFragments()){
                            highLightValue += text.string();
                        }
                    }
                    entryMap.put(hField.getName(),highLightValue);

                }
            }
            resultList.add(entryMap);

        }
        if (null != resultList) {
            result.append(",\"" + objectName + "\":" + JSONArray.fromObject(resultList).toString() + "");
        } else {
            result.append(",\"" + objectName + "\":[{}]");
        }

        // TODO: 2017/3/27 封装聚合结果
        String aggsStr = null;
        Aggregations aggs = scrollResp.getAggregations();
        if (aggs != null && aggs.asList().size() > 0) {
            Map<String, Object> aggsMap = this.aggsToMap(aggs, null);
            result.append(",\"" + "aggs" + "\":" + JSONObject.fromObject(aggsMap).toString() + "");
        }
        result.append("}");

        System.out.print("#######################################   搜索结果  #############################################" + "\n");
        System.out.print(result + "\n");
        return result.toString();
    }

    private Map<String, Object> aggsToMap(Aggregations aggs, Map<String, Object> aggMap) {
        Iterator<Terms.Bucket> bucketIterator = null;
        Map<String, Object> valueMap = null;
        ArrayList<Map<String, Object>> valueList = null;

        for (Aggregation agg : aggs) {
            if(aggMap == null) {
                aggMap = new HashedMap();
            }
            if (agg instanceof Terms) {
                bucketIterator = ((Terms) agg).getBuckets().iterator();
                while (bucketIterator.hasNext()) {
                    Terms.Bucket bucket = bucketIterator.next();
                    valueMap = new HashedMap();
                    valueMap.put("key", bucket.getKey());
                    valueMap.put("doc_count", bucket.getDocCount());
                    if (bucket.getAggregations() != null && bucket.getAggregations().asList().size() > 0) {
                        valueMap = aggsToMap(bucket.getAggregations(),valueMap);
                    }
                    if (valueList == null) {
                        valueList = new ArrayList<>();
                    }
                    valueList.add(valueMap);
                }
                aggMap.put(agg.getName(),valueList);
            } else {
                try {
                    Class<?> clazz = Class.forName(agg.getClass().getName());
                    Method getDocCount = clazz.getMethod("getDocCount");
                    Method getAggregations = clazz.getMethod("getAggregations");
                    valueMap = new HashedMap();
                    Object aggregations = getAggregations.invoke(agg);
                    if(aggregations != null && ((Aggregations)aggregations).asList().size() > 0) {
                        aggMap.put(agg.getName(),aggsToMap((Aggregations)aggregations, valueMap));
                    }
                    valueMap.put("doc_count",getDocCount.invoke(agg));
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return aggMap;
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
            entryMap.put(key, StringUtil.arrayToString((List) fieldValue));
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
