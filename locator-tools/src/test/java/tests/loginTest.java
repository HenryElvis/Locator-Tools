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

        GetPage().pause();

        ElementData dataFilter = new ElementData()
            .withType("input")
            .withFormat("locator")
            .withId("user-name");

        LocatorService tools = new LocatorService(GetPage(), false, true);
        String html = tools.GetPageSnapshot();

        String targetElement = tools.GetElementHtmlWithFilter(html, dataFilter);
        String proposedLocator = tools.GenerateLocator(targetElement, dataFilter);

        GetPage().pause();
    }

    @Test
    void testLogin()
    {
        playwright.selectors().setTestIdAttribute("data-test");

        loginPage = new loginPage(GetPage());

        loginPage.FillUsername("standard_user");
        loginPage.FillPassword("secret_sauce");

        loginPage.ClickOnLogin();

        GetPage().pause();
    }
}
