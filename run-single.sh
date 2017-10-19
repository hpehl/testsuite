#!/bin/sh

#
# Env args:
# - WILDFLY_HOME: WildFly base directory
# - FIREFOX_BINARY: Path to the Firefox binary
#

if [ "${WILDFLY_HOME}x" == "x" ] ; then
   echo "Please set WILDFLY_HOME!"
   exit 1
fi

if [ "${FIREFOX_BINARY}x" == "x" ] ; then
   echo "Please set FIREFOX_BINARY!"
   exit 1
fi

TEST_CLASS=$1
shift

mvn test -Pstandalone,basic \
         -Djboss.dist=${WILDFLY_HOME} \
         -Darq.extension.webdriver.firefox_binary="${FIREFOX_BINARY}" \
         -Dtake.screenshot.after.each.test=true \
         -Dfindbugs.skip -Dcheckstyle.skip \
         -Dtest=${TEST_CLASS} $@
