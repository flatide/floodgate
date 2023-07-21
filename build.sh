#!/bin/bash
cp ../floodgate_core/target/floodgate_core*.jar ./lib
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=../floodgate_core/target/floodgate_core-1.2.0.jar
mvn clean package
