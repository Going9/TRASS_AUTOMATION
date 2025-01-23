package com.trass_automation.trass_automation.controller;

import com.trass_automation.trass_automation.dto.LoginRequest;
import com.trass_automation.trass_automation.dto.ProvisionalValueRequest;
import com.trass_automation.trass_automation.dto.ProvisionalValueRequestWrapper;
import com.trass_automation.trass_automation.service.ProvisionalValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


// 잠정치 조회 컨트롤러
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/provisional-value")
public class ProvisionalValueController {

    private final ProvisionalValueService provisionalValueService;

    @PostMapping("/login")
    public void loginTRASS(@RequestBody LoginRequest request) {
        provisionalValueService.loginTRASS(request);
    }

    @PostMapping
    public void getProvisionalValue(@RequestBody ProvisionalValueRequestWrapper request) {
        provisionalValueService.getProvisionalValue(request);
    }

    @GetMapping("/test")
    public void test() {
        System.out.println("????");
    }

}
