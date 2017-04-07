package wst.prj.es.servlet;/**
 * Created by shuting.wu on 2017/4/7.
 */

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author shuting.wu
 * @date 2017-04-07 14:07
 **/
public class TestServlet {
    private static final long serialVersionUID = 1L;

    public TestServlet(){

    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>" + "Hello World !" + "</h1>");
        response.getWriter().println("session=" + request.getSession(true).getId());
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
