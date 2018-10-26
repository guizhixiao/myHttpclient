package util.jdbcUtil;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.*;
import java.util.*;

public class DataSourceUtil {
    // public static Logger logger = null;
    public static DruidDataSource mysqlDataSource = null;
    public static DruidDataSource postgrepDataSource = null;
    public final static String MYSQLDATABASETYPE="MYQL";
    public final static String POSTGREPSQLDATABASETYPE="POSTGREPSQL";
    static {
        // logger = Logger.getLogger(DataSourceUtil.class.getName());
        // 初始化ThreadLocal变量
        mysqlDataSource = getDruidDataSource(MYSQLDATABASETYPE);
        postgrepDataSource = getDruidDataSource(POSTGREPSQLDATABASETYPE);
//        System.out.println("获取mysqlDataSource:"+mysqlDataSource);
//        System.out.println("获取postgrepDataSource:"+postgrepDataSource);
    }

    // 获取DruidDataSource
    public static DruidDataSource getDruidDataSource(String dataType) {

        Properties properties = PropsUtil.loadProps("druidconfig.properties");
        // 创建DruidDataSource
        DruidDataSource dataSource = new DruidDataSource();
        // 对DruidDataSource设置属性值
        String drvieClass = "";
        String jdbcUrl = "";
        String username = "";
        String password = "";
        if(MYSQLDATABASETYPE.equals(dataType)) {
            drvieClass = "mysqldriverClassName";
            jdbcUrl = "mysqljdbcUrl";
            username = "mysqlusername";
            password = "mysqlpassword";
        } else if(POSTGREPSQLDATABASETYPE.equals(dataType)) {
            drvieClass = "postgrepsqldriverClassName";
            jdbcUrl = "postgrepsqljdbcUrl";
            username = "postgrepsqlusername";
            password = "postgrepsqlpassword";
        }
        dataSource
                .setDriverClassName(properties.getProperty(drvieClass));
        dataSource.setUrl(properties.getProperty(jdbcUrl));
        dataSource.setUsername(properties.getProperty(username));
        dataSource.setPassword(properties.getProperty(password));
        dataSource.setMaxActive(Integer.parseInt(properties
                .getProperty("maxActive")));
        dataSource.setInitialSize(Integer.parseInt(properties
                .getProperty("initialSize")));
        dataSource
                .setMaxWait(Long.parseLong(properties.getProperty("maxWait")));
        dataSource.setMinIdle(Integer.parseInt(properties
                .getProperty("minIdle")));
        dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(properties
                .getProperty("timeBetweenEvictionRunsMillis")));
        dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(properties
                .getProperty("minEvictableIdleTimeMillis")));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(properties
                .getProperty("testWhileIdle")));
        dataSource.setTestOnBorrow(Boolean.parseBoolean(properties
                .getProperty("testOnBorrow")));
        dataSource.setTestOnReturn(Boolean.parseBoolean(properties
                .getProperty("testOnReturn")));
        dataSource.setPoolPreparedStatements(Boolean.parseBoolean(properties
                .getProperty("poolPreparedStatements")));
        dataSource.setMaxOpenPreparedStatements(Integer.parseInt(properties
                .getProperty("maxOpenPreparedStatements")));
        System.out.println("完成设置DruidDataSource参数");
        return dataSource;
    }

    // 获取数据库连接
    public static Connection getConnection(String dataBaseType) {
        Connection connection = null;
        // 定义连接变量
        try {

            if (MYSQLDATABASETYPE.equals(dataBaseType)) {
                if (mysqlDataSource == null) {
                    mysqlDataSource = getDruidDataSource(dataBaseType);
                }
                return mysqlDataSource.getConnection();
            } else if (POSTGREPSQLDATABASETYPE.equals(dataBaseType)) {
                if (postgrepDataSource == null) {
                    postgrepDataSource = getDruidDataSource(dataBaseType);
                }
                return postgrepDataSource.getConnection();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 获取list
     * @param sql
     * @return
     */
    public static List<Map<String, Object>> getList(String sql, String dataType) {
        Connection conn = null;
        Statement sta = null;
        ResultSet rs = null;
        conn = getConnection(dataType);
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        try {
            sta = conn.createStatement();
            rs = sta.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
            int columnCount = md.getColumnCount();   //获得列数
            while (rs.next()) {
                Map<String,Object> rowData = new HashMap<String,Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add(rowData);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(conn != null) {
                    conn.close();
                }
                if(sta != null) {
                    sta.close();
                }
                if(rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * 批量插入数据
     * @param sql
     * @param args
     */
    public static void addBatch(String sql, int argslength, List<Map<String, String>> args, String dataType) {
        PreparedStatement pstm = null;
        Connection connection = getConnection(dataType);
        try {
             pstm = connection.prepareStatement(sql);
             connection.setAutoCommit(false);
             for(int i = 0; i < args.size(); i++) {
                 Map<String, String> mp = args.get(i);
                 for(int j = 0; j < argslength; j++) {
                     pstm.setString(j+1, mp.get(String.valueOf(j)));
                 }
                 pstm.addBatch();
             }
             pstm.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(pstm != null) {
                try {
                    pstm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String arg[]) {
//        List<Map<String, Object>> list = getList("select * from sys_log");
//        String sql = "insert into t_xf_jg(id, name, parentid, orderby) values (?,?,?,?)";
//        Map<String, String> mp = new HashMap<String, String>();
//        mp.put("0","1");
//        mp.put("1","2");
//        mp.put("2","3");
//        mp.put("3","4");
//        List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
//        list1.add(mp);
//        addBatch(sql,4,list1);
//        System.out.println(list);
    }

}