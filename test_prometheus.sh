#!/usr/bin/env bash

set -euo pipefail

function cleanup {
    echo "Stopping weather-service..."
    pkill -f weather-service
    echo "Stopping prometheus..."
    docker stop $(docker ps | grep prometheus | cut -d ' ' -f 1)
}

trap cleanup SIGINT SIGTERM EXIT

echo "Setting local IP address in prometheus configuration"
LOCAL_IP=$(ifconfig | grep -Eo '192.168([\.0-9]+)' | head -n 1)
sed -i '' "s/localhost/$LOCAL_IP/g" prometheus.yml

echo "Starting prometheus service..."
(docker run -p 9090:9090 -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
       prom/prometheus > /dev/null 2>&1)&

echo "Building project..."
gradle clean build -x test > /dev/null 2>&1

echo "Starting weather-service..."
(java -DAPPID=$APPID -jar build/libs/weather-service-0.1.0.jar > /dev/null 2>&1)&

echo "Waiting for service to be online"
while true; do
    set +e
    curl -f -v http://localhost:8080/weather-at/0.1,0.2
    result=$?
    [ $result -ne 0 ] || break
    set -e
done

echo "Executing couple of calls to have something to show in prometheus"
ab -c 3 -n 60 http://localhost:8080/weather-at/0.1,0.2

echo "Prometheus should be reachable under http://localhost:9090"

while true; do
    sleep 5
done