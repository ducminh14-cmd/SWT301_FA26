package utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import tests.BaseTest;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        WebDriver driver = BaseTest.getDriver();
        if (driver == null) return;
        try {
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String name = (context.getRequiredTestClass().getSimpleName() + "_"
                    + context.getDisplayName()).replaceAll("[^a-zA-Z0-9._-]", "_");
            Path file = dir.resolve(name + "_" + time + ".png");

            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(file, png);
            System.out.println("📸 Screenshot saved: " + file.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Cannot save screenshot: " + e.getMessage());
        }
    }
}
