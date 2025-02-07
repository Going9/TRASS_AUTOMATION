package com.trass_automation.trass_automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ElementWaiter {
    private WebDriverWait wait;

    public ElementWaiter(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(180));
    }

    public void awaitUrl(String url) {
        wait.until(ExpectedConditions.urlToBe(url));
    }

    public void awaitElementVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement awaitElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement awaitElementPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void awaitNewWindowOpen() {
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
    }
}
