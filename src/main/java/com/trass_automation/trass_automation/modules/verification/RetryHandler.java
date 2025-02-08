package com.trass_automation.trass_automation.modules.verification;

import com.trass_automation.trass_automation.utils.CheckCaptchaHandler;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RetryHandler {

    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);
    private final CheckCaptchaHandler checkCaptchaHandler;

    public void executeWithRetry(WebDriver driver, CaptchaRetryableOperation operation, int maxRetries, long delayMillis) throws Exception {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("attempt {}/{}", attempt,maxRetries);

                // 작업 수행
                operation.execute(driver);
                return;

            } catch (Exception e) {
                logger.error("Catch error while operating: ", e);
                closeAllWindows(driver);

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
    }

    // 현재 모든 창을 닫는 메서드 예시
    private void closeAllWindows(WebDriver driver) {
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            driver.close();
        }
        // Selenium 4 이상일 경우, 새 창을 열어 세션을 유지
        driver.switchTo().newWindow(WindowType.WINDOW);
    }
}

