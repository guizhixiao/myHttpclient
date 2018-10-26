import dept.DeptManage;
import net.sf.json.JSONObject;
import util.Contants;
import util.GetToken;
import util.jdbcUtil.DataSourceUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AddAllDept {

    public static void  addAllDept() {
        String sql = "select (case when a.parentid is null then 1 else a.parentid end) parentid, id, name " +
                     "  from t_xf_jg_ms a " +
                     " where a.id REGEXP '^[0-9]*$' " +
                     "order by a.orderby ";
        List<Map<String, Object>> resultList = DataSourceUtil.getList(sql, DataSourceUtil.MYSQLDATABASETYPE);
        for (int i = 0 ; i < resultList.size(); i++) {
            Map<String, Object> param = resultList.get(i);
            param.put("order", resultList.size()-i);
            System.out.println(param);
            String result =  DeptManage.addDept(param);
            System.out.println(result);
        }
    }


    public static void deleteAllDept() {
        String sql = "select (case when a.parentid is null then 1 else a.parentid end) parentid, id, name " +
                "  from t_xf_jg_ms a " +
                " where a.id REGEXP '^[0-9]*$' " +
                "order by a.orderby ";
        List<Map<String, Object>> resultList = DataSourceUtil.getList(sql, DataSourceUtil.MYSQLDATABASETYPE);
        resultList.forEach((k) -> {
            System.out.println(DeptManage.deletDept(k.get("id").toString()));;
        });
    }

    public static void main(String[] args) {
//        addAllDept();
        String result = null;
        try {
            result = GetToken.getToken();
            String token = JSONObject.fromObject(result).getString("access_token");
            Contants.TOKEN = token;
        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteAllDept();
    }
}
