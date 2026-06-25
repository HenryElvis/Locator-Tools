package tests;

import org.junit.jupiter.api.Test;

import com.henryelvis.ElementData;
import com.henryelvis.LocatorService;

import pages.loginPage;
import pages.productsPage;
import tools.baseTest;

public class productsTest extends baseTest {
    private loginPage loginPage;
    private productsPage productsPage;

    @Test
    void testAddBag()
    {
        // Login
        {
            loginPage = new loginPage(GetPage());
            productsPage = new productsPage(GetPage());

            loginPage.FillUsername("standard_user");
            loginPage.FillPassword("secret_sauce");

            loginPage.ClickOnLogin();
        }
        //

        playwright.selectors().setTestIdAttribute("data-test");

        ElementData dataFilter = new ElementData()
                .withType("button")
                .withName("add-to-cart-sauce-labs-backpack");

        LocatorService tools = new LocatorService(GetPage(), true);
        
        String html = tools.GetPageSnapshot();
        String targetElement = tools.GetElementHtmlWithFilter(html, dataFilter);
        String proposedLocator = tools.GenerateLocator(targetElement, dataFilter);

        productsPage.AddProduct(proposedLocator);

        GetPage().pause();
    }

    @Test
    void testGoToCheckout() {
        // Login
        {
            loginPage = new loginPage(GetPage());
            productsPage = new productsPage(GetPage());

            loginPage.FillUsername("standard_user");
            loginPage.FillPassword("secret_sauce");

            loginPage.ClickOnLogin();
        }
        //

        playwright.selectors().setTestIdAttribute("data-test");

        ElementData dataFilter = new ElementData()
                .withType("div")
                .withId("shopping_cart_container");

        LocatorService tools = new LocatorService(GetPage(), true);
        
        String html = tools.GetPageSnapshot();
        String targetElement = tools.GetElementHtmlWithFilter(html, dataFilter);
        String proposedLocator = tools.GenerateLocator(targetElement, dataFilter);

        productsPage.ClickOnIcon(proposedLocator);

        GetPage().pause();
    }
}
