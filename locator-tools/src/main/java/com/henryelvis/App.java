package com.henryelvis;

import java.util.List;
import java.util.Scanner;

import com.microsoft.playwright.Locator;

public class App 
{
    public static void main( String[] args )
    {
        final String URL = "https://www.saucedemo.com/";
        final String TYPE = "input";
        final String FORMAT = "locator";
        final boolean HEADLESS = true;
        final boolean CLAUDE = false;

        GenerateLocator(URL, FORMAT, TYPE, HEADLESS, CLAUDE);
    }

    // --------------------------
    // saucedemo.com test Locator
    // --------------------------

    static void GenerateLocator(String _url, String _format, String _type, boolean _headless, boolean _useClaude)
    {
        Scanner scanner = new Scanner(System.in);
        LocatorService locatorService = new LocatorService(_useClaude, _headless);

        try 
        {
            List<Locator> elements = locatorService.GetAvailableLocators(_url, _type);

            Locator target = locatorService.AskForTarget(elements, scanner);

            String proposedLocator = locatorService.GenerateLocator(target, _format, _type);
            
            System.out.println("Locator généré : " + proposedLocator);
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
