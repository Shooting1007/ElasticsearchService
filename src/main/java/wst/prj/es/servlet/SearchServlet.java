package wst.prj.es.servlet;/**
 * Created by shuting.wu on 2017/4/7.
 */

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import wst.prj.es.common.StringUtil;
import wst.prj.es.pojo.AggregationParam;
import wst.prj.es.pojo.Pagination;
import wst.prj.es.pojo.QueryParam;
import wst.prj.es.pojo.SortParam;
import wst.prj.es.service.ISearchService;
import wst.prj.es.service.impl.SearchServiceImpl;

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

    public SearchServlet() {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
            Pagination pagination = (!StringUtils.isEmpty(page)?StringUtil.jsonStrToBean(page,Pagination.class):null);

            //排序
            SortParam[] sortParams = StringUtil.jsonStrToList(sortParam, new TypeReference<SortParam[]>() {
            });

            //聚合
            AggregationParam[] aggParams = StringUtil.jsonStrToList(aggParam, new TypeReference<AggregationParam[]>() {
            });
            //查询条件
            QueryParam[] queryParams = StringUtil.jsonStrToList(queryParam, new TypeReference<QueryParam[]>() {
            });

            //TODO 搜索
            if(searchService == null) {
                searchService = new SearchServiceImpl();
            }
            String result = searchService.commonQuery(indices,types,pagination,returnFields,"fabric",queryParams,aggParams,sortParams,highLightFields);

            response.setContentType("text/html");
            response.setCharacterEncoding("utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(result);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
