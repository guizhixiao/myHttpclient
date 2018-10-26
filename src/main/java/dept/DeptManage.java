package dept;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import util.Contants;
import util.net.okHttpUtil.Network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DeptManage {
    /**
     * 新增部门
     * @param paramMap
     * @return
     */
    public static String addDept(Map<String, Object> paramMap) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.ADD_DEPT,Contants.TOKEN), paramMap);
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
     * 查看部门列表
     * @param deptId
     * @return
     */
    public static String getDeptList(String deptId) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.GET_DEPT,Contants.TOKEN, deptId));
            Response body = network.perGet();
            if(body.isSuccessful()) {
                return body.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新部门
     * @return
     */
    public static String updateDept(Map<String, Object> paramMap) {
        String result = null;
        try {
            Network network = new Network(String.format(Contants.UPDATE_DEPT,Contants.TOKEN), paramMap);
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
     * 删除部门
     * @param deptId
     * @return
     */
    public static String deletDept(String deptId) {
        String result = null;
        if(StringUtils.isEmpty(deptId)){
            return result;
        }
        try {
            Network network = new Network(String.format(Contants.DELET_DEPT,Contants.TOKEN, deptId));
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
//        System.out.println(getDeptList("4"));
        Map<String, Object> body = new HashMap<>();
        body.put("id","3e");
        body.put("name","3422");
        body.put("parentid",1);
//        body.put("order",200);
//        body.put("access_token", Contants.TOKEN);
//        System.out.println(updateDept(body));;
//        System.out.println(addDept(body));
        System.out.println(getDeptList(""));
        JSONObject json = JSONObject.fromObject(getDeptList(""));
//        JSONObject jd = json.getJSONArray("department");
//        System.out.println(jd);
        JSONArray jsonArray = json.getJSONArray("department");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject j = jsonArray.getJSONObject(i);
            String id = j.getString("id");
            System.out.println(deletDept(id));
        }
    }

}
