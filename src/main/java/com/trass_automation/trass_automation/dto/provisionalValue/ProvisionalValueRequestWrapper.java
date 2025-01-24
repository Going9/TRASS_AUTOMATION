package com.trass_automation.trass_automation.dto.provisionalValue;

import com.trass_automation.trass_automation.dto.login.LoginRequest;
import lombok.Data;

@Data
public class ProvisionalValueRequestWrapper {
    LoginRequest loginRequest;
    ProvisionalValueRequest[] provisionalValueRequests;
}
