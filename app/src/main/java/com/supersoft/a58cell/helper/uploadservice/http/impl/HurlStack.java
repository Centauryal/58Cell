package com.supersoft.a58cell.helper.uploadservice.http.impl;


import com.supersoft.a58cell.helper.uploadservice.http.HttpConnection;
import com.supersoft.a58cell.helper.uploadservice.http.HttpStack;

import java.io.IOException;

/**
 * HttpUrlConnection stack implementation.
 * @author gotev (Aleksandar Gotev)
 */
public class HurlStack implements HttpStack {

    private boolean mFollowRedirects;
    private boolean mUseCaches;
    private int mConnectTimeout;
    private int mReadTimeout;

    public HurlStack() {
        mFollowRedirects = true;
        mUseCaches = false;
        mConnectTimeout = 15000;
        mReadTimeout = 30000;
    }

    public HurlStack(boolean followRedirects,
                     boolean useCaches,
                     int connectTimeout,
                     int readTimeout) {
        mFollowRedirects = followRedirects;
        mUseCaches = useCaches;
        mConnectTimeout = connectTimeout;
        mReadTimeout = readTimeout;
    }

    @Override
    public HttpConnection createNewConnection(String method, String url) throws IOException {
        return new HurlStackConnection(method, url, mFollowRedirects, mUseCaches,
                                       mConnectTimeout, mReadTimeout);
    }

}
