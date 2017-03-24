package wst.prj.es.pojo;/**
 * Created by shuting.wu on 2017/3/24.
 */

import lombok.Data;
import wst.prj.es.common.AggregationType;

import java.util.ArrayList;

/**
 * @author shuting.wu
 * @date 2017-03-24 16:31
 **/
@Data
public class AggregationParam {
    private boolean isGlobal; //是否包含查询条件
    private String aggName; //名称
    private String field; //统计字段
    private AggregationType type; //聚合类型
    private boolean isReverse = false; //nested字段查询要聚合上级字段，需设置为true
    private ArrayList<AggregationParam> nestedAggs = null; //嵌套聚合
}
