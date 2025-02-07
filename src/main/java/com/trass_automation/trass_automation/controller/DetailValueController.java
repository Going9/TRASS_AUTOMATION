package com.trass_automation.trass_automation.controller;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsRequest;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponseWrapper;
import com.trass_automation.trass_automation.service.DetailValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/detail-value")
public class DetailValueController {

    private final DetailValueService detailValueService;

    @PostMapping
    public DetailValueOfTwoItemsResponseWrapper getDetailValueOfTwoItems(@RequestBody DetailValueOfTwoItemsRequest request) throws IOException {
        return detailValueService.getDetailValue(request);
    }
}
