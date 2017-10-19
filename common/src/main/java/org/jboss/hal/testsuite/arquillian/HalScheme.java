package org.jboss.hal.testsuite.arquillian;

import org.jboss.arquillian.graphene.spi.location.Scheme;

public class HalScheme extends Scheme {

    @Override
    public String toString() {
        return "hal://";
    }
}
