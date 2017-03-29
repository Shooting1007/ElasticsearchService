package wst.prj.es.pojo;/**
 * Created by shuting.wu on 2017/3/29.
 */

import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author shuting.wu
 * @date 2017-03-29 13:48
 **/
@Data
public class SortParam {
    private String field;//排序字段
    private SortOrder order; //升序或者降序
}
