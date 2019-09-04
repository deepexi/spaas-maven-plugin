package com.deepexi.maven;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.deepexi.maven.constant.PluginConstants;
import com.deepexi.maven.tools.SpaasCliPropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.StringJoiner;

/**
 * 搜索模块
 * @title: SearchMojo
 * @package com.deepexi.maven
 * @description:
 * @author chenling
 * @date 2019/8/22 17:12
 * @since V1.0.0
 */
@Mojo(name = "search", requiresProject = false)
public class SearchMojo extends AbstractMojo {

    @Parameter(property = "artifactId")
    private String artifactId;

    @Parameter(property = "size", defaultValue = "15")
    private Integer size;

    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String settingPath = session.getRequest().getUserSettingsFile().getParent();
        String url = SpaasCliPropertiesUtils.readMarketUrlProperty(settingPath);
        StringBuilder reqUrl = new StringBuilder(url);
        reqUrl.append(PluginConstants.MARKET_URI_SEARCH_PROPERTY_KEY)
                .append("?size=").append(size);
        if (StringUtils.isNotBlank(artifactId)) {
            reqUrl.append("&artifactId=").append(artifactId);
        }
        String response = HttpUtil.get(reqUrl.toString());
        JSONObject obj = JSONUtil.parseObj(response);
        Integer code = obj.getInt("code");
        if (code != null && code.equals(0)) {
            JSONArray payloads = obj.getJSONArray("payload");
            if (payloads != null && payloads.size() > 0) {
                System.out.printf("%-100s", "ARTIFACT_ID");
                System.out.println("VERSION");
                for (Object payload : payloads) {
                    JSONObject payloadObj = (JSONObject) payload;
                    String artifactId = payloadObj.getStr("artifactId");
                    if (artifactId.length() > 100) {
                        artifactId = artifactId.substring(0, 97) + "...";
                    }
                    System.out.printf("%-100s", artifactId);
                    System.out.println(payloadObj.getStr("version"));
                }
            }
        } else {
            getLog().info("查无数据");
        }
    }
}
