package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getResourceAsStream("/config.properties")) {
            if (in != null) PROPS.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config.properties", e);
        }
    }

    private ConfigReader() {}

    /** Ưu tiên: -Dkey=value trên command line > config.properties > giá trị mặc định */
    public static String get(String key, String defaultValue) {
        return System.getProperty(key, PROPS.getProperty(key, defaultValue));
    }

    public static boolean getBool(String key, boolean def) { return Boolean.parseBoolean(get(key, String.valueOf(def))); }
    public static int getInt(String key, int def)          { return Integer.parseInt(get(key, String.valueOf(def))); }
}

