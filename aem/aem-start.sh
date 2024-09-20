#!/usr/bin/env bash

export OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
export OTEL_RESOURCE_ATTRIBUTES=service.name=aem-author

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

(
    cd "$SCRIPT_DIR"

    if type -p java > /dev/null; then
        JAVA_EXEC=java
    elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
        JAVA_EXEC="$JAVA_HOME/bin/java"
    else
        echo "Java is not installed."
        exit 1
    fi

    # Get the Java version
    JAVA_VERSION=$($JAVA_EXEC -version 2>&1 | awk -F[\"_] 'NR==1 {print $2}')
    if ! [[ $JAVA_VERSION == 11* ]]; then
        echo "Current version of Java is not set to 11, please update your java / change your JAVA_HOME to a JDK 11 version."
        exit 1
    fi 

    # shellcheck disable=SC2144
    if ! [ -f aem-*.jar ]; then
        echo "Error: No AEM quickstart jar (aem-*.jar) found"
    fi

    echo "==> Start AEM using quickstart jar"
    CQ_JVM_OPTS='-server -Xmx4096m -Djava.awt.headless=true' \
        $JAVA_EXEC -jar \
        -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
        aem-*.jar \
        -nointeractive \
        -gui \
        -r local,author \
        -p 4502 &
    PID=$!
    mkdir -p ./crx-quickstart/conf
    echo "${PID}" >./crx-quickstart/conf/cq.pid

    echo -n "==> Waiting for error log to be created.."
    while ! [ -f ./crx-quickstart/logs/error.log ]; do
        sleep 5
        echo -n "."
    done
)