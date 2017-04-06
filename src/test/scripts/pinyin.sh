#!/bin/sh
#删除索引
curl -XDELETE http://localhost:9200/t_pinyin
#创建索引
curl -XPUT http://localhost:9200/t_pinyin -d '
{
    "settings":{
        "index":{
            "number_of_replicas":"0",
            "number_of_shards":"1",
            "refresh_integererval":"5s",
            "analysis" : {
                  "analyzer" : {
                      "pinyin_analyzer" : {
                           "type": "custom",
                           "tokenizer": "ik_smart",
                           "filter": ["pinyin_filter","word_delimiter"]
                      },
                      "pinyin_analyzer2" : {
                             "tokenizer" : "my_pinyin",
                             "filter" : ["standard","nGram"]
                       }
                  },
                  "tokenizer" : {
                      "my_pinyin" : {
                          "type" : "pinyin",
                          "first_letter" : "prefix",
                          "padding_char" : " ",
                          "keep_first_letter":true,
                          "keep_separate_first_letter" : true,
                          "keep_full_pinyin" : true,
                          "keep_original" : true,
                          "limit_first_letter_length" : 16,
                          "lowercase" : true,
                          "remove_duplicated_term" : true
                      },
                      "ik_smart": {
                        "type": "ik",
                        "use_smart": true
                      }
                  },
                  "filter": {
                    "pinyin_filter" :{
                          "type": "pinyin",
                          "first_letter" : "prefix",
                          "padding_char" : " ",
                          "keep_first_letter":false,
                          "keep_separate_first_letter" : false,
                          "keep_full_pinyin" : true,
                          "keep_original" : false,
                          "limit_first_letter_length" : 16,
                          "lowercase" : true,
                          "remove_duplicated_term" : true
                    }
                  }
              }
        }
    },
    "mappings":{
        "t_pinyin":{
            "properties" :{
                "fabricName":{
                    "type":"multi_field",
                    "fields": {
                        "pinyin": {
                            "type": "string",
                            "store": "no",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "pinyin_analyzer",
                            "boost": 10
                        },
                        "raw": {
                            "type": "string",
                            "store": "yes",
                            "analyzer": "keyword"
                        }
                    }
                },
                 "fabricName2":{
                    "type":"string",
                    "analyzed":"ik_max_word",
                    "fields":{
                        "pinyin":{
                            "type":"string",
                            "analyzed":"ik_pinyin"
                        }
                    },
                    "boost":"10"
                }
            }
        }
    }
}
'
#添加数据
curl -XPUT 'http://localhost:9200/t_pinyin/t_pinyin/1' -d '
{
"fabricName":"经典黑白条纹",
"fabricName2":"经典黑白条纹"
}
'
curl -XPUT 'http://localhost:9200/t_pinyin/t_pinyin/2' -d '
{
"fabricName":"水洗棉",
"fabricName2":"水洗棉"
}
'