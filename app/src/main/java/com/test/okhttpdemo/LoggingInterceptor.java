package com.test.okhttpdemo;

import android.util.Log;


import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author： Victory
 * @Time： 2018/12/5
 * @QQ： 949021037
 * @Explain： com.test.okhttpdemo
 */
public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) {
        Request request = null;
        Response response = null;
        try {
            request = chain.request();
            Log.i("LOGGER",String.format("Sending request %s", request.url()));
            response = chain.proceed(request);
//            Log.i("LOGGER", String.format("Received response for %s", response.body().string()));
            StringBuffer stringBuffer=new StringBuffer();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    stringBuffer.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
            }
            Log.i("LOGGER", String.format("Received response for %s", stringBuffer));
        } catch (Exception e) {
            Log.i("LOGGER", e.toString());
        }
        return response;
    }
}
