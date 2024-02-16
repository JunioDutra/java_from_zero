package system.common;

import static java.lang.System.getenv;

public class Env {
    public static final String PORT = "PORT";
    public static final String CONCURRENCY_SIZE = "CONCURRENCY_SIZE";
    public static final String DATABASE_URL = "DATABASE_URL";
    public static final String DATABASE_USER = "DATABASE_USER";
    public static final String DATABASE_PASSWORD = "DATABASE_PASSWORD";

    public static int getEnvInt(String key, int defaultValue) {
        return getenv(key) != null ? Integer.parseInt(getenv(key)) : defaultValue;
    }

    public static String getEnvStr(String key, String defaultValue) {
        return getenv(key) != null ? getenv(key) : defaultValue;
    }
}
