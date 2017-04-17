package com.qishon.es.common;/**
 * Created by shuting.wu on 2017/4/17.
 */

import com.qishon.es.enums.SearchType;
import com.qishon.es.pojo.AggParam;
import com.qishon.es.pojo.QueryParam;
import org.apache.commons.lang.StringUtils;

/**
 * @author shuting.wu
 * @date 2017-04-17 14:09
 **/
public class ParamsUtils {

    public static QueryParam[] parseRange(String range) throws Exception{
        String[] ranges = RegexpUtils.getArrayByPattern(range, "pattern.param.range");
        String[] op ;
        QueryParam rangeParam ;
        QueryParam[] rangeParams = new QueryParam[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            String rp = ranges[i];
            rangeParam = new QueryParam();
            op = RegexpUtils.getArrayByPattern(rp, "pattern.param.rangeOp");
            rangeParam.setQueryField(StringUtils.substringBefore(rp,op[0]));
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

    public static AggParam[] parseAggFields(String aggFields,String aggSize) throws Exception{
        AggParam[] aggParams ;
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

}
