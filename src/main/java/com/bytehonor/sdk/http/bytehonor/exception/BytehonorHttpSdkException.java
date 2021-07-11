package com.bytehonor.sdk.http.bytehonor.exception;

/**
 * @author lijianqiang
 *
 */
public class BytehonorHttpSdkException extends RuntimeException {

    private static final long serialVersionUID = 7371246830617370553L;

    public BytehonorHttpSdkException() {
        super();
    }

    public BytehonorHttpSdkException(String message) {
        super(message);
    }

    public BytehonorHttpSdkException(Exception cause) {
        super(cause);
    }
}
