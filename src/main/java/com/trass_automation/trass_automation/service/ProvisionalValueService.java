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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
@Service
public class ProvisionalValueService {

    private final WebDriverFactory webDriverFactory;
    private final TRASSLoginHandler loginHandler;
    private final FetchProvisionalValueHandler fetchProvisionalValueHandler;
    private final Semaphore semaphore = new Semaphore(1);

    public ProvisionalValueResponseWrapper getProvisionalValue(ProvisionalValueRequestWrapper request) throws IOException {
        try {
            semaphore.acquire();

            WebDriver driver = webDriverFactory.createDriver();

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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for semaphore", e);
        } finally {
            semaphore.release();
        }
    }
}
