package com.trass_automation.trass_automation.modules;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebDriverFactory {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public WebDriver createHeadlessDriver() {
        ChromeOptions options = new ChromeOptions();

        // Headless 모드 설정
        options.addArguments("--headless=new");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=" + USER_AGENT);

        // 1. 새로운 임시 사용자 프로필 생성 (쿠키, 캐시 등 초기화)
        try {
            String tempUserDataDir = Files.createTempDirectory("chrome_profile_").toAbsolutePath().toString();
            options.addArguments("--user-data-dir=" + tempUserDataDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. 압축 풀린 확장 프로그램 로드
        File extensionDir = new File("src/main/resources/extension/0.2.1_0"); // 확장 프로그램 폴더
        if (extensionDir.exists()) {
            options.addArguments("--load-extension=" + extensionDir.getAbsolutePath());
        }

        // 3. 기본 브라우저 환경설정 (알림, 팝업 차단 등)
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.managed_default_content_settings.popups", 2);
        options.setExperimentalOption("prefs", prefs);

        // 4. WebDriver 생성
        WebDriver driver = new ChromeDriver(options);

        // 5. 쿠키 삭제: 이전 세션의 캐시나 쿠키 초기화
        driver.manage().deleteAllCookies();

        // 6. Selenium 탐지 우회 스크립트 적용
        applyAntiDetection(driver);

        return driver;
    }

    public WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();

        // 1. 새로운 임시 사용자 프로필 생성 (쿠키, 캐시 등 초기화)
        try {
            String tempUserDataDir = Files.createTempDirectory("chrome_profile_").toAbsolutePath().toString();
            options.addArguments("--user-data-dir=" + tempUserDataDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Selenium 탐지 우회를 위한 옵션 추가
        options.addArguments("--disable-blink-features=AutomationControlled");

        // 2. 압축 풀린 확장 프로그램 로드
        File extensionDir = new File("src/main/resources/extension/0.2.1_0"); // 확장 프로그램 폴더
        if (extensionDir.exists()) {
            options.addArguments("--load-extension=" + extensionDir.getAbsolutePath());
        }

        // 3. 기본 브라우저 환경설정 (알림, 팝업 차단 등)
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.managed_default_content_settings.popups", 2);
        options.setExperimentalOption("prefs", prefs);

        // 4. WebDriver 생성
        WebDriver driver = new ChromeDriver(options);

        // 5. 쿠키 삭제: 이전 세션의 캐시나 쿠키 초기화
        driver.manage().deleteAllCookies();

        return driver;
    }

    private void applyAntiDetection(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // `navigator.webdriver` 제거
        js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

        // `navigator.plugins` 설정
        js.executeScript("Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]})");

        // `navigator.languages` 설정
        js.executeScript("Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']})");

        // WebGL 설정 변경 (Headless 감지 방지)
        js.executeScript(
                "Object.defineProperty(WebGLRenderingContext.prototype, 'getParameter', {" +
                        "    value: (parameter) => (parameter === 37445 ? 'NVIDIA' : parameter === 37446 ? 'GeForce GTX 1050' : 0)" +
                        "})"
        );
    }
}
