package system.common;

import static java.lang.System.getenv;

public class Env {
    public static int getEnvInt(String key, int defaultValue) {
        return getenv(key) != null ? Integer.parseInt(getenv(key)) : defaultValue;
    }

    public static String getEnvStr(String key, String defaultValue) {
        return getenv(key) != null ? getenv(key) : defaultValue;
    }
}
