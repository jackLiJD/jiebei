package com.ald.ebei.ui.ref;

/**
 * Email: hbh@erongdu.com
 * Created by hebihe on 4/18/16.
 */
public class EbeiRefException extends Exception {
    private static final long serialVersionUID = 8248853556846348815L;

    public EbeiRefException() {
        super();
    }

    public EbeiRefException(String detailMessage) {
        super(detailMessage);
    }

    public EbeiRefException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public EbeiRefException(Throwable throwable) {
        super(throwable);
    }
}
