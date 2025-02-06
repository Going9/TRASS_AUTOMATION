package com.trass_automation.trass_automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

public class WaitForElements {
    private WebDriverWait wait;

    public WaitForElements(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void waitForUrlToBe(String url) {
        wait.until(ExpectedConditions.urlToBe(url));
    }

    public void waitForTableLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table#table_list_1 tr[id]")));
    }

    public WebElement waitForElementToBeClickable(String cssSelector) {
        return wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
    }

    public WebElement waitForElementToBePresent(String cssSelector) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
    }
}
