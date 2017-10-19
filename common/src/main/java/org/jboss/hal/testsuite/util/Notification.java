package org.jboss.hal.testsuite.util;

import org.jboss.hal.resources.CSS;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.WebDriver;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.resources.CSS.toastNotificationsListPf;

public class Notification {

    public static Notification withBrowser(WebDriver browser) {
        return new Notification(browser);
    }

    private final WebDriver browser;

    public Notification(WebDriver browser) {
        this.browser = browser;
    }

    public void success() {
        waitGui(browser).until("No success notification")
                .element(new ById("." + toastNotificationsListPf + " > ." + CSS.alertSuccess))
                .is().visible();
    }
}
