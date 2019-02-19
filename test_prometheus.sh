#!/usr/bin/env bash

set -euo pipefail

function cleanup {
    echo "Stopping weather-service..."
    pkill -f weather-service
    echo "Stopping prometheus..."
    docker stop $(docker ps | grep prometheus | cut -d ' ' -f 1)
}

trap cleanup SIGINT SIGTERM EXIT

echo "Starting prometheus service..."
(docker run -p 9090:9090 -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
       prom/prometheus > /dev/null 2>&1)&

echo "Building project..."
gradle clean build -x test > /dev/null 2>&1

echo "Starting weather-service..."
(java -DAPPID=$APPID -jar build/libs/weather-service-0.1.0.jar > /dev/null 2>&1)&

echo "Prometheus should be reachable under http://localhost:9090"

while true; do
    sleep 5
done