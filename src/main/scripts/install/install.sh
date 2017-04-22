#!/bin/sh
groupadd es
useradd -g es es
ES_HOME=/server/es

mkdir -p /server
unzip -o es.zip -d /server
rm $ES_HOME/lib/es-*.jar -rf
version=v1.0.0

mkdir -p /logs/es

dos2unix $ES_HOME/bin/*.properties

#配置搜素引擎IP
echo -e "\n 请输入搜索引擎IP： \c"
read ES_HOST
sed -i "s;^elasticsearch.host=.*;elasticsearch.host=$ES_HOST;g" $ES_HOME/bin/elasticsearch.properties 

chown es:es /server/es -R
chown es:es /logs/es -R

#设置开机启动服务
cp es /etc/init.d/
chown es:es /etc/init.d/es
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
#记录版本
echo -e `date "+%c"` install $version >> $ES_HOME/version
# java -verbose -classpath .:/server/elastic/lib/*:/usr/local/jdk/lib/*.jar ElasticServer
#启动服务
service es restart

