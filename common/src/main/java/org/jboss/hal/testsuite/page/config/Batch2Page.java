package org.jboss.hal.testsuite.page.config;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.hal.testsuite.arquillian.HalScheme;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Location(scheme = HalScheme.class, value = "#batch-jberet-configuration${profile:full}")
public class Batch2Page {

    @FindBy(id = "batch-configuration-item") private WebElement configurationItem;
    @FindBy(id = "batch-configuration-form") private FormFragment configurationForm;

    @FindBy(id = "batch-in-memory-job-repo-item") private WebElement inMemory;
    @FindBy(id = "batch-jdbc-job-repo-item") private WebElement jdbc;
    @FindBy(id = "batch-thread-factory-item") private WebElement threadFactory;
    @FindBy(id = "batch-thread-pool-item") private WebElement threadPool;

    public WebElement getConfigurationItem() {
        return configurationItem;
    }

    public FormFragment getConfigurationForm() {
        return configurationForm;
    }

    public WebElement getInMemory() {
        return inMemory;
    }

    public WebElement getJdbc() {
        return jdbc;
    }

    public WebElement getThreadFactory() {
        return threadFactory;
    }

    public WebElement getThreadPool() {
        return threadPool;
    }
}
