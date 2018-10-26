package util;

import java.util.Map;


/**
 * @author suntf
 * @desc
 * @date 2017-3-24
 */
public class MapUtils {
    /**
     * 通过key从map中获取value字符串
     *
     * @param map
     * @param key
     * @return
     * @auth suntf
     * @date 2017-3-24
     */
    public static String getStringValueByKey(Map<?, ?> map, String key) {
        if (checkMapIsEmpty(map)) {
            return "";
        }

        if (!map.containsKey(key)) {
            return "";
        }

        if (checkObjectISEmpty(map.get(key))) {
            return "";
        }

        return map.get(key).toString().trim();
    }

    /**
     * 验证map为null 或者 空对象
     *
     * @param map
     * @return
     * @auth suntf
     * @date 2017-3-24
     */
    public static boolean checkMapIsEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty()) ? true : false;
    }

    /**
     * 验证Object对象是否为空
     *
     * @param obj
     * @return
     * @auth suntf
     * @date 2017-3-24
     */
    public static boolean checkObjectISEmpty(Object obj) {
        return (obj == null || "".equals(obj)) ? true : false;
    }

    /**
     * 通过key获取数字
     *
     * @param map
     * @param key
     * @auth suntf
     * @date 2017-3-24
     */
    public static int getIntValueByKey(Map<?, ?> map, String key) {
        return getDefaultIntValueByKey(map, key, 0);
    }

    /**
     * 通过map获取数字，如果map获取的value为空，放回用户给定的默认值
     *
     * @param map
     * @param key
     * @param defaultValue
     * @return
     * @auth suntf
     * @date 2017-3-24
     */
    public static int getDefaultIntValueByKey(Map<?, ?> map, String key, int defaultValue) {
        String value = getStringValueByKey(map, key);
        return checkObjectISEmpty(value) ? defaultValue : Integer.parseInt(value);
    }
}