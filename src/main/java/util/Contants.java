package util;

public class Contants {

    public static final String JSONBODY = "json";

    public static String TOKEN = "vwUQ70uNvHik7qLfUufGCLSHJCSlXCGsEgS8hWZPhDTx5QNY7pzJUVs1Z4LsMySvrzFAh1XLp1yPJSqAAslYVB_XzJ5z1GemTSa8qajaWNxmDcmJUMGGQlxvOTaV2TWrMFGfn6560bp8LXAFCYTyFVlE4N6yGI8QZjtV5NQQEl39kht8PDVVDPrnHtuY_vClacRsBt1VMw0NGQl_QntPbg";

    // 获取部门信息
    public final static String GET_DEPT = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s&id=%s";

    // 新增部门信息
    public final static String ADD_DEPT = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=%s";

    //修改部门
    public final static String UPDATE_DEPT = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=%s";

    // 删除部门
    public final static String DELET_DEPT = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=%s&id=%s";


    // 获取部门成员
    public final static String GET_USERS = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=%s&department_id=%s&fetch_child=%s";

    // 获取部门成员详情
    public final static String GET_USERS_DETAIL = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=%s&department_id=%s&fetch_child=%s";

    // 获取用户信息
    public final static String GET_USER = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";

    // 新增用户信息
    public final static String ADD_USER = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=%s";

    // 修改用户
    public final static String UPDATE_USER = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=%s";

    // 删除用户
    public final static String DELET_USER = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=%s&userid=%s";

    // 批量删除用户
    public final static String DELET_USERS = "https://qyapi.weixin.qq.com/cgi-bin/user/batchdelete?access_token=%s";

    // userid转openid
    public final static String USERTOOPNID = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid?access_token=%s";

}
