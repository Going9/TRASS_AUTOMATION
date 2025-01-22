package com.trass_automation.trass_automation.modules.login;

import com.trass_automation.trass_automation.modules.verification.RetryHandler;
import com.trass_automation.trass_automation.utils.CheckCaptchaHandler;
import com.trass_automation.trass_automation.utils.WaitForElements;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TRASSLoginHandler implements LoginStrategy{

    private final Logger logger = LoggerFactory.getLogger(TRASSLoginHandler.class);
    private final WaitForElements waitForElements;
    private final RetryHandler retryHandler;

    @Override
    public void login(WebDriver driver, String id, String password) {
        try {
            retryHandler.executeWithRetry(driver, drv -> {
                // 로그인 페이지 호출
                drv.get("https://www.bandtrass.or.kr/login.do?returnPage=M");

                // 로그인 정보 입력
                WebElement idBox = waitForElements.waitForElementToBeClickable(drv, "#id.form-control", 30);
                idBox.sendKeys(id);
                logger.info("Input id");

                WebElement passwordBox = waitForElements.waitForElementToBeClickable(drv, "#pw.form-control", 30);
                passwordBox.sendKeys(password);
                logger.info("Input password");

                WebElement loginButton = waitForElements.waitForElementToBeClickable(drv, "#page-wrapper > div > div > div:nth-child(2) > div > table > tbody > tr:nth-child(1) > td:nth-child(2) > button", 30);
                loginButton.click();
                logger.info("Click loginButton");

                WebElement changePassButton = waitForElements.waitForElementToBeClickable(drv, "#pass_change > div > div > div.modal-body.inquiry_box > button.btn.btn-outline.btn-primary", 30);
                changePassButton.click();
                logger.info("Click changePassButton");
            }, 5, 2000);

            waitForElements.waitForUrlToBe(driver,"https://www.bandtrass.or.kr/index.do", 30);
            logger.info("Success to login TRASS");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
