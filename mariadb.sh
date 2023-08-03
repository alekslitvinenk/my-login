#!/usr/bin/env bash

docker run -d --name mariadb -p 3306:3306 \
-e MARIADB_USER=alexli \
-e MARIADB_PASSWORD="dockovpn123" \
-e MARIADB_ROOT_PASSWORD="dockovpn123" \
mariadb:latest