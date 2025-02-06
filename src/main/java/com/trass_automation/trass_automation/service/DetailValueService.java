package com.trass_automation.trass_automation.service;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsRequest;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponseWrapper;
import com.trass_automation.trass_automation.modules.WebDriverFactory;
import com.trass_automation.trass_automation.modules.fetch.FetchDetailValueHandler;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DetailValueService {

    private final WebDriverFactory webDriverFactory;
    private final FetchDetailValueHandler fetchDetailValueHandler;

    public DetailValueOfTwoItemsResponseWrapper getDetailValue(DetailValueOfTwoItemsRequest request) {
        String itemCode = request.getItemCode();
        String[] domesticRegions = request.getDomesticRegions();
        for (String domesticRegion : domesticRegions) {
            WebDriver driver = webDriverFactory.createDriver();
            fetchDetailValueHandler.fetchData(driver, itemCode, domesticRegion);
        }
        return new DetailValueOfTwoItemsResponseWrapper();
    }
}
