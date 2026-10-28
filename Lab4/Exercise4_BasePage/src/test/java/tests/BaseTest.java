package tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import utils.DriverFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import utils.ScreenshotExtension;

@ExtendWith(ScreenshotExtension.class)



public abstract class BaseTest {
    protected static WebDriver driver;

    public static WebDriver getDriver() { return driver; }

    @BeforeAll
    public static void setUpBase() {
        driver = DriverFactory.createDriver();
        driver.manage().window().maximize();
    }

    @AfterAll
    public static void tearDownBase() {
        if (driver != null) {
            driver.quit();
        }
    }
}
