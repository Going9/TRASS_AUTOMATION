package com.trass_automation.trass_automation.service;

import com.trass_automation.trass_automation.dto.login.LoginRequest;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueRequest;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueRequestWrapper;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueResponse;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueResponseWrapper;
import com.trass_automation.trass_automation.modules.fetch.FetchProvisionalValueHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
@Service
public class ProvisionalValueService {

    private final FetchProvisionalValueHandler fetchProvisionalValueHandler;
    private final Semaphore SharedSemaphore;
    private final Logger logger = LoggerFactory.getLogger(ProvisionalValueService.class);

    public ProvisionalValueResponseWrapper getProvisionalValue(ProvisionalValueRequestWrapper request) {
        try {
            SharedSemaphore.acquire();

            // request 파싱
            LoginRequest loginRequest = request.getLoginRequest();
            ProvisionalValueRequest[] provisionalValueRequests = request.getProvisionalValueRequests();

            // 데이터 패치 및 응답생성
            ProvisionalValueResponseWrapper provisionalValueResponseWrapper = new ProvisionalValueResponseWrapper();
            List<ProvisionalValueResponse> provisionalValueResponseList = new ArrayList<>();
            for (ProvisionalValueRequest provisionalValueRequest : provisionalValueRequests) {
                ProvisionalValueResponse provisionalValueResponse = fetchProvisionalValueHandler.fetchData(provisionalValueRequest, loginRequest);
                provisionalValueResponseList.add(provisionalValueResponse);
            }
            logger.info("Finished fetch provisional value");
            provisionalValueResponseWrapper.setProvisionalValueResponses(provisionalValueResponseList);

            return provisionalValueResponseWrapper;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for semaphore", e);
        } finally {
            SharedSemaphore.release();
        }
    }
}
