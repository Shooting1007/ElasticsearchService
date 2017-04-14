/**
 * Created by shuting.wu on 2017/4/11.
 */

import com.qishon.es.common.PropertiesUtil;

/**
 * @author shuting.wu
 * @date 2017-04-11 20:44
 **/
public class HelloWorld {
    public static void main(String[] args){
        System.out.print(System.getProperty("user.dir") + "\n");
        System.out.print(PropertiesUtil.getIntegerByKey("elasticsearch.host")  + "\n");
    }
}
