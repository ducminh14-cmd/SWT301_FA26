package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    public static WebDriver createDriver() {
        String browser   = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = ConfigReader.getBool("headless", false);
        boolean jsOn     = ConfigReader.getBool("javascriptEnabled", true);

        switch (browser) {
            case "firefox": {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("-headless");
                if (!jsOn) o.addPreference("javascript.enabled", false);
                return new FirefoxDriver(o);
            }
            case "edge": {
                WebDriverManager.edgedriver().setup();
                EdgeOptions o = new EdgeOptions();
                if (headless) o.addArguments("--headless=new", "--window-size=1920,1080");
                return new EdgeDriver(o);
            }
            case "chrome":
            default: {
                WebDriverManager.chromedriver().setup();
                ChromeOptions o = new ChromeOptions();
                o.addArguments("--incognito");
                if (headless) o.addArguments("--headless=new", "--window-size=1920,1080");
                if (!jsOn) {
                    Map<String, Object> prefs = new HashMap<>();
                    prefs.put("profile.managed_default_content_settings.javascript", 2);
                    o.setExperimentalOption("prefs", prefs);
                }
                return new ChromeDriver(o);
            }
        }
    }
}
