package employee;

import com.sun.istack.internal.NotNull;
import net.sf.json.JSONArray;
import okhttp3.Response;
import util.Contants;
import util.net.okHttpUtil.Network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理
 */
public class EmployeeManage {

        /**
         * 新增用户
         * @param paramMap
         * @return
         */
        public static String addEmployee(Map<String, Object> paramMap) {
            String result = null;
            try {
                Network network = new Network(String.format(Contants.ADD_USER,Contants.TOKEN), paramMap);
                Response body = network.performJSONPost();
                if(body.isSuccessful()) {
                    return body.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

    /**
     * 修改用户
     * @param paramMap
     * @return
     */
    public static String updateEmployee(Map<String, Object> paramMap) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.UPDATE_USER,Contants.TOKEN), paramMap);
            Response body = network.performJSONPost();
            if(body.isSuccessful()) {
                return body.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取部门成员
     * @param department_id
     * @param fetch_child
     * @return
     */
    public static String getEmployees(String department_id, String fetch_child) {
        String result = null;

        try {
            Network network = new Network(String.format(Contants.GET_USERS, Contants.TOKEN, department_id, fetch_child));
            Response body = network.performJSONPost();
            if(body.isSuccessful()) {
                return body.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取部门成员详情
     * @param department_id
     * @param fetch_child
     * @return
     */
    public static String getEmployeesDetail(String department_id, String fetch_child) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.GET_USERS_DETAIL,Contants.TOKEN, department_id, fetch_child));
            Response body = network.performJSONPost();
            if(body.isSuccessful()) {
                return body.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取用户信息
     * @return
     */
    public static String getEmployeeDetail(String userId) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.GET_USER,Contants.TOKEN, userId));
            Response body = network.performJSONPost();
            if(body.isSuccessful()) {
                return body.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 删除用户
     * @return
     */
    public static String deletUser(String userId) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.DELET_USER,Contants.TOKEN, userId));
            Response body = network.performJSONPost();
            if(body.isSuccessful()) {
                return body.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除用户
     * @return
     */
    public static String deletUsers(Map<String, Object> param) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.DELET_USERS,Contants.TOKEN), param);
            Response body = network.performJSONPost();
            if(body.isSuccessful()) {
                return body.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * userid转openid
     * @return
     */
    public static String userToOpenId(Map<String, Object> param) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.USERTOOPNID,Contants.TOKEN), param);
            Response body = network.performJSONPost();
            if(body.isSuccessful()) {
                return body.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("1");
//        System.out.println(jsonArray);
        Map<String, Object> param = new HashMap<>();
        param.put("userid","cs");
        param.put("name","测试11");
        param.put("mobile","15961989000");
        param.put("department",jsonArray);
        param.put("order","100");
        JSONArray userArray = new JSONArray();
        userArray.add("loudf");
        userArray.add("XiaMingSong");
        param.put("useridlist", userArray);
        String result = deletUsers(param);
//        String result = deletUser("ces");
//        String result = userToOpenId(param);
//        String result = getEmployeesDetail("1","1");
//        String result = getEmployees("1","1");
//        String result = updateEmployee(param);
//        String result = getEmployeeDetail("ces");
//        String result = addEmployee(param);
        System.out.println(result);
    }



}
