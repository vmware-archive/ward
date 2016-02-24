@echo off

set "CATALINA_HOME=${project.build.directory}\\apache-tomcat-${tomcat.version}"
set "CATALINA_BASE=${project.build.directory}\\base-instance"
call "%CATALINA_HOME%\\bin\\catalina.bat" start
