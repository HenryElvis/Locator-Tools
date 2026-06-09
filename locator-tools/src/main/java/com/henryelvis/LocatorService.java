package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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

    public String GetElementHtmlWithFilter(String _html, String _tagType, ElementData filter)
    {
        Document doc = Jsoup.parse(_html);

        if (filter.id != null)
        {
            Elements element = doc.select(_tagType + "#" + filter.id);

            if (!element.isEmpty())
                return element.first().outerHtml();
        }

        StringBuilder selector = new StringBuilder(_tagType);

        if (filter.className != null)
            selector.append(".").append(filter.className);

        if (filter.placeholder != null)
            selector.append("[placeholder='").append(filter.placeholder).append("']");

        if (filter.name != null)
            selector.append("[name='").append(filter.name).append("']");

        Elements elements = doc.select(selector.toString());

        if (filter.textContent != null)
            elements = new Elements(
                elements.stream()
                        .filter(e -> e.text().contains(filter.textContent))
                        .toList());
        
        return elements.get(0).outerHtml();
    }

    /**
     * 
     * @param _page
     */
    public void AttachToPage(Page _page)
    {
        activePage = _page;
    }

    public String GetPageSnapshot()
    {
        return playwrightService.GetHTMLSnapshot();
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

        if (!isPlaywrightLocator && !playwrightService.IsElementUnique(locatorElement))
        {
            System.out.println("--- Locator is not unique, we try to correct it---");
            locatorElement = locatorGenerator.AutoCorrectLocator(_targetLocator, locatorElement, _format, _type);
        }

        CopyToClipboard(locatorElement);

        return locatorElement;
    }

    public String GenerateLocator(String _outerHTML, String _formatType, String _locatorName)
    {
        String locatorElement = locatorGenerator.GenerateFromHtml(_outerHTML, _formatType, _locatorName);

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
