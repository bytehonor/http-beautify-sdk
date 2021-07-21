package com.bytehonor.sdk.http.bytehonor.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytehonor.sdk.http.bytehonor.exception.BytehonorHttpSdkException;

/**
 * @author lijianqiang
 *
 */
public class BytehonorHttpClient {

    private static Logger LOG = LoggerFactory.getLogger(BytehonorHttpClient.class);

    /**
     * socket超时时间
     * 
     * private static final int SOCKET_TIMEOUT = 10 * 1000;
     */

    /**
     * 连接请求超时时间
     */
    private static final int CONNECTION_REQUEST_TIMEOUT = 10 * 1000;

    /**
     * 连接超时时间
     */
    private static final int CONNECT_TIMEOUT = 10 * 1000;

    private static final int CONNECT_POOL_MAX_TOTAL = 1024;

    private static final int CONNECT_POOL_MAX_PER_ROUTE = 512;

    private static final int CACHE = 1024;

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4535.3 Safari/537.36";

    private CloseableHttpClient httpClient;

    private BytehonorHttpClient() {
        this.init();
    }

    private void init() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT).build();

        // https://blog.csdn.net/qq_28929589/article/details/88284723
//        SSLContext sslcontext = SSLContexts.createDefault();
//        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" },
//                null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
//
//        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", factory) // 用来配置支持的协议
//                .build();
//        // 加个共享连接池
//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
//                socketFactoryRegistry);
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(CONNECT_POOL_MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(CONNECT_POOL_MAX_PER_ROUTE);
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager).setConnectionManagerShared(true).build();
    }

    private static class LazzyHolder {
        private static BytehonorHttpClient INSTANCE = new BytehonorHttpClient();
    }

    private static BytehonorHttpClient getInstance() {
        return LazzyHolder.INSTANCE;
    }

    private static String execute(HttpUriRequest request) {
        if (getInstance().httpClient == null) {
            throw new BytehonorHttpSdkException("httpClient not init");
        }
        CloseableHttpResponse response = null;
        String body = "";
        try {
            response = getInstance().httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                body = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
            } else {
                LOG.error("statusCode:{}", statusCode);
                LOG.error("reason:{}", statusLine.getReasonPhrase());
                throw new BytehonorHttpSdkException(String.valueOf(statusCode));
            }
        } catch (Exception e) {
            LOG.error("execute", e);
            throw new BytehonorHttpSdkException(e);
        } finally {
            close(response);
        }
        return body;
    }

    private static void close(CloseableHttpResponse response) {
//        try {
//            getInstance().httpClient.close();
//        } catch (IOException e1) {
//            LOG.error("httpClient close", e1);
//        }
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                LOG.error("response close", e);
            }
        }
    }

    /**
     * 不传递参数的get请求。
     * 
     * @param url
     * @return
     * @throws BytehonorHttpSdkException
     */
    public static String get(String url) throws BytehonorHttpSdkException {
        return get(url, null, null);
    }

    /**
     * 传递参数的get请求。
     * 
     * @param url
     * @param paramsMap
     * @return
     * @throws BytehonorHttpSdkException
     */
    public static String get(String url, Map<String, String> paramsMap) throws BytehonorHttpSdkException {
        return get(url, paramsMap, null);
    }

    /**
     * 传递参数，且传递请求头的get请求。
     * 
     * @param url
     * @param paramsMap
     * @param headerMap
     * @return
     * @throws BytehonorHttpSdkException
     */
    public static String get(String url, Map<String, String> paramsMap, Map<String, String> headerMap)
            throws BytehonorHttpSdkException {
        Objects.requireNonNull(url, "url");
        if (paramsMap != null && paramsMap.isEmpty() == false) {
            StringBuilder sb = new StringBuilder(url);
            if (url.indexOf('?') < 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            for (Entry<String, String> item : paramsMap.entrySet()) {
                sb.append(item.getKey()).append("=").append(item.getValue());
                sb.append("&");
            }
            int length = sb.length();
            url = sb.substring(0, length - 1);
        }

        HttpGet request = new HttpGet(url);
        if (headerMap != null && headerMap.isEmpty() == false) {
            for (Entry<String, String> item : headerMap.entrySet()) {
                request.addHeader(item.getKey(), item.getValue());
            }
        } else {
            request.addHeader("User-Agent", USER_AGENT);
        }

        return execute(request);
    }

    /**
     * 传递参数的postForm请求。
     * 
     * @param url
     * @param paramsMap
     * @return
     * @throws BytehonorHttpSdkException
     */
    public static String postForm(String url, Map<String, String> paramsMap) throws BytehonorHttpSdkException {
        return postForm(url, paramsMap, null);
    }

    /**
     * 传递参数，且传递请求头的postForm请求。
     * 
     * @param url
     * @param paramsMap
     * @param headerMap
     * @return
     * @throws BytehonorHttpSdkException
     */
    public static String postForm(String url, Map<String, String> paramsMap, Map<String, String> headerMap)
            throws BytehonorHttpSdkException {
        Objects.requireNonNull(url, "url");

        HttpPost request = new HttpPost(url);
        if (headerMap != null && headerMap.isEmpty() == false) {
            for (Entry<String, String> item : headerMap.entrySet()) {
                request.addHeader(item.getKey(), item.getValue());
            }
        } else {
            request.addHeader("User-Agent", USER_AGENT);
        }

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (paramsMap != null && paramsMap.isEmpty() == false) {
            for (Entry<String, String> item : paramsMap.entrySet()) {
                pairs.add(new BasicNameValuePair(item.getKey(), item.getValue()));
            }
        }
        try {
            request.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (UnsupportedEncodingException e) {
            LOG.error("postForm error", e);
            throw new BytehonorHttpSdkException(e);
        }

        return execute(request);
    }

    /**
     * @param url
     * @param json
     * @return
     */
    public static String postJson(String url, String json) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(json, "json");

        HttpPost request = new HttpPost(url);
        request.addHeader("Accept", "application/json");
        request.addHeader("Content-type", "application/json; charset=utf-8");
        try {
            request.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
        } catch (Exception e) {
            LOG.error("postJson error", e);
            throw new BytehonorHttpSdkException(e);
        }
        return execute(request);
    }

    public static String postXml(String url, String xml) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(xml, "xml");

        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/xml; charset=utf-8");
        try {
            request.setEntity(new StringEntity(xml, Charset.forName("UTF-8")));
        } catch (Exception e) {
            LOG.error("postJson error", e);
            throw new BytehonorHttpSdkException(e);
        }
        return execute(request);
    }

    public static String uploadMedia(String url, Map<String, String> paramsMap, File file)
            throws BytehonorHttpSdkException {
        return upload(url, paramsMap, file, "media");
    }

    public static String uploadPic(String url, Map<String, String> paramsMap, File file)
            throws BytehonorHttpSdkException {
        return upload(url, paramsMap, file, "pic");
    }

    public static String uploadFile(String url, Map<String, String> paramsMap, File file)
            throws BytehonorHttpSdkException {
        return upload(url, paramsMap, file, "file");
    }

    public static String upload(String url, Map<String, String> paramsMap, File file, String fileKey)
            throws BytehonorHttpSdkException {
        Objects.requireNonNull(url, "url");
//        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        if (file != null) {
//            RequestBody fileBody = FormBody.create(file, MultipartBody.FORM);
//            multipartBuilder.addFormDataPart(fileKey, file.getName(), fileBody);
//            multipartBuilder.addFormDataPart("filename", file.getName());
//            multipartBuilder.addFormDataPart("filelength", String.valueOf(file.length()));
//        }
//
//        if (paramsMap != null && paramsMap.isEmpty() == false) {
//            for (Entry<String, String> item : paramsMap.entrySet()) {
//                multipartBuilder.addFormDataPart(item.getKey(), item.getValue());
//            }
//        }
//        RequestBody multipartBody = multipartBuilder.build();
//        Request.Builder requestBuilder = new Request.Builder();
//
//        Request request = requestBuilder.url(url).post(multipartBody).build();
//

//        return execute(request);
        return null;
    }

    public static void download(String url, String filePath) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(filePath, "filePath");
        try {
            HttpGet request = new HttpGet(url);
            request.addHeader("User-Agent", USER_AGENT);
            HttpResponse response = getInstance().httpClient.execute(request);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            File file = new File(filePath);
            file.getParentFile().mkdirs();
            FileOutputStream fileout = new FileOutputStream(file);
            /**
             * 根据实际运行效果 设置缓冲区大小
             */
            byte[] buffer = new byte[CACHE];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer, 0, ch);
            }
            is.close();
            fileout.flush();
            fileout.close();
        } catch (Exception e) {
            LOG.error("download url:{}", url, e);
            throw new BytehonorHttpSdkException(e);
        } finally {
            close(null);
        }
    }
}
