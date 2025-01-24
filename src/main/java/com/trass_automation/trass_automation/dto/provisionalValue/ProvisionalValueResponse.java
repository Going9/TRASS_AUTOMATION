package com.trass_automation.trass_automation.dto.provisionalValue;

import lombok.Data;

import java.util.List;

@Data
public class ProvisionalValueResponse {
    String itemCode;
    List<CountryDollar> countryDollar;
}
