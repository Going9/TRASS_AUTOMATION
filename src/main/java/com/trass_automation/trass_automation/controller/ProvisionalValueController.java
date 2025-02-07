package com.trass_automation.trass_automation.controller;

import com.trass_automation.trass_automation.dto.login.LoginRequest;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueRequestWrapper;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueResponseWrapper;
import com.trass_automation.trass_automation.service.ProvisionalValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


// 잠정치 조회 컨트롤러
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/provisional-value")
public class ProvisionalValueController {

    private final ProvisionalValueService provisionalValueService;

    @PostMapping
    public ProvisionalValueResponseWrapper getProvisionalValue(@RequestBody ProvisionalValueRequestWrapper request) throws IOException {
        return provisionalValueService.getProvisionalValue(request);
    }

    @GetMapping("/test")
    public void test() {
        System.out.println("????");
    }

}
