package com.trass_automation.trass_automation.dto.detailValue;

import lombok.Data;

@Data
public class DetailValueOfTwoItemsResponse {
    private String itemCode;
    private String year;
    private String month;
    private DomesticRegionDollar domesticRegionDollars;
}
