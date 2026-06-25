package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class loginPage 
{
    private final Page page;

    private final Locator usernameLocator;
    private final Locator passwordLocator;
    private final Locator loginLocator;

    public loginPage(Page _page)
    {
        page = _page;

        usernameLocator = page.locator("//input[@data-test='username']");
        passwordLocator = page.locator("//input[@data-test='password']");
        loginLocator = page.locator("//input[@data-test='login-button']");
    }

    public void FillUsername(String _username)
    {
        usernameLocator.fill(_username);
    }

    public void FillPassword(String _password)
    {
        passwordLocator.fill(_password);
    }

    public void ClickOnLogin()
    {
        loginLocator.click();
    }
}
