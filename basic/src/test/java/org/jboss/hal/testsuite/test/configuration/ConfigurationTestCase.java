package org.jboss.hal.testsuite.test.configuration;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.testsuite.category.Standalone;
import org.jboss.hal.testsuite.page.config.Configuration2Page;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.jboss.arquillian.graphene.Graphene.goTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** Test case for the configuration top level category */
@RunWith(Arquillian.class)
@Category(Standalone.class)
public class ConfigurationTestCase {

    @Drone private WebDriver browser;
    @Page private Configuration2Page page;

    @Before
    public void setUp() throws Exception {
        goTo(Configuration2Page.class);
    }

    @Test
    public void items() throws Exception {
        assertNotNull(page.getSubsystems());
        assertNotNull(page.getInterfaces());
        assertNotNull(page.getSocketBindings());
        assertNotNull(page.getPaths());
        assertNotNull(page.getSystemProperties());
    }

    @Test
    public void linkToRuntime() throws Exception {
        assertNotNull(page.getRuntimeLink());
        page.getRuntimeLink().click();
        String currentUrl = browser.getCurrentUrl();
        assertTrue(currentUrl.endsWith("#" + NameTokens.RUNTIME));
    }
}
