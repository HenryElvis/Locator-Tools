package com.henryelvis;

import java.util.ArrayList;
import java.util.List;

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

    public PlaywrightService() 
    {
        this.playwright = Playwright.create();
        this.browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    public void Navigate(String _url)
    {
        this.page = browser.newPage();
        this.page.navigate(_url);
        this.page.waitForLoadState();

        this.page.waitForTimeout(1000);
    }

    public int GetLocatorCount(String _locatorType)
    {
        if (page == null) 
            throw new IllegalStateException("Page is not initialized. Call navigate() first.");

        int count = page.locator(_locatorType).count();

        System.out.println("### Found " + count + " locators of type '" + _locatorType + "' ###");

        return count;
    }

    public List<Locator> GetLocators(String _locatorType, int _count)
    {
        if (page == null) 
            throw new IllegalStateException("Page is not initialized. Call Navigate() first.");

        Locator locatorsElement = page.locator(_locatorType);

        locatorsElement.first().waitFor(new Locator.WaitForOptions().setTimeout(5000));

        int locatorCount = locatorsElement.count();

        if (locatorCount <= 0) 
            throw new IllegalStateException("No locators found for the specified locator type: " + _locatorType);

        locatorCount = Math.min(locatorCount, _count);

        List<Locator> locators = new ArrayList<>();

        for (int i = 0; i < locatorCount; i++) 
            locators.add(locatorsElement.nth(i));

        return locators;
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
