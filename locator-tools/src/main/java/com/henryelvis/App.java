package com.henryelvis;

import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);

        String defaultURL = "https://www.saucedemo.com/";
        String defaultLocatorType = "button";
        String defaultFormatType = "locator";

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

        System.out.println("### Extracting locators from '" + URL + "' with locator type '" + locatorType + "' and format type '" + formatType + "' ###");
    }
}

