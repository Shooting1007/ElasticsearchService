#!/bin/bash
#删除索引
curl -XDELETE http://localhost:9200/my_company_v1
#创建索引
curl -XPUT http://localhost:9200/my_company_v1?pretty=true -d '
{
    "settings":{
        "index":{
            "number_of_replicas":"0",
            "number_of_shards":"1",
            "refresh_integererval":"5s",
            "analysis":{
                "analyzer":{
                    "en":{
                        "tokenizer":"standard",
                        "filter":[
                            "asciifolding",
                            "lowercase",
                            "ourEnglishFilter"
                        ]
                    },
                    "code_analyzer":{
                        "char_filter":[
                             "my_mapping"
                         ],
                        "filter":[
                            "lowercase"
                        ],
                        "tokenizer":"my_ngram_tokenizer"
                    },
                    "mapping_analyzer":{
                        "char_filter":[
                            "my_mapping"
                        ],
                        "tokenizer":"standard"
                    },
                    "path_analyzer":{
                        "tokenizer":"path_hierarchy"
                    },
                    "pinyin_analyzer" : {
                        "type": "custom",
                        "tokenizer": "ik_smart",
                        "filter": ["my_pinyin","word_delimiter"]
                    }
                },
                "filter":{
                    "ourEnglishFilter":{
                        "type":"kstem"
                    },
                    "my_pinyin" :{
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
                },
                "tokenizer" : {
                      "ik_smart": {
                        "type": "ik",
                        "use_smart": true
                      },
                      "my_ngram_tokenizer" : {
                          "type" : "nGram",
                          "min_gram": "2",
                          "max_gram": "3",
                          "token_chars": []
                      }
                },
                "char_filter":{
                    "my_mapping":{
                        "type":"mapping",
                        "mappings":[
                            "免运费 => 0",
                            "免邮 => 0",
                            "包邮 => 0",
                            ", =>  "
                        ]
                    }
                }
            }
        }
    },
    "mappings":{
        "my_company_info":{
            "_analyzer":{
                "path":"lang"
            },
            "_source":{
                "excludes":[
                    "lang"
                ]
            },
            "dynamic":"strict",
            "properties":{
                "lang":{
                    "type":"string",
                    "index":"not_analyzed",
                    "store":"no"
                },
                "companyKey":{
                    "type":"long",
                    "index":"not_analyzed"
                },
                "companyCode":{
                    "type":"string",
                    "analyzed":"code_analyzer"
                },
                "companyName":{
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
                            "analyzer": "ik_max_word"
                        }
                    }
                },
                "regTime":{
                    "type":"date",
                    "format":"yyyy-MM-dd HH:mm:ss"
                },
                "business":{
                    "type":"string",
                    "analyzed":"ik_max_word"
                },
                "contactPhone":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "contactName":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "contactAddr":{
                    "type":"string",
                    "analyzed":"ik_max_word"
                },
                "logo":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "sort":{
                    "type":"integer"
                },
                "fineFlag":{
                    "type":"byte"
                },
                "available":{
                    "type":"byte"
                },
                "authFlag":{
                    "type":"byte"
                },
                "province":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "city":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "area":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "fabricClass":{
                    "properties":{
                        "name":{
                            "type":"multi_field",
                            "fields": {
                                "pinyin": {
                                    "type": "string",
                                    "store": "no",
                                    "term_vector": "with_positions_offsets",
                                    "analyzer": "pinyin_analyzer"
                                },
                                "raw": {
                                    "type": "string",
                                    "store": "yes",
                                    "index": "not_analyzed"
                                }
                            }
                        },
                        "path":{
                            "type":"multi_field",
                            "fields": {
                                "tree": {
                                    "type":"string",
                                     "analyzed":"path_analyzer"
                                },
                                "raw": {
                                    "type": "string",
                                    "store": "no",
                                    "analyzer": "ik"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
'
#创建别名
curl -XPOST http://localhost:9200/_aliases -d'
{
    "actions" : [
        {"remove" : { "index" : "my_company_v1", "alias" : "my_company" }}
    ]
}'
curl -XPOST http://localhost:9200/_aliases -d'
{
    "actions" : [
        {"add" : { "index" : "my_company_v1", "alias" : "my_company" }}
    ]
}'