package util.net.okHttpUtil;

import com.google.gson.Gson;
import okhttp3.*;
import util.Contants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Network {
    //请求的url
    private String  mUrl;
    //通过单例类得到的OkHttpClient 对象
    private OkHttpClient mClient;
    //解析服务器返回数据的Gson
    private Gson  mGson;

    private Map<String, Object> bodyMap;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Network(String url) {
        this.mUrl = url;
        mClient = OkHttpUtil.getInstance();
        mGson = new Gson();
    }

    public Network(String url, Map<String, Object> bodyMap) {
        this.mUrl = url;
        mClient = OkHttpUtil.getInstance();
        mGson = new Gson();
        this.bodyMap = bodyMap;
    }

    /**
     * 执行普通的post请求，参数集合全部转为json
     */
    public Response performJSONPost() throws IOException {
        String params = mGson.toJson(bodyMap);
        RequestBody body = RequestBody.create(JSON, params);
        final Request request = new Request.Builder()
                .url(mUrl)
                .post(body)
                .build();
        return mClient.newCall(request).execute();
    }

    /**
     * 执行普通的post请求
     */
    public Response perGet() throws IOException {
        Request request = new Request.Builder().url(mUrl).build();
        return mClient.newCall(request).execute();
    }



    /**
     * form表单
     * @return
     * @throws Exception
     */
    public Response perHtmlformPost() throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : bodyMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue().toString());
        }
        Request request = new Request.Builder()
                .url(mUrl)
                .post(builder.build())
                .build();
       return mClient.newCall(request).execute();
    }
}
