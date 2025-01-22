package com.trass_automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class CheckCaptcha {

    private static final Logger logger = LoggerFactory.getLogger(CheckCaptcha.class);

    public void checkForCaptcha(WebDriver driver) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            WebElement captchaModal = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("#modal_captcha > div > div > div.modal-body")
                    )
            );
            logger.warn("Captcha detected.");

        } catch (TimeoutException e) {
            // 2초 안에 안 뜨면 없는 것으로 간주
            logger.warn("Captcha not detected");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while checking for captcha", e);
        }
    }
}
