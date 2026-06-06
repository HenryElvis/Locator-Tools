package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Locator;

public class App 
{
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);

        boolean useClaude = false;

        String defaultURL = "https://www.saucedemo.com/";
        String defaultLocatorType = "button";
        String defaultFormatType = "locator";

        final int defaultLocatorCount = 3;

        System.out.println("### Extract Locators tools ###");
        System.out.println("### Enter site URL : ");

        String URL = scanner.nextLine();

        if (URL == null || URL.trim().isEmpty()) 
            URL = defaultURL;

        System.out.println("### Enter locator type (ex: button, a, input) : ");

        String locatorType = scanner.nextLine();
        if (locatorType == null || locatorType.trim().isEmpty()) locatorType = defaultLocatorType;

        System.out.println("### Enter format type (ex: xpath, locator) : ");

        String formatType = scanner.nextLine();
        if (formatType == null || formatType.trim().isEmpty()) formatType = defaultFormatType;

        System.out.println("### Ollama AI model is free to use but requires local installation and setup.");
        System.out.println("### Do you want to use Claude AI model (token required) ? (yes/no) : ");

        String useClaudeInput = scanner.nextLine();
        if (useClaudeInput != null && useClaudeInput.trim().equalsIgnoreCase("yes"))
            useClaude = true;

        System.out.println("### ------------------- ###");
        System.out.println("### Extracting locators from '" + URL + "' with locator type '" + locatorType + "' and format type '" + formatType + "' ###");
        System.out.println("### ------------------- ###");

        try (PlaywrightService playwrightService = new PlaywrightService())
        {
            LocatorGenerator locatorGenerator = new LocatorGenerator(useClaude);
            playwrightService.Navigate(URL);

            // int locatorsFound = playwrightService.GetLocatorCount(locatorType);

            List<Locator> locatorsElement = playwrightService.GetLocators(locatorType, defaultLocatorCount);

            for (int i = 0; i < locatorsElement.size(); i++)
            {
                Locator locatorElement = locatorsElement.get(i);

                String proposedPath = locatorGenerator.GenerateLocator(locatorElement, formatType, locatorType);

                System.out.println("--- Element " + (i + 1) + " ---");
                System.out.println("Proposed path : " + proposedPath);
                System.out.println();
            }
        }
        catch (Exception e) 
        {
            System.out.println("### An error occurred: " + e.getMessage() + " ###");
        }
        finally 
        {
            scanner.close();
        }
    }
}
