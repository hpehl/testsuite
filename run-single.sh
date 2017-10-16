#!/bin/sh
mvn test -Pstandalone,basic \
         -Djboss.dist=/Users/hpehl/dev/jboss/wildfly/wildfly/build/target/wildfly-11.0.0.Final-SNAPSHOT \
         -Darq.extension.webdriver.firefox_binary=/Applications/Firefox\ 31.2.0.app/Contents/MacOS/firefox \
         -Dtake.screenshot.after.each.test=true \
         -Dfindbugs.skip -Dcheckstyle.skip \
         -Dtest=$1 \
