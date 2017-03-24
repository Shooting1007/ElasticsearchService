#!/bin/sh
#插入单条数据
curl -XPUT http://localhost:9200/my_index/my_type/1 -d'
{
    "title":"第一条数据"
}
'
curl -XPUT http://localhost:9200/my_index/my_type/2 -d'
{
    "title":"第一条数据",
    "content":"测试是否自动修改mapper"
}
'
#批量插入数据
curl -XPOST http://localhost:9200/_bulk --data-binary @data.json
#示例
{ "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
{ "field1" : "value1" }
{ "delete" : { "_index" : "test", "_type" : "type1", "_id" : "2" } }
{ "create" : { "_index" : "test", "_type" : "type1", "_id" : "3" } }
{ "field1" : "value3" }
{ "update" : {"_id" : "1", "_type" : "type1", "_index" : "index1"} }
{ "doc" : {"field2" : "value2"} }