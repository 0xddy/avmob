package com.av.utils;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

@Service
public class OkHttp {


    static final Proxy.Type PROXY_TYPE = Proxy.Type.SOCKS;
    static final String PROXY_ADDRESS = "192.168.100.5";
    static final int PROXY_PORT = 1080;
    static final boolean USE_PROXY = true;

    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36Name";

    @Cacheable(value = "list", key = "#url", unless = "#result == null")
    public String GET(String url) {

        String html = null;
        OkHttpClient.Builder OkHttpClientBuilder = new OkHttpClient.Builder();
        if (USE_PROXY) {
            OkHttpClientBuilder.proxy(new Proxy(PROXY_TYPE, new InetSocketAddress(PROXY_ADDRESS, PROXY_PORT)));
        }
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClientBuilder.addInterceptor(logInterceptor);
        OkHttpClient okHttpClient = OkHttpClientBuilder.build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get()
                .url(url)
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Referer", url)
                .addHeader("X-Forwarded-For", getRandomIP())
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            if (response.code() == 200) {
                html = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.body().close();
        }
        return html;
    }

    public void stream(String url, Map reqHeaders, HttpServletResponse response1) {

        String clientRange = (String) reqHeaders.get("Range");
        String ifRange = (String) reqHeaders.get("If-Range");

        InputStream inputStream;
        OkHttpClient.Builder OkHttpClientBuilder = new OkHttpClient.Builder();
        if (USE_PROXY) {
            OkHttpClientBuilder.proxy(new Proxy(PROXY_TYPE, new InetSocketAddress(PROXY_ADDRESS, PROXY_PORT)));
        }
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClientBuilder.addInterceptor(logInterceptor);
        OkHttpClient okHttpClient = OkHttpClientBuilder.build();
        Request.Builder builder = new Request.Builder();

        builder.get()
                .url(url)
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Referer", url)
                .addHeader("X-Forwarded-For", getRandomIP());


        if (!StringUtils.isEmpty(clientRange))
            builder.addHeader("Range", clientRange);
        if(!StringUtils.isEmpty(ifRange))
            builder.addHeader("If-Range", ifRange);
        Request request = builder.build();

        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            if (response.code() == 200) {

//                Server: Tengine/2.1.1
//                Date: Mon, 17 Sep 2018 09:43:13 GMT
//                Content-Type: video/mp4
//                Content-Length: 13212973
//                Last-Modified: Mon, 17 Sep 2018 07:47:27 GMT
//                Connection: keep-alive
//                X-Mod-H264-Streaming: version=2.2.7
//                Accept-Ranges: bytes
                inputStream = response.body().byteStream();

                String contentLength = response.header("Content-Length");
                String contentType = response.header("Content-Type");
                String data = response.header("Date");
                String acceptRanges = response.header("Accept-Ranges");
                String lastModified = response.header("Last-Modified");
                String xModH264Streaming = response.header("X-Mod-H264-Streaming");


                if (!StringUtils.isEmpty(contentLength) && Utils.isInteger(contentLength))
                    //设置Content-Length
                    response1.setIntHeader("Content-Length", Integer.parseInt(contentLength));
                if (!StringUtils.isEmpty(contentType))
                    response1.setHeader("Content-Type", contentType);
                if (!StringUtils.isEmpty(data))
                    response1.setHeader("data", data);
                if (!StringUtils.isEmpty(acceptRanges))
                    response1.setHeader("Accept-Ranges", acceptRanges);
                if(!StringUtils.isEmpty(lastModified))
                    response1.setHeader("Last-Modified",lastModified);
                if(!StringUtils.isEmpty(xModH264Streaming))
                    response1.setHeader("X-Mod-H264-Streaming",xModH264Streaming);

                byte[] b = new byte[1024];
                int len;

                ServletOutputStream outputStream = response1.getOutputStream();
                while ((len = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                }
                outputStream.close();
                //response1.flushBuffer();
            }
        } catch (Exception e) {

            //e.printStackTrace();
        } finally {
            if (response != null)
                response.body().close();
        }

    }

    public static String getRandomIP() {

        return (new Random().nextInt(254) + 1) + "."
                + (new Random().nextInt(254) + 1) + "."
                + (new Random().nextInt(254) + 1) + "."
                + (new Random().nextInt(254) + 1);
    }


    public static class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            System.out.println(message);
        }
    }

}
