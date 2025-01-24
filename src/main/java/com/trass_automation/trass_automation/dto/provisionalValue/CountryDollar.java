package com.trass_automation.trass_automation.dto.provisionalValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CountryDollar {
    String country;
    BigDecimal dollar;
}
