#!/bin/sh

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

LIB_DIR=$DEPLOY_DIR/lib

PIDS=`ps -ef | grep java | grep "$LIB_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The Service already started!"
    echo "PID: $PIDS"
    exit 1
fi


LOGS_DIR=$DEPLOY_DIR/logs
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi

STDOUT_FILE=$LOGS_DIR/stdout.log



LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -XX:+HeapDumpOnOutOfMemoryError"


JAVA_DEBUG_OPTS=""

JAVA_JMX_OPTS=""

JAVA_MEM_OPTS=" -server -Xmx256m -Xms256m -Xmn32m -XX:PermSize=64m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"
echo "use JAVA_HOME: ${JAVA_HOME}"
JAVA_BIN=java
BITS=`$JAVA_BIN -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server -Xmx256m -Xms256m -Xmn32m -XX:PermSize=64m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"
else
    JAVA_MEM_OPTS=" -server -Xmx256m -Xms256m -Xmn32m -XX:PermSize=64m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"
fi

echo -e "Starting ...\c"

nohup $JAVA_BIN $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -classpath .:$LIB_JARS com.yd.container.Main >> $STDOUT_FILE 2>&1 &

echo "OK!"
PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
