package com.henryelvis;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightService implements AutoCloseable 
{
    private final Playwright playwright;
    private final Browser browser;
    private Page page;

    public PlaywrightService(boolean _headless) 
    {
        this.playwright = Playwright.create();
        this.browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(_headless));
    }

    public void Navigate(String _url)
    {
        this.page = browser.newPage();
        this.page.navigate(_url);
        this.page.waitForLoadState();

        this.page.waitForTimeout(1000);
    }

    public List<Locator> GetLocators(String _locatorType)
    {
        if (page == null) 
            throw new IllegalStateException("Page is not initialized. Call Navigate() first.");

        Locator locatorsElement = page.locator(_locatorType);

        locatorsElement.first().waitFor(new Locator.WaitForOptions().setTimeout(5000));

        int locatorCount = locatorsElement.count();

        if (locatorCount <= 0) 
            throw new IllegalStateException("No locators found for the specified locator type: " + _locatorType);

        List<Locator> locators = new ArrayList<>();

        for (int i = 0; i < locatorCount; i++) 
            locators.add(locatorsElement.nth(i));

        return RemoveHiddenElements(locators);
    }

    private List<Locator> RemoveHiddenElements(List<Locator> _locators)
    {
        List<Locator> visibleLocators = new ArrayList<>();

        for (Locator locator : _locators)
        {
            if (locator.isVisible())
                visibleLocators.add(locator);
        }

        return visibleLocators;
    }

    public Locator GetTargetLocator(List<Locator> _visibleLocators, Scanner _scanner)
    {
        if (_visibleLocators.isEmpty()) 
            throw new IllegalStateException("No locators found for the specified locator type");

        if (_visibleLocators.size() == 1)
            return _visibleLocators.get(0);

        System.out.println();
        System.out.println("### VISIBLE ELEMENTS FOUNDED : ###");
        System.out.println("--------------------------------------------------------------------------------");

        for (int i = 0; i < _visibleLocators.size(); i++)
        {
            Locator element = _visibleLocators.get(i);

            String id = element.getAttribute("id");
            String name = element.getAttribute("class");
            String placeholder = element.getAttribute("placeholder");
            String textContent = element.innerText();

            System.out.printf("[%d] - ID: %s | Name: %s | Placeholder: %s | Text: %s\n", 
                              (i + 1), 
                              (id != null ? id : "N/A"), 
                              (name != null ? name : "N/A"), 
                              (placeholder != null ? placeholder : "N/A"),
                              (!textContent.strip().isEmpty() ? textContent.strip() : "vide"));
        }

        System.out.println("--------------------------------------------------------------------------------");

        int choice = -1;

        while (choice < 1 || choice > _visibleLocators.size())
        {
            System.out.println("### Multiple visible elements detected. Please select the target element by entering its number (1-" + _visibleLocators.size() + ") : ");

            if (_scanner.hasNextInt())
            {
                choice = _scanner.nextInt();
                _scanner.nextLine();
            }
            else
            {
                _scanner.next();
                System.out.println("### Invalid input. Please enter a number between 1 and " + _visibleLocators.size() + " ###");
            }
        }

        return _visibleLocators.get(choice - 1);
    }

    public boolean IsElementUnique(String _locator)
    {
        return page.locator(_locator).count() == 1;
    }

    @Override
    public void close() 
    {
        try
        {
            if (page != null)
                page.close();
            
            if (browser != null)
                browser.close();
            
            if (playwright != null)
                playwright.close();
        }
        catch (Exception e)
        {
            System.out.println("### Error while closing Playwright resources: " + e.getMessage() + " ###");
        }
    }
}
