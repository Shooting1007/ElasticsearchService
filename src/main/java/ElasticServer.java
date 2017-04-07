/**
 * Created by shuting.wu on 2017/4/7.
 */

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import wst.prj.es.common.PropertiesUtil;
import wst.prj.es.servlet.SearchServlet;

/**
 * @author shuting.wu
 * @date 2017-04-07 13:19
 **/
public class ElasticServer {
    public static void main(String[] args) throws Exception{
        Server server = new Server(PropertiesUtil.getIntegerByKey("server.port"));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new SearchServlet()), "/search");

        server.start();
        server.join();

        System.out.print("jetty server started !!! \n");
    }
}
