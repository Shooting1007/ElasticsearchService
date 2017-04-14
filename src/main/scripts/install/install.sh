#!/bin/sh
groupadd es
useradd -g es es

mkdir -p /server
unzip es.zip -d /server
rm /server/es/lib/esservice-*.jar -rf


mkdir -p /logs/es
ES_HOME=/server/es
dos2unix $ES_HOME/bin/*

#配置搜素引擎IP
echo -e "\n 请输入搜索引擎IP： \c"
read ES_HOST
sed -i "s;^elasticsearch.host=.*;elasticsearch.host=$ES_HOST;g" $ES_HOME/bin/elasticsearch.properties

chown es:es /server/es -R
chown es:es /logs/es -R

#设置开机启动服务
cp es /etc/init.d/
chmod 751 /etc/init.d/es
chkconfig --add es
chkconfig --level 35 es on

#防火墙设置
#-A INPUT -m state --state NEW -m tcp -p tcp --dport 9100 -j ACCEPT
# 保存规则并重启IPTABLES
if [ -z "$(grep "9100" /etc/sysconfig/iptables)" ] ; then
	/sbin/iptables -A INPUT -p tcp --dport 9100 -j ACCEPT
	service iptables save
	service iptables restart
fi

# java -verbose -classpath .:/server/elastic/lib/*:/usr/local/jdk/lib/*.jar ElasticServer
#启动服务
service es restart

