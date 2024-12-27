package com.bytehonor.sdk.beautify.http.client;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytehonor.sdk.beautify.http.exception.HttpBeautifyException;

public class HttpBeautifyClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(HttpBeautifyClientTest.class);

    @Test
    public void testGetString2() {
        boolean isOk = true;
        try {
            // 测测header是否是浏览器的
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("User-Agent", "Second");
            String html = HttpBeautifyClient.get("https://vue.bytehonor.com", null, headers);
            LOG.info("html:{}", html);
        } catch (HttpBeautifyException e) {
            LOG.error("xxxx", e);
            isOk = false;
        }

        assertTrue("*testStartThread*", isOk);
    }

//    @Test
    public void testDownload() {
        String url = "https://wx4.sinaimg.cn/orj480/9d6d01f9ly1gdmj3ac93vj20u0140u0x.jpg";
        String path = "/Users/lijianqiang/data/testDownloadWeibo.jpg";
        boolean isOk = true;
        try {
            // Referer: https://m.weibo.cn/detail/4854157586215881
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Referer", "https://m.weibo.cn/detail/4854157586215881");
            HttpBeautifyClient.download(url, path, headers);
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
