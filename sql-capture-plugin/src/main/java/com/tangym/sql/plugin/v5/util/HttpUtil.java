package com.tangym.sql.plugin.v5.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author :       yiming.tang
 */
@Slf4j
public class HttpUtil {
    public static void post(String url, String json) {
        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        OkHttpClient okhttp = new OkHttpClient();
        okhttp.newBuilder().connectTimeout(20000L, TimeUnit.MILLISECONDS).readTimeout(20000, TimeUnit.MILLISECONDS).build();
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request request = new Request.Builder().post(requestBody).url(url).build();
        try {
            Response response = okhttp.newCall(request).execute();
            if (!response.isSuccessful())
                response.body().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String url) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        Response response;
        OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.SECONDS)
                .readTimeout(20000L, TimeUnit.SECONDS)
                .build();
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            if (response.code() == 200) {
                assert response.body() != null;
                String body = response.body().string();
                response.body().close();
                return body;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
