package com.trass_automation.trass_automation.cache;

import com.trass_automation.trass_automation.dto.detailValue.DetailValueOfTwoItemsRequest;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component("detailValueKeyGenerator")
public class DetailValueKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object object, Method method, Object... params) {
        DetailValueOfTwoItemsRequest request = (DetailValueOfTwoItemsRequest) params[0];

        // 요청에서 itemCode, year, month 분리
        String itemCode = request.getItemCode();
        String year = request.getYear();
        String month = request.getMonth();

        // domesticRegions 분리
        String[] regions = request.getDomesticRegions();

        // 원본 배열 순서 유지 위해 복사 후 정렬
        String[] sortedRegions = Arrays.copyOf(regions, regions.length);
        Arrays.sort(sortedRegions);

        // 정렬된 배열을 문자열로 변환
        String regionKeys = Arrays.toString(sortedRegions);

        // 모든 정보 연결하여 하나의 캐시 키 생성
        String key = itemCode + "-" + regionKeys + "-" + year + "-" + month;

        return key;
    }
}
