// pages/DynamicLoadingPage.java
package pages;
import org.openqa.selenium.*;

public class DynamicLoadingPage extends BasePage {
    private final By startBtn = By.cssSelector("#start button");
    private final By finish   = By.cssSelector("#finish h4");
    public DynamicLoadingPage(WebDriver d) { super(d); }
    public DynamicLoadingPage open(int example) {
        navigateTo("https://the-internet.herokuapp.com/dynamic_loading/" + example); return this;
    }
    /** Bấm Start rồi chờ (loading ~5s) đến khi chữ kết quả hiện ra */
    public String startAndGetResult() { click(startBtn); return getText(finish); }
}
