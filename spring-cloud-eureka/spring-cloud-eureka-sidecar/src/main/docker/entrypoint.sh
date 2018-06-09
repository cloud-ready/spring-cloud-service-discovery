#!/usr/bin/env bash

set -e









JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/urandom";
JAVA_OPTS="${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}";
. /opt/java_debug_monitor_profiler.sh

# if command starts with an option, prepend java
if [ "${1:0:1}" == '-' ]; then
    set -- java "$@" -jar *-exec.jar
elif [ "${1:0:1}" != '/' ]; then
    set -- java ${JAVA_OPTS} -jar *-exec.jar "$@"
fi

exec "$@"
