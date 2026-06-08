package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Locator;

public class App 
{
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
