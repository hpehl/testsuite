package org.jboss.hal.testsuite.test.configuration;

import java.util.List;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.category.Standalone;
import org.jboss.hal.testsuite.creaper.ManagementClientProvider;
import org.jboss.hal.testsuite.page.config.SubsystemColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.apache.commons.lang.StringUtils.substringBetween;
import static org.jboss.arquillian.graphene.Graphene.goTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
@Category(Standalone.class)
public class SubsystemColumnTestCase {

    private static final OnlineManagementClient client = ManagementClientProvider.createOnlineManagementClient();
    private static final Operations operations = new Operations(client);
    private static final String FILTER_SOME = "io";
    private static final String FILTER_NONE = "gibtsdochgarnet";

    @Drone private WebDriver browser;
    @Page private SubsystemColumn page;
    private List<String> subsystems;

    @Before
    public void setUp() throws Exception {
        ModelNodeResult result = operations.readChildrenNames(Address.root(), ModelDescriptionConstants.SUBSYSTEM);
        subsystems = result.stringListValue();

        goTo(SubsystemColumn.class);
        page.clearFilter();
    }

    @Test
    public void numberOfSubsystems() throws Exception {
        String expected = String.valueOf(subsystems.size());
        String actual = substringBetween(page.getHeader().getText(), "(", ")");
        assertEquals(expected, actual);
    }

    @Test
    public void filterSome() throws Exception {
        page.getFilter().sendKeys(FILTER_SOME);

        long count = subsystems.stream().filter(name -> name.contains(FILTER_SOME)).count();
        String expected = String.format("%d / %d", count, subsystems.size());
        String actual = substringBetween(page.getHeader().getText(), "(", ")");
        assertEquals(expected, actual);
    }

    @Test
    public void filterNone() throws Exception {
        page.getFilter().sendKeys(FILTER_NONE);

        String expected = String.format("0 / %d", subsystems.size());
        String actual = substringBetween(page.getHeader().getText(), "(", ")");
        assertEquals(expected, actual);
        assertNotNull(page.getEmpty());
    }
}
