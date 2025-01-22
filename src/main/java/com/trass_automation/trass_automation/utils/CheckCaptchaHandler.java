package com.trass_automation.trass_automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CheckCaptchaHandler {

    private static final Logger logger = LoggerFactory.getLogger(CheckCaptchaHandler.class);

    /**]
     * 캡챠 확인 메서드
     *
     * @param driver WebDriver 인스턴스
     * @return true: 캡챠 감지됨, false: 캡챠 감지되지 않음
     */
    public boolean checkForCaptcha(WebDriver driver) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            WebElement captchaModal = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("#modal_captcha > div > div > div.modal-body")
                    )
            );
            logger.warn("Captcha detected.");
            return true;

        } catch (TimeoutException e) {
            // 2초 안에 안 뜨면 없는 것으로 간주
            logger.warn("Captcha not detected");
            return false;

        } catch (Exception e) {
            logger.error("Unexpected error occurred while checking for captcha", e);
            return false;
        }
    }
}
