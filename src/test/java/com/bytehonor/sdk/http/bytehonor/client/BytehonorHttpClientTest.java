package com.bytehonor.sdk.http.bytehonor.client;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytehonor.sdk.http.bytehonor.exception.BytehonorHttpSdkException;

public class BytehonorHttpClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(BytehonorHttpClientTest.class);

    @Test
    public void testGetString2() {
        boolean isOk = true;
        try {
            // 测测header是否是浏览器的
            String html = BytehonorHttpClient.get("https://www.bytehonor.com");
            LOG.info("html:{}", html);
        } catch (BytehonorHttpSdkException e) {
            LOG.error("xxxx", e);
            isOk = false;
        }

        assertTrue("*testStartThread*", isOk);
    }

//    @Test
    public void testUpload() {
        String url = "xx";

        boolean isOk = true;
        File file = null;
        try {
            file = new File("/xxx/1.jpg");
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("status", "xx");
            paramsMap.put("access_token", "xx");

            String res = BytehonorHttpClient.uploadPic(url, paramsMap, file);
            LOG.info("res:{}", res);
        } catch (Exception e) {
            isOk = false;
            LOG.error("testUpload", e);
        }
        assertTrue("testUpload", isOk);
    }
}
