package com.qishon.es.common;/**
 * Created by shuting.wu on 2017/5/4.
 */

import com.qishon.es.pojo.Pagination;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author shuting.wu
 * @date 2017-05-04 16:30
 **/
public class ResultUtils {


    /**
     * @param searchHit
     * @param parseMode
     * @return
     * @Descrption searchhit to map
     * @author shuting.wu
     * @date 2017/5/4 16:31
     **/
    public static Map<String, Object> parseHit(SearchHit searchHit, String parseMode) throws Exception {
        Map<String, Object> hitMap = new HashMap();
        if (StringUtils.equals(parseMode, "SOURCE")) {
            hitMap = JsonStrUtils.jsonStrToBean(searchHit.getSourceAsString(), hitMap.getClass());
        } else {
            Iterator<Map.Entry<String, SearchHitField>> fieldIterable;
            Map.Entry<String, SearchHitField> fieldEntry;
            SearchHitField searchHitField;
            Map<String, Object> entryMap;
            entryMap = null;
            fieldIterable = searchHit.getFields().entrySet().iterator();
            while (fieldIterable.hasNext()) {
                fieldEntry = fieldIterable.next();
                searchHitField = fieldEntry.getValue();
                entryMap = entryToMap(searchHitField.getName(), searchHitField.getValues(), entryMap, false);
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
     * @param fieldName
     * @param fieldValue
     * @param entryMap
     * @param isList
     * @return
     * @Descrption 自上而下生成结果集
     * @author shuting.wu
     * @date 2017/3/22 14:19
     **/
    public static Map<String, Object> entryToMap(String fieldName, Object fieldValue, Map<String, Object> entryMap, boolean isList) throws Exception {
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


    /**
     * @param aggs
     * @param aggMap
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/5/4 18:44
     **/
    public static Map<String, Object> aggsToMap(Aggregations aggs, Map<String, Object> aggMap) throws Exception {
        Iterator<Terms.Bucket> bucketIterator = null;
        Map<String, Object> valueMap = null;
        ArrayList<Map<String, Object>> valueList = null;

        for (Aggregation agg : aggs) {
            if (aggMap == null) {
                aggMap = new HashMap();
            }
            valueList = null;
            if (agg instanceof Terms) {
                bucketIterator = ((Terms) agg).getBuckets().iterator();
                while (bucketIterator.hasNext()) {
                    Terms.Bucket bucket = bucketIterator.next();
                    valueMap = new HashMap();
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
                Class<?> clazz = Class.forName(agg.getClass().getName());
                Method getDocCount = clazz.getMethod("getDocCount");
                Method getAggregations = clazz.getMethod("getAggregations");
                valueMap = new HashMap();
                Object aggregations = getAggregations.invoke(agg);
                if (aggregations != null && ((Aggregations) aggregations).asList().size() > 0) {
                    aggMap.put(agg.getName(), aggsToMap((Aggregations) aggregations, valueMap));
                }
                valueMap.put("doc_count", getDocCount.invoke(agg));
            }
        }
        return aggMap;
    }

    /**
     * @param scrollResp
     * @return Json格式的String字符串
     * @Descrption 从source中获取结果，未设置返回的fields参数
     * @author shuting.wu
     * @date 2017/3/21 14:04
     **/
    public static String jsonResultBySource(SearchResponse scrollResp, Pagination pagination, long begin) throws Exception {
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
        Map<String, Object> entryMap;
        for (SearchHit searchHit : searchHits) {
            entryMap = ResultUtils.parseHit(searchHit, "SOURCE");
            resultList.add(entryMap);
        }
        if (null != resultList) {
            result.append(",\"hits\":" + JsonStrUtils.objectToStr(resultList) + "");
        } else {
            result.append(",\"hits\":[{}]");
        }

        // TODO: 2017/3/27 封装聚合结果
        Aggregations aggs = scrollResp.getAggregations();
        if (aggs != null && aggs.asList().size() > 0) {
            Map<String, Object> aggsMap = ResultUtils.aggsToMap(aggs, null);
            result.append(",\"" + "aggs" + "\":" + JsonStrUtils.objectToStr(aggsMap) + "");
        }
        result.append("}");

        System.out.print("#######################################   搜索结果  #############################################" + "\n");
        System.out.print(result + "\n");
        return result.toString();
    }

    public static StringBuffer formatResult(String queryResult, String format) {
        StringBuffer result = new StringBuffer();
        if (!StringUtils.isEmpty(format)) {
            String code = "200";
            String msg = "操作成功！";
            if (queryResult.toLowerCase().contains("error")) {
                code = "999";
                msg = queryResult;
            }
            switch (format.toLowerCase()) {
                case "standard":
                    result.append("{\"result\":{\"code\":\"");
                    result.append(code);
                    result.append("\",\"msg\":\"");
                    result.append(msg);
                    result.append("\",\"info\":");
                    result.append(queryResult);
                    result.append("}}");
                    break;
                default:
                    result.append(queryResult);
                    break;
            }
        } else {
            result.append(queryResult);
        }
        return result;
    }

}
