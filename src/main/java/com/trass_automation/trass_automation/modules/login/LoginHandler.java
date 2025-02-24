package com.trass_automation.trass_automation.modules.login;

import com.trass_automation.trass_automation.modules.utils.ElementWaiter;
import com.trass_automation.trass_automation.modules.verification.CheckCaptchaHandler;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LoginHandler implements LoginStrategy{

    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final CheckCaptchaHandler checkCaptchaHandler;

    @Override
    public void login(WebDriver driver, String id, String password) {
        try {
            ElementWaiter elementWaiter = new ElementWaiter(driver);

            // 로그인 페이지 호출
            driver.get("https://www.bandtrass.or.kr/login.do?returnPage=M");
            checkCaptchaHandler.checkForCaptcha(driver);

            // 로그인 정보 입력
            WebElement idBox = elementWaiter.awaitElementClickable(By.cssSelector("#id.form-control"));
            idBox.sendKeys(id);
            logger.info("Input id");

            WebElement passwordBox = elementWaiter.awaitElementClickable(By.cssSelector("#pw.form-control"));
            passwordBox.sendKeys(password);
            logger.info("Input password");

            WebElement loginButton = elementWaiter.awaitElementClickable(By.cssSelector("#page-wrapper > div > div > div:nth-child(2) > div > table > tbody > tr:nth-child(1) > td:nth-child(2) > button"));
            loginButton.click();
            logger.info("Click loginButton");

            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
                WebElement changePassButton = shortWait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("#pass_change > div > div > div.modal-body.inquiry_box > button.btn.btn-outline.btn-primary")
                        )
                );
                logger.info("Find change pass button");
                changePassButton.click();
                logger.info("Click changePassButton");
            } catch (TimeoutException e) {
                logger.info("Change pass button not found, skipping click");
            }

            elementWaiter.awaitUrl("https://www.bandtrass.or.kr/index.do");
            checkCaptchaHandler.checkForCaptcha(driver);
            logger.info("Success to login TRASS");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
