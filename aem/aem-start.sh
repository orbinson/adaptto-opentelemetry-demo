#!/usr/bin/env bash

set -euo pipefail

export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
export OTEL_SERVICE_NAME=aem-author
export OTEL_RESOURCE_ATTRIBUTES=env=local,project=adaptTo

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

(
    cd "$SCRIPT_DIR" || exit 1

    for arg in "$@"; do
        if [ "$arg" == "--reset" ]; then
            echo "==> Reset AEM"
            rm -rf ./crx-quickstart || true
            cp -r ./crx-quickstart-clean ./crx-quickstart
            break
        fi
        if [ "$arg" == "--restore" ]; then
            echo "==> Restore AEM"
            rm -rf ./crx-quickstart || true
            cp -r ./crx-quickstart-snapshot ./crx-quickstart
            break
        fi
    done

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
        exit 1
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
