#!/usr/bin/env bash

set -euo pipefail

if [ -z $APPID ]; then
    echo "APPID not set!"
    exit 1
fi

function cleanup {
    echo "Cleaning up"
    set +e
    kubectl delete deployment,service weather-service-node > $log_file 2>&1
    set -e
}

log_file=build/build_and_test.log
echo "Verbose output is written to $log_file"

trap cleanup SIGINT SIGTERM EXIT

set +e
minikube status > $log_file 2>&1
result=$?
set -e
if [ $result -ne 0 ]; then
    echo "Minikube not running!"
    exit 1
fi

set +e
kubectl get deployment weather-service-node > $log_file 2>&1
result=$?
set -e
if [ $result -ne 0 ]; then
    echo "Creating deployment..."
    (envsubst < weather-service-node.json | kubectl create -f -) > $log_file 2>&1
fi

set +e
kubectl get service weather-service-node > $log_file 2>&1
result=$?
set -e
if [ $result -ne 0 ]; then
    echo "Creating service..."
    kubectl expose deployment weather-service-node --type=LoadBalancer --port=8080 > $log_file 2>&1
fi
echo "Waiting for service..."
sleep 10

# Smoke test
service_url=$(minikube service weather-service-node --url)
echo "Smoke test using url $service_url"
num_retries=3
retries=$num_retries
while [ $retries -gt 0 ]; do
    set +e
    curl -f -v $service_url/weather-at/0.1,1.2 > $log_file 2>&1
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
