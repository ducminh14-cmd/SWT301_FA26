// pages/DropdownPage.java
package pages;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

public class DropdownPage extends BasePage {
    private final By dropdown = By.id("dropdown");
    public DropdownPage(WebDriver d) { super(d); }
    public DropdownPage open() { navigateTo("https://the-internet.herokuapp.com/dropdown"); return this; }
    public void select(String visibleText) { new Select(waitForVisibility(dropdown)).selectByVisibleText(visibleText); }
    public String getSelected() { return new Select(waitForVisibility(dropdown)).getFirstSelectedOption().getText(); }
}
