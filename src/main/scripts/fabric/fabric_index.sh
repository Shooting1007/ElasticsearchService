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
        "my_fabric_info":{
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
                "fabricKey":{
                    "type":"long",
                    "index":"not_analyzed"
                },
                "fabricCode":{
                    "type":"string",
                    "analyzed":"code_analyzer"
                },
                "fabricName":{
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
                            "analyzer": "ik_max_word",
                            "boost": 10
                        }
                    }
                },
                "uploadTime":{
                    "type":"date",
                    "format":"yyyy-MM-dd HH:mm:ss"
                },
                "yarnCount":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "weight":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "width":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "density":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "serial":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "compsnDesc":{
                    "type":"string",
                    "analyzed":"ik_max_word"
                },
                "minPrice":{
                    "type":"float"
                },
                "thumb":{
                    "type":"string",
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
                    "type":"string",
                    "analyzed":"ik_max_word"
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
                "weave":{
                    "type":"multi_field",
                    "fields": {
                        "pinyin": {
                            "type": "string",
                            "store": "no",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "pinyin_analyzer"
                        },
                        "raw": {
                            "type":"string",
                            "analyzer":"strict_analyzer",
                            "boost": 5
                        }
                    }
                },
                "companyKey":{
                    "type":"string",
                    "analyzed":"strict_analyzer"
                },
                "customerTag":{
                    "type":"string",
                    "analyzed":"strict_analyzer"
                },
                "tech":{
                    "type":"multi_field",
                    "fields": {
                        "pinyin": {
                            "type": "string",
                            "store": "no",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "pinyin_analyzer"
                        },
                        "raw": {
                            "type":"string",
                            "analyzer":"strict_analyzer",
                            "boost": 2
                        }
                    }
                },
                "tag":{
                    "type":"string",
                    "analyzed":"strict_analyzer"
                },
                "sex":{
                    "type":"string",
                    "analyzed":"ik_and_word"
                },
                "compsn":{
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
                                    "index": "not_analyzed",
                                    "boost": 5
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
                                    "analyzer": "simple",
                                    "boost": 5
                                }
                            }
                        }
                    }
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
                                    "index": "not_analyzed",
                                    "boost": 5
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
                                    "analyzer": "simple",
                                    "boost": 5
                                }
                            }
                        }
                    }
                },
                "sampleClass":{
                    "properties":{
                        "name":{
                            "type":"multi_field",
                            "fields":{
                                "raw": {
                                    "type":"string",
                                     "index":"not_analyzed"
                                },
                                "word":{
                                    "type":"string",
                                    "analyzed":"ik_max_word"
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
                                    "analyzer": "simple",
                                    "boost": 2
                                }
                            }
                        }
                    }
                },
                "stepPrice":{
                    "type":"nested",
                    "properties":{
                        "priceName":{
                            "type":"string",
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
#修改mapping
#curl -XPOST http://localhost:9200/my_fabric/my_fabric_info/_mapping -d ''
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


