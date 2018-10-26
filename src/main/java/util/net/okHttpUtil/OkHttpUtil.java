package util.net.okHttpUtil;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    public static OkHttpClient instance;

    private OkHttpUtil() {}

    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (OkHttpUtil.class) {
                if (instance == null) {
                    //配置了网络请求的超时时间
                    instance = new OkHttpClient().newBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();

                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        String url = "https://www.baidu.com/";
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("键", "值")
                .add("键", "值")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
