package tests;

import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.henryelvis.LocatorService;
import com.microsoft.playwright.Locator;

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
        
        LocatorService tools = new LocatorService(getPage(), false, false);
        
        List<Locator> elements = tools.GetAvailableLocators(typeOfTargetLocator);

        try (Scanner scanner = new Scanner(System.in))
        {
            Locator targetLocator = tools.AskForTarget(elements, scanner);

            String proposedLocator = tools.GenerateLocator(targetLocator, "locator", typeOfTargetLocator);
            System.out.println("Proposed path : " + proposedLocator);
        }

        getPage().pause();
    }
}
