package com.deepexi.maven.tools;

import com.deepexi.maven.constant.PluginConstants;

import java.io.*;
import java.util.Properties;

public final class SpaasCliPropertiesUtils {

    private SpaasCliPropertiesUtils(){}


    /**
     * 获取配置文件market.url的属性值
     * @param path
     * @return
     */
    public static String readMarketUrlProperty(String path) {
        return readProperty(path, PluginConstants.MARKET_URL_PROPERTY_KEY, PluginConstants.DEFAULT_MARKET_URL_PROPERTY_KEY);
    }

    /**
     * 获取配置信息
     * @param path 配置文件路径
     * @param key 配置key
     * @return
     * @throws IOException
     */
    public static String readProperty(String path, String key, String defaultVal) {
        File propFile = new File(path, "spaas-cli.properties");
        if (propFile.exists()) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(propFile));
                return prop.getProperty(key);
            } catch (Exception e) {
            }
        }
        return defaultVal;
    }

    /**
     * 写入properties配置
     * @param path
     * @param key
     * @param val
     * @return
     */
    public static Boolean writeProperty(String path, String key, String val) {
        File propFile = new File(path, "spaas-cli.properties");
        if (propFile.exists()) {
            OutputStream os = null;
            try {
                os = new FileOutputStream(propFile);
                Properties prop = new Properties();
                prop.setProperty(key, val);
                prop.store(os, "update prop");
            } catch (Exception e) {
            }
        }
        return Boolean.FALSE;
    }
}
