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
            "refresh_interval":"5s",
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
                    "mapping_analyzer":{
                        "char_filter":[
                             "my_mapping"
                         ],
                        "filter":[
                            "lowercase"
                        ],
                        "tokenizer":"my_ngram_tokenizer"
                    },
                    "ik_and_word": {
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
            "_source":{
                "excludes":[
                    "lang"
                ]
            },
            "dynamic": false,
            "properties":{
                "lang":{
                    "type":"keyword",
                    "store":"no"
                },
                "companyKey":{
                    "type":"keyword",
                    "store":"yes"
                },
                "companyCode":{
                    "type":"text",
                    "analyzer":"mapping_analyzer"
                },
                "companyName":{
                    "type": "text",
                    "analyzer": "ik_max_word",
                    "boost": 10,
                    "fields": {
                        "pinyin": {
                            "type": "text",
                            "store": "no",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "pinyin_analyzer"
                        },
                        "raw": {
                            "type": "keyword",
                            "store": "yes"
                        }
                    }
                },
                "regTime":{
                    "type":"date",
                    "format":"yyyy-MM-dd HH:mm:ss"
                },
                "business":{
                    "type":"text",
                    "analyzer":"ik_max_word"
                },
                "contactPhone":{
                    "type":"keyword",
                    "store":"yes"
                },
                "contactName":{
                    "type":"keyword",
                    "store":"yes"
                },
                "contactAddr":{
                    "type":"text",
                    "analyzer":"ik_max_word"
                },
                "logo":{
                    "type":"keyword",
                    "store":"yes"
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
                    "type":"keyword",
                     "store":"yes"
                },
                "city":{
                    "type":"keyword",
                    "store":"yes"
                },
                "area":{
                    "type":"keyword",
                     "store":"yes"
                },
                "fabricClass":{
                    "properties":{
                        "name":{
                            "type": "text",
                            "analyzer": "ik_max_word",
                            "fields": {
                                "pinyin": {
                                    "type": "text",
                                    "store": "no",
                                    "term_vector": "with_positions_offsets",
                                    "analyzer": "pinyin_analyzer"
                                },
                                "raw": {
                                    "type": "keyword",
                                    "store": "yes"
                                }
                            }
                        },
                        "path":{
                            "type":"text",
                            "analyzer": "ik_max_word",
                            "fields": {
                                "tree": {
                                    "type":"text",
                                     "analyzer":"path_analyzer"
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