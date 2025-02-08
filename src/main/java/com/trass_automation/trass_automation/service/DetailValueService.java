package com.trass_automation.trass_automation.service;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsRequest;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponse;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponseWrapper;
import com.trass_automation.trass_automation.modules.fetch.FetchDetailValueHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
@Service
public class DetailValueService {

    private final FetchDetailValueHandler fetchDetailValueHandler;
    private final Semaphore semaphore = new Semaphore(1);

    public DetailValueOfTwoItemsResponseWrapper getDetailValue(DetailValueOfTwoItemsRequest request) throws IOException {
        try {
            semaphore.acquire();

            // 응답생성
            DetailValueOfTwoItemsResponseWrapper response = new DetailValueOfTwoItemsResponseWrapper();
            List<DetailValueOfTwoItemsResponse> fetchResultList = new ArrayList<>();

            String itemCode = request.getItemCode();
            String[] domesticRegions = request.getDomesticRegions();
            String year = request.getYear();
            String month = request.getMonth();

            for (String domesticRegion : domesticRegions) {
                DetailValueOfTwoItemsResponse fetchResult = fetchDetailValueHandler.fetchData(itemCode, domesticRegion, year, month);
                fetchResultList.add(fetchResult);
            }
            response.setDetailValueOfTwoItemsResponseList(fetchResultList);

            return response;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for semaphore", e);

        } finally {
            semaphore.release();
        }
    }
}
