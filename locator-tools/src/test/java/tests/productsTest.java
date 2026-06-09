package tests;

import org.junit.jupiter.api.Test;

import com.henryelvis.ElementData;
import com.henryelvis.LocatorService;

import pages.loginPage;
import pages.productsPage;
import tools.baseTest;

public class productsTest extends baseTest 
{
    private loginPage loginPage;
    private productsPage productsPage;
    
    @Test
    void findLocator()
    {
        playwright.selectors().setTestIdAttribute("data-test");

        // Login 
        {
            playwright.selectors().setTestIdAttribute("data-test");

            loginPage = new loginPage(GetPage());
            productsPage = new productsPage(GetPage());

            loginPage.FillUsername("standard_user");
            loginPage.FillPassword("secret_sauce");

            loginPage.ClickOnLogin();
        }
        //

        GetPage().pause();

        ElementData dataFilter = new ElementData()
            .withType("")
            .withFormat("");

        LocatorService tools = new LocatorService(GetPage(), false, false);
        String html = tools.GetPageSnapshot();

        String targetElement = tools.GetElementHtmlWithFilter(html, dataFilter);
        String proposedLocator = tools.GenerateLocator(targetElement, dataFilter);

        GetPage().pause();
    }

    @Test
    void testAddProduct()
    {
        // Login 
        {
            playwright.selectors().setTestIdAttribute("data-test");

            loginPage = new loginPage(GetPage());
            productsPage = new productsPage(GetPage());

            loginPage.FillUsername("standard_user");
            loginPage.FillPassword("secret_sauce");

            loginPage.ClickOnLogin();
        }
        //

        productsPage.AddBagToCart();
        productsPage.AddBikeToCart();
    }

    @Test
    void testGoToCheckout()
    {
        // Login 
        {
            playwright.selectors().setTestIdAttribute("data-test");

            loginPage = new loginPage(GetPage());
            productsPage = new productsPage(GetPage());

            loginPage.FillUsername("standard_user");
            loginPage.FillPassword("secret_sauce");

            loginPage.ClickOnLogin();
        }
        //

        productsPage.CheckoutProduct();
    }
}
