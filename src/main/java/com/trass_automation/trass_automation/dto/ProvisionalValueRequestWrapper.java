package com.trass_automation.trass_automation.dto;

import lombok.Data;

@Data
public class ProvisionalValueRequestWrapper {
    LoginRequest loginRequest;
    ProvisionalValueRequest[] provisionalValueRequests;
}
