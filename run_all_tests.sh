#!/bin/bash

sbt clean compile coverage test coverageOff coverageReport dependencyUpdates
