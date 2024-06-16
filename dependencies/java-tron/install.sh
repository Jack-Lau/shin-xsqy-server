#!/bin/bash

mvn install:install-file -DgroupId=org.tron -DartifactId=java-tron -Dversion=1.0.0 -DpomFile=pom.xml -Dfile=java-tron-1.0.0.jar
