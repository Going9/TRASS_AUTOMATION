package com.trass_automation.trass_automation.modules.login;

import org.openqa.selenium.WebDriver;

public interface LoginStrategy {
    void login(WebDriver driver, String id, String password);
}
