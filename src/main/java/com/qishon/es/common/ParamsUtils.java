package com.qishon.es.common;/**
 * Created by shuting.wu on 2017/4/17.
 */

import com.qishon.es.enums.SearchType;
import com.qishon.es.enums.SortType;
import com.qishon.es.pojo.AggParam;
import com.qishon.es.pojo.QueryParam;
import com.qishon.es.pojo.SortParam;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author shuting.wu
 * @date 2017-04-17 14:09
 **/
public class ParamsUtils {

    /**
     * @param range format:field[lower,upper],field(,upper)...
     * @return
     * @throws Exception
     */
    public static QueryParam[] parseRange(String range) throws Exception {
        String[] ranges = RegexpUtils.getArrayByPattern(range, "pattern.param.range");
        String[] op;
        QueryParam rangeParam;
        QueryParam[] rangeParams = new QueryParam[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            String rp = ranges[i];
            rangeParam = new QueryParam();
            op = RegexpUtils.getArrayByPattern(rp, "pattern.param.rangeOp");
            rangeParam.setQueryField(StringUtils.substringBefore(rp, op[0]));
            QueryParam.RangeParam r = rangeParam.new RangeParam();
            r.setLower(StringUtils.substring(rp, rp.indexOf(op[0]) + 1, rp.indexOf(",")));
            r.setUpper(StringUtils.substring(rp, rp.indexOf(",") + 1, rp.indexOf(op[1])));
            if (op[0].equals("(")) {
                r.setIncludeLower(false);
            }
            if (op[1].equals(")")) {
                r.setIncludeUpper(false);
            }
            rangeParam.setRange(r);
            rangeParam.setType(SearchType.RANGE);
            rangeParams[i] = rangeParam;
        }
        return rangeParams;
    }

    /**
     * @param aggFields,format:f1,f2,....
     * @param aggSize
     * @return
     * @throws Exception
     */
    public static AggParam[] parseAggFields(String aggFields, String aggSize) throws Exception {
        AggParam[] aggParams;
        String[] fields = aggFields.split(",");
        aggParams = new AggParam[fields.length];
        AggParam aggParam;
        int iAggSize = 100;
        if (!StringUtils.isEmpty(aggSize)) {
            iAggSize = Integer.parseInt(aggSize);
        }
        for (int i = 0; i < fields.length; i++) {
            aggParam = new AggParam();
            aggParam.setAggName(fields[i]);
            aggParam.setField(fields[i]);
            aggParam.setSize(iAggSize);
            aggParams[i] = aggParam;
        }
        return aggParams;
    }

    /**
     * 解析字段排序，格式 field|sort,field|sort..
     * @param sort
     * @return
     * @throws Exception
     */
    public static SortParam[] parseSorts(String sort) throws Exception {
        SortParam[] sortParams = null;
        String[] sortArray = sort.split(",");
        if (sortArray != null && sortArray.length > 0) {
            sortParams = new SortParam[sortArray.length];
            SortParam sortParam;
            for (int i = 0; i < sortArray.length; i++) {
                sortParam = new SortParam();
                sortParam.setField(sortArray[i].split("\\|")[0]);
                sortParam.setOrder(SortOrder.fromString(sortArray[i].split("\\|")[1]));
                sortParams[i] = sortParam;
            }
        }
        return sortParams;
    }

    /**
     * 解析优先级排序，格式：field,value|field,value...
     * @param sortPriority
     * @return
     */
    public static SortParam[] parseSortPriority(String sortPriority) {
        SortParam[] sortParams = null;
        String[] sortArray = sortPriority.split("\\|");
        if (sortArray != null && sortArray.length > 0) {
            sortParams = new SortParam[sortArray.length];
            SortParam sortParam;
            for (int i = 0; i < sortArray.length; i++) {
                sortParam = new SortParam();
                sortParam.setSortType(SortType.SCRIPT);
                sortParam.setField(sortArray[i].split(",")[0]);
                sortParam.setPriorValue(sortArray[i].split(",")[1]);
                sortParams[i] = sortParam;
            }
        }
        return sortParams;
    }

    /**
     * 生成排序优先级的脚本，默认使用painless
     * @param field
     * @param paramKey
     * @return
     */
    public static String geteSortScript(String field,String paramKey) {
        StringBuffer result = new StringBuffer();
        result.append("doc");
        result.append("['").append(field).append("']");
        result.append(".values.contains");
        result.append("(").append("params.").append(paramKey).append(")");
        result.append("?0:1");
        return result.toString();
    }

}
