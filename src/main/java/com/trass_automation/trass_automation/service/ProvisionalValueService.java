package com.trass_automation.trass_automation.service;

import com.trass_automation.trass_automation.dto.LoginRequest;
import com.trass_automation.trass_automation.modules.WebDriverFactory;
import com.trass_automation.trass_automation.modules.login.TRASSLoginHandler;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProvisionalValueService {

    private final WebDriverFactory webDriverFactory;
    private final TRASSLoginHandler loginHandler;

    public void loginTRASS(LoginRequest request) {
        WebDriver driver = webDriverFactory.createHeadlessDriver();
        loginHandler.login(driver, request.getId(), request.getPassword());
    }
}
