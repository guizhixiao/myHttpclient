package util;

import net.sf.json.JSONObject;
import util.net.okHttpUtil.Network;

import java.io.IOException;

public class GetToken {

    public final static String TOKEN = "MvfZxQcvf1yUoiRtKlH2sbuaAnRgVt-4YvmpoXI10pSGaZlXJt-2f5JTedR4sx6SA3GCgc5ICM3MtbGDcm2mGBqnZqnDLHKc-3t5lEUxohHxHLqt8Gux6Jf06nYRnMvbJ-DSwSmD3J2zeJ_-EFvB9J4wyEc2Ef6andhkSEtbe-pYiZEqp8iENjQnNAofb8TghRqte5_lTYdQRxGlJeooLA";
    private final static String url ="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
//    private final static String CORPID = "11111";
    private final static String CORPID = "11111";
//    private final static String CORPSECRET = "111-11133";
    private final static String CORPSECRET = "111111";
    public static String getToken() throws IOException {
        Network network = new Network(String.format(url,CORPID,CORPSECRET));
        return network.perGet().body().string();
    }

    public static void main(String[] args) throws IOException {
        String result = getToken();
//        System.out.println(result);
        System.out.println(JSONObject.fromObject(result).getString("access_token"));
    }
}
