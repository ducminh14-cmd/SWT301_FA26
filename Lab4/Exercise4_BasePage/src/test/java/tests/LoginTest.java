package tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Login Tests using Page Object Model + BasePage/BaseTest")
public class LoginTest extends BaseTest {
    static LoginPage loginPage;

    @BeforeAll
    static void initPage() {               // chạy SAU BaseTest.setUpBase() → driver đã sẵn sàng
        loginPage = new LoginPage(driver);
    }

    @Test
    @Order(1)
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() {
        loginPage.navigate();
        loginPage.login("tomsmith", "SuperSecretPassword!");
        assertTrue(loginPage.getMessageText(loginPage.getSuccessLocator())
                .contains("You logged into a secure area!"));
    }

    @Test
    @Order(2)
    @DisplayName("Should show error for invalid credentials")
    void testLoginFail() {
        loginPage.navigate();
        loginPage.login("wronguser", "wrongpassword");
        assertTrue(loginPage.getMessageText(loginPage.getErrorLocator())
                .toLowerCase().contains("invalid"));
    }

    @ParameterizedTest(name = "CSV Inline: {0} / {1}")
    @Order(3)
    @CsvSource({
            "tomsmith, SuperSecretPassword!, success",
            "wronguser, SuperSecretPassword!, error",
            "tomsmith, wrongpassword, error",
            "'', '', error"
    })
    void testLoginCsvInline(String username, String password, String expected) {
        verifyLogin(username, password, expected);
    }

    @ParameterizedTest(name = "CSV File: {0} / {1}")
    @Order(4)
    @CsvFileSource(resources = "/login-data.csv", numLinesToSkip = 1)
    void testLoginFromCSV(String username, String password, String expected) {
        verifyLogin(username, password, expected);
    }

    private void verifyLogin(String username, String password, String expected) {
        loginPage.navigate();
        username = (username == null) ? "" : username.trim();
        password = (password == null) ? "" : password.trim();
        loginPage.login(username, password);

        if (expected.equals("success")) {
            assertTrue(loginPage.getMessageText(loginPage.getSuccessLocator())
                    .contains("You logged into a secure area!"));
        } else {
            assertTrue(loginPage.getMessageText(loginPage.getErrorLocator())
                    .toLowerCase().contains("invalid"));
        }
    }
    // Không cần @AfterAll – BaseTest.tearDownBase() đã quit driver
}
