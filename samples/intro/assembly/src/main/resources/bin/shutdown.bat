@echo off

set "CATALINA_HOME=${project.build.directory}\\apache-tomcat-${tomcat.version}"
call "%CATALINA_HOME%/bin/catalina.bat" stop
