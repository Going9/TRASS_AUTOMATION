package com.trass_automation.trass_automation.cache;

import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueRequest;
import com.trass_automation.trass_automation.dto.provisionalValue.ProvisionalValueRequestWrapper;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
@Component("provisionalValueKeyGenerator")
public class ProvisionalValueKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        // ProvisionalValueRequestWrapper가 캐시 메서드의 첫 번째 인자라고 가정합니다.
        ProvisionalValueRequestWrapper requestWrapper = (ProvisionalValueRequestWrapper) params[0];

        // 로그인 요청에서 id 값을 캐시 키에 포함 (비밀번호는 보통 캐시에 포함하지 않습니다)
        String loginId = requestWrapper.getLoginRequest().getId();

        // 프로비저널 값 요청 배열 추출
        ProvisionalValueRequest[] requests = requestWrapper.getProvisionalValueRequests();

        // 아이템 코드 순서에 상관없이 동일한 키가 생성되도록,
        // 먼저 요청 배열을 아이템 코드 기준으로 정렬합니다.
        Arrays.sort(requests, Comparator.comparing(ProvisionalValueRequest::getItemCode));

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append("loginId:").append(loginId).append(";");

        // 각 프로비저널 요청에 대해 아이템 코드와 정렬된 국가 배열을 문자열로 변환하여 키에 추가합니다.
        for (ProvisionalValueRequest req : requests) {
            keyBuilder.append("itemCode:").append(req.getItemCode()).append(";");
            String[] countries = req.getCountries();
            // 원본 배열을 보존하기 위해 복사 후 정렬합니다.
            String[] sortedCountries = Arrays.copyOf(countries, countries.length);
            Arrays.sort(sortedCountries);
            keyBuilder.append("countries:").append(Arrays.toString(sortedCountries)).append(";");
        }

        return keyBuilder.toString();
    }
}
