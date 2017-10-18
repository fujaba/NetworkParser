#!/bin/bash
echo "$TRAVIS_BRANCH"
if [ "$TRAVIS_BRANCH" == "master" ]; then
  curl -sSL https://download.sourceclear.com/ci.sh |  bash
  sonar-scanner
fi
