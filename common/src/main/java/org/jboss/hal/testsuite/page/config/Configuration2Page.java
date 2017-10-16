package org.jboss.hal.testsuite.page.config;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.hal.meta.token.NameTokens;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Location("#" + NameTokens.CONFIGURATION)
public class Configuration2Page {

    @FindBy(id = "subsystems") private WebElement subsystems;
    @FindBy(id = "interfaces") private WebElement interfaces;
    @FindBy(id = "socket-bindings") private WebElement socketBindings;
    @FindBy(id = "paths") private WebElement paths;
    @FindBy(id = "system-properties") private WebElement systemProperties;
    @FindBy(css = "a[href='#runtime']") private WebElement runtimeLink;

    public WebElement getSubsystems() {
        return subsystems;
    }

    public WebElement getInterfaces() {
        return interfaces;
    }

    public WebElement getSocketBindings() {
        return socketBindings;
    }

    public WebElement getPaths() {
        return paths;
    }

    public WebElement getSystemProperties() {
        return systemProperties;
    }

    public WebElement getRuntimeLink() {
        return runtimeLink;
    }
}
