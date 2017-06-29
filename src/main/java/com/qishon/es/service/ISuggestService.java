package com.qishon.es.service;

import com.qishon.es.pojo.*;

/**
 * Created by shuting.wu on 2017/3/13.
 */
public interface ISuggestService {


    /**
     * @Descrption
     * @param origin
     * @param suggestParam
     * @param highLightTags
     * @return
     * @author shuting.wu
     * @date 2017/5/2 14:52
    **/
    String completionSuggest(String origin, SuggestParam suggestParam, String highLightTags)  throws Exception;

    /**
     * @Descrption
     * @param origin
     * @param  suggestParam
     * @param highLightTags
     * @return
     * @author shuting.wu
     * @date 2017/5/5 10:29
    **/
    String phraseSuggest(String origin, SuggestParam suggestParam, String highLightTags) throws Exception;

    /**
     * @Descrption
     * @param origin
     * @param suggestParam
     * @param highLightTags
     * @return
     * @author shuting.wu
     * @date 2017/5/5 10:31
    **/
    String termSuggest(String origin, SuggestParam suggestParam, String highLightTags) throws Exception;

    /**
     * @Descrption
     * @param origin
     * @param type
     * @param keywords
     * @param size
     * @param hightLightTags
     * @return
     * @author shuting.wu
     * @date 2017/5/4 15:06
    **/
    String suggestByQuery(String origin, String type, String keywords, int size, String hightLightTags)  throws Exception;


}
