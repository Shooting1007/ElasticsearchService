#!/bin/bash

if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi

bin=`dirname "$0"`

export ES_HOME=`cd $bin/../; pwd`

ES_CONF_DIR=$ES_HOME/conf
CLASSPATH="${ES_CONF_DIR}"

for f in $ES_HOME/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

LOG_DIR=${ES_HOME}/logs

CLASS=ElasticServer
nohup ${JAVA_HOME}/bin/java -classpath "$CLASSPATH" $CLASS > ${LOG_DIR}/es-server.out 2>&1 < /dev/null &

