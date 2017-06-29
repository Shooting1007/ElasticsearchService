package com.qishon.es.servlet;/**
 * Created by shuting.wu on 2017/4/11.
 */

import com.qishon.es.common.CommonUtils;
import com.qishon.es.common.ResultUtils;
import com.qishon.es.service.ISuggestService;
import com.qishon.es.service.impl.SearchServiceImpl;
import com.qishon.es.service.impl.SuggestServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author shuting.wu
 * @date 2017-04-11 17:08
 **/
@WebServlet(name = "HistorySuggestServlet", urlPatterns = "/suggest/history")
public class HistorySuggestServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(HistorySuggestServlet.class);
    private static final String C_SUGGEST = "history";
    @Resource
    private static ISuggestService suggestService;
    public HistorySuggestServlet() {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        StringBuffer result;
        String queryResult;
        //获取参数
        String origin = request.getParameter("origin");
        String highLightFlag = request.getParameter("highLightFlag");
        String keywords = request.getParameter("keywords");
        String size = request.getParameter("size");
        String format = request.getParameter("format");
        LOGGER.info("查询参数：IP={},origin={},highLightFlag={},keywords={},size={},format={}",
                CommonUtils.getIpAddr(request), origin, highLightFlag, keywords,size,format);
        try {
            // 参数处理
            if (StringUtils.isEmpty(origin) || StringUtils.isEmpty(keywords)) {
                throw new Exception("Error 999:parameters error(origin,keywords)");
            }
            //TODO 搜索
            if (suggestService == null) {
                suggestService = new SuggestServiceImpl();
            }
            queryResult = suggestService.suggestByQuery(origin,C_SUGGEST,keywords,StringUtils.isEmpty(size)?10:Integer.getInteger(size),highLightFlag);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            queryResult = "Error:" + e.getMessage();
        }
        result = ResultUtils.formatResult(queryResult,format);
        response.getWriter().println(result);
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
