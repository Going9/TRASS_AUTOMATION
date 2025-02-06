package com.trass_automation.trass_automation.modules.fetch;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponse;
import com.trass_automation.trass_automation.modules.verification.RetryHandler;
import com.trass_automation.trass_automation.utils.ElementWaiter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
public class FetchDetailValueHandler{

    private final RetryHandler retryHandler;
    private final Logger logger = LoggerFactory.getLogger(FetchDetailValueHandler.class);

    public DetailValueOfTwoItemsResponse fetchData(WebDriver driver, String itemCode, String domesticRegion) {
        try {
            ElementWaiter elementWaiter = new ElementWaiter(driver);

            logger.info("ItemCode: {}", itemCode);
            logger.info("DomesticRegion: {}", domesticRegion);

            // 응답생성
            DetailValueOfTwoItemsResponse detailValueOfTwoItemsResponse = new DetailValueOfTwoItemsResponse();

            retryHandler.executeWithRetry(driver, drv -> {
                // 상세조회 페이지 이동
                drv.get("https://www.bandtrass.or.kr/customs/total.do?command=CUS001View&viewCode=CUS00301");
                elementWaiter.awaitUrl("https://www.bandtrass.or.kr/customs/total.do?command=CUS001View&viewCode=CUS00301");

                // 복합형 2개 항목 클릭
                WebElement typeButton = elementWaiter.awaitElementClickable(By.cssSelector("#search_div > table:nth-child(1) > tbody > tr > td > div:nth-child(3) > label"));
                typeButton.click();

                // 품목 && 국내지역 클릭
                WebElement itemCodeButton = elementWaiter.awaitElementClickable(By.cssSelector("#GODS_DIV"));
                itemCodeButton.click();

                WebElement domesticRegionButton = elementWaiter.awaitElementClickable(By.cssSelector("#LOCATION_DIV"));
                domesticRegionButton.click();

                // 폼목 선택
                elementWaiter.awaitElementClickable(By.xpath("//*[@id=\"GODS_TYPE\"]/option[@value=\"H\"]")).click();

                // 기존 창 저장
                String originalWindow = drv.getWindowHandle();

                // 폼목 검색 클릭
                WebElement searchCodeButton = elementWaiter.awaitElementClickable(By.cssSelector("div#FILTER1_POPUP_CODE"));
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
                int codeSize = itemCode.length();
                String convertedCodeSize = String.valueOf(codeSize);
                Select selectOfCodeSize = new Select(elementWaiter.awaitElementClickable(By.cssSelector("select#UNIT")));
                selectOfCodeSize.selectByValue(convertedCodeSize);

                // 코드 입력
                WebElement codeInputBox = elementWaiter.awaitElementClickable(By.cssSelector("input#CustomText"));
                codeInputBox.clear();
                codeInputBox.sendKeys(itemCode);

                // 직접 입력 추가 버튼 클릭
                WebElement addDirectInputButton = elementWaiter.awaitElementClickable(By.cssSelector("#CustomCheck"));
                addDirectInputButton.click();

                // 선택적용 버튼 클릭
                WebElement applyButton = elementWaiter.awaitElementClickable(By.cssSelector("#wrap > div > div.tb_in_wrap > div > p:nth-child(2) > button"));
                applyButton.click();

                // 기존 창으로 전환
                drv.switchTo().window(originalWindow);

                // 시군구 선택
                drv.findElement(By.xpath("//*[@id=\"LOCATION_TYPE\"]/option[@value=\"B\"]")).click();

                // 선택하세요 버튼 클릭
                WebElement choiceButton = elementWaiter.awaitElementClickable(By.cssSelector("#FILTER2_SELECT_CODE > span > div > button"));
                choiceButton.click();

                // 검색
                WebElement searchInput = drv.findElement(By.cssSelector("#FILTER2_SELECT_CODE > span > div > ul > li.multiselect-item.multiselect-filter > div > input"));
                searchInput.sendKeys(domesticRegion);

                // 지역 이름 클릭
                String xpathExpression = "//*[@id='FILTER2_SELECT_CODE']/span/div/ul/li/a/label[contains(text(),'" + domesticRegion + "')]";
                WebElement desiredLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathExpression)));
                WebElement parentLink = desiredLabel.findElement(By.xpath("./.."));
                parentLink.click();

                // 조회하기 버튼 클릭
                WebElement button = drv.findElement(By.cssSelector("#form > div > div:nth-child(1) > div.text-center.m-t-sm > button"));
                JavascriptExecutor js = (JavascriptExecutor) drv;
                js.executeScript("arguments[0].click();", button);

                // 시군구 클릭
                elementWaiter.awaitElementClickable(By.cssSelector("//*[@id=\"1\"]/td[3]")).click();





            }, 5, 2000);

            return detailValueOfTwoItemsResponse; 
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }
}
