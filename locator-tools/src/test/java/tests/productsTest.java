package tests;

import org.junit.jupiter.api.Test;

import pages.productsPage;
import tools.baseTest;

public class productsTest extends baseTest 
{
    private productsPage productsPage;
    
    @Test
    void testAddProduct()
    {
        productsPage = new productsPage(getPage());
        
    }
}
