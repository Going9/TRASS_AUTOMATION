package com.trass_automation.trass_automation.modules.fetch;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsResponse;
import com.trass_automation.trass_automation.dto.detailValue.DomesticRegionDollar;
import com.trass_automation.trass_automation.modules.WebDriverFactory;
import com.trass_automation.trass_automation.modules.verification.RetryHandler;
import com.trass_automation.trass_automation.utils.CheckCaptchaHandler;
import com.trass_automation.trass_automation.utils.ElementWaiter;
import com.trass_automation.trass_automation.utils.WindowSwitcher;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Component
public class FetchDetailValueHandler {

    private final RetryHandler retryHandler;
    private final WebDriverFactory webDriverFactory;
    private final Logger logger = LoggerFactory.getLogger(FetchDetailValueHandler.class);
    private final CheckCaptchaHandler checkCaptchaHandler;
    private ElementWaiter elementWaiter;
    private WindowSwitcher windowSwitcher;

    public DetailValueOfTwoItemsResponse fetchData(String itemCode, String domesticRegion, String year, String month) throws IOException {
        WebDriver driver = webDriverFactory.createHeadlessDriver();
        this.elementWaiter = new ElementWaiter(driver);
        this.windowSwitcher = new WindowSwitcher(driver, elementWaiter);

        try {
            // 응답생성
            DetailValueOfTwoItemsResponse detailValueOfTwoItemsResponse = new DetailValueOfTwoItemsResponse();
            DomesticRegionDollar domesticRegionDollar = new DomesticRegionDollar();

            retryHandler.executeWithRetry(driver, drv -> {
                logger.info("Start fetching detail value..");
                logger.info("ItemCode: {}", itemCode);
                logger.info("DomesticRegion: {}", domesticRegion);

                // 상세조회 페이지 이동
                drv.get("https://www.bandtrass.or.kr/customs/total.do?command=CUS001View&viewCode=CUS00301");
                elementWaiter.awaitUrl("https://www.bandtrass.or.kr/customs/total.do?command=CUS001View&viewCode=CUS00301");

                // 복합형 2개 항목 클릭
                elementWaiter.awaitElementClickable(By.cssSelector("#search_div > table:nth-child(1) > tbody > tr > td > div:nth-child(3) > label")).click();

                // 품목 && 국내지역 클릭
                elementWaiter.awaitElementClickable(By.cssSelector("#GODS_DIV")).click();
                elementWaiter.awaitElementClickable(By.cssSelector("#LOCATION_DIV")).click();

                // 폼목 선택
                elementWaiter.awaitElementClickable(By.xpath("//*[@id=\"GODS_TYPE\"]/option[@value=\"H\"]")).click();

                // 기존 창 저장
                String currentWindow = drv.getWindowHandle();

                // 폼목 검색 클릭(이 시점에 새로운 창이 열림)
                elementWaiter.awaitElementClickable(By.cssSelector("div#FILTER1_POPUP_CODE")).click();

                // 새 창 대기
                elementWaiter.awaitNewWindowOpen();

                // 새로운 창으로 전환
                windowSwitcher.switchToNewWindow(currentWindow);

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
                elementWaiter.awaitElementClickable(By.cssSelector("#CustomCheck")).click();

                // 선택적용 버튼 클릭
                elementWaiter.awaitElementClickable(By.cssSelector("#wrap > div > div.tb_in_wrap > div > p:nth-child(2) > button")).click();

                // 기존 창으로 전환
                drv.switchTo().window(currentWindow);

                // 시군구 선택
                elementWaiter.awaitElementClickable(By.xpath("//*[@id=\"LOCATION_TYPE\"]/option[@value=\"B\"]")).click();

                // 선택하세요 버튼 클릭
                elementWaiter.awaitElementClickable(By.cssSelector("#FILTER2_SELECT_CODE > span > div > button")).click();

                // 검색
                WebElement searchInput = drv.findElement(By.cssSelector("#FILTER2_SELECT_CODE > span > div > ul > li.multiselect-item.multiselect-filter > div > input"));
                searchInput.sendKeys(domesticRegion);

                // 지역 이름 클릭
                WebElement desiredLabel = elementWaiter.awaitElementClickable(By.xpath(String.format("//*[@id='FILTER2_SELECT_CODE']/span/div/ul/li/a/label[contains(text(),'%s')]", domesticRegion)));
                WebElement parentLink = desiredLabel.findElement(By.xpath("./.."));
                parentLink.click();

                // 조회하기 버튼 클릭
                WebElement button = drv.findElement(By.cssSelector("#form > div > div:nth-child(1) > div.text-center.m-t-sm > button"));
                JavascriptExecutor js = (JavascriptExecutor) drv;
                js.executeScript("arguments[0].click();", button);
                checkCaptchaHandler.checkForCaptcha(drv);

                // 정확한 시군구 이름 추출
                String regionName = elementWaiter.awaitElementPresent(By.id("FILTER2_KOR")).getText();
                checkCaptchaHandler.checkForCaptcha(drv);

                // 기존 창 저장
                currentWindow = drv.getWindowHandle();
                checkCaptchaHandler.checkForCaptcha(drv);

                // 시군구 클릭
                elementWaiter.awaitElementClickable(By.xpath(String.format("//td[@title='%s']", regionName))).click();
                checkCaptchaHandler.checkForCaptcha(drv);

                // 새 창 열릴 때까지 대기
                logger.info("Start waiting for main table, it may takes some minutes...");
                elementWaiter.awaitNewWindowOpen();
                checkCaptchaHandler.checkForCaptcha(drv);

                // 새 창으로 드라이버 전환
                windowSwitcher.switchToNewWindow(currentWindow);
                checkCaptchaHandler.checkForCaptcha(drv);

                // 입력받은 년도 클릭
                elementWaiter.awaitElementClickable(By.xpath(String.format("//td[@title='%s년']", year))).click();
                checkCaptchaHandler.checkForCaptcha(drv);

                // 입력받은 월의 수출액 추출
                String exportAmount = elementWaiter.awaitElementPresent(By.xpath(String.format("//tr[td[1][contains(normalize-space(.), '%s월')]]/td[2]", month))).getText();
                logger.info("{} 수출액: {}", regionName, exportAmount);
                checkCaptchaHandler.checkForCaptcha(drv);

                detailValueOfTwoItemsResponse.setItemCode(itemCode);
                detailValueOfTwoItemsResponse.setYear(year);
                detailValueOfTwoItemsResponse.setMonth(month);
                domesticRegionDollar.setDomesticRegion(regionName);
                domesticRegionDollar.setDollar(exportAmount);
                detailValueOfTwoItemsResponse.setDomesticRegionDollars(domesticRegionDollar);

            }, 10, 2000);

            driver.quit();
            return detailValueOfTwoItemsResponse;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
