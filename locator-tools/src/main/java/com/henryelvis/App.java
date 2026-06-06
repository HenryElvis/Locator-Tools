package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Locator;

public class App 
{
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);

        String defaultURL = "https://www.saucedemo.com/";
        String defaultLocatorType = "button";
        String defaultFormatType = "locator";

        final int defaultLocatorCount = 3;

        System.out.print("### Extract Locators tools ###");
        System.out.print("### Enter site URL : ");

        String URL = scanner.nextLine();

        if (URL.isEmpty()) 
            URL = defaultURL;

        System.out.print("### Enter locator type (ex: button, a, input) : ");

        String locatorType = scanner.nextLine();

        if (locatorType.isEmpty()) 
            locatorType = defaultLocatorType;

        System.out.print("### Enter format type (ex: xpath, locator) : ");

        String formatType = scanner.nextLine();

        if (formatType.isEmpty()) 
            formatType = defaultFormatType;

        System.out.println("### ------------------- ###");

        System.out.println("### Extracting locators from '" + URL + "' with locator type '" + locatorType + "' and format type '" + formatType + "' ###");

        System.out.println("### ------------------- ###");

        try (PlaywrightService playwrightService = new PlaywrightService())
        {
            playwrightService.navigate(URL);

            int locatorsFound = playwrightService.getLocatorCount(locatorType);

            List<Locator> locatorsElement = playwrightService.getLocators(locatorType, defaultLocatorCount);

            
        }
    }
}
