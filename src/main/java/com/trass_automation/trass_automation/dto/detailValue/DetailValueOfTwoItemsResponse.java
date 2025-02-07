package com.trass_automation.trass_automation.dto.detailValue;

import lombok.Data;

@Data
public class DetailValueOfTwoItemsResponse {
    String itemCode;
    String year;
    String month;
    DomesticRegionDollar domesticRegionDollars;
}
