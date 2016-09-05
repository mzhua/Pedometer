package com.wonders.xlab.pedometer.base;

/**
 * Created by hua on 16/8/21.
 */

public class DefaultException extends Exception {

    public DefaultException() {
    }

    public DefaultException(String detailMessage) {
        super(detailMessage);
    }

    public DefaultException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DefaultException(Throwable throwable) {
        super(throwable);
    }
}
