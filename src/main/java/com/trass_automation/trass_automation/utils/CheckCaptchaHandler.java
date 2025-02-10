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
            // **1️⃣ CAPTCHA 감지 여부 확인**
            WebElement captchaModal = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("#modal_captcha > div > div > div.modal-body")
                    )
            );

            logger.warn("CAPTCHA detected! Waiting for user to solve...");

            // **2️⃣ CAPTCHA 해결을 위한 대기 시간 (사용자가 해결할 동안 대기)**
            int waitTime = 180; // 120초 (2분) 대기
            for (int i = waitTime; i > 0; i--) {
                System.out.print("\rWaiting for CAPTCHA to be solved: " + i + " seconds remaining...");
                Thread.sleep(1000);
            }
            System.out.println("\nCAPTCHA solving time ended.");

            // **3️⃣ Alert 확인 후 자동으로 "OK" 클릭**
            try {
                Alert alert = driver.switchTo().alert();
                logger.info("Alert found: " + alert.getText());
                alert.accept();  // OK 버튼 클릭
                logger.info("Alert dismissed.");
            } catch (NoAlertPresentException ex) {
                logger.info("No alert found, checking for modal.");
            }

            // **4️⃣ CSS 기반 모달 확인 후 자동 클릭**
            try {
                WebElement modalOkButton = shortWait.until(
                        ExpectedConditions.elementToBeClickable(By.cssSelector(".modal-footer .btn-primary"))
                );
                modalOkButton.click();
                logger.info("Modal dismissed.");
            } catch (Exception e) {
                logger.info("No modal detected.");
            }

        } catch (TimeoutException e) {
            // 3초 안에 CAPTCHA가 감지되지 않으면 없는 것으로 간주
            logger.info("Captcha not detected, proceeding with crawling.");

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while checking for captcha", e);
        }
    }


    /**
     * reCAPTCHA 자동 해결 메서드
     *
     * @param driver WebDriver 인스턴스
     * @return 성공 여부 (true: 해결됨, false: 해결 실패)
     */
    private boolean solveRecaptcha(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // **1️⃣ reCAPTCHA iframe 내부로 이동**
            WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("iframe[title='reCAPTCHA']")
            ));
            driver.switchTo().frame(iframe);
            logger.info("Switched to reCAPTCHA iframe");

            // **2️⃣ reCAPTCHA 체크박스 클릭**
            WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.className("recaptcha-checkbox-border")
            ));
            checkbox.click();
            logger.info("Clicked on reCAPTCHA checkbox");

            // **3️⃣ 원래 페이지로 돌아가기**
            driver.switchTo().defaultContent();

            // **4️⃣ reCAPTCHA 해결 확인**
            boolean captchaSolved = wait.until(ExpectedConditions.attributeContains(
                    By.cssSelector("iframe[title='reCAPTCHA']"), "src", "recaptcha/api2/anchor"
            ));

            return captchaSolved;

        } catch (Exception e) {
            logger.error("Error solving reCAPTCHA: " + e.getMessage());
            return false;
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
