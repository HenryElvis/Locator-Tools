package tests;

import org.junit.jupiter.api.Test;

import com.henryelvis.ElementData;
import com.henryelvis.LocatorService;

import pages.loginPage;
import tools.baseTest;

public class loginTest extends baseTest
{
    private loginPage loginPage;

    @Test
    void findLocator()
    {
        playwright.selectors().setTestIdAttribute("data-test");

        ElementData dataFilter = new ElementData()
            .withType("input")
            .withFormat("xpath")
            .withPlaceholder("Password");

        LocatorService tools = new LocatorService(GetPage(), true);
        String html = tools.GetPageSnapshot();

        String targetElement = tools.GetElementHtmlWithFilter(html, dataFilter);
        tools.GenerateLocator(targetElement, dataFilter);
    }

    @Test
    void testLogin()
    {
        playwright.selectors().setTestIdAttribute("data-test");

        loginPage = new loginPage(GetPage());

        GetPage().pause();

        loginPage.FillUsername("standard_user");

        GetPage().pause();

        loginPage.FillPassword("secret_sauce");

        GetPage().pause();

        loginPage.ClickOnLogin();

        GetPage().pause();
    }
}
