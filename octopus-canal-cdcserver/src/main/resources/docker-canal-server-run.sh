#! /bin/bash

set -e
./set-env.sh

docker-compose -f docker-compose-canal-server.yml up