package com.trass_automation.trass_automation.service;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsRequest;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponse;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponseWrapper;
import com.trass_automation.trass_automation.modules.WebDriverFactory;
import com.trass_automation.trass_automation.modules.fetch.FetchDetailValueHandler;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DetailValueService {

    private final WebDriverFactory webDriverFactory;
    private final FetchDetailValueHandler fetchDetailValueHandler;

    public DetailValueOfTwoItemsResponseWrapper getDetailValue(DetailValueOfTwoItemsRequest request) throws IOException {
        // 응답생성
        DetailValueOfTwoItemsResponseWrapper response = new DetailValueOfTwoItemsResponseWrapper();
        List<DetailValueOfTwoItemsResponse> fetchResultList = new ArrayList<>();

        String itemCode = request.getItemCode();
        String[] domesticRegions = request.getDomesticRegions();
        String year = request.getYear();
        String month = request.getMonth();
        for (String domesticRegion : domesticRegions) {
            // WebDriver driver = webDriverFactory.createDriver();
            WebDriver driver = webDriverFactory.createHeadlessDriver();
            DetailValueOfTwoItemsResponse fetchResult =
                    fetchDetailValueHandler.fetchData(driver, itemCode, domesticRegion, year, month);
            fetchResultList.add(fetchResult);
        }
        response.setDetailValueOfTwoItemsResponseList(fetchResultList);
        return response;
    }
}
