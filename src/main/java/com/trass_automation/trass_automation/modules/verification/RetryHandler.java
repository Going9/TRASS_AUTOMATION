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

    public void executeWithRetry(WebDriver driver, CaptchaRetryableOperation operation, int maxRetries, long delayMillis) throws Exception {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("attempt {}/{}", attempt, maxRetries);
                operation.execute(driver);
                return;

            } catch (CheckCaptchaHandler.CaptchaDetectedException e) {
                logger.warn("Captcha detected on attempt {}/{}, retrying...", attempt, maxRetries);
                closeAllWindowsExceptOne(driver);  // 모든 창을 닫고 다시 시도

                if (attempt == maxRetries) {
                    logger.error("Max retries exceeded due to Captcha detection");
                    driver.quit();  // 마지막 재시도 실패 시 WebDriver 종료
                    throw e;
                }

                // 일정 시간 대기 후 재시도
                Thread.sleep(delayMillis);

            } catch (Exception e) {
                logger.error("Catch error while operating: ", e);
                closeAllWindowsExceptOne(driver);

                if (attempt == maxRetries) {
                    logger.error("Exceed max retries");
                    driver.quit();  // 마지막 재시도 실패 시 WebDriver 종료
                    throw e;
                }
            }
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

    private void closeAllWindowsExceptOne(WebDriver driver) {
        var windowHandles = driver.getWindowHandles();
        if (windowHandles.size() > 1) {
            String firstWindow = windowHandles.iterator().next(); // 첫 번째 창의 핸들값 저장
            for (String handle : windowHandles) {
                if (!handle.equals(firstWindow)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
            }
            driver.switchTo().window(firstWindow); // 남겨둔 창으로 전환
        } else {
            logger.warn("Only one window remains, skipping window closure.");
        }
    }
}

