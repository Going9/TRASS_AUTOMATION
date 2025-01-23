package com.trass_automation.trass_automation.modules.verification;

import org.openqa.selenium.WebDriver;

@FunctionalInterface
public interface CaptchaRetryableOperation {
    void execute(WebDriver driver) throws InterruptedException;
}
