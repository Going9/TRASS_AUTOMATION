package com.trass_automation.trass_automation.modules.fetch;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsRequest;
import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponse;
import com.trass_automation.trass_automation.modules.verification.RetryHandler;
import com.trass_automation.trass_automation.utils.WaitForElements;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class FetchDetailValueHandler implements FetchStrategy<DetailValueOfTwoItemsRequest, DetailValueOfTwoItemsResponse> {

    private final WaitForElements waitForElements;
    private final RetryHandler retryHandler;
    private final Logger logger = LoggerFactory.getLogger(FetchProvisionalValueHandler.class);

    @Override
    public DetailValueOfTwoItemsResponse fetchData(WebDriver driver, DetailValueOfTwoItemsRequest request) {
        try {
            // 응답생성
            DetailValueOfTwoItemsResponse detailValueOfTwoItemsResponse = new DetailValueOfTwoItemsResponse();

            retryHandler.executeWithRetry(driver, drv -> {
                // 상세조회 페이지 이동
                drv.get("https://www.bandtrass.or.kr/customs/total.do?command=CUS001View&viewCode=CUS00301");
                waitForElements.waitForUrlToBe(drv, "https://www.bandtrass.or.kr/customs/total.do?command=CUS001View&viewCode=CUS00301", 30);

                // 복합형 2개 항목 클릭
                WebElement typeButton = waitForElements.waitForElementToBeClickable(drv, "input[type=\"radio\"][name=\"grid_type\"][value=\"B\"]", 10);
                typeButton.click();

                // 품목 && 국내지역 클릭
                WebElement itemCodeButton = waitForElements.waitForElementToBeClickable(drv, "#GODS_DIV", 10);
                itemCodeButton.click();

                WebElement domesticRegionButton = waitForElements.waitForElementToBeClickable(drv, "#LOCATION_DIV", 10);
                domesticRegionButton.click();

                // 폼목 선택
                Select selectOfItemCode = new Select(waitForElements.waitForElementToBeClickable(drv, "select[id=\"GODS_TYPE\"][name=\"GODS_TYPE\"]", 10));
                selectOfItemCode.selectByValue("H");

                // 기존 창 저장
                String originalWindow = drv.getWindowHandle();

                // 폼목 검색 클릭
                WebElement searchCodeButton = waitForElements.waitForElementToBeClickable(drv, "div#FILTER1_POPUP_CODE", 10);
                searchCodeButton.click();

                // 새 창 열릴 때까지 대기
                WebDriverWait wait = new WebDriverWait(drv, Duration.ofSeconds(30));
                wait.until(ExpectedConditions.numberOfWindowsToBe(2));

                // 모든 창 핸들 가져오기
                Set<String> windowHandles = drv.getWindowHandles();
                String newWindow = "";
                for (String handle : windowHandles) {
                    if (!handle.equals(originalWindow)) {
                        newWindow = handle;
                        break;
                    }
                }

                // 새 창으로 드라이버 전환
                drv.switchTo().window(newWindow);

                // 새 창 열리고,
                // HS단위선택
                int codeSize = request.getItemCode().length();
                String convertedCodeSize = String.valueOf(codeSize);
                Select selectOfCodeSize = new Select(waitForElements.waitForElementToBeClickable(drv, "select#UNIT", 10));
                selectOfCodeSize.selectByValue(convertedCodeSize);

                // 코드 입력
                WebElement codeInputBox = waitForElements.waitForElementToBeClickable(drv, "input#CustomText", 30);
                codeInputBox.clear();
                codeInputBox.sendKeys(request.getItemCode());

                // 직접 입력 추가 버튼 클릭
                WebElement addDirectInputButton = waitForElements.waitForElementToBeClickable(drv, "#CustomCheck", 10);
                addDirectInputButton.click();

                // 선택적용 버튼 클릭
                WebElement applyButton = waitForElements.waitForElementToBeClickable(drv, "#wrap > div > div.tb_in_wrap > div > p:nth-child(2) > button", 10);
                applyButton.click();

                // 시군구 선택
                Select selectOfDomesticRegion = new Select(waitForElements.waitForElementToBeClickable(drv, "select[id=\"LOCATION_TYPE\"][name=\"LOCATION_TYPE\"]", 10));
                selectOfDomesticRegion.selectByValue("B");

            }, 5, 2000);

            return detailValueOfTwoItemsResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
