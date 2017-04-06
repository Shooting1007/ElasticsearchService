#!/bin/sh
#删除索引
curl -XDELETE http://localhost:9200/my_fabric
#创建索引
curl -XPUT http://localhost:9200/my_fabric?pretty=true -d '
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
                      }
                }
            }
        }
    },
    "mappings":{
        "_default_":{
            "dynamic":"strict"
        },
        "event":{
            "_source":{
                "excludes":[
                    "lang"
                ]
            }
        },
        "my_fabric_info":{
            "_analyzer":{
                "path":"lang"
            },
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
                    "analyzed":"ik_max_word"
                },
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
                "serial":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "compsnDesc":{
                    "type":"string",
                    "analyzed":"ik_max_word"
                },
                "minPrice":{
                    "type":"double"
                },
                "thumb":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "sort":{
                    "type":"integer"
                },
                "fineFlag":{
                    "type":"integer"
                },
                "hasVfx":{
                    "type":"integer"
                },
                "available":{
                    "type":"integer"
                },
                "supplierKey":{
                    "type":"long",
                    "index":"not_analyzed"
                },
                "supplierName":{
                    "type":"string",
                    "analyzed":"ik_max_word"
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
                    "type":"string",
                    "analyzed":"ik_max_word"
                },
                "customerTag":{
                    "type":"string",
                    "analyzed":"not_analyzed"
                },
                "tech":{
                    "type":"string",
                    "index":"not_analyzed"
                },
                "tag":{
                    "type":"string",
                    "analyzed":"not_analyzed"
                },
                "sex":{
                    "type":"string",
                    "analyzed":"ik_max_word"
                },
                "composn":{
                    "properties":{
                        "name":{
                            "type":"multi_field",
                                "fields": {
                                    "pinyin": {
                                        "type": "string",
                                        "store": "no",
                                        "term_vector": "with_positions_offsets",
                                        "analyzer": "pinyin_analyzer",
                                        "boost": 5
                                    },
                                    "raw": {
                                        "type": "string",
                                        "store": "yes",
                                        "analyzer": "ik_max_word",
                                        "boost": 5
                                    }
                                }
                        },
                        "path":{
                            "type":"string",
                            "analyzed":"path_analyzer"
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
                                    "analyzer": "pinyin_analyzer",
                                    "boost": 2
                                },
                                "raw": {
                                    "type": "string",
                                    "store": "yes",
                                    "analyzer": "ik_max_word",
                                    "boost": 2
                                }
                            }
                        },
                        "path":{
                            "type":"string",
                            "analyzed":"path_analyzer"
                        }
                    }
                },
                "sampleClass":{
                    "properties":{
                        "name":{
                            "type":"string",
                            "analyzed":"ik_max_word"
                        },
                        "path":{
                            "type":"string",
                            "analyzed":"path_analyzer"
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
                            "type":"double"
                        }
                    }
                }
            }
        }
    }
}
'
#修改mapping
curl -XPOST http://localhost:9200/my_fabric/my_fabric_info/_mapping -d '

'

