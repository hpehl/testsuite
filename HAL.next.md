# HAL.next

Steps to execute test cases written for HAL.next:

1. Add remote and checkout branch `hal.next`  
    ```bash
    git remote add hpehl git@github.com:hpehl/testsuite.git
    git fetch
    git checkout hal.next 
    ```

1. Start WildFly w/ HAL.next,  The easiest way is to use the docker image [`hal-console`](https://hub.docker.com/r/hpehl/hal-console/):  
    ```bash
    docker run -p 9990:9990 \
               -it hpehl/hal-console \
               /opt/jboss/wildfly/bin/standalone.sh \
               -b 0.0.0.0 -bmanagement 0.0.0.0
    ```
    
1. Unsecure Management Interface. Use `admin:admin` to login for the first time.   
    ```
    /core-service=management/management-interface=http-interface:undefine-attribute(name=security-realm)
    :reload
    ```
      
1. Run a single test case e.g. `org.jboss.hal.testsuite.test.configuration.ConfigurationTestCase`  
    ```bash
    mvn test -Pstandalone,basic \
             -Djboss.dist=<WILDFLY_HOME> \
             -Darq.extension.webdriver.firefox_binary=<FIREFOX_BINARY> \
             -Dfindbugs.skip -Dcheckstyle.skip \
             -Dtest=org.jboss.hal.testsuite.test.configuration.ConfigurationTestCase
    ```
