package org.jboss.hal.testsuite.fragment;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.resources.CSS.btnPrimary;
import static org.jboss.hal.resources.CSS.editing;
import static org.jboss.hal.resources.CSS.formButtons;
import static org.jboss.hal.resources.CSS.readonly;

/** Page fragment for a form using read-only and editing states. */
public class FormFragment {

    @Drone private WebDriver browser;
    @FindBy(css = "a[data-operation=edit]") private WebElement editLink;
    @FindBy(css = "." + formButtons + " ." + btnPrimary) private WebElement saveButton;
    @FindBy(css = "." + readonly) private WebElement readOnlySection;
    @FindBy(css = "." + editing) private WebElement editingSection;


    // ------------------------------------------------------ edit mode

    public void edit() {
        editLink.click();
        waitGui(browser).until().element(editingSection).is().visible();
    }

    public void save() {
        saveButton.click();
        waitGui(browser).until().element(readOnlySection).is().visible();
    }

    public void checkbox(String id, boolean value) {
        WebElement element = browser.findElement(new ById(id));
        boolean inputValue = parseBoolean(element.getAttribute("value"));
        if (inputValue != value) {
            WebElement switchContainer = browser.findElement(new ByCssSelector(".bootstrap-switch-id-" + id));
            switchContainer.click();
            if (value) {
                waitGui(browser).until().element(element).is().selected();
            } else {
                waitGui(browser).until().element(element).is().not().selected();
            }
        }
    }

    private boolean parseBoolean(String text) {
        if ("on".equals(text)) {
            return true;
        }
        return Boolean.parseBoolean(text);
    }
}
