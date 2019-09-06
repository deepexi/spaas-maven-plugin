package com.deepexi.maven.constant;

/**
 * @author huangzh
 */
public final class PluginConstants {

    /**
     * maven创建项目脚手架的groupId
     */
    public static final String ARCHETYPE_GROUP_ID_FOR_PROJECT = "com.github.deepexi";
    /**
     * maven创建项目脚手架的artifactId
     */
    public static final String ARCHETYPE_ARTIFACT_ID_FOR_PROJECT = "spaas-template-project";
    /**
     * maven创建项目脚手架的version
     */
    public static final String ARCHETYPE_VERSION_FOR_PROJECT = "1.0.1";

    /**
     * maven创建模块脚手架的groupId
     */
    public static final String ARCHETYPE_GROUP_ID_FOR_MODULE = "com.github.deepexi";
    /**
     * maven创建模块脚手架的artifactId
     */
    public static final String ARCHETYPE_ARTIFACT_ID_FOR_MODULE = "spaas-template-module";
    /**
     * maven创建模块脚手架的version
     */
    public static final String ARCHETYPE_VERSION_FOR_MODULE = "1.0.0";

    /**
     * maven创建模块API脚手架的groupId
     */
    public static final String ARCHETYPE_GROUP_ID_FOR_MODULE_API = "com.github.deepexi";
    /**
     * maven创建模块API脚手架的artifactId
     */
    public static final String ARCHETYPE_ARTIFACT_ID_FOR_MODULE_API = "spaas-template-module-api";
    /**
     * maven创建模块API脚手架的version
     */
    public static final String ARCHETYPE_VERSION_FOR_MODULE_API = "1.0.0";

    /**
     * 默认创建项目名
     */
    public static final String DEFAULT_ARTIFACT_ID_FOR_PROJECT = "spaas-demo-center";
    /**
     * 默认创建项目groupId
     */
    public static final String DEFAULT_GROUP_ID_FOR_PROJECT = "com.deepexi";
    /**
     * 默认创建项目版本
     */
    public static final String DEFAULT_VERSION_FOR_PROJECT = "1.0.0-SNAPSHOT";
    /**
     * 默认创建模块名
     */
    public static final String DEFAULT_ARTIFACT_ID_FOR_MODULE = "spaas-demo-center-module";

    /**
     * 多模块分隔符
     */
    public static final String MULTI_PARAMETER_SEPARATOR = ",";

    /**
     * 模块市场前缀URL的配置前缀属性名
     */
    public static final String MARKET_URL_PROPERTY_KEY = "market.url";
    /**
     * 默认的模块市场前缀URL
     */
    public static final String DEFAULT_MARKET_URL_PROPERTY_KEY = "http://172.26.38.11:5002/spaas-module-market-api";
    /**
     * 模块市场search的URI
     */
    public static final String MARKET_URI_SEARCH_PROPERTY_KEY = "/api/v1/moduleCli/search";
    /**
     * 模块市场pull的URI
     */
    public static final String MARKET_URI_PULL_PROPERTY_KEY = "/api/v1/moduleCli/pull";
    /**
     * 模块市场push的URI
     */
    public static final String MARKET_URI_PUSH_PROPERTY_KEY = "/api/v1/moduleCli/push";


}
