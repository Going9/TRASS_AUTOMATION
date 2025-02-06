package com.trass_automation.trass_automation.service;

import com.trass_automation.trass_automation.dto.login.LoginRequest;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueRequest;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueRequestWrapper;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueResponse;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueResponseWrapper;
import com.trass_automation.trass_automation.modules.WebDriverFactory;
import com.trass_automation.trass_automation.modules.fetch.FetchProvisionalValueHandler;
import com.trass_automation.trass_automation.modules.login.TRASSLoginHandler;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProvisionalValueService {

    private final WebDriverFactory webDriverFactory;
    private final TRASSLoginHandler loginHandler;
    private final FetchProvisionalValueHandler fetchProvisionalValueHandler;

    public ProvisionalValueResponseWrapper getProvisionalValue(ProvisionalValueRequestWrapper request) {
        WebDriver driver = webDriverFactory.createHeadlessDriver();

        // request 파싱
        LoginRequest loginRequest = request.getLoginRequest();
        ProvisionalValueRequest[] provisionalValueRequests = request.getProvisionalValueRequests();

        // 로그인
        loginHandler.login(driver, loginRequest.getId(), loginRequest.getPassword());

        // 데이터 패치 및 응답생성
        ProvisionalValueResponseWrapper provisionalValueResponseWrapper = new ProvisionalValueResponseWrapper();
        List<ProvisionalValueResponse> provisionalValueResponseList = new ArrayList<>();
        for (ProvisionalValueRequest provisionalValueRequest : provisionalValueRequests) {
            ProvisionalValueResponse provisionalValueResponse = fetchProvisionalValueHandler.fetchData(driver, provisionalValueRequest);
            provisionalValueResponseList.add(provisionalValueResponse);
        }
        provisionalValueResponseWrapper.setProvisionalValueResponses(provisionalValueResponseList);

        return provisionalValueResponseWrapper;
    }
}
