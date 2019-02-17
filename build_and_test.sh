#!/usr/bin/env bash

function stop_container {
    local docker_container_id=$(docker ps | grep 'whatstheweatherlike' | cut -d ' ' -f 1)
    if [ -z "$docker_container_id" ]; then
        echo "No docker container running!"
        exit 1
    fi
    echo "Stopping container"
    docker stop "$docker_container_id"
}

log_file=build/build_and_test.log
echo "Verbose output is written to $log_file"

echo "Building project..."
./gradlew build docker > $log_file 2>&1
result=$?
if [ $result -ne 0 ]; then
    cat $log_file
    echo "Build failed!"
    exit 1
fi
echo "Building project finished!"

if [ -z $APPID ]; then
    echo "Env var APPID is not set!"
    exit 1
fi

trap stop_container SIGINT SIGTERM EXIT

echo "Starting docker container..."
(docker run -e APPID=$APPID -p 8080:8080 whatstheweatherlike/weather-service > $log_file 2>&1)&

echo "Waiting 10 seconds..."
sleep 10

echo "Testing service..."
num_retries=3
retries=$num_retries
while [ $retries -gt 0 ]; do
    json_result=$(curl -f http://localhost:8080/weather-at/0.1,1.2)
    result=$?
    if [ $result -eq 0 ]; then
        name=$(echo $json_result | jq -r '.name')
        result=$?
        if [ $result -eq 0 ] && [ $name == "Earth" ]; then
            break
        fi
    fi
    ((retries--))
    sleep 10
done

if [ $retries -eq 0 ]; then
    cat $log_file
    echo "Smoke test failed after $num_retries retries!"
    exit 1
else
    echo "Smoke test successful!"
    exit 0
fi