/*
 * @author 许正海
 * @date 2013-08-15 12:45:40
 * Copyright (c) 2013 Njry. All Rights Reserved.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import util.Contants;

/**
 * @description HttpURLConnection + XML 接口请求方式
 * @author 许正海
 * @date 2013-08-15
 */
public class HttpURLConnHelper
{
    public static final Logger log = Logger.getLogger(HttpURLConnHelper.class);

    private static int CONNECTTIMEOUT = 10000; // 连接主机的超时时间（单位：毫秒）

    private static int READTIMEOUT = 10000; // 从主机读取数据的超时时间（单位：毫秒）
    private static HttpClient httpClient;
    private static final String CHARSET = HTTP.UTF_8;
    private static final int SERVER_PORT = 80;


    class AnyTrustStrategy implements TrustStrategy
    {
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            return true;
        }

    }

    private static int bufferSize = 1024;

    private static volatile HttpURLConnHelper instance;

    private ConnectionConfig connConfig;

    private SocketConfig socketConfig;

    private ConnectionSocketFactory plainSF;

    private KeyStore trustStore;

    private SSLContext sslContext;

    private LayeredConnectionSocketFactory sslSF;

    private Registry<ConnectionSocketFactory> registry;

    private PoolingHttpClientConnectionManager connManager;

    private volatile HttpClient client;

    private volatile BasicCookieStore cookieStore;

    public static String defaultEncoding = "utf-8";


    /**
     * @description 调用Http请求
     * @author 许正海 2013-08-07
     * @return
     * @throws IOException
     */
    public static String execute(String serverUrl, java.util.TreeMap<String, String> message, String flag) throws IOException
    {
        return execute(serverUrl, message, new HashMap(), flag);
    }

    /**
     * @description 调用Http请求
     * @author 许正海 2013-08-07
     * @return
     * @throws IOException
     */
    public static String execute(String serverUrl) throws IOException
    {
        return execute(serverUrl, new TreeMap<String, String>(), "");
    }


    /**
     * @description 调用Http请求
     * @author 许正海 2013-08-07
     * @return
     * @throws IOException
     */
    public static String execute(String serverUrl, String message) throws IOException
    {
        return execute(serverUrl, message);
    }

    public static String execute(String serverUrl, java.util.TreeMap<String, String> message, Map<String, String> header) throws IOException {
        return execute(serverUrl, message, header, "");
    }


    public static String execute(String serverUrl, java.util.TreeMap<String, String> message, Map<String, String> header, String flag) throws IOException
    {
//        System.out.println("serverUrl:"+serverUrl);
        String result = "";
        HttpURLConnHelper util = HttpURLConnHelper.getInstance();
        InputStream in;
        try
        {
            in = util.doPostForStream(serverUrl, message, new HashMap(), header, flag);
            result = HttpURLConnHelper.readStream(in, HttpURLConnHelper.defaultEncoding);
            System.out.println(result);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        return result;
//		String retVal = HttpURLConnHelper.readStream(in, HttpURLConnHelper.defaultEncoding);
//		System.out.println(retVal);
//
//		return execute(serverUrl, message, CONNECTTIMEOUT, READTIMEOUT);
    }


    /**
     * @description 调用Http请求
     * @author 戴晓飞 2014-09-05
     * @return
     * @throws IOException
     */
    public static String execute(String serverUrl, java.util.TreeMap<String, String> messageMap, int connectTimeout,
                                 int readTimeout) throws IOException
    {

        String result = null;

        HttpPost postRequest = new HttpPost(serverUrl);
        List<NameValuePair> pairs = convertMapToNameValuePairs(messageMap);
        HttpEntity entity = null;
        HttpResponse response = null;
        HttpClient httpclient = getHttpClient();
        try
        {
            entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
            postRequest.setEntity(entity);
            response = httpclient.execute(postRequest);
            // System.out.println("getStatusCode:"+response.getStatusLine().getStatusCode());
            log.debug("getStatusCode:" + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            {
            }
            else
            {
                // printHeader(response.getAllHeaders());
                HttpEntity resEntity = response.getEntity();
                result = (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
            }
            // result = getResponseText(response);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            postRequest.abort();
        }
        return result;
    }

    public HttpURLConnHelper()
    {
        // 设置连接参数
//		connConfig = ConnectionConfig.custom().setCharset(Charset.forName(defaultEncoding)).build();
        socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
        plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        // 指定信任密钥存储对象和连接套接字工厂
        try
        {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new AnyTrustStrategy()).build();
            sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            registryBuilder.register("https", sslSF);
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        registry = registryBuilder.build();
        // 设置连接管理器
        connManager = new PoolingHttpClientConnectionManager(registry);
        connManager.setDefaultConnectionConfig(connConfig);
        connManager.setDefaultSocketConfig(socketConfig);
        // 指定cookie存储对象
        cookieStore = new BasicCookieStore();
        // 构建客户端
        client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setConnectionManager(connManager)
                .build();
    }

    public static HttpURLConnHelper getInstance()
    {
        synchronized (HttpURLConnHelper.class)
        {
            if (HttpURLConnHelper.instance == null)
            {
                instance = new HttpURLConnHelper();
            }
            return instance;
        }
    }

    public InputStream doGet(String url) throws URISyntaxException, ClientProtocolException, IOException
    {
        HttpResponse response = this.doGet(url, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    public String doGetForString(String url) throws URISyntaxException, ClientProtocolException, IOException
    {
        return HttpURLConnHelper.readStream(this.doGet(url), null);
    }

    public static String readStream(InputStream in, String encoding)
    {
        if (in == null)
        {
            return null;
        }
        try
        {
            InputStreamReader inReader = null;
            if (encoding == null)
            {
                inReader = new InputStreamReader(in, defaultEncoding);
            }
            else
            {
                inReader = new InputStreamReader(in, encoding);
            }
            char[] buffer = new char[bufferSize];
            int readLen = 0;
            StringBuffer sb = new StringBuffer();
            while ((readLen = inReader.read(buffer)) != -1)
            {
                sb.append(buffer, 0, readLen);
            }
            inReader.close();
            return sb.toString();
        }
        catch (IOException e)
        {
            log.error("读取返回内容出错", e);
        }
        return null;
    }

    public InputStream doGetForStream(String url, Map<String, String> queryParams) throws URISyntaxException,
            ClientProtocolException, IOException
    {
        HttpResponse response = this.doGet(url, queryParams);
        return response != null ? response.getEntity().getContent() : null;
    }

    public String doGetForString(String url, Map<String, String> queryParams) throws URISyntaxException,
            ClientProtocolException, IOException
    {
        return HttpURLConnHelper.readStream(this.doGetForStream(url, queryParams), null);
    }

    /**
     * 基本的Get请求
     *
     * @param url
     *            请求url
     * @param queryParams
     *            请求头的查询参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doGet(String url, Map<String, String> queryParams) throws URISyntaxException,
            ClientProtocolException, IOException
    {
        HttpGet gm = new HttpGet();
        URIBuilder builder = new URIBuilder(url);
        // 填入查询参数
        if (queryParams != null && !queryParams.isEmpty())
        {
            builder.setParameters(HttpURLConnHelper.convertMapToNameValuePairs(queryParams));
        }
        gm.setURI(builder.build());
        return client.execute(gm);
    }

    public String doPostForString(String url, Map<String, String> queryParams) throws URISyntaxException,
            ClientProtocolException, IOException
    {
        return HttpURLConnHelper.readStream(this.doPostForStream(url, queryParams, new HashMap()), null);
    }

    public InputStream doPostForStream(String url, Map<String, String> queryParams)
            throws URISyntaxException, ClientProtocolException, IOException
    {
        return doPostForStream(url, queryParams, new HashMap());
    }

    public InputStream doPostForStream(String url, Map<String, String> queryParams, Map<String, String> formParams)
            throws URISyntaxException, ClientProtocolException, IOException
    {
        HttpResponse response = this.doPost(url, queryParams, formParams);
        return response != null ? response.getEntity().getContent() : null;
    }

    public InputStream doPostForStream(String url, Map<String, String> queryParams, Map<String, String> formParams, Map<String, String> header, String flag)
            throws URISyntaxException, ClientProtocolException, IOException
    {
        HttpResponse response = this.doPost(url, queryParams, formParams, header, flag);
        return response != null ? response.getEntity().getContent() : null;
    }

    public String doPostRetString(String url, Map<String, String> queryParams, Map<String, String> formParams)
            throws URISyntaxException, ClientProtocolException, IOException
    {
        return HttpURLConnHelper.readStream(this.doPostForStream(url, queryParams, formParams), null);
    }

    public HttpResponse doPost(String url, Map<String, String> queryParams, Map<String, String> formParams)
            throws URISyntaxException, ClientProtocolException, IOException{
        return this.doPost(url, queryParams, formParams, new HashMap(),"");
    }

    /**
     * 基本的Post请求
     *
     * @param url
     *            请求url
     * @param queryParams
     *            请求头的查询参数
     * @param formParams
     *            post表单的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public HttpResponse doPost(String url, Map<String, String> queryParams, Map<String, String> formParams, Map<String, String> header, String flag)
            throws URISyntaxException, ClientProtocolException, IOException
    {
        HttpPost pm = new HttpPost();
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
//            System.out.println(entry.getKey() + "||" + entry.getValue());
            pm.setHeader(entry.getKey(), entry.getValue());
        }
//        log.debug(builder);
        // 填入查询参数
        if (queryParams != null && !queryParams.isEmpty())
        {
            builder.setParameters(HttpURLConnHelper.convertMapToNameValuePairs(queryParams));
        }
        pm.setURI(builder.build());
        // 填入表单参数
        if (formParams != null && !formParams.isEmpty())
        {
            if(Contants.JSONBODY.equals(flag)) {
                pm.setEntity(new StringEntity(JSONObject.fromObject(formParams).toString()));
            } else {
                pm.setEntity(new UrlEncodedFormEntity(HttpURLConnHelper.convertMapToNameValuePairs(formParams)));
            }
        }
        System.out.println(pm);
        return client.execute(pm);
    }

    public static synchronized HttpClient getHttpClient()
    {
        if (null == httpClient)
        {
            try
            {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                HttpParams params = new BasicHttpParams();
                // 设置字符集
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, CHARSET);
                HttpProtocolParams.setUseExpectContinue(params, false);
                HttpProtocolParams.setContentCharset(params, "UTF-8");
                // 从连接池获取连接超时
                ConnManagerParams.setTimeout(params, 5000);
                // 连接超时
                HttpConnectionParams.setConnectionTimeout(params, 15000);
                // Socket超时
                HttpConnectionParams.setSoTimeout(params, 30000);
                // 設置最大連接數
                ConnManagerParams.setMaxTotalConnections(params, 100);

                ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute()
                {
                    public int getMaxForRoute(HttpRoute route)
                    {
                        // TODO Auto-generated method stub
                        return 100;
                    }
                });

                SchemeRegistry schReg = new SchemeRegistry();

                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), SERVER_PORT));
                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 80));
                schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
                schReg.register(new Scheme("https", sf, 443));
                schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 430));
                ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
                httpClient = new DefaultHttpClient(conMgr, params);

                // 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
                ((DefaultHttpClient) httpClient).setHttpRequestRetryHandler(myRetryHandler);
            }
            catch (Exception e)
            {
                httpClient = new DefaultHttpClient();
            }
        }
        httpClient
                .getParams()
                .setParameter(
                        HttpProtocolParams.USER_AGENT,
                        "Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; ME525+ Build/4.5.3-109_DPP-11) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        return httpClient;
    }

    public static List<NameValuePair> convertMapToNameValuePairs(Map<String, String> params)
    {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (params != null)
        {
            java.util.Set<String> keys = params.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext())
            {
                String key = it.next();
                String value = params.get(key);
                NameValuePair pair;
                try
                {
                    /*
                     * pair = new BasicNameValuePair(key,
                     * URLEncoder.encode(value, "UTF-8"));
                     */
                    pair = new BasicNameValuePair(key, value);
                    pairs.add(pair);
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        log.debug(params.size());
        return pairs;
    }

    static HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler()
    {
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
        {
            System.out.println("retryRequest＝" + executionCount);
            if (executionCount >= 3) // 如果超过最大重试次数，那么就不要继续了
                return false;
            if (exception instanceof NoHttpResponseException)// 如果服务器丢掉了连接，那么就重试
                return true;
            if (exception instanceof javax.net.ssl.SSLHandshakeException)// 不要重试SSL握手异常
                return false;
            if (exception instanceof ConnectTimeoutException)//
            {
                System.out.println("retryRequest ConnectTimeoutException ");
                return true;
            }
            else if (exception instanceof SocketTimeoutException)//
            {
                System.out.println("retryRequest SocketTimeoutException ");
                return true;
            }
            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) // 如果请求被认为是幂等的，那么就重试
                return true;
            return false;
        }

    };

    /**
     * @description 调用Http请求
     * @author 戴晓飞 2014-09-05
     * @return
     * @throws IOException
     */
    public static String execute2(String serverUrl, String message, int connectTimeout, int readTimeout)
            throws IOException
    {

        java.net.URL connURL = new java.net.URL(serverUrl);
        HttpURLConnection httpCon = (HttpURLConnection) connURL.openConnection();

        // 设置http请求的头部
        httpCon.setUseCaches(false);// Post 请求不能使用缓存
        httpCon.setDoOutput(true); // http正文内，因此需要设为true, 默认情况下是false;
        httpCon.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true;

        // 设定传送的内容类型是可序列化的java对象
        httpCon.setRequestProperty("Content-type", "text/xml; charset=UTF-8");
        httpCon.setRequestMethod("POST"); // 设定请求的方法为"POST"，默认是GET
        httpCon.setConnectTimeout(connectTimeout); // 连接主机的超时时间（单位：毫秒）
        httpCon.setReadTimeout(readTimeout); // 从主机读取数据的超时时间（单位：毫秒）

        // 写入http请求的正文
        DataOutputStream dataOutputStream = new DataOutputStream(httpCon.getOutputStream());
        dataOutputStream.write(message.getBytes("UTF-8"));
        dataOutputStream.flush();
        dataOutputStream.close();
        int responseCode = httpCon.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) // 返回码正确
        {
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(httpCon.getInputStream(), "UTF-8"));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = bufReader.readLine()) != null)
            {
                sb.append(line);
            }
            bufReader.close();
            return sb.toString();
        }
        else
        // 返回码错误，例如：404
        {
            log.debug("调用Http请求有异常！返回码为：" + responseCode);
            return "";
        }
    }

    private static class MySSLSocketFactory extends SSLSocketFactory
    {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException
        {
            super(truststore);

            TrustManager tm = new X509TrustManager()
            {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                }

                public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
                UnknownHostException
        {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException
        {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    public static int getCONNECTTIMEOUT()
    {
        return CONNECTTIMEOUT;
    }

    public static void setCONNECTTIMEOUT(int connecttimeout)
    {
        CONNECTTIMEOUT = connecttimeout;
    }

    public static int getREADTIMEOUT()
    {
        return READTIMEOUT;
    }

    public static void setREADTIMEOUT(int readtimeout)
    {
        READTIMEOUT = readtimeout;
    }
}