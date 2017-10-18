#!/bin/bash
echo "$TRAVIS_BRANCH"
if [ "$TRAVIS_BRANCH" == "master" ]; then
  #sonar-scanner
fi
