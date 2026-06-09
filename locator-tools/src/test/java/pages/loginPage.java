package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class loginPage 
{
    private final Page page;

    public loginPage(Page _page)
    {
        page = _page;
    }

    public void FillUsername(String _username)
    {
        Locator username = page.getByTestId("username");
        username.fill(_username);
    }

    public void FillPassword(String _password)
    {
        Locator password = page.locator("//input[@id='password']");
        password.fill(_password);
    }

    public void ClickOnLogin()
    {
        Locator loginBtn = page.locator("//input[@data-test='login-button']");
        loginBtn.click();

        // Locator loginBtn = page.getByTestId("login-button");
        // loginBtn.click();
    }
}
