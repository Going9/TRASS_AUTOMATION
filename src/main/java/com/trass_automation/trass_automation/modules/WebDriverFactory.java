package com.trass_automation.trass_automation.modules;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class WebDriverFactory {
    public WebDriver createHeadlessDriver() throws IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // 최신 Chrome에서는 --headless 대신 --headless=new 사용
        // 프로필 분리를 위해, 임시 폴더를 유니크하게 생성해서 user-data-dir로 넣어줌
        String tempUserDataDir = Files.createTempDirectory("chrome_profile_").toAbsolutePath().toString();
        options.addArguments("--user-data-dir=" + tempUserDataDir);
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용하지 않음
        options.addArguments("--remote-allow-origins=*"); // CORS 문제 회피

        return new ChromeDriver(options);
    }

    public WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        return new ChromeDriver(options);
    }
}

