package com.bytehonor.sdk.beautify.http.exception;

/**
 * @author lijianqiang
 *
 */
public class HttpBeautifyException extends RuntimeException {

    private static final long serialVersionUID = 7371246830617370553L;

    public HttpBeautifyException() {
        super();
    }

    public HttpBeautifyException(String message) {
        super(message);
    }

    public HttpBeautifyException(Exception cause) {
        super(cause);
    }
}
