package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Locator;

public class App 
{
    // TODO #2: Add an option to get css selector
    // TODO #3: Factorize code and add some summary comments for each method. Maybe add JavaDoc comments.

    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);

        boolean useClaude = false;

        String defaultURL = "https://www.saucedemo.com/";
        String defaultLocatorType = "button";
        String defaultFormatType = "locator";

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

            List<Locator> locatorsElement = playwrightService.GetLocators(locatorType);
            Locator targetLocator = playwrightService.GetTargetLocator(locatorsElement, scanner);

            String proposedPath = locatorGenerator.GenerateLocator(targetLocator, formatType, locatorType);

            if (formatType.trim().equalsIgnoreCase("xpath"))
            {
                if (playwrightService.IsElementUnique(proposedPath)) 
                {
                    System.out.println("--- Element ---");
                    System.out.println("Proposed path : " + proposedPath);
                    System.out.println();
                } 
                else 
                {
                    System.out.println("### Warning: The proposed locator is not unique on the page. Agent will consider refining it. ###");

                    int attemps = 0;

                    while (!playwrightService.IsElementUnique(proposedPath) && attemps < 5)
                    {
                        proposedPath = locatorGenerator.AutoCorrectLocator(targetLocator, proposedPath, formatType, locatorType);
                        attemps++;
                    }

                    if (playwrightService.IsElementUnique(proposedPath)) 
                    {
                        System.out.println("--- Element ---");
                        System.out.println("Proposed path : " + proposedPath);
                        System.out.println();
                    } 
                    else 
                    {
                        System.out.println("### Failed to generate a unique locator after " + attemps + " attempts. Please review the proposed locator: " + proposedPath + " ###");
                    }
                }
            }
            else
            {
                System.out.println("--- Element ---");
                System.out.println("Proposed path : " + proposedPath);
                System.out.println();

                // TODO #1 : Find a way to check if playwright locator is unique. Maybe ask user if he wants to call AutoCorrectLocator until it's unique or user is satisfied with the proposed locator.
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
