package com.bytehonor.sdk.beautify.http.client;

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

import com.bytehonor.sdk.beautify.http.config.HttpConfig;
import com.bytehonor.sdk.beautify.http.exception.HttpBeautifyException;

/**
 * @author lijianqiang
 *
 */
public class HttpBeautifyClient {

    private static Logger LOG = LoggerFactory.getLogger(HttpBeautifyClient.class);

    private static final String USER_AGENT = "User-Agent";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String ACCEPT = "Accept";

    private static final String UTF_8 = "UTF-8";

    private static final int CACHE = 1024;

    private CloseableHttpClient httpClient;

    private HttpBeautifyClient() {
        this.httpClient = build();
    }

    public static CloseableHttpClient build() {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(HttpConfig.config().getSocketTimeout())
                .setConnectTimeout(HttpConfig.config().getConnectTimeout())
                .setConnectionRequestTimeout(HttpConfig.config().getConnectRequestTimeout()).build();

        // https://blog.csdn.net/qq_28929589/article/details/88284723
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(HttpConfig.config().getConnectPollMaxTotal());
        manager.setDefaultMaxPerRoute(HttpConfig.config().getConnectPollMaxPerRoute());
        manager.setValidateAfterInactivity(1000 * 300);

        return HttpClientBuilder.create().setDefaultRequestConfig(config).setConnectionManager(manager)
                .setConnectionManagerShared(true).build();
    }

    private static class LazzyHolder {
        private static HttpBeautifyClient SINGLE = new HttpBeautifyClient();
    }

    private static HttpBeautifyClient self() {
        return LazzyHolder.SINGLE;
    }

    private static CloseableHttpClient client() {
        return self().httpClient;
    }

    private static String execute(HttpUriRequest request) {
        if (client() == null) {
            throw new HttpBeautifyException("httpClient not init");
        }

        CloseableHttpResponse response = null;
        String body = "";
        try {
            response = client().execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                body = EntityUtils.toString(entity, UTF_8);
                EntityUtils.consume(entity);
            } else {
                LOG.error("statusCode:{}, reason:{}", statusCode, statusLine.getReasonPhrase());
                throw new HttpBeautifyException(String.valueOf(statusCode));
            }
        } catch (Exception e) {
            LOG.error("execute", e);
            throw new HttpBeautifyException(e);
        } finally {
            close(response);
        }
        return body;
    }

    private static void close(CloseableHttpResponse response) {
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
     * @throws HttpBeautifyException
     */
    public static String get(String url) throws HttpBeautifyException {
        return get(url, null, null);
    }

    /**
     * 传递参数的get请求。
     * 
     * @param url
     * @param params
     * @return
     * @throws HttpBeautifyException
     */
    public static String get(String url, Map<String, String> params) throws HttpBeautifyException {
        return get(url, params, null);
    }

    /**
     * 传递参数，且传递请求头的get请求。
     * 
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpBeautifyException
     */
    public static String get(String url, Map<String, String> params, Map<String, String> headers)
            throws HttpBeautifyException {
        Objects.requireNonNull(url, "url");
        if (params != null && params.isEmpty() == false) {
            StringBuilder sb = new StringBuilder(url);
            if (url.indexOf('?') < 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            for (Entry<String, String> item : params.entrySet()) {
                sb.append(item.getKey()).append("=").append(item.getValue());
                sb.append("&");
            }
            int length = sb.length();
            url = sb.substring(0, length - 1);
        }

        HttpGet request = new HttpGet(url);
        request.setHeader(USER_AGENT, HttpConfig.config().getUserAgent());
        if (headers != null && headers.isEmpty() == false) {
            for (Entry<String, String> item : headers.entrySet()) {
                request.setHeader(item.getKey(), item.getValue());
            }
        }

        return execute(request);
    }

    /**
     * 传递参数的postForm请求。
     * 
     * @param url
     * @param params
     * @return
     * @throws HttpBeautifyException
     */
    public static String postForm(String url, Map<String, String> params) throws HttpBeautifyException {
        return postForm(url, params, null);
    }

    /**
     * 传递参数，且传递请求头的postForm请求。
     * 
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpBeautifyException
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers)
            throws HttpBeautifyException {
        Objects.requireNonNull(url, "url");

        HttpPost request = new HttpPost(url);
        request.setHeader(USER_AGENT, HttpConfig.config().getUserAgent());
        if (headers != null && headers.isEmpty() == false) {
            for (Entry<String, String> item : headers.entrySet()) {
                request.setHeader(item.getKey(), item.getValue());
            }
        }

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (params != null && params.isEmpty() == false) {
            for (Entry<String, String> item : params.entrySet()) {
                pairs.add(new BasicNameValuePair(item.getKey(), item.getValue()));
            }
        }
        try {
            request.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (UnsupportedEncodingException e) {
            LOG.error("postForm error", e);
            throw new HttpBeautifyException(e);
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

        return postJson(url, json, null);
    }

    public static String postJson(String url, String json, Map<String, String> headers) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(json, "json");

        HttpPost request = new HttpPost(url);
        request.setHeader(USER_AGENT, HttpConfig.config().getUserAgent());
        request.setHeader(ACCEPT, "application/json");
        request.setHeader(CONTENT_TYPE, "application/json; charset=utf-8");
        if (headers != null && headers.isEmpty() == false) {
            for (Entry<String, String> item : headers.entrySet()) {
                request.setHeader(item.getKey(), item.getValue());
            }
        }

        try {
            request.setEntity(new StringEntity(json, Charset.forName(UTF_8)));
        } catch (Exception e) {
            LOG.error("postJson error", e);
            throw new HttpBeautifyException(e);
        }
        return execute(request);
    }

    public static String postXml(String url, String xml) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(xml, "xml");

        return postXml(url, xml);
    }

    public static String postXml(String url, String xml, Map<String, String> headers) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(xml, "xml");

        HttpPost request = new HttpPost(url);
        request.setHeader(USER_AGENT, HttpConfig.config().getUserAgent());
        request.setHeader(CONTENT_TYPE, "application/xml; charset=utf-8");
        if (headers != null && headers.isEmpty() == false) {
            for (Entry<String, String> item : headers.entrySet()) {
                request.setHeader(item.getKey(), item.getValue());
            }
        }

        try {
            request.setEntity(new StringEntity(xml, Charset.forName(UTF_8)));
        } catch (Exception e) {
            LOG.error("postJson error", e);
            throw new HttpBeautifyException(e);
        }
        return execute(request);
    }

    public static String uploadMedia(String url, Map<String, String> params, File file) throws HttpBeautifyException {
        return upload(url, params, file, "media");
    }

    public static String uploadPic(String url, Map<String, String> params, File file) throws HttpBeautifyException {
        return upload(url, params, file, "pic");
    }

    public static String uploadFile(String url, Map<String, String> params, File file) throws HttpBeautifyException {
        return upload(url, params, file, "file");
    }

    public static String upload(String url, Map<String, String> params, File file, String fileKey)
            throws HttpBeautifyException {
        Objects.requireNonNull(url, "url");

        throw new RuntimeException("TODO");
//        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        if (file != null) {
//            RequestBody fileBody = FormBody.create(file, MultipartBody.FORM);
//            multipartBuilder.addFormDataPart(fileKey, file.getName(), fileBody);
//            multipartBuilder.addFormDataPart("filename", file.getName());
//            multipartBuilder.addFormDataPart("filelength", String.valueOf(file.length()));
//        }
//
//        if (params != null && params.isEmpty() == false) {
//            for (Entry<String, String> item : params.entrySet()) {
//                multipartBuilder.addFormDataPart(item.getKey(), item.getValue());
//            }
//        }
//        RequestBody multipartBody = multipartBuilder.build();
//        Request.Builder requestBuilder = new Request.Builder();
//
//        Request request = requestBuilder.url(url).post(multipartBody).build();
//

//        return execute(request);
    }

    public static void download(String url, String filePath) {
        download(url, filePath, null);
    }

    public static void download(String url, String filePath, Map<String, String> headers) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(filePath, "filePath");
        try {
            HttpGet request = new HttpGet(url);
            request.setHeader(USER_AGENT, HttpConfig.config().getUserAgent());
            if (headers != null && headers.isEmpty() == false) {
                for (Entry<String, String> item : headers.entrySet()) {
                    request.setHeader(item.getKey(), item.getValue());
                }
            }
            HttpResponse response = client().execute(request);

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
            throw new HttpBeautifyException(e);
        } finally {
            close(null);
        }
    }
}
