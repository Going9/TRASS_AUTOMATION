package com.trass_automation.trass_automation.modules.verification;

import org.openqa.selenium.WebDriver;

@FunctionalInterface
public interface CaptchaRetryableOperation {
    public void execute(WebDriver driver);
}
