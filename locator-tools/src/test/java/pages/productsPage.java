package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class productsPage 
{
    private final Page page;

    private final Locator bagLocator;
    private final Locator bikeLocator;
    private final Locator basketLocator;

    public productsPage(Page _page)
    {
        page = _page;

        bagLocator = page.locator("");
        bikeLocator = page.locator("");
        basketLocator = page.locator("");
    }

    public void AddBagToCart()
    {
        Locator bagProduct = bagLocator;
        bagProduct.click();
    }

    public void AddBikeToCart()
    {
        Locator bikeProduct = bikeLocator;
        bikeProduct.click();
    }

    public void CheckoutProduct()
    {
        Locator checkout = basketLocator;
        checkout.click();
    }
}
