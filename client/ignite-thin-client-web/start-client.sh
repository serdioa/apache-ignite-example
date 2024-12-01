#!/bin/bash

LOG_DIR=target/log
mkdir -p ${LOG_DIR}

GC_LOG=${LOG_DIR}/gc.log

JAVA_OPTIONS="-XX:+UseShenandoahGC -XX:+UseStringDeduplication"
IGNITE_OPTIONS="--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED --add-opens=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED --add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED --add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED -DIGNITE_QUIET=false"

SPRING_OPTIONS="--spring.config.location=src/main/config/ignite-thin-client-web.properties --logging.config=src/main/config/logback-thin-client-web.xml"

# mvn package spring-boot:repackage

EXEC_JAR=$(/bin/find target -name ignite-thin-client-web-*.jar)

if [[ -z "${EXEC_JAR}" ]] ; then
    echo "Can not find executable jar" >&2
else
    java ${JAVA_OPTIONS} ${IGNITE_OPTIONS} -jar ${EXEC_JAR} ${SPRING_OPTIONS} "$@"
fi
