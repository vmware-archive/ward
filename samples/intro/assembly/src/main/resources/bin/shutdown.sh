#!/bin/sh

export CATALINA_HOME="${project.build.directory}/apache-tomcat-${tomcat.version}"
"${CATALINA_HOME}/bin/catalina.sh" stop