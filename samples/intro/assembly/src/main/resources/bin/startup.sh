#!/bin/sh

export CATALINA_HOME="${project.build.directory}/apache-tomcat-${tomcat.version}"
export CATALINA_BASE="${project.build.directory}/base-instance"
"${CATALINA_HOME}/bin/catalina.sh" start