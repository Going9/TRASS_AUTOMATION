package com.trass_automation.trass_automation.controller;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsRequest;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponseWrapper;
import com.trass_automation.trass_automation.modules.WebDriverFactory;
import com.trass_automation.trass_automation.service.DetailValueService;
import com.trass_automation.trass_automation.utils.CheckCaptchaHandler;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/detail-value")
public class DetailValueController {

    private final DetailValueService detailValueService;
    private final CheckCaptchaHandler checkCaptchaHandler;

    @PostMapping
    public DetailValueOfTwoItemsResponseWrapper getDetailValueOfTwoItems(@RequestBody DetailValueOfTwoItemsRequest request) throws IOException {
        return detailValueService.getDetailValue(request);
    }

    @GetMapping("/test")
    public void testCaptcha() throws IOException {
        WebDriverFactory webDriverFactory = new WebDriverFactory();
        WebDriver driver = webDriverFactory.createDriver();

        driver.get("https://www.google.com/recaptcha/api2/demo"); // reCAPTCHA 테스트 페이지
        checkCaptchaHandler.checkForCaptcha(driver);

        try {
            Thread.sleep(150000); // 확장 프로그램이 실행될 시간을 기다림
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.quit();
    }
}
