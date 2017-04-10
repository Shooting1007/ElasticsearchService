#!/bin/bash
curl -XPOST '127.0.0.1:9200/_cluster/settings' -d '{
    "transient" :
        {
            "index.indexing.slowlog.threshold.index.warn": "10s",
            "index.indexing.slowlog.threshold.index.info": "5s",
            "index.indexing.slowlog.threshold.index.debug": "2s",
            "index.indexing.slowlog.threshold.index.trace": "500ms",
            "index.indexing.slowlog.level": "info",
            "index.indexing.slowlog.source": "1000",
            "indices.memory.index_buffer_size": "20%"
        }

}'