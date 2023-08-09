call mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=./lib/floodgate_core-1.2.0.jar
call mvn clean package -P%1
if %1 == dev (copy target\floodgate-*.jar release\dev) else (copy target\floodgat-*.jar release\op)
