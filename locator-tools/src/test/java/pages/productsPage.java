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

        bagLocator = page.getByTestId("add-to-cart-sauce-labs-backpack");
        bikeLocator = page.getByTestId("add-to-cart-sauce-labs-bike-light");
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
