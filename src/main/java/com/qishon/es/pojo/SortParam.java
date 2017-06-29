package com.qishon.es.pojo;/**
 * Created by shuting.wu on 2017/3/29.
 */

import com.qishon.es.enums.SortType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author shuting.wu
 * @date 2017-03-29 13:48
 **/
@Data @ToString @AllArgsConstructor @NoArgsConstructor
public class SortParam {
    private String field;//排序字段
    private SortOrder order; //升序或者降序
    private SortType sortType = SortType.FIELD; //默认按照字段排序
    private Object priorValue; //此值排序优先,第一期只支持String类型
}
