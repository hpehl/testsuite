package org.jboss.hal.testsuite.test.configuration.JCA;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.category.Standalone;
import org.jboss.hal.testsuite.creaper.ManagementClientProvider;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.dmr.ModelNodeGenerator;
import org.jboss.hal.testsuite.page.config.DatasourcesPage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.extras.creaper.commands.foundation.online.SnapshotBackup;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.junit.Assert.assertTrue;

/**
 * Created by pcyprian on 24.8.15.
 *
 * CAN NOT TEST WRONG PROPERTY
 */
@RunWith(Arquillian.class)
@Category(Standalone.class)
public class DatasourceNonXAPolicyTestCase {

    private static final OnlineManagementClient client = ManagementClientProvider.createOnlineManagementClient();
    private static final Administration adminOps = new Administration(client);

    private final Address datasourceAddress = Address.subsystem("datasources").and("data-source", "ExampleDS");
    private final ResourceVerifier verifier = new ResourceVerifier(datasourceAddress, client);
    private final ModelNodeGenerator nodeGenerator = new ModelNodeGenerator();

    private static final String POOL_SAVE_FAIL_MESSAGE = "Probably caused by https://issues.jboss.org/browse/HAL-1311";
    private static final String DATASOURCE_NAME = "ExampleDS";

    private static final SnapshotBackup backup = new SnapshotBackup();

    @BeforeClass
    public static void setUp() throws CommandFailedException {
        client.apply(backup.backup());
    }

    @AfterClass
    public static void tearDown() {
        try {
            client.apply(backup.restore());
            adminOps.reloadIfRequired();
        } catch (Exception e) {
            IOUtils.closeQuietly(client);
        }
    }

    @Drone
    private WebDriver browser;
    @Page
    private DatasourcesPage datasourcePage;

    @Before
    public void before() {
        datasourcePage.invokeViewDatasource(DATASOURCE_NAME);
        datasourcePage.getPoolConfig().edit();
    }

    @Test
    public void setDecrementerClass() throws Exception {
        final String decrementerClass = "org.jboss.jca.core.connectionmanager.pool.capacity.WatermarkDecrementer";
        datasourcePage.setDecrementerClass(decrementerClass);
        boolean finished = datasourcePage.getConfigFragment().save();
        assertTrue("Config should be saved and closed.", finished);
        verifier.verifyAttribute("capacity-decrementer-class",
                "org.jboss.jca.core.connectionmanager.pool.capacity.WatermarkDecrementer",
                POOL_SAVE_FAIL_MESSAGE);
    }

    @Test
    public void setDecrementerProperty() throws Exception {
        final String propertyKey = "Watermark";
        final String propertyValue = "9";
        datasourcePage.setDecrementerProperty(propertyKey, propertyValue);
        boolean finished = datasourcePage.getConfigFragment().save();
        assertTrue("Config should be saved and closed.", finished);
        ModelNode expectedPropertiesNode = nodeGenerator.createObjectNodeWithPropertyChild(propertyKey, propertyValue);
        verifier.verifyAttribute("capacity-decrementer-properties", expectedPropertiesNode, POOL_SAVE_FAIL_MESSAGE);
    }

    @Test
    public void unsetDecrementerProperty() throws Exception {
        datasourcePage.unsetDecrementerProperty();
        boolean finished = datasourcePage.getConfigFragment().save();
        assertTrue("Config should be saved and closed.", finished);
        verifier.verifyAttributeIsUndefined("capacity-decrementer-properties");
    }

    @Test
    public void unsetDecrementerClass() throws Exception {
        datasourcePage.unsetDecrementerClass();
        boolean finished = datasourcePage.getConfigFragment().save();
        assertTrue("Config should be saved and closed.", finished);
        verifier.verifyAttributeIsUndefined("capacity-decrementer-class");
    }

    @Test
    public void setIncrementerClass() throws Exception {
        final String incrementerClass = "org.jboss.jca.core.connectionmanager.pool.capacity.WatermarkIncrementer";
        datasourcePage.setIncrementerClass(incrementerClass);
        boolean finished = datasourcePage.getConfigFragment().save();
        assertTrue("Config should be saved and closed.", finished);
        verifier.verifyAttribute("capacity-incrementer-class", incrementerClass, POOL_SAVE_FAIL_MESSAGE);
    }

    @Test
    public void setIncrementerProperty() throws Exception {
        final String propertyKey = "Size";
        final String propertyValue = "7";
        datasourcePage.setIncrementerProperty(propertyKey, propertyValue);
        boolean finished = datasourcePage.getConfigFragment().save();
        assertTrue("Config should be saved and closed.", finished);
        ModelNode expectedPropertiesNode = nodeGenerator.createObjectNodeWithPropertyChild(propertyKey, propertyValue);
        verifier.verifyAttribute("capacity-incrementer-properties", expectedPropertiesNode, POOL_SAVE_FAIL_MESSAGE);
    }

    @Test
    public void unsetIncrementerProperty() throws Exception {
        datasourcePage.unsetIncrementerProperty();
        boolean finished = datasourcePage.getConfigFragment().save();
        assertTrue("Config should be saved and closed.", finished);
        verifier.verifyAttributeIsUndefined("capacity-incrementer-properties");
    }

    @Test
    public void unsetIncrementerClass() throws Exception {
        datasourcePage.unsetIncrementerClass();
        boolean finished = datasourcePage.getConfigFragment().save();
        assertTrue("Config should be saved and closed.", finished);
        verifier.verifyAttributeIsUndefined("capacity-incrementer-class", POOL_SAVE_FAIL_MESSAGE);
    }

}
