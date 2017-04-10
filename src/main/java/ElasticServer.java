/**
 * Created by shuting.wu on 2017/4/7.
 */

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import com.qishon.es.common.PropertiesUtil;
import com.qishon.es.servlet.SearchServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author shuting.wu
 * @date 2017-04-07 13:19
 **/
public class ElasticServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticServer.class);

    static Properties getSystemProps()
    {
        Properties props = new Properties();
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties");
        try {
            props.load(input);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }

        return props;
    }
    public static void main(String[] args) throws Exception{
        Properties props = getSystemProps();
        Object port = props.get("server.port");
        if(port == null)
        {
            throw new  Exception("port is empty...\n");
            //System.exit(1);
        }
        Server server = new Server(Integer.valueOf(port.toString()));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new SearchServlet()), "/search");

        server.start();
        server.join();
    }
}
