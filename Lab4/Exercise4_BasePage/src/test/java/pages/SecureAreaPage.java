package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SecureAreaPage extends BasePage {
    private final By heading      = By.cssSelector("h2");
    private final By flashMessage = By.id("flash");
    private final By logoutButton = By.cssSelector("a[href='/logout']");

    public SecureAreaPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("/secure")); // xác nhận đã vào đúng trang
    }

    public String getHeading()      { return getText(heading); }
    public String getFlashMessage() { return getText(flashMessage); }

    public LoginPage logout() {
        click(logoutButton);
        return new LoginPage(driver);
    }
}

