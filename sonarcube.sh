#!/bin/bash
if [ "$TRAVIS_BRANCH" == "master" ]; then
  sonar-scanner
fi