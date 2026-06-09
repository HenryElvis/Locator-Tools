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
    void testLogin()
    {
        playwright.selectors().setTestIdAttribute("data-test");

        loginPage = new loginPage(getPage());

        loginPage.FillUsername("standard_user");
        loginPage.FillPassword("secret_sauce");

        loginPage.ClickOnLogin();

        String typeOfTargetLocator = "button";
        String formatType = "xpath";

        ElementData dataFilter = new ElementData()
            // .withId("add-to-cart-sauce-labs-bike-light")
            .withName("add-to-cart-sauce-labs-bike-light");

        LocatorService tools = new LocatorService(getPage(), false, false);
        String html = tools.GetPageSnapshot();
    
        String proposedLocator = tools.GenerateLocator(html, formatType, typeOfTargetLocator);

        getPage().pause();
    }
}
