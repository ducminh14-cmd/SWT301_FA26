package tests;

import org.junit.jupiter.api.*;
import pages.LoginPage;
import pages.SecureAreaPage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("End-to-end login → logout flow")
class LoginFlowTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("User can login and then logout")
    void loginThenLogout() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigate();

        SecureAreaPage secure = loginPage.loginAs("tomsmith", "SuperSecretPassword!");
        assertAll("Secure area",
                () -> assertEquals("Secure Area", secure.getHeading()),
                () -> assertTrue(secure.getFlashMessage().contains("You logged into a secure area!")));

        LoginPage afterLogout = secure.logout();
        assertAll("After logout",
                () -> assertTrue(afterLogout.isAt(), "Không quay về trang /login"),
                () -> assertTrue(afterLogout.getFlashMessage().contains("You logged out of the secure area!")));
    }

    @Test
    @DisplayName("Secure page cannot be accessed without login")
    void secureRequiresLogin() {
        driver.manage().deleteAllCookies();
        driver.get("https://the-internet.herokuapp.com/secure");
        LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.getFlashMessage().contains("You must login to view the secure area!"));
    }
}

