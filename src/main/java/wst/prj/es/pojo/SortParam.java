package wst.prj.es.pojo;/**
 * Created by shuting.wu on 2017/3/29.
 */

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
}
