package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Locator;

public class App 
{
    // TODO: #2 Add an option to get css selector
    // TODO: #3 Factorize code and add some summary comments for each method. Maybe add JavaDoc comments.
    // TODO: #4 Clipboard copy of the proposed locator
    // TODO: #5 Remove main method and create a specific class to handle locator extraction logic
    // TODO: #6 Add graphical interface

    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);

        final String URL = "https://www.saucedemo.com/";
        final String TYPE = "input";
        final String FORMAT = "locator";
        final boolean HEADLESS = true;

        LocatorService locatorService = new LocatorService(false, HEADLESS);

        try
        {
            List<Locator> elements = locatorService.GetAvailableLocators(URL, TYPE);

            Locator target = locatorService.AskForTarget(elements, scanner);
            String proposedLocator = locatorService.GenerateLocator(target, FORMAT, TYPE);

            System.out.println("Proposed locator : " + proposedLocator);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally
        {
            locatorService.Close();
            scanner.close();
        }
    }
}
