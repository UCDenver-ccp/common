#!/bin/sh

mvn install:install-file -DpomFile=pom.xml -Dpackaging=jar -Dfile=../build/distribution/ccp-common.jar

