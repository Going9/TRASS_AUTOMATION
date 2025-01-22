package com.trass_automation.modules;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Component
public class WebDriverFactory {
    public WebDriver createHeadlessDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용하지 않음
        options.addArguments("--remote-allow-origins=*"); // CORS 문제 회피
        return new ChromeDriver();
    }
}
