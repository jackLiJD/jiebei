package com.ald.ebei.network.converter;

import com.ald.ebei.BuildConfig;
import com.ald.ebei.util.log.EbeiLogger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;

public class EbeiLoggingInterceptor implements Interceptor {
    private final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        RequestBody requestBody = request.body();

        String body = null;

        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            body = buffer.readString(charset);
        }
        if (BuildConfig.LOG_DEBUG) {

            EbeiLogger.d("watermelon", "发送响应 %s%s %ss\n发送url：%s\n发送body：%s\n发送body：%s" +
                    request.method() + request.url() + request.headers() + body);
        }
        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        String rBody = null;

        if (HttpEngine.hasBody(response)) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }
            }
            rBody = buffer.clone().readString(charset);
        }
        if (BuildConfig.LOG_DEBUG) {
            EbeiLogger.d("watermelon", "收到响应 %s%s %ss\n请求url：%s\n请求body：%s\n响应body：%s" +
                    response.code() + response.message() + tookMs + response.request().url() + body + rBody);
        }

        return response;
    }
}