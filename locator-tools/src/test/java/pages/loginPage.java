package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class loginPage 
{
    private final Page page;

    private final Locator usernameLocator;
    private final Locator passwordLocator;
    private final Locator signinLocator;

    public loginPage(Page _page)
    {
        page = _page;

        usernameLocator = page.getByTestId("username");
        passwordLocator = page.locator("//input[@id='password']");
        signinLocator = page.locator("//input[@data-test='login-button']");
    }

    public void FillUsername(String _username)
    {
        Locator username = usernameLocator;
        username.fill(_username);
    }

    public void FillPassword(String _password)
    {
        Locator password = passwordLocator;
        password.fill(_password);
    }

    public void ClickOnLogin()
    {
        Locator loginBtn = signinLocator;
        loginBtn.click();

        // Locator loginBtn = page.getByTestId("login-button");
        // loginBtn.click();
    }
}
