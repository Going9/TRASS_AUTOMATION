package com.trass_automation.trass_automation.modules.fetch;

import org.openqa.selenium.WebDriver;

public interface FetchStrategy<T, R> {
    R fetchData(WebDriver driver, T requset);
}
