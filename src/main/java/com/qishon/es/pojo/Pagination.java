package com.qishon.es.pojo;/**
 * Created by shuting.wu on 2017/3/20.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author shuting.wu
 * @date 2017-03-20 14:19
 **/
@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class Pagination {
    int pageNo = 1; //当前页面
    int pageSize = 50; //每页条数
    int pageCount = 0; //当前页条数
    int totalPage = 0; //总页数
    long totalCount = 0; //总条数
}
