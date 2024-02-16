package system.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SimpleJsonParser {
    public static <T> String toJsonArray(List<T> list) {
        var response = list.stream()
                .map(item -> item.toString())
                .reduce((a, b) -> a + ",\n" + b)
                .orElse("");

        return "[%s]".formatted(response);
    }

    public static String simpleMessage(String msg) {
        return """
                {
                    "message": "%s"
                }
                """.formatted(msg);
    }

    public static String simpleError(String msg, String value) {
        return """
                {
                    "error": "%s",
                    "value": "%s"
                }
                """.formatted(msg, value);
    }

    public static Map<String, Object> objectToMap(String body) {
        var map = new HashMap<String, Object>();
        var props = body.trim()
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .replaceAll("\\{(.*)\\}", "$1")
                .split(",");

        for (var p : props) {
            var pSlip = p.split(":");
            var key = pSlip[0].trim().replaceAll("\"", "");
            var value = pSlip[1].trim().replaceAll("\"", "");

            map.put(key, pSlip[1].trim().equals("null") ? null : value);
        }

        return map;
    }

    public static String getProp(String body, String prop, String defaultValue) {
        String returnValue = defaultValue;
        Pattern pattern = Pattern.compile("\"%s\":(.*?),".formatted(prop));
        var matcher = pattern.matcher(body.trim().replaceAll("\r", "").replaceAll("\n", ""));
        if (matcher.find()) {
            returnValue = matcher.group(1);
        }

        return returnValue.replaceAll("\"", "").trim();
    }

    public static Long getProp(String body, String prop, Long defaultValue) {
        var value = getProp(body, prop, defaultValue.toString());
        return Long.parseLong(value);
    }

    public static Boolean getProp(String body, String prop, Boolean defaultValue) {
        var value = getProp(body, prop, defaultValue.toString());
        return Boolean.parseBoolean(value);
    }

    public static char getProp(String body, String prop, char defaultValue) {
        var value = getProp(body, prop, String.valueOf(defaultValue));
        return value.charAt(0);
    }
}
