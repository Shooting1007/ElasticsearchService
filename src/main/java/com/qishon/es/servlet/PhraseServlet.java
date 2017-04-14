package com.qishon.es.servlet;/**
 * Created by shuting.wu on 2017/4/11.
 */

import com.qishon.es.common.CommonUtils;
import com.qishon.es.common.JsonStrUtils;
import com.qishon.es.common.PropertiesUtil;
import com.qishon.es.pojo.AggParam;
import com.qishon.es.pojo.Pagination;
import com.qishon.es.pojo.QueryParam;
import com.qishon.es.pojo.SortParam;
import com.qishon.es.service.ISearchService;
import com.qishon.es.service.impl.SearchServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author shuting.wu
 * @date 2017-04-11 17:08
 **/
public class PhraseServlet {
    private static final long serialVersionUID = 1L;
    private static ISearchService searchService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhraseServlet.class);

    public PhraseServlet() {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        //TODO: 索引，类型和返回字段，分页，排序，统计，高亮
        try {
            //获取参数
            String keyword = request.getParameter("keyword");
            String origin = request.getParameter("origin");
            String index = PropertiesUtil.getStringByKey("elasticsearch."+ origin + ".index");
            String type = PropertiesUtil.getStringByKey("elasticsearch."+ origin + ".type");
            String returnField = request.getParameter("returnFields");
            String highLightField = request.getParameter("highLightFields");
            String queryParam = request.getParameter("queryParam");
            String aggParam = request.getParameter("aggParam");
            String page = request.getParameter("pagination");
            String sortParam = request.getParameter("sortParam");
            LOGGER.info("查询参数：" + CommonUtils.getIpAddr(request)
                    + "," + index + "," + type + "," + returnField
                    + "," + highLightField + "," + queryParam + "," + aggParam
                    + "," + page + "," + sortParam);
            // 参数处理
            if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type) || StringUtils.isEmpty(returnField)){
                throw new Exception("Error 999:parameters error(index，type，returnFields)");
            }
            String[] indices = index.split(",");
            String[] types = type.split(",");
            //返回字段
            String[] returnFields = returnField.split(",");
            //高亮
            String[] highLightFields = (StringUtils.isEmpty(highLightField)?null:highLightField.split(","));

            //分页
            Pagination pagination = (!StringUtils.isEmpty(page)? JsonStrUtils.jsonStrToBean(page,Pagination.class):null);

            //排序
            SortParam[] sortParams = (!StringUtils.isEmpty(sortParam)? JsonStrUtils.jsonStrToList(sortParam, new TypeReference<SortParam[]>() {
            }):null);

            //聚合
            AggParam[] aggParams = (!StringUtils.isEmpty(aggParam)? JsonStrUtils.jsonStrToList(aggParam, new TypeReference<AggParam[]>() {
            }):null);
            //查询条件
            QueryParam[] queryParams = (!StringUtils.isEmpty(queryParam)? JsonStrUtils.jsonStrToList(queryParam, new TypeReference<QueryParam[]>() {
            }):null);

            //TODO 搜索
            if(searchService == null) {
                searchService = new SearchServiceImpl();
            }
            String result = searchService.commonQuery(indices,types,pagination,returnFields,queryParams,aggParams,sortParams,highLightFields);
            response.getWriter().println(result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
