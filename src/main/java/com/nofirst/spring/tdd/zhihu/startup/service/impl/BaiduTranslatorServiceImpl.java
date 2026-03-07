package com.nofirst.spring.tdd.zhihu.startup.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.config.BaiduTranslatorConfig;
import com.nofirst.spring.tdd.zhihu.startup.service.TranslatorService;
import com.nofirst.spring.tdd.zhihu.startup.util.HttpGet;
import com.nofirst.spring.tdd.zhihu.startup.util.MD5;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
@Slf4j
public class BaiduTranslatorServiceImpl implements TranslatorService {

    private static final String TRANS_API_HOST = "https://api.fanyi.baidu.com/api/trans/vip/translate";

    private BaiduTranslatorConfig baiduTranslatorConfig;

    @Override
    public String translate(String text) {
        String transResult = getTransResult(text, "auto", "en");
        log.info("translate: {}", transResult);
        Map<String, Object> resultMap = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            resultMap = objectMapper.readValue(transResult, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        List<Map<String, String>> transResultList = (List<Map<String, String>>) resultMap.get("trans_result");

        if (!CollectionUtils.isEmpty(transResultList)) {
            String dst = transResultList.get(0).get("dst");
            if (dst != null) {
                return dst.toLowerCase().replace(" ", "-");
            }
        }

        return "";
    }

    public String getTransResult(String query, String from, String to) {
        Map<String, String> params = this.buildParams(query, from, to);
        return HttpGet.get(TRANS_API_HOST, params);
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<>(8);
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        String appId = this.baiduTranslatorConfig.getAppId();
        String appKey = this.baiduTranslatorConfig.getAppKey();
        params.put("appid", appId);
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);
        String src = appId + query + salt + appKey;
        params.put("sign", MD5.md5(src));
        return params;
    }
}
