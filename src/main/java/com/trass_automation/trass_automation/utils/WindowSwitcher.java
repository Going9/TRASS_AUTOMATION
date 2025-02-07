package com.trass_automation.trass_automation.utils;

import org.openqa.selenium.WebDriver;

import java.util.Set;

public class WindowSwitcher {
    private WebDriver driver;
    private ElementWaiter elementWaiter;

    public WindowSwitcher(WebDriver driver, ElementWaiter elementWaiter) {
        this.driver = driver;
        this.elementWaiter = elementWaiter;
    }

    public void switchToNewWindow(String originalWindow) {
        elementWaiter.awaitNewWindowOpen();

        // 모든 핸들 창 가져오기
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                return;
            }
        }
        throw new RuntimeException("No new window");
    }
}
