package tests;

import org.junit.jupiter.api.Test;

import pages.loginPage;
import tools.baseTest;

public class loginTest extends baseTest
{
    private loginPage loginPage;

    @Test
    void testLogin()
    {
        playwright.selectors().setTestIdAttribute("data-test");

        loginPage = new loginPage(getPage());

        loginPage.FillUsername("standard_user");
        loginPage.FillPassword("secret_sauce");

        page.pause();

        loginPage.ClickOnLogin();

        page.pause();
    }
}
