#!/usr/bin/env bash


SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

(
    cd "$SCRIPT_DIR"

    if [ -f ./crx-quickstart/conf/cq.pid ]; then
        PID=$(cat ./crx-quickstart/conf/cq.pid)
        if ! [ "${PID}" = "" ]; then
        echo -n "==> Stopping AEM.."
        # shellcheck disable=SC2086
        kill "${PID}"
        while ps -p "${PID}" &>/dev/null; do
            echo -n "."
            sleep 5
        done
        echo " Done"
        fi
    else
        echo "Error: cq.pid file not found"
    fi
)