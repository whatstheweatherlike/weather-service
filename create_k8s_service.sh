#!/usr/bin/env bash
set -euo pipefail

function usage {
    local message
    [ "$#" -gt 0 ] || message="\n$1"
    cat <<HERE
usage: $0 <appid>

appid: the app id required for accessing https://openweathermap.org/api$message
HERE
}

function main {

    if [ "$#" -ne 1 ]; then
        usage "APPID not provided!"
        exit 1
    fi

    if [ -z "$1" ]; then
        usage "APPID not provided!"
        exit 1
    fi

    export APPID="$1"

    LOG_FILE="build/create_k8s_service.log"
    set +e
    kubectl get deployment weather-service-node > $LOG_FILE 2>&1
    result=$?
    set -e
    if [ $result -ne 0 ]; then
        echo "Creating deployment..."
        (envsubst < weather-service-node.json | kubectl create -f -) > $LOG_FILE 2>&1
    fi

    set +e
    kubectl get service weather-service-node > $LOG_FILE 2>&1
    result=$?
    set -e
    if [ $result -ne 0 ]; then
        echo "Creating service..."
        kubectl expose deployment weather-service-node --type=LoadBalancer --port=8080 > $LOG_FILE 2>&1
    fi

}

main "$@"