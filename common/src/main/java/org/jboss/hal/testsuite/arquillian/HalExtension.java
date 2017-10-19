package org.jboss.hal.testsuite.arquillian;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.graphene.spi.location.LocationDecider;

public class HalExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(LocationDecider.class, HalLocationDecider.class);
    }
}
