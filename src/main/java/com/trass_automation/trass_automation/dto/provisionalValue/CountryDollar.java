package com.trass_automation.trass_automation.dto.provisionalValue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class CountryDollar {
    String country;
    BigDecimal dollar;

    public CountryDollar() {

    }
}
