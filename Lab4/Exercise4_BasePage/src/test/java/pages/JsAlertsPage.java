// pages/JsAlertsPage.java
package pages;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class JsAlertsPage extends BasePage {
    private final By jsAlertBtn   = By.xpath("//button[text()='Click for JS Alert']");
    private final By jsConfirmBtn = By.xpath("//button[text()='Click for JS Confirm']");
    private final By jsPromptBtn  = By.xpath("//button[text()='Click for JS Prompt']");
    private final By result       = By.id("result");

    public JsAlertsPage(WebDriver d) { super(d); }
    public JsAlertsPage open() { navigateTo("https://the-internet.herokuapp.com/javascript_alerts"); return this; }

    private Alert waitAlert() { return wait.until(ExpectedConditions.alertIsPresent()); }

    public void acceptAlert()            { click(jsAlertBtn);   waitAlert().accept(); }
    public void dismissConfirm()         { click(jsConfirmBtn); waitAlert().dismiss(); }
    public void answerPrompt(String txt) { click(jsPromptBtn);  Alert a = waitAlert(); a.sendKeys(txt); a.accept(); }
    public String getResult()            { return getText(result); }
}

