package com.trass_automation.trass_automation.service;

import com.trass_automation.trass_automation.dto.LoginRequest;
import com.trass_automation.trass_automation.dto.ProvisionalValueRequest;
import com.trass_automation.trass_automation.dto.ProvisionalValueRequestWrapper;
import com.trass_automation.trass_automation.modules.WebDriverFactory;
import com.trass_automation.trass_automation.modules.fetch.FetchProvisionalValueHandler;
import com.trass_automation.trass_automation.modules.login.TRASSLoginHandler;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProvisionalValueService {

    private final WebDriverFactory webDriverFactory;
    private final TRASSLoginHandler loginHandler;
    private final FetchProvisionalValueHandler fetchProvisionalValueHandler;

    public void loginTRASS(LoginRequest request) {
        WebDriver driver = webDriverFactory.createHeadlessDriver();
        //WebDriver driver = webDriverFactory.createDriver();
        loginHandler.login(driver, request.getId(), request.getPassword());
    }

    public void getProvisionalValue(ProvisionalValueRequestWrapper request) {
        WebDriver driver = webDriverFactory.createHeadlessDriver();

        // request 파싱
        LoginRequest loginRequest = request.getLoginRequest();
        ProvisionalValueRequest[] provisionalValueRequests = request.getProvisionalValueRequests();

        // 로그인
        loginHandler.login(driver, loginRequest.getId(), loginRequest.getPassword());

        // 데이터 패치
        for (ProvisionalValueRequest provisionalValueRequest : provisionalValueRequests) {
            fetchProvisionalValueHandler.fetchData(driver, provisionalValueRequest);
        }

    }
}
