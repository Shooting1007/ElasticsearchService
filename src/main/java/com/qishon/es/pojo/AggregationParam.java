package com.qishon.es.pojo;/**
 * Created by shuting.wu on 2017/3/24.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.qishon.es.common.AggregationType;

/**
 * @author shuting.wu
 * @date 2017-03-24 16:31
 **/
@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class AggregationParam {
    private String aggName; //名称
    private String field; //统计字段
    private AggregationType type; //聚合类型
    private boolean isReverse = false; //nested子聚合，聚合字段为父级字段，需设置为true
    private AggregationParam[]  nestedAggs = null; //嵌套聚合
    private int size = 100; //限制长度,用于TERM聚合
}
