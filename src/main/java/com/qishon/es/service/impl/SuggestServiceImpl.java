package com.qishon.es.service.impl;
/**
 * Created by shuting.wu on 2017/3/13.
 */

import com.qishon.es.common.ParamsUtils;
import com.qishon.es.common.PropertiesUtil;
import com.qishon.es.common.ResultUtils;
import com.qishon.es.pojo.ElasticClient;
import com.qishon.es.pojo.Pagination;
import com.qishon.es.pojo.SortParam;
import com.qishon.es.pojo.SuggestParam;
import com.qishon.es.service.ISuggestService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author shuting.wu
 * @date 2017-03-13 11:50
 **/
public class SuggestServiceImpl implements ISuggestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestServiceImpl.class);


    /**
     * @param origin
     * @param suggestParam
     * @param highLightTags
     * @return
     * @Descrption 默认使用competition，当查询结果为空，再使用 phrase，最后使用term
     * @author shuting.wu
     * @date 2017/5/2 14:52
     **/
    @Override
    public String completionSuggest(String origin, SuggestParam suggestParam, String highLightTags) throws Exception {
        long begin = new Date().getTime();
        String indics = PropertiesUtil.getStringByKey("es." + origin + ".index");
        String types = PropertiesUtil.getStringByKey("es." + origin + ".type");
        SuggestBuilder sb = new SuggestBuilder();
        String name = StringUtils.isEmpty(suggestParam.getName()) ? suggestParam.getField() : suggestParam.getName();
        // TODO: 2017/5/3 Completion
        CompletionSuggestionBuilder suggestionBuilder = new CompletionSuggestionBuilder(suggestParam.getField());
        if (!StringUtils.isEmpty(suggestParam.getAnalyzer())) {
            suggestionBuilder.analyzer(suggestParam.getAnalyzer());
        }
        suggestionBuilder.size(suggestParam.getSize());
        suggestionBuilder.text(suggestParam.getText());
        sb.addSuggestion(name, suggestionBuilder);
        SearchRequestBuilder srb = ElasticClient.getTransportClient().prepareSearch()
                .setIndices(indics.split(",")).setTypes(types.split(","));
        srb.suggest(sb);
        // TODO: 2017/5/3 result
        SearchResponse resp = srb.execute().actionGet();
        return jsonResult(resp, begin);
    }

    /**
     * @param origin
     * @param suggestParam
     * @param highLightTags
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/5/5 10:29
     **/
    @Override
    public String phraseSuggest(String origin, SuggestParam suggestParam, String highLightTags) throws Exception {
        long begin = new Date().getTime();
        String indics = PropertiesUtil.getStringByKey("es." + origin + ".index");
        String types = PropertiesUtil.getStringByKey("es." + origin + ".type");
        SuggestBuilder sb = new SuggestBuilder();
        String name = StringUtils.isEmpty(suggestParam.getName()) ? suggestParam.getField() : suggestParam.getName();
        // TODO: 2017/5/3  Phrase
        PhraseSuggestionBuilder suggestionBuilder = new PhraseSuggestionBuilder(suggestParam.getField());
        if (!StringUtils.isEmpty(suggestParam.getAnalyzer())) {
            suggestionBuilder.analyzer(suggestParam.getAnalyzer());
        }
        suggestionBuilder.size(suggestParam.getSize());
        suggestionBuilder.text(suggestParam.getText());
        sb.addSuggestion(name, suggestionBuilder);

        return null;
    }

    /**
     * @param origin
     * @param suggestParam
     * @param highLightTags
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/5/5 10:31
     **/
    @Override
    public String termSuggest(String origin, SuggestParam suggestParam, String highLightTags) throws Exception {
        long begin = new Date().getTime();
        String indics = PropertiesUtil.getStringByKey("es." + origin + ".index");
        String types = PropertiesUtil.getStringByKey("es." + origin + ".type");
        SuggestBuilder sb = new SuggestBuilder();
        String name = StringUtils.isEmpty(suggestParam.getName()) ? suggestParam.getField() : suggestParam.getName();
        // TODO: 2017/5/3 Term
        TermSuggestionBuilder suggestionBuilder = new TermSuggestionBuilder(suggestParam.getField());
        if (!StringUtils.isEmpty(suggestParam.getAnalyzer())) {
            suggestionBuilder.analyzer(suggestParam.getAnalyzer());
        }
        suggestionBuilder.size(suggestParam.getSize());
        suggestionBuilder.text(suggestParam.getText());
        sb.addSuggestion(name, suggestionBuilder);

        return null;
    }

    /**
     * @param origin
     * @param type
     * @param keywords
     * @param size
     * @param hightLightTags
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/5/4 15:06
     **/
    @Override
    public String suggestByQuery(String origin,String type, String keywords,int size, String hightLightTags) throws Exception {
        long begin = new Date().getTime();
        // TODO: 2017/5/10 参数处理
        String[] indices = PropertiesUtil.getStringByKey("es.suggest.index").split(",");
        String[] types = PropertiesUtil.getStringByKey("es.suggest.type").split(",");
        String[] returnFields = PropertiesUtil.getStringByKey("es.suggest.returnFields").split(",");
        String[] queryFields = PropertiesUtil.getStringByKey("es.suggest.queryFields").split(",");

        Pagination pagination = new Pagination();
        pagination.setPageSize(size);
        pagination.setPageNo(1);

        SortParam[] sortParams = ParamsUtils.parseSorts(PropertiesUtil.getStringByKey("es.suggest.sort"));

        // TODO: 2017/5/4 查询
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        for(String field:queryFields) {
            queryBuilder.should(QueryBuilders.prefixQuery(field,keywords));
        }
        queryBuilder.should(QueryBuilders.multiMatchQuery(keywords, queryFields));
        queryBuilder.minimumShouldMatch(1);
        if(!StringUtils.isEmpty(type)) {
            queryBuilder.filter(QueryBuilders.termQuery("type",type));
        }
        SearchRequestBuilder srb = ElasticClient.getTransportClient().prepareSearch()
                .setIndices(indices).setTypes(types);
        srb.setFetchSource(returnFields, null);
        srb.setQuery(queryBuilder);
        srb.setSize(pagination.getPageSize());
        srb.setFrom(pagination.getPageSize() * (pagination.getPageNo() - 1));
        //TODO 2017/3/29  设置排序
        if (sortParams != null && sortParams.length > 0) {
            for (SortParam sortParam : sortParams) {
                srb.addSort(sortParam.getField(), sortParam.getOrder());
            }
        }
        // TODO: 2017/5/10 查询结果处理
        SearchResponse scrollResp = srb.execute().actionGet();
        //TODO 2017/3/29  处理分页信息
        long totalCount = scrollResp.getHits().getTotalHits();
        Integer totalPage = (int) Math.ceil((double) totalCount / (double) pagination.getPageSize());
        pagination.setPageCount(scrollResp.getHits().getHits().length);
        pagination.setTotalCount(totalCount);
        pagination.setTotalPage(totalPage);
        return ResultUtils.jsonResultBySource(scrollResp, pagination, begin);
    }


    /**
     * @param resp
     * @return
     */
    private String jsonResult(SearchResponse resp, long begin) {
        StringBuffer result = new StringBuffer();
        long took = resp.getTook().millis();
        String suggest = null;
        if (resp.getSuggest().size() > 0) {
            suggest = parseSuggest(resp.getSuggest());
        }
        return result.toString();
    }

    /**
     * parse completionSuggest to Map result
     *
     * @param suggest
     * @return
     */
    private String parseSuggest(Suggest suggest) {
        System.out.print(suggest.toString());
        return null;
    }
}
