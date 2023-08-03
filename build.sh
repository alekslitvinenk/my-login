#!/usr/bin/env bash

#
# Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
#

rm -R target

./bump.sh "$@"

version=$(cat VERSION)
ver="v$version"

sbt ';build' && \
docker build -t alekslitvinenk/dockovpn-login:"$ver" -t alekslitvinenk/dockovpn-login:latest . --no-cache && \
docker push alekslitvinenk/dockovpn-login:"$ver"