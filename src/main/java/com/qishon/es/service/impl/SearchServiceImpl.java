package com.qishon.es.service.impl;
/**
 * Created by shuting.wu on 2017/3/13.
 */

import com.qishon.es.common.JsonStrUtils;
import com.qishon.es.common.PropertiesUtil;
import com.qishon.es.enums.SearchOperator;
import com.qishon.es.enums.SearchType;
import com.qishon.es.pojo.*;
import com.qishon.es.service.ISearchService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author shuting.wu
 * @date 2017-03-13 11:50
 **/
public class SearchServiceImpl implements ISearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

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
    @Override
    public String commonQuery(String origin, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] highLightFields) {
        return query(origin, null, null, pagination, returnFields, queryParams, aggParams, sortParams, highLightFields, null);
    }

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
    @Override
    public String commonQuery(String origin, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] highLightFields, String highLightTags) {
        return query(origin, null, null, pagination, returnFields, queryParams, aggParams, sortParams, highLightFields, highLightTags);
    }

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
    @Override
    public String commonQuery(String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] highLightFields) {
        return query(null, indices, types, pagination, returnFields, queryParams, aggParams, sortParams, highLightFields, null);
    }

    /**
     * @param indices
     * @param types
     * @param queryParams
     * @param aggParams
     * @return
     * @Descrption 基础查询
     * @author shuting.wu
     * @date 2017/3/20 11:31
     **/

    private String query(String origin, String[] indices, String[] types, Pagination pagination, String[] returnFields, QueryParam[] queryParams, AggParam[] aggParams, SortParam[] sortParams, String[] highLightFields, String highLightTags) {
        try {
            long begin = new Date().getTime();

            if (indices == null || indices.length == 0) {
                indices = PropertiesUtil.getStringByKey("es." + origin + ".index").split(",");
                types = PropertiesUtil.getStringByKey("es." + origin + ".type").split(",");
            }

            if (returnFields == null || returnFields.length == 0) {
                returnFields = PropertiesUtil.getStringByKey("es." + origin + ".return").split(",");
            }

            // TODO: 2017/3/29  查询
//            SearchRequestBuilder srb = new SearchRequestBuilder(ElasticClient.getTransportClient());
//            srb.setIndices(indices).setTypes(types).addFields(returnFields);
            SearchRequestBuilder srb = ElasticClient.getTransportClient().prepareSearch()
                    .setIndices(indices).setTypes(types)
                    ;
            srb.setFetchSource(returnFields,null);

            // TODO: 2017/3/29 转换查询参数
            Map<String, BoolQueryBuilder> builders ;
            BoolQueryBuilder queryBuilder = null;
            BoolQueryBuilder filterBuilder = null;
            if (null != queryParams && queryParams.length > 0) {
                builders = this.parseQuery(queryParams, origin);
                if (builders.get("query") != null) {
                    queryBuilder =  builders.get("query");
                }
                if (builders.get("filter") != null) {
                    filterBuilder = builders.get("filter");
                }
            }
            String defaultFilters = PropertiesUtil.getStringByKey("es." + origin + ".defaultFilters");
            /*BoolFilterBuilder defaultFilterBuilder = null;
            if (!StringUtils.isEmpty(defaultFilters)) {
                defaultFilterBuilder = new BoolFilterBuilder();
                for (String df : defaultFilters.split(",")) {
                    defaultFilterBuilder.must(FilterBuilders.termFilter(StringUtils.substringBefore(df, ":"), StringUtils.substringAfter(df, ":")));
                }
            }*/
            BoolQueryBuilder defaultQueryBuilder = QueryBuilders.boolQuery();
            if (!StringUtils.isEmpty(defaultFilters)) {
                defaultQueryBuilder = new BoolQueryBuilder();
                for (String df : defaultFilters.split(",")) {
                    defaultQueryBuilder.must(QueryBuilders.termQuery(StringUtils.substringBefore(df, ":"), StringUtils.substringAfter(df, ":")));
                }
            }
            if(queryBuilder != null) {
                queryBuilder.filter(defaultQueryBuilder);
            } else {
                queryBuilder = defaultQueryBuilder;
            }
            //TODO 2017/3/29  设置查询条件
            /*srb.setQuery(QueryBuilders.filteredQuery(queryBuilder, defaultFilterBuilder));
            if (filterBuilder != null) {
                srb.setPostFilter(filterBuilder);
            }*/
            if(queryBuilder != null) {
                srb.setQuery(queryBuilder);
            }
            if(filterBuilder != null) {
                srb.setPostFilter(filterBuilder);
            }

            //TODO 2017/3/29  设置聚合参数
            if (aggParams != null && aggParams.length > 0) {
                for (AggParam aggParam : aggParams) {
                    srb.addAggregation(this.parseAggregation(aggParam, origin));
                }
            }

            //TODO 2017/3/29  设置高亮
            if (highLightFields != null && highLightFields.length > 0) {
                if (StringUtils.isEmpty(highLightTags)) {
                    highLightTags = PropertiesUtil.getStringByKey("highlighterPreTags")
                            + "," + PropertiesUtil.getStringByKey("highlighterPostTags");
                }
                HighlightBuilder hb = new HighlightBuilder().requireFieldMatch(false)/*.field("*")*/;
                for (String field : highLightFields) {
                    hb.fields().add(new HighlightBuilder.Field(field));
                }
                hb.preTags(highLightTags.split(",")[0]);
                hb.postTags(highLightTags.split(",")[1]);
                srb.highlighter(hb);
            }
            //TODO 2017/3/29  设置排序
            if (sortParams != null && sortParams.length > 0) {
                for (SortParam sortParam : sortParams) {
                    srb.addSort(sortParam.getField(), sortParam.getOrder());
                }
            }
            //TODO 2017/3/29  设置分页
            if (pagination == null) {
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
            return this.jsonResultBySource(scrollResp, pagination, begin);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR:" + e.getMessage();
        }
    }

    /**
     * @param queryParams
     * @return Map包含两个对象： query，filter
     * @Descrption 参数转换为查询对象
     * @author shuting.wu
     * @date 2017/3/24 16:22
     **/
    private Map<String,BoolQueryBuilder> parseQuery(QueryParam[] queryParams, String origin) throws Exception {
        BoolQueryBuilder boolQueryBuilder = null;
        BoolQueryBuilder boolFilterBuilder = null;
        QueryBuilder queryBuilder = null;
        QueryBuilder filterBuilder = null;
        SearchOperator searchOperate;
        SearchType searchType;
        Object value;
        String field;
        for (QueryParam queryParam : queryParams) {
            value = queryParam.getValue();
            searchType = queryParam.getType();
            if ((value == null || StringUtils.isEmpty(value.toString())) && searchType != SearchType.RANGE) {
                continue;
            }
            queryBuilder = null;
            field = queryParam.getQueryField();
            if (field.equals("_all")) {
                field = PropertiesUtil.getStringByKey("es." + origin + ".queryFields");
            }
            switch (searchType) {
                case MATCH:
                    if (field.indexOf(",") == -1) {
                        queryBuilder = QueryBuilders.matchQuery(field, value)
                                .analyzer(queryParam.getAnalyzer())
                                .boost(queryParam.getBoost());
                    } else {
                        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(value, field.split(","));
                        String boostFields = PropertiesUtil.getStringByKey("es.fabric.boostFields");
                        if (!StringUtils.isEmpty(boostFields)) {
                            for (String bf : boostFields.split(",")) {
                                multiMatchQueryBuilder.field(StringUtils.substringBefore(bf, "^"),
                                        Integer.parseInt(StringUtils.substringAfter(bf, "^")));
                            }
                        }
                        queryBuilder = multiMatchQueryBuilder;
                    }
                    break;
                case PHRASE:
                    queryBuilder = QueryBuilders.matchQuery(field, value)
                            //.type(MatchQueryBuilder.Type.PHRASE)
                            .analyzer(queryParam.getAnalyzer())
                            .boost(queryParam.getBoost());
                    break;
                case PREFIX:
                    queryBuilder = QueryBuilders.matchQuery(field, value)
                            //.type(MatchQueryBuilder.Type.PHRASE_PREFIX)
                            .analyzer(queryParam.getAnalyzer())
                            .boost(queryParam.getBoost());
                    break;
                case TERM:
                    if (PropertiesUtil.getStringByKey("es." + origin + ".rawFields").contains(field)) {
                        field = field + ".raw";
                    }
                    if (queryParam.isFilterMode()) {
                        //filterBuilder = FilterBuilders.termsFilter(field, value.toString().split(","));]
                        filterBuilder = QueryBuilders.termsQuery(field, value.toString().split(","));
//                        filterBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(field, value.toString().split(",")));
                    } else {
                        queryBuilder = QueryBuilders.termsQuery(field, value.toString().split(","));
//                        queryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(field, value.toString().split(",")));
                    }
                    //filterBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(field, value.toString().split(",")));
                    break;
                case RANGE:
                    /*RangeFilterBuilder rangeFilterBuilder = FilterBuilders.rangeFilter(field);
                    if (queryParam.getRange().getLower() != null && !StringUtils.isEmpty(queryParam.getRange().getLower().toString())) {
                        rangeFilterBuilder.from(queryParam.getRange().getLower())
                                .includeLower(queryParam.getRange().isIncludeLower());
                    }
                    if (queryParam.getRange().getUpper() != null && !StringUtils.isEmpty(queryParam.getRange().getUpper().toString())) {
                        rangeFilterBuilder.to(queryParam.getRange().getUpper())
                                .includeUpper(queryParam.getRange().isIncludeUpper());
                    }*/
                    //filterBuilder = rangeFilterBuilder;
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
                    if (queryParam.getRange().getLower() != null && !StringUtils.isEmpty(queryParam.getRange().getLower().toString())) {
                        rangeQueryBuilder.from(queryParam.getRange().getLower())
                                .includeLower(queryParam.getRange().isIncludeLower());
                    }
                    if (queryParam.getRange().getUpper() != null && !StringUtils.isEmpty(queryParam.getRange().getUpper().toString())) {
                        rangeQueryBuilder.to(queryParam.getRange().getUpper())
                                .includeUpper(queryParam.getRange().isIncludeUpper());
                    }
                    queryBuilder = rangeQueryBuilder;
                    break;
                default:
                    break;
            }
            if (queryParam.isNested()) {
                if (field.indexOf(".") == -1) {
                    continue;
                }
                if (queryBuilder != null) {
                    //queryBuilder = QueryBuilders.nestedQuery(field.substring(0, field.lastIndexOf(".")), queryBuilder);
                    queryBuilder = QueryBuilders.nestedQuery(field.substring(0, field.lastIndexOf(".")), queryBuilder, ScoreMode.Max);
                }
                if (filterBuilder != null) {
                    //filterBuilder = FilterBuilders.nestedFilter(field.substring(0, field.lastIndexOf(".")), filterBuilder);
                    filterBuilder = QueryBuilders.nestedQuery(field.substring(0, field.lastIndexOf(".")), queryBuilder, ScoreMode.Max);
                }
            }

            searchOperate = queryParam.getOperator();
            if (null != queryBuilder && null == boolQueryBuilder) {
                boolQueryBuilder = new BoolQueryBuilder();
            }
            if (null != filterBuilder && null == boolFilterBuilder) {
                boolFilterBuilder = new BoolQueryBuilder();
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
        Map<String, BoolQueryBuilder> builders = new HashMap<>();
        builders.put("query", boolQueryBuilder);
        builders.put("filter", boolFilterBuilder);
        return builders;
    }


    /**
     * @param aggParam
     * @return
     * @Descrption 参数转换为聚合对象 -- 5.3.0 MetrixAgg...与AggregationBuilder合并，去掉Metrix..Builder
     * @author shuting.wu
     * @date 2017/3/26 16:34
     **/
    private AggregationBuilder parseAggregation(AggParam aggParam, String origin) throws Exception {
        String field = aggParam.getField();
        if (StringUtils.isEmpty(aggParam.getAggName())) {
            aggParam.setAggName(aggParam.getField());
        }
        AggregationBuilder aggBuilder = null;

        if (PropertiesUtil.getStringByKey("es." + origin + ".rawFields").contains(field)) {
            field = field + ".raw";
        }
        switch (aggParam.getType()) {
            case GLOBAL:
                aggBuilder = AggregationBuilders.global(aggParam.getAggName());
                break;
            case TERM:
                aggBuilder = AggregationBuilders.terms(aggParam.getAggName()).field(field)
                        .size(aggParam.getSize());
                break;
            case NESTED:
                if (aggParam.isReverse()) {
                    aggBuilder = AggregationBuilders.reverseNested(aggParam.getAggName()).path(field);
                } else {
                   //aggBuilder = AggregationBuilders.nested(aggParam.getAggName()).path(field);
                    aggBuilder = AggregationBuilders.nested(aggParam.getAggName(),field);
                }
                break;
            case IS_NULL:
                aggBuilder = AggregationBuilders.missing(aggParam.getAggName());
                break;
            case DATE_HISTOGRAM:
                aggBuilder = AggregationBuilders.dateHistogram(aggParam.getAggName()).field(field);
                break;
            case METRICS_MAX:
                break;
            case METRICS_MIN:
                aggBuilder = AggregationBuilders.min(aggParam.getAggName()).field(field);
                break;
            case METRICS_SUM:
                aggBuilder = AggregationBuilders.sum(aggParam.getAggName()).field(field);
                break;
            case METRICS_AVG:
                aggBuilder = AggregationBuilders.avg(aggParam.getAggName()).field(field);
                break;
            case METRICS_STATS:
                aggBuilder = AggregationBuilders.stats(aggParam.getAggName()).field(field);
                break;
            default:
                break;
        }
        // TODO: 2017/3/26 下级聚合
        if (aggParam.getNestedAggs() != null && aggParam.getNestedAggs().length > 0) {
            for (AggParam subAggParam : aggParam.getNestedAggs()) {
                aggBuilder = aggBuilder.subAggregation(parseAggregation(subAggParam, origin));
            }
        }
        return aggBuilder;
    }

    private Map<String,Object> parseHit(SearchHit searchHit,String parseMode) throws Exception{
        Map<String, Object> hitMap = new HashMap();
        if(StringUtils.equals(parseMode,"SOURCE")) {
            hitMap = JsonStrUtils.jsonStrToBean(searchHit.getSourceAsString(),hitMap.getClass());
        } else {
            Iterator<Map.Entry<String, SearchHitField>> fieldIterable;
            Map.Entry<String, SearchHitField> fieldEntry ;
            SearchHitField searchHitField ;
            Map<String, Object> entryMap ;
            entryMap = null;
            fieldIterable = searchHit.getFields().entrySet().iterator();
            while (fieldIterable.hasNext()) {
                fieldEntry = fieldIterable.next();
                searchHitField = fieldEntry.getValue();
                entryMap = this.entryToMap(searchHitField.getName(), searchHitField.getValues(), entryMap, false);
            }
        }
        if (!searchHit.getHighlightFields().isEmpty()) {
            Iterator<HighlightField> iterable = searchHit.getHighlightFields().values().iterator();
            HighlightField hField;
            String highLightValue;
            while (iterable.hasNext()) {
                hField = iterable.next();
                highLightValue = "";
                //System.out.print("highlightfield=" + hField.getName() + "\n");
                if (hField.getFragments() != null && hField.getFragments().length > 0) {
                    for (Text text : hField.getFragments()) {
                        highLightValue += text.string();
                    }
                }
                hitMap.put(hField.getName(), highLightValue);

            }
        }
        return hitMap;
    }
    /**
     * @param scrollResp
     * @return Json格式的String字符串
     * @Descrption 从source中获取结果，未设置返回的fields参数
     * @author shuting.wu
     * @date 2017/3/21 14:04
     **/
    private String jsonResultBySource(SearchResponse scrollResp,Pagination pagination, long begin) throws Exception{
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
        List<Object> resultList = new ArrayList<>();
        Map<String, Object> entryMap ;
        for (SearchHit searchHit : searchHits) {
            entryMap = this.parseHit(searchHit,"SOURCE");
            resultList.add(entryMap);
        }
        if (null != resultList) {
            result.append(",\"hits\":" + JSONArray.fromObject(resultList).toString() + "");
        } else {
            result.append(",\"hits\":[{}]");
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
            if (aggMap == null) {
                aggMap = new HashedMap();
            }
            valueList = null;
            if (agg instanceof Terms) {
                bucketIterator = ((Terms) agg).getBuckets().iterator();
                while (bucketIterator.hasNext()) {
                    Terms.Bucket bucket = bucketIterator.next();
                    valueMap = new HashedMap();
                    valueMap.put("key", bucket.getKey());
                    valueMap.put("doc_count", bucket.getDocCount());
                    if (bucket.getAggregations() != null && bucket.getAggregations().asList().size() > 0) {
                        valueMap = aggsToMap(bucket.getAggregations(), valueMap);
                    }
                    if (valueList == null) {
                        valueList = new ArrayList<>();
                    }
                    valueList.add(valueMap);
                }
                aggMap.put(agg.getName(), valueList);
            } else {
                try {
                    Class<?> clazz = Class.forName(agg.getClass().getName());
                    Method getDocCount = clazz.getMethod("getDocCount");
                    Method getAggregations = clazz.getMethod("getAggregations");
                    valueMap = new HashedMap();
                    Object aggregations = getAggregations.invoke(agg);
                    if (aggregations != null && ((Aggregations) aggregations).asList().size() > 0) {
                        aggMap.put(agg.getName(), aggsToMap((Aggregations) aggregations, valueMap));
                    }
                    valueMap.put("doc_count", getDocCount.invoke(agg));
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
    private Map<String, Object> entryToMap(String fieldName, Object fieldValue, Map<String, Object> entryMap, boolean isList) throws Exception {
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
            entryMap.put(key, JsonStrUtils.arrayToString((List) fieldValue));
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
