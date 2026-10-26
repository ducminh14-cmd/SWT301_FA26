package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // ===== Locators (private – test không được biết) =====
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.cssSelector("button[type='submit']");
    private final By successMsg    = By.cssSelector(".flash.success");
    private final By errorMsg      = By.cssSelector(".flash.error");

    // ===== Actions =====
    public void navigate() {
        driver.get("https://the-internet.herokuapp.com/login");
    }

    public void login(String username, String password) {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginButton).click();
    }

    public By getSuccessLocator() { return successMsg; }
    public By getErrorLocator()   { return errorMsg; }
}

