// pages/CheckboxesPage.java
package pages;
import org.openqa.selenium.*;
import java.util.List;

public class CheckboxesPage extends BasePage {
    private final By boxes = By.cssSelector("#checkboxes input[type='checkbox']");
    public CheckboxesPage(WebDriver d) { super(d); }
    public CheckboxesPage open() { navigateTo("https://the-internet.herokuapp.com/checkboxes"); return this; }
    private List<WebElement> all() { waitForVisibility(boxes); return driver.findElements(boxes); }
    public void check(int index)   { WebElement b = all().get(index); if (!b.isSelected()) b.click(); }
    public void uncheck(int index) { WebElement b = all().get(index); if (b.isSelected()) b.click(); }
    public boolean isChecked(int index) { return all().get(index).isSelected(); }
}
