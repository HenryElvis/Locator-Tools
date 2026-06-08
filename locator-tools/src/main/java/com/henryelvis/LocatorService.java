package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Locator;

public class LocatorService 
{
    private final PlaywrightService playwrightService;
    private final LocatorGenerator locatorGenerator;

    LocatorService(boolean _useClaude, boolean _headless)
    {
        playwrightService = new PlaywrightService(_headless);
        locatorGenerator = new LocatorGenerator(_useClaude);
    }

    public List<Locator> GetAvailableLocators(String _url, String _type)
    {
        playwrightService.Navigate(_url);

        return playwrightService.GetLocators(_type);
    }

    public Locator AskForTarget(List<Locator> _elements, Scanner _scanner)
    {
        return playwrightService.GetTargetLocator(_elements, _scanner);
    }

    public String GenerateLocator(Locator _targetLocator, String _format, String _type)
    {
        String locatorElement = locatorGenerator.GenerateLocator(_targetLocator, _format, _type);

        boolean isPlaywrightLocator = locatorElement.contains("getBy") ||locatorElement.contains("page.");

        if (!isPlaywrightLocator && !playwrightService.IsElementUnique(locatorElement))
        {
            System.out.println("--- Locator is not unique, we try to correct it---");
            locatorElement = locatorGenerator.AutoCorrectLocator(_targetLocator, locatorElement, _format, _type);
        }

        return locatorElement;
    }

    public void Close()
    {
        playwrightService.close();
    }
}
