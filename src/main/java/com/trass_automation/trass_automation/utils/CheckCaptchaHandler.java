package com.trass_automation.trass_automation.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CheckCaptchaHandler {

    private static final Logger logger = LoggerFactory.getLogger(CheckCaptchaHandler.class);

    /**
     * 캡챠 확인 및 해결 메서드
     *
     * @param driver WebDriver 인스턴스
     * @throws CaptchaDetectedException 캡챠 감지되었으나 해결 실패 시 예외 발생
     */
    public void checkForCaptcha(WebDriver driver) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

        try {
            // 1. CAPTCHA 감지 여부 확인
            WebElement captchaModal = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("#modal_captcha > div > div > div.modal-body")
                    )
            );

            logger.warn("CAPTCHA detected! Waiting for user to solve...");

            // 2. CAPTCHA 해결을 위한 대기
            int waitTime = 180; // 180초 (3분) 대기
            for (int i = waitTime; i > 0; i--) {
                logger.info("Waiting captcha for {} seconds...", i);
                Thread.sleep(1000);
            }

            try {
                Alert alert = driver.switchTo().alert();
                alert.accept();  // OK 버튼 클릭
                logger.info("Captcha successfully verified!");

            } catch (NoAlertPresentException e) {
                logger.error("Cannot find alert.");
            }

        } catch (TimeoutException e) {
            // 3초 안에 CAPTCHA가 감지되지 않으면 없는 것으로 간주
            logger.info("Captcha not detected, proceeding with crawling.");

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while checking for captcha", e);
        }
    }

    /**
     * 캡챠 감지 예외
     */
    public static class CaptchaDetectedException extends RuntimeException {
        public CaptchaDetectedException(String message) {
            super(message);
        }
    }
}
