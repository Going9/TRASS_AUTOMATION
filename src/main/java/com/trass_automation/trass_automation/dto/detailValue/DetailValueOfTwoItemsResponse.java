package com.trass_automation.trass_automation.dto.detailValue;

import lombok.Data;

import java.util.List;

@Data
public class DetailValueOfTwoItemsResponse {
    String itemCode;
    List<DomesticRegionDollar> domesticRegionDollars;
}
