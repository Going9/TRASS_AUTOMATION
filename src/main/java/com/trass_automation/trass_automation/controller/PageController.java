package com.trass_automation.trass_automation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class PageController {
    private final DetailValueController detailValueController;
    private final ProvisionalValueController provisionalValueController;

    @GetMapping("/index")
    public String showIndexForm() {
        return"index-form";
    }

}
