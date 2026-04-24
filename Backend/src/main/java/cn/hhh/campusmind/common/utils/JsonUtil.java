package cn.hhh.campusmind.common.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象转换为JSON数组字符串
     * @param value 要转换的对象
     * @return JSON数组字符串
     */
    public static String toJsonArray(Object value) {
        try {
            if (value == null) {
                return "[]";
            }
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    /**
     * 将对象转换为JSON对象字符串
     * @param value 要转换的对象
     * @return JSON对象字符串
     */
    public static String toJsonObject(Object value) {
        try {
            if (value == null) {
                return "{}";
            }
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }


    /**
     * 格式化JSON数组字符串，确保数组元素包含双引号
     * @param value JSON数组字符串输入
     * @return 格式化后的JSON数组字符串输出
     */
    public static String normalizeJsonArray(String value) {
        if (value == null || value.isBlank()) {
            return "[]";
        }
        String v = value.trim();
        if (v.startsWith("[") && v.endsWith("]")) {
            return v;
        }
        return "[\"" + v.replace("\"", "\\\"") + "\"]";
    }

}
