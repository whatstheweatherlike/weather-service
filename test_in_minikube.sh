#!/usr/bin/env bash
set -euo pipefail

function cleanup {
    echo "Cleaning up"
    set +e
    kubectl delete deployment,service weather-service-node > $LOG_FILE 2>&1
    set -e
}

function check_minikube_running {
    set +e
    minikube status > $LOG_FILE 2>&1
    result=$?
    set -e
    if [ $result -ne 0 ]; then
        echo "Minikube not running!"
        exit 1
    fi
}

function main {
    if [ -z $APPID ]; then
        echo "APPID not set!"
        exit 1
    fi

    export LOG_FILE=build/test_in_minikube.log
    echo "Verbose output is written to $LOG_FILE"

    trap cleanup SIGINT SIGTERM EXIT

    check_minikube_running

    ./create_k8s_service.sh $APPID

    # Smoke test
    service_url=$(minikube service weather-service-node --url)
    echo "Smoke test using url $service_url"
    num_retries=3
    retries=$num_retries
    while [ $retries -gt 0 ]; do
        set +e
        curl -f -v $service_url/weather-at/0.1,1.2 > $LOG_FILE 2>&1
        result=$?
        set -e
        if [ $result -eq 0 ]; then
            break
        fi
        echo "Failed, retrying..."
        sleep 10
    done

    if [ $retries -eq 0 ]; then
        echo "Smoke test failed after $num_retries retries!"
        exit 1
    else
        echo "Smoke test succeeded!"
        exit 0
    fi

}

main "$@"