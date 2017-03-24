package wst.prj.es.service.impl;/**
 * Created by shuting.wu on 2017/3/13.
 */

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wst.prj.es.pojo.ElasticsearchClientBak;
import wst.prj.es.service.IManageService;

import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author shuting.wu
 * @date 2017-03-2017/3/13 19:02
 **/
public class ManageServiceImpl implements IManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageServiceImpl.class);
    /**
     * @param client
     * @param indexName
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/13 19:00
     **/
    @Override
    public long createIndex(ElasticsearchClientBak client, String indexName, String typeName) throws Exception {
        ;
        if (null == client || null == client.getTransportClient()) {
            throw new Exception("未连接搜索引擎");
        }
        try{
            Client transportClient = client.getTransportClient();
            IndexResponse response = transportClient.prepareIndex(indexName, typeName, "1")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("user", "Eva")
                            .field("postDate", new Date())
                            .field("message", "trying out Elasticsearch")
                            .endObject()
                    )
                    .execute()
                    .actionGet();
            return response.getVersion();
        }catch (Exception e) {
            LOGGER.error("elasticsearch","create index fail",e);
            throw new Exception("创建索引失败");
        }finally {
            client.closeTransportClient();
        }


    }

    /**
     * @param client
     * @param indexName
     * @param type
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/13 19:01
     **/
    @Override
    public void createMapping(ElasticsearchClientBak client, String indexName, String type) {

    }

    /**
     * @param client
     * @param indexName
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/13 19:02
     **/
    @Override
    public void deleteIndex(ElasticsearchClientBak client, String indexName) {

    }

    /**
     * @param indexFrom
     * @param typeFrom
     * @param indexTo
     * @param typeTo
     * @return
     * @Descrption
     * @author shuting.wu
     * @date 2017/3/13 20:55
     **/
    @Override
    public void transferData(ElasticsearchClientBak client, String indexFrom, String typeFrom, String indexTo, String typeTo) {
        BulkProcessor bulkProcessor = BulkProcessor.builder(client.getTransportClient(),
        new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId,
                                   BulkRequest request) { }

            @Override
            public void afterBulk(long executionId,
                                  BulkRequest request,
                                  BulkResponse response) {  }

            @Override
            public void afterBulk(long executionId,
                                  BulkRequest request,
                                  Throwable failure) {  }
        })
        .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .build();
        bulkProcessor.add((IndexRequest) new IndexRequest("twitter", "tweet", "1").source(/* your doc here */));
    }


}
