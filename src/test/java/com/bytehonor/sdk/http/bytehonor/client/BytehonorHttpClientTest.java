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
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("User-Agent", "Second");
            String html = BytehonorHttpClient.get("https://www.bytehonor.com", null, headers);
            LOG.info("html:{}", html);
        } catch (BytehonorHttpSdkException e) {
            LOG.error("xxxx", e);
            isOk = false;
        }

        assertTrue("*testStartThread*", isOk);
    }

    // @Test
    public void testDownload() {
        String url = "https://bytehonor-vpn.oss-us-west-1.aliyuncs.com/v1/twitter/1382943097454088192/d6abb9692b553381fd4c0688d5ce99c2.jpg";
        String path = "/Users/lijianqiang/data/testDownload.jpg";
        boolean isOk = true;
        try {
            BytehonorHttpClient.download(url, path);
            File file = new File(path);
            isOk = file.exists();
            LOG.info("isOk:{}", isOk);
        } catch (Exception e) {
            isOk = false;
            LOG.error("testDownload", e);
        }
        assertTrue("testDownload", isOk);
    }
}
