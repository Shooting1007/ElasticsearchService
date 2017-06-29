#!/bin/sh
groupadd ess
useradd -g ess ess
ESS_HOME=/services/ess

mkdir -p /services
unzip -o ess.zip -d /services
rm $ESS_HOME/lib/ess-*.jar -rf
version=v1.0.0

mkdir -p /var/logs/ess

dos2unix $ESS_HOME/bin/*.properties

#配置搜素引擎IP
echo -e "\n 请输入搜索引擎IP： \c"
read ES_HOST
sed -i "s;^elasticsearch.host=.*;elasticsearch.host=$ES_HOST;g" $ESS_HOME/bin/elasticsearch.properties 

chown ess:ess /services/ess -R
chown ess:ess /var/logs/ess -R

#设置开机启动服务
cp ess /etc/init.d/
chown ess:ess /etc/init.d/ess
chmod 751 /etc/init.d/ess
chkconfig --add ess
chkconfig --level 35 ess on

#防火墙设置
#-A INPUT -m state --state NEW -m tcp -p tcp --dport 9100 -j ACCEPT
# 保存规则并重启IPTABLES
if [ -z "$(grep "9100" /etc/sysconfig/iptables)" ] ; then
	/sbin/iptables -A INPUT -p tcp --dport 9100 -j ACCEPT
	service iptables save
	service iptables restart
fi
#记录版本
echo -e `date "+%c"` install $version >> $ESS_HOME/version
# java -verbose -classpath .:/server/elastic/lib/*:/usr/local/jdk/lib/*.jar ElasticServer
#启动服务
service ess restart

