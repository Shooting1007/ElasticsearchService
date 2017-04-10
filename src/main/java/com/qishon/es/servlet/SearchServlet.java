package com.qishon.es.servlet;/**
 * Created by shuting.wu on 2017/4/7.
 */

import com.qishon.es.common.StringUtil;
import com.qishon.es.pojo.AggregationParam;
import com.qishon.es.pojo.Pagination;
import com.qishon.es.pojo.QueryParam;
import com.qishon.es.pojo.SortParam;
import com.qishon.es.service.ISearchService;
import com.qishon.es.service.impl.SearchServiceImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author shuting.wu
 * @date 2017-04-07 13:26
 **/
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static ISearchService searchService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServlet.class);

    public SearchServlet() {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        //TODO: 索引，类型和返回字段，分页，排序，统计，高亮
        try {
            //获取参数
            String index = request.getParameter("indics");
            String type = request.getParameter("types");
            String returnField = request.getParameter("returnFields");
            String highLightField = request.getParameter("highLightFields");
            String queryParam = request.getParameter("queryParam");
            String aggParam = request.getParameter("aggParam");
            String page = request.getParameter("pagination");
            String sortParam = request.getParameter("sortParam");
            LOGGER.info("查询参数：" + getIpAddr(request),index,type,returnField,highLightField,queryParam,aggParam,page,sortParam);
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
            Pagination pagination = (!StringUtils.isEmpty(page)? StringUtil.jsonStrToBean(page,Pagination.class):null);

            //排序
            SortParam[] sortParams = (!StringUtils.isEmpty(sortParam)?StringUtil.jsonStrToList(sortParam, new TypeReference<SortParam[]>() {
            }):null);

            //聚合
            AggregationParam[] aggParams = (!StringUtils.isEmpty(aggParam)?StringUtil.jsonStrToList(aggParam, new TypeReference<AggregationParam[]>() {
            }):null);
            //查询条件
            QueryParam[] queryParams = (!StringUtils.isEmpty(queryParam)?StringUtil.jsonStrToList(queryParam, new TypeReference<QueryParam[]>() {
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

    public String getIpAddr(HttpServletRequest request) {
        String strClientIp = request.getHeader("x-forwarded-for");
        if(strClientIp == null || strClientIp.length() == 0 ||"unknown".equalsIgnoreCase(strClientIp))
        {
            strClientIp = request.getRemoteAddr();
        }else{
            String[] ipList = strClientIp.split(",");
            for(String strIp:ipList)
            {
                if(!("unknown".equalsIgnoreCase(strIp)))
                {
                    strClientIp = strIp;
                    break;
                }
            }
        }
        return strClientIp;
    }
}
