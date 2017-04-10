#!/bin/sh
mkdir -p /server
unzip elastic.zip -d /server

ES_HOME=/server/elastic
#配置搜素引擎IP
echo -e "\n 请输入搜索引擎IP： \c"
read ES_HOST
sed -i 's;^elasticsearch.host=.*;elasticsearch.host=$ES_HOST;g' $ES_HOME/conf/elasticsearch.properties

#设置开机启动服务
cp elastic /etc/init.d/
chmod 751 /etc/init.d/elastic
chkconfig --add elastic
chkconfig --level 35 elastic on

#启动服务
service elastic restart

