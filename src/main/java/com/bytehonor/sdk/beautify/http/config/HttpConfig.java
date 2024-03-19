package com.bytehonor.sdk.beautify.http.config;

public class HttpConfig {

    private static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1";

    private static final int MAX_IDLE = 10;

    private static final int CONNECT_POOL_MAX_TOTAL = 1024;

    private static final int CONNECT_POOL_MAX_PER_ROUTE = 1024;

    /**
     * socket超时时间
     * 
     */
    private static final int SOCKET_TIMEOUT = 10 * 1000;

    /**
     * 连接超时时间
     */
    private static final int CONNECT_TIMEOUT = 10 * 1000;

    /**
     * 连接请求超时时间
     */
    private static final int CONNECT_REQUEST_TIMEOUT = 10 * 1000;

    private String userAgent;

    private int maxIdle;

    private int connectPollMaxTotal;

    private int connectPollMaxPerRoute;

    private int socketTimeout;

    private int connectTimeout;

    private int connectRequestTimeout;

    private HttpConfig() {
        this.userAgent = USER_AGENT;
        this.maxIdle = MAX_IDLE;
        this.connectPollMaxTotal = CONNECT_POOL_MAX_TOTAL;
        this.connectPollMaxPerRoute = CONNECT_POOL_MAX_PER_ROUTE;
        this.socketTimeout = SOCKET_TIMEOUT;
        this.connectTimeout = CONNECT_TIMEOUT;
        this.connectRequestTimeout = CONNECT_REQUEST_TIMEOUT;
    }

    private static class LazyHolder {
        private static HttpConfig SINGLE = new HttpConfig();
    }

    public static HttpConfig config() {
        return LazyHolder.SINGLE;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getConnectPollMaxTotal() {
        return connectPollMaxTotal;
    }

    public void setConnectPollMaxTotal(int connectPollMaxTotal) {
        this.connectPollMaxTotal = connectPollMaxTotal;
    }

    public int getConnectPollMaxPerRoute() {
        return connectPollMaxPerRoute;
    }

    public void setConnectPollMaxPerRoute(int connectPollMaxPerRoute) {
        this.connectPollMaxPerRoute = connectPollMaxPerRoute;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectRequestTimeout() {
        return connectRequestTimeout;
    }

    public void setConnectRequestTimeout(int connectRequestTimeout) {
        this.connectRequestTimeout = connectRequestTimeout;
    }

}
