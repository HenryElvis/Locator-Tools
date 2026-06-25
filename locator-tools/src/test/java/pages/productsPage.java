package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class productsPage 
{
    private final Page page;

    private Locator productLocator;
    private Locator iconLocator;

    public productsPage(Page _page)
    {
        page = _page;
    }

    public void AddProduct(String _product)
    {
        productLocator = page.locator(_product);
        productLocator.click();
    }

    public void ClickOnIcon(String _icon)
    {
        iconLocator = page.locator(_icon);
        iconLocator.click();
    }
}
