package org.jboss.hal.testsuite.test.configuration.batch;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.hal.testsuite.creaper.ManagementClientProvider;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.creaper.command.BackupAndRestoreAttributes;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.config.Batch2Page;
import org.jboss.hal.testsuite.util.ConfigUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.arquillian.graphene.Graphene.goTo;
import static org.jboss.arquillian.graphene.Graphene.waitGui;

@RunWith(Arquillian.class)
public class BatchTestCase {

    private static final OnlineManagementClient client = ManagementClientProvider.createOnlineManagementClient();
    private static final Operations operations = new Operations(client);
    private static BackupAndRestoreAttributes backup;

    @Drone private WebDriver browser;
    @Page private Batch2Page page;

    @BeforeClass
    public static void beforeClass() throws CommandFailedException {
        backup = new BackupAndRestoreAttributes.Builder(address()).build();
        client.apply(backup.backup());
    }

    private static Address address() {
        Address address = Address.root();
        if (ConfigUtils.isDomain()) {
            address = address.and("profile", ConfigUtils.getDefaultProfile());
        }
        return address.and("subsystem", "batch-jberet");
    }

    @Before
    public void before() throws Exception {
        goTo(Batch2Page.class);
        waitGui(browser).until().element(page.getConfigurationItem()).is().present();
    }

    @AfterClass
    public static void afterClass() throws CommandFailedException {
        client.apply(backup.restore());
    }

    @Test
    public void batchSubsystem() throws Exception {
        page.getConfigurationItem().click();
        FormFragment form = page.getConfigurationForm();
        form.edit();
        form.checkbox("batch-configuration-form-restart-jobs-on-resume-editing", false);
        form.save();
        // Notification.withBrowser(browser).success();

        new ResourceVerifier(address(), client, 500)
                .verifyAttribute("restart-jobs-on-resume", false);
    }
}
