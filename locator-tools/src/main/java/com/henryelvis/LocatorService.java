package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class LocatorService 
{
    private final PlaywrightService playwrightService;
    private final LocatorGenerator locatorGenerator;

    private Page activePage;

    public LocatorService(boolean _useClaude, boolean _headless)
    {
        playwrightService = new PlaywrightService(_headless);
        locatorGenerator = new LocatorGenerator(_useClaude);

        activePage = playwrightService.GetPage();
    }

    public LocatorService(Page _page, boolean _useClaude, boolean _headless)
    {
        this(_useClaude, _headless);

        playwrightService.SetPage(_page);
        activePage = _page;
    }

    /**
     * 
     * @param _page
     */
    public void AttachToPage(Page _page)
    {
        activePage = _page;
    }

    /**
     * Get a list of locator, depends on type
     * @param _url of the site
     * @param _type of locator, can be xpath or playwright locator
     * @return all a list of visible locator with specific type
     */
    public List<Locator> GetAvailableLocators(String _url, String _type)
    {
        playwrightService.Navigate(_url);

        return playwrightService.GetLocators(_type);
    }

    /**
     * 
     * @param _type
     * @return
     */
    public List<Locator> GetAvailableLocators(String _type)
    {
        return playwrightService.GetLocators(_type);
    }

    /**
     * Ask user to choose locator from a list of locator
     * @param _elements with all same type
     * @param _scanner for get the user input
     * @return a list of locator
     */
    public Locator AskForTarget(List<Locator> _elements, Scanner _scanner)
    {
        return playwrightService.GetTargetLocator(_elements, _scanner);
    }

    /**
     * Generate a locator from AI model
     * @param _targetLocator the locator who has been choose by user
     * @param _format can be xpath or playwright locator
     * @param _type of locator (ex: input, button etc...)
     * @return the locator who has been generate
     */
    public String GenerateLocator(Locator _targetLocator, String _format, String _type)
    {
        String locatorElement = locatorGenerator.GenerateLocator(_targetLocator, _format, _type);

        boolean isPlaywrightLocator = locatorElement.contains("getBy") ||locatorElement.contains("page.");

        if (!isPlaywrightLocator /*&& !playwrightService.IsElementUnique(locatorElement)*/)
        {
            System.out.println("--- Locator is not unique, we try to correct it---");
            locatorElement = locatorGenerator.AutoCorrectLocator(_targetLocator, locatorElement, _format, _type);
        }

        CopyToClipboard(locatorElement);

        return locatorElement;
    }

    /**
     * Copy element
     * @param _proposedPath locator to copy
     */
    private void CopyToClipboard(String _proposedPath)
    {
        StringSelection selection = new StringSelection(_proposedPath);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(selection, null);
    }

    /**
     * Close safely playwright without a risk of memory leak
     */
    public void Close()
    {
        playwrightService.close();
    }
}
