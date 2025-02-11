package com.trass_automation.trass_automation.modules.fetch;

import com.trass_automation.trass_automation.dto.provisionalValue.CountryDollar;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueRequest;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueResponse;
import com.trass_automation.trass_automation.modules.verification.RetryHandler;
import com.trass_automation.trass_automation.utils.ElementWaiter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RequiredArgsConstructor
@Component
public class FetchProvisionalValueHandler implements FetchStrategy<ProvisionalValueRequest, ProvisionalValueResponse>{

    private final RetryHandler retryHandler;
    private final Logger logger = LoggerFactory.getLogger(FetchProvisionalValueHandler.class);
    private ElementWaiter elementWaiter;

    @Override
    public ProvisionalValueResponse fetchData(WebDriver driver, ProvisionalValueRequest provisionalValueRequest) {
        try {
            this.elementWaiter = new ElementWaiter(driver);

            // 응답생성
            ProvisionalValueResponse provisionalValueResponse = new ProvisionalValueResponse();

            retryHandler.executeWithRetry(driver, drv -> {
                // 품목조회 페이지 이동
                drv.get("https://www.bandtrass.or.kr/customs/total.do?command=CUS001View&viewCode=CUS00401");
                elementWaiter.awaitUrl("https://www.bandtrass.or.kr/customs/total.do?command=CUS001View&viewCode=CUS00401");

                // 품목 다중조회 클릭
                WebElement itemButton = elementWaiter.awaitElementClickable(By.cssSelector("#tr1 > td > div:nth-child(2) > label"));
                itemButton.click();

                // 품목코드 조회 시작
                String itemCode = provisionalValueRequest.getItemCode();
                String[] countries = provisionalValueRequest.getCountries();
                SearchResult result = searchItemCode(driver, itemCode, countries);

                provisionalValueResponse.setItemCode(itemCode);
                provisionalValueResponse.setCountryDollar(result.getCountryDollars());
                provisionalValueResponse.setTotalDollarSum(String.valueOf(result.getTotalDollarSum()));

            }, 5, 2000);
            driver.quit();
            return provisionalValueResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SearchResult searchItemCode(WebDriver driver, String itemCode, String[] countries) throws InterruptedException {
        logger.debug("ItemCode: {}", itemCode);
        logger.debug("Target countries: {}", Arrays.toString(countries));

        // 품목 초기화 및 입력
        WebElement inputBox = driver.findElement(By.cssSelector("#SelectCd"));
        inputBox.clear();
        inputBox.sendKeys(itemCode);

        // 품목 검색
        WebElement searchButton = elementWaiter.awaitElementClickable(By.cssSelector("#form > div > div:nth-child(1) > div.text-center.m-t-sm > button.btn.btn-ok.btn-lg"));
        searchButton.click();

        // 품목 검색 결과 리스트 중 첫 페이지로 이동
        forceGoToPageOne(driver);

        // 달러총합
        BigDecimal totalDollarSum = BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);

        // 선택된 국가들의 달러를 담을 해쉬맵
        Map<String, BigDecimal> targetCountries = new HashMap<>();
        for (String country : countries) {
            targetCountries.put(country, new BigDecimal("0.000"));
        }

        // 페이지 순회
        int currentPage = 1;
        while (true) {
            // (1) 현재 페이지의 (국가, 달러) 리스트를 파싱
            List<CountryDollar> rows = parseTableData(driver);

            // (2) 파싱된 데이터를 합산
            for (CountryDollar cd : rows) {
                BigDecimal dollarValue = cd.getDollar();

                // 전체 합계
                totalDollarSum = totalDollarSum.add(dollarValue);

                // 요청한 국가인지 확인
                if (targetCountries.containsKey(cd.getCountry())) {
                    targetCountries.put(cd.getCountry(), targetCountries.get(cd.getCountry()).add(dollarValue));
                }
            }

            // 다음 페이지 이동 시도
            boolean hasNext = goNextPage(driver, currentPage + 1);
            if (!hasNext) break;
            currentPage++;
        }

        logger.debug("Total dollar sum: {}", totalDollarSum);

        // 응답생성: 각 국가별 달러 합계 리스트 생성
        List<CountryDollar> countryDollars = new ArrayList<>();
        for (String country : countries) {
            CountryDollar countryDollar = new CountryDollar();
            countryDollar.setCountry(country);

            BigDecimal targetCountryDollar = targetCountries.get(country);
            countryDollar.setDollar(targetCountryDollar);

            countryDollars.add(countryDollar);
            logger.debug("{}: {}", country, targetCountryDollar);
        }

        return new SearchResult(totalDollarSum, countryDollars);
    }

    private List<CountryDollar> parseTableData(WebDriver driver) {
        elementWaiter.awaitElementVisible(By.cssSelector("table#table_list_1 tr[id]"));

        logger.info("Start table parsing..");
        List<CountryDollar> result = new ArrayList<>();

        // table#table_list_1 아래 tr[id]들
        List<WebElement> rows = driver.findElements(By.cssSelector("table#table_list_1 tr[id]"));
        logger.info("Size of rows: {}", rows.size());

        for (WebElement row : rows) {
            try {
                // 국가
                WebElement countryCell = row.findElement(By.cssSelector("td[aria-describedby='table_list_1_NATN_NAME']"));
                String country = countryCell.getText().trim();

                // 달러
                WebElement dollarCell = row.findElement(By.cssSelector("td[aria-describedby='table_list_1_AMT_OF_DLR']"));
                String dollarStr = dollarCell.getText().trim(); // 예: "966,164"
                String cleanedDollarStr = dollarStr.replace(",", ""); // 쉼표 제거
                BigDecimal dollarValue = new BigDecimal(cleanedDollarStr);

                CountryDollar data = new CountryDollar(country, dollarValue);
                logger.info("Country: {}, Dollar: {}", data.getCountry(), data.getDollar());

                result.add(data);

            } catch (Exception e) {
                logger.error(String.valueOf(e));
                logger.warn("Failed to parse row: {}", row.getText(), e);
            }
        }
        return result;
    }

    private void forceGoToPageOne(WebDriver driver) throws InterruptedException {
        String xpath = "//a[@href=\"javascript:pageLink('1')\"]";
        List<WebElement> linkToPageOne = driver.findElements(By.xpath(xpath));

        if (!linkToPageOne.isEmpty()) {
            linkToPageOne.get(0).click();
            Thread.sleep(500);
        }
    }

    /**
     * 다음 페이지로 이동할 수 있으면 이동, 없으면 false
     */
    private boolean goNextPage(WebDriver driver, int pageNum) throws InterruptedException {
        elementWaiter.awaitElementVisible(By.cssSelector("table#table_list_1 tr[id]"));
        String xpathForLink = String.format("//a[@href=\"javascript:pageLink('%d')\"]", pageNum);
        List<WebElement> nextPageLink = driver.findElements(By.xpath(xpathForLink));
        if (nextPageLink.isEmpty()) {
            return false;
        }
        nextPageLink.get(0).click();
        Thread.sleep(5000);
        return true;
    }

    // 새로 추가된 SearchResult 클래스: totalDollarSum과 각 국가별 달러 리스트를 함께 반환
    @Getter
    private static class SearchResult {
        private final BigDecimal totalDollarSum;
        private final List<CountryDollar> countryDollars;

        public SearchResult(BigDecimal totalDollarSum, List<CountryDollar> countryDollars) {
            this.totalDollarSum = totalDollarSum;
            this.countryDollars = countryDollars;
        }

    }
}
