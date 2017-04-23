#!/bin/bash
#删除索引
curl -XDELETE http://localhost:9200/my_fabric_v1
#创建索引
curl -XPUT http://localhost:9200/my_fabric_v1?pretty=true -d '
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
                        "filter":[
                            "lowercase"
                        ],
                        "tokenizer":"my_ngram_tokenizer"
                    },
                    "strict_analyzer":{
                        "filter":[
                            "lowercase"
                        ],
                        "tokenizer":"whitespace"
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
        "my_fabric_info":{
            "_source":{
                "excludes":[
                    "lang"
                ]
            },
            "_all": { "enabled": false  },
            "dynamic":false,
            "properties":{
                "lang":{
                    "type":"text",
                    "index":"not_analyzed",
                    "store":"no"
                },
                "fabricKey":{
                    "type":"long",
                    "index":"not_analyzed"
                },
                "fabricCode":{
                    "type":"text",
                    "analyzer":"mapping_analyzer"
                },
                "fabricName":{
                    "type": "text",
                    "store": "yes",
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
                            "store": "no"
                        }
                    }
                },
                "uploadTime":{
                    "type":"date",
                    "format":"yyyy-MM-dd HH:mm:ss"
                },
                "yarnCount":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "weight":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "width":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "density":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "serial":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "compsnDesc":{
                    "type":"text",
                    "analyzer":"ik_max_word"
                },
                "minPrice":{
                    "type":"float"
                },
                "thumb":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "sort":{
                    "type":"integer"
                },
                "fineFlag":{
                    "type":"byte"
                },
                "hasVfx":{
                    "type":"byte"
                },
                "available":{
                    "type":"byte"
                },
                "supplierKey":{
                    "type":"long",
                    "index":"not_analyzed"
                },
                "supplierName":{
                    "type":"text",
                    "analyzer":"ik_max_word"
                },
                "authFlag":{
                    "type":"byte"
                },
                "province":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "city":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "area":{
                    "type":"text",
                    "index":"not_analyzed"
                },
                "weave":{
                    "type":"text",
                    "analyzer":"strict_analyzer",
                    "boost": 5,
                    "fields": {
                        "pinyin": {
                            "type": "text",
                            "store": "no",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "pinyin_analyzer"
                        },
                        "raw": {
                            "type":"keyword",
                            "store":"yes"
                        }
                    }
                },
                "companyKey":{
                    "type":"keyword",
                    "store":"true"
                },
                "customerTag":{
                    "type":"text",
                    "analyzer":"strict_analyzer"
                },
                "tech":{
                    "type":"text",
                    "analyzer":"strict_analyzer",
                    "fields": {
                        "pinyin": {
                            "type": "text",
                            "store": "no",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "pinyin_analyzer"
                        },
                        "raw": {
                            "type":"keyword",
                            "store":"yes"
                        }
                    }
                },
                "tag":{
                    "type":"text",
                    "analyzer":"strict_analyzer"
                },
                "sex":{
                    "type":"text",
                    "analyzer":"ik_and_word"
                },
                "compsn":{
                    "properties":{
                        "name":{
                            "type":"text",
                            "boost": 5,
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
                            "type": "text",
                            "store": "no",
                            "analyzer": "simple",
                            "boost": 5,
                            "fields": {
                                "tree": {
                                    "type":"text",
                                     "analyzer":"path_analyzer"
                                }
                            }
                        }
                    }
                },
                "fabricClass":{
                    "properties":{
                        "name":{
                            "type": "text",
                            "store": "yes",
                            "index": "not_analyzed",
                            "boost": 5,
                            "fields": {
                                "pinyin": {
                                    "type": "text",
                                    "store": "no",
                                    "term_vector": "with_positions_offsets",
                                    "analyzer": "pinyin_analyzer"
                                },
                                "raw": {
                                    "type": "keyword",
                                    "store": "yse"
                                }
                            }
                        },
                        "path":{
                            "type": "text",
                            "store": "no",
                            "analyzer": "simple",
                            "fields": {
                                "tree": {
                                    "type":"text",
                                     "analyzer":"path_analyzer"
                                }
                            }
                        }
                    }
                },
                "sampleClass":{
                    "properties":{
                        "name":{
                            "type":"text",
                            "analyzer":"ik_max_word",
                            "fields":{
                                "pinyin": {
                                    "type":"text",
                                     "analyzer":"pinyin_analyzer"
                                },
                                "raw":{
                                    "type":"keyword",
                                    "store":"yes"
                                }
                            }
                        },
                        "path":{
                             "type": "text",
                            "store": "no",
                            "analyzer": "simple",
                            "fields": {
                                "tree": {
                                    "type":"text",
                                     "analyzer":"path_analyzer"
                                }
                            }
                        }
                    }
                },
                "stepPrice":{
                    "type":"nested",
                    "properties":{
                        "priceName":{
                            "type":"text",
                            "analyzer":"ik_max_word"
                        },
                        "priceValue":{
                            "type":"float"
                        }
                    }
                }
            }
        }
    }
}
'
#update setting
curl -XPUT 'localhost:9200/my_fabric/_settings?pretty' -H 'Content-Type: application/json' -d'
{
    "index" : {
        "number_of_replicas" : 2
    }
}
'
#update analyze
#curl -XPOST 'localhost:9200/my_fabric/_close?pretty'
#curl -XPUT 'localhost:9200/my_fabric/_settings?pretty' -H 'Content-Type: application/json' -d'
#{
#  "analysis" : {
#    "analyzer":{
#      "content":{
#        "type":"custom",
#        "tokenizer":"whitespace"
#      }
#    }
#  }
#}
#'
#curl -XPOST 'localhost:9200/my_fabric/_open?pretty'

#修改mapping
#curl -XPOST http://localhost:9200/my_fabric/_mapping/my_fabric_info -d ''
#创建别名
curl -XPOST http://localhost:9200/_aliases -d'
{
    "actions" : [
        {"remove" : { "index" : "my_fabric_v1", "alias" : "my_fabric" }}
    ]
}'
curl -XPOST http://localhost:9200/_aliases -d'
{
    "actions" : [
        {"add" : { "index" : "my_fabric_v1", "alias" : "my_fabric" }}
    ]
}'


