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

    public LocatorService( boolean _useClaude, boolean _headless)
    {
        playwrightService = new PlaywrightService(_headless);
        locatorGenerator = new LocatorGenerator(_useClaude);
    }

    public LocatorService(Page _page, boolean _useClaude, boolean _headless)
    {
        this(_useClaude, _headless);

        playwrightService.SetPage(_page);
    }

    /**
     * Get element from html snapshot
     * @param _html page snapshot
     * @param _filter to get the element, format and type
     * @return one element with attribute wanted
     */
    public String GetElementHtmlWithFilter(String _html, ElementData _filter)
    {
        Document doc = Jsoup.parse(_html);

        if (_filter.id != null)
        {
            Elements element = doc.select(_filter.typeOfLocator + "#" + _filter.id);

            if (!element.isEmpty())
                return element.first().outerHtml();
        }

        StringBuilder selector = new StringBuilder(_filter.typeOfLocator != null ? _filter.typeOfLocator : "*");

        if (_filter.className != null)
            selector.append(".").append(_filter.className);

        if (_filter.placeholder != null)
            selector.append("[placeholder='").append(_filter.placeholder).append("']");

        if (_filter.name != null)
            selector.append("[name='").append(_filter.name).append("']");

        Elements elements = doc.select(selector.toString());

        if (_filter.textContent != null)
            elements = new Elements(
                elements.stream()
                        .filter(e -> e.text().contains(_filter.textContent))
                        .toList());
        
        return elements.get(0).outerHtml();
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
     * Generate a locator from AI model with a locator 
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

    /**
     * Generate locator and copy it to clipboard
     * @param _outerHTML snapshot html element
     * @param _filter format of element and type
     * @return the locator find
     */
    public String GenerateLocator(String _outerHTML, ElementData _filter)
    {
        String format = (_filter.formatOfLocator != null) ? _filter.formatOfLocator : "xpath";
        String tag = (_filter.typeOfLocator != null) ? _filter.typeOfLocator : "element";

        String locatorElement = locatorGenerator.GenerateFromHtml(_outerHTML, format, tag);

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
