package com.trass_automation.trass_automation.modules.login;

import com.trass_automation.trass_automation.modules.verification.RetryHandler;
import com.trass_automation.trass_automation.utils.ElementWaiter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TRASSLoginHandler implements LoginStrategy{

    private final Logger logger = LoggerFactory.getLogger(TRASSLoginHandler.class);
    private final RetryHandler retryHandler;

    @Override
    public void login(WebDriver driver, String id, String password) {
        try {
            ElementWaiter elementWaiter = new ElementWaiter(driver);

            retryHandler.executeWithRetry(driver, drv -> {
                // 로그인 페이지 호출
                drv.get("https://www.bandtrass.or.kr/login.do?returnPage=M");

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

                WebElement changePassButton = elementWaiter.awaitElementClickable(By.cssSelector("#pass_change > div > div > div.modal-body.inquiry_box > button.btn.btn-outline.btn-primary"));
                changePassButton.click();
                logger.info("Click changePassButton");
            }, 5, 2000);

            elementWaiter.awaitUrl("https://www.bandtrass.or.kr/index.do");
            logger.info("Success to login TRASS");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
