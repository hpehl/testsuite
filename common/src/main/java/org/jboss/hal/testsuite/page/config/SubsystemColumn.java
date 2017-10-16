package org.jboss.hal.testsuite.page.config;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Location;
import org.jboss.hal.resources.CSS;
import org.jboss.hal.resources.Ids;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.arquillian.graphene.Graphene.waitGui;

@Location("#configuration;path=configuration~subsystems")
public class SubsystemColumn {

    @Drone private WebDriver browser;
    @FindBy(css = "#" + Ids.CONFIGURATION_SUBSYSTEM + " h1") private WebElement header;
    @FindBy(id = Ids.CONFIGURATION_SUBSYSTEM + "-filter") private WebElement filter;
    @FindBy(id = Ids.CONFIGURATION_SUBSYSTEM + "-filter-icon") private WebElement clear;
    @FindBy(css = "#" + Ids.CONFIGURATION_SUBSYSTEM + " ." + CSS.empty) private WebElement empty;

    public void clearFilter() {
        waitGui(browser).until().element(clear).is().present();
        if (clear.isDisplayed()) {
            clear.click();
            waitGui(browser).until().element(clear).is().not().visible();
        }
    }

    public WebElement getHeader() {
        return header;
    }

    public WebElement getFilter() {
        return filter;
    }

    public WebElement getEmpty() {
        return empty;
    }
}
