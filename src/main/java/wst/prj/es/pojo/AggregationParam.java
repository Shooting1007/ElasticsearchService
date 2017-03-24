package wst.prj.es.pojo;/**
 * Created by shuting.wu on 2017/3/24.
 */

import lombok.Data;

/**
 * @author shuting.wu
 * @date 2017-03-24 16:31
 **/
@Data
public class AggregationParam {
    private boolean isGlobal; //是否包含查询条件
    private String aggName; //名称
    private String field; //统计字段
    private QueryParam queryParam;//过滤条件

}
