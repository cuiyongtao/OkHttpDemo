package com.test.okhttpdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * @Author： Victory
 * @Time： 2018/11/7
 * @QQ： 949021037
 * @Explain： com.test.androiddemo.activity
 */

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private TextView btnAsyncGet;
    private TextView btnSyncGet;
    private TextView btnPostFrom;
    private TextView btnJsonPost;
    private TextView btnFilePost;
    private TextView btnFileDown;
    private TextView btnFileAndParam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btnAsyncGet = findViewById(R.id.btnAsyncGet);
        btnSyncGet = findViewById(R.id.btnSyncGet);
        btnPostFrom = findViewById(R.id.btnPostFrom);
        btnJsonPost = findViewById(R.id.btnJsonPost);
        btnFilePost = findViewById(R.id.btnFilePost);
        btnFileDown = findViewById(R.id.btnFileDown);
        btnFileAndParam = findViewById(R.id.btnFileAndParam);
        onClick();
    }

    private void onClick() {
        btnAsyncGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAsyncRequest();
            }
        });
        btnSyncGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSyncRequest();
            }
        });
        btnPostFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFormRequest();
            }
        });
        btnJsonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postJsonRequest();
            }
        });
        btnFilePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFileRequest();
            }
        });
        btnFileAndParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFileAndParam();
            }
        });
        btnFileDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getfileDown();
            }
        });
    }


    /**
     * 使用Okhttp步骤
     * 1.创建OkHttpClient
     * 2.构建Request对象
     * 3.构建Call对象
     * 4.通过Call.enqueue方法回调返回请求到的数据
     */


    /**
     * 异步get请求
     * API:http://www.wanandroid.com/tools/mockapi/12606/test
     */
    private void getAsyncRequest() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://www.wanandroid.com/tools/mockapi/12606/test")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "OkHttpE: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "OkHttpR: " + response.code());
                Log.d(TAG, "OkHttpR: " + response.message());
                Log.d(TAG, "OkHttpR: " + response.body().string());
            }
        });
    }

    /**
     * 同步get请求
     * API:http://www.wanandroid.com/tools/mockapi/12606/test
     */

    private void getSyncRequest() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://www.wanandroid.com/tools/mockapi/12606/test").build();
        final Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Log.d(TAG, "OkHttpR: " + response.code());
                    Log.d(TAG, "OkHttpR: " + response.message());
                    //不能使用toString方法，不然只会返回一段未序列化的字符串
                    Log.d(TAG, "OkHttpR: " + response.body().string());
                } catch (Exception e) {
                    Log.d(TAG, "OkHttpR: " + e.toString());
                }
            }
        }).start();
    }

    /**
     * post请求
     * 表单参数请求
     * API:http://www.wanandroid.com/user/login
     */

    private void postFormRequest() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor()).build();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("username", "victory");
        formBody.add("password", "123456");
        Request request = new Request.Builder().url("http://www.wanandroid.com/user/login").post(formBody.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "OkHttpR: " + response.code());
                Log.d(TAG, "OkHttpR: " + response.message());
                Log.d(TAG, "OkHttpR: " + response.body().string());
            }
        });
    }

    /**
     * post请求
     * json参数请求
     * API：http://api.kuailai.me/Api/Tool
     */
    private void postJsonRequest() {
        OkHttpClient okHttpClient = new OkHttpClient();
        //数据类型指定为JSON
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MethodName", "MachineInfo");
            jsonObject.put("PhoneModel", android.os.Build.MODEL);
            jsonObject.put("Manufacturer", android.os.Build.MANUFACTURER);
            jsonObject.put("System", "Android");
            jsonObject.put("SystemVersion", android.os.Build.VERSION.SDK);
            jsonObject.put("AppVersion", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {

        }
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url("你的请求地址")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "OkHttpR: " + response.code());
                Log.d(TAG, "OkHttpR: " + response.message());
                Log.d(TAG, "OkHttpR: " + response.body().string());
            }
        });
    }

    /**
     * post请求
     * file上传文件
     * API:https://www.baidu.com 没有什么可以上传文件测试开放的API，用百度将就一下
     */
    private void postFileRequest() {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("file/*");
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/aa.jpg");
        RequestBody requestBody = RequestBody.create(mediaType, file);
        Request request = new Request.Builder()
                .url("你的url路径")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "OkHttpR: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "OkHttpR: " + response.code());
                Log.d(TAG, "OkHttpR: " + response.message());
                Log.d(TAG, "OkHttpR: " + response.body().string());
            }
        });
    }

    /**
     *上传文件加参数
     */
    private void postFileAndParam() {
        OkHttpClient okHttpClient = new OkHttpClient();
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/aa.jpg");
        MultipartBody multipartBody = new MultipartBody.Builder()
                .addFormDataPart("username", "victory")
                .addFormDataPart("password", "123456")
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("file/*"), file))
                .build();
        Request request = new Request.Builder()
                .url("")
                .post(multipartBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "OkHttpR: " + response.code());
                Log.d(TAG, "OkHttpR: " + response.message());
                Log.d(TAG, "OkHttpR: " + response.body().string());
            }
        });
    }

    /**
     * get
     * downloadFile
     * 下载文件
     */
    private void getfileDown() {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "OkHttpR: " + response.code());
                Log.d(TAG, "OkHttpR: " + response.message());
                BufferedSource source = response.body().source();
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/12345.apk");
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(source);
                sink.flush();
                Log.d(TAG, "OkHttpR: " + "Ok");
            }
        });
    }

}
