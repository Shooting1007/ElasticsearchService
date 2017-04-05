package wst.prj.es.pojo;/**
 * Created by shuting.wu on 2017/3/14.
 */

import lombok.Data;
import wst.prj.es.common.RangeOperator;
import wst.prj.es.common.SearchOperator;
import wst.prj.es.common.SearchType;

import java.util.Map;

/**
 * @author shuting.wu
 * @date 2017-03-2017/3/14 10:51
 **/
@Data
public class QueryParam {
    /**
     * 不提供more like 相关查询，等价于 多个term的should查询合并成一个bool
     * prefix查询会消耗资源--通常用于搜索框输入查询，要求速度要快，默认限制max_expansions
     * 通常，区间范围，状态(暂时还没想到还有什么) 使用过滤器
     * filter不计算相关性，且可以缓存，效率高于query，但是query会更精准一些
     */
    private Object value= null; //搜索值
    private SearchOperator operator = SearchOperator.OR; //多关键字搜索关系 AND，OR
    private SearchType type = null; //match,match_phrase,match_phrase_prefix...
    private String queryField = "_all"; //搜索字段,默认搜索所有字段,（1）可使用通配符匹配：*name,（2）加权查询：field^2.0
    private String analyzer = null; //定义queryString的分析器，默认用字段定义的分析器或者搜索类型
    private Map<String, Object> fuzzy = null; //允许多少长度的字符纠正:Number Param,AUTO ???  (1)key={fuzziniess,prefix_length}
    private int slop = 0; //灵活度，即移动多少次项使得文档和查询匹配
    private int expansions = 20; //限制匹配前缀扩展的词条数
    private boolean isNested = false; //是否数组
    private RangeParam range; //范围查询
    private float boost = 1.0f; //提升字段重要性，影响相关性分数，但仅为影响因素之一

    @Data
    public class RangeParam {
        private boolean isIncludeLower = true; //是否包含最小值
        private boolean isIncludeUpper = true; //是否包含最大值
        private Object lower; //最小值
        private Object upper; //最大值
    }

}
