package com.trass_automation.trass_automation.controller;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsRequest;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class PageController {
    private final DetailValueController detailValueController;
    private final ProvisionalValueController provisionalValueController;

    @GetMapping("/index")
    public String showIndexForm() {
        return"index-form";
    }

    @PostMapping("/detail-value")
    public String fetchDetailValueOfTwoItems(@ModelAttribute DetailValueOfTwoItemsRequest request, Model model) throws IOException {
        DetailValueOfTwoItemsResponseWrapper detailValueOfTwoItemsResponseWrapper = detailValueController.getDetailValueOfTwoItems(request);
        model.addAttribute("detailValueOfTwoItemsResponseWrapper", detailValueOfTwoItemsResponseWrapper);
        return "detail-value-list";
    }
}
