package com.trass_automation.trass_automation.modules.verification;

import com.trass_automation.trass_automation.utils.CheckCaptchaHandler;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RetryHandler {

    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);
    private final CheckCaptchaHandler checkCaptchaHandler;

    public void executeWithRetry(
            WebDriver driver, CaptchaRetryableOperation operation, int maxRetries, long delayMillis) throws Exception {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("attempt {}/{}", attempt,maxRetries);

                // 작업 수행
                operation.execute(driver);

                if (!checkCaptchaHandler.checkForCaptcha(driver)) {
                    logger.info("Success operation");
                    return;
                } else {
                    logger.warn("Prepare retries..");
                    driver.navigate().refresh();
                    logger.info("Refresh current page");
                }
            } catch (Exception e) {
                logger.error("Catch error while operating: ", e);
                if (attempt == maxRetries) {
                    logger.error("Exceed max retries");
                    throw e;
                }
            }
        }

        // 재시도 전 대기
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Catch error while preparing retries");
        }

        // 모든 시도 후에도 캡챠가 감지됨
        throw new RuntimeException("Cannot operating because of the captcha");
    }
}
