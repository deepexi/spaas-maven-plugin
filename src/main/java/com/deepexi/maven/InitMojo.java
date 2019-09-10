package com.deepexi.maven;

import com.deepexi.maven.constant.PluginConstants;
import com.deepexi.maven.entity.BaseModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Properties;

/**
 * 项目初始化插件命令
 * eg: mvn spaas:init -Dname=spaas-xxx-project 或 mvn spaas:init -Dname=spaas-xxx-module
 * @author chenling
 * @since V1.0.0
 */
@Mojo(name = "init", requiresProject = false, defaultPhase = LifecyclePhase.INITIALIZE)
public class InitMojo extends AbstractGenerateMojo {

    @Parameter(property = "name")
    private String name;
    @Parameter(property = "groupId")
    private String groupId;
    @Parameter(property = "artifactId")
    private String artifactId;
    @Parameter(property = "version")
    private String version;
    @Parameter(property = "package")
    private String packageName;
    @Parameter(property = "module")
    private String module;
    @Parameter(property = "appName", defaultValue = "HelloWorld")
    private String appName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (StringUtils.isNotBlank(name)) {
            String[] parameters = name.split(PluginConstants.PARAMETER_SEPARATOR_SYMBOL);
            artifactId = parameters[0];
            if (parameters.length > 2) {
                version = parameters[1];
            }
        }
        // 判断是否创建工程
        if (isProjectDir()) {
            if (StringUtils.isBlank(artifactId)) {
                artifactId = PluginConstants.DEFAULT_ARTIFACT_ID_FOR_PROJECT;
            }
            if (StringUtils.isBlank(groupId)) {
                groupId = PluginConstants.DEFAULT_GROUP_ID_FOR_PROJECT;
            }
            if (StringUtils.isBlank(version)) {
                version = PluginConstants.DEFAULT_VERSION_FOR_PROJECT;
            }
            if (StringUtils.isBlank(packageName)) {
                packageName = groupId;
            }
            File projectDir = new File(outputDirectory, artifactId);
            Properties executionProperties = getExecutionProperties(groupId, artifactId, version, packageName);
            executionProperties.put("appName", appName);
            if (!projectDir.exists()) {
                // 生成项目目录
                generateProject(executionProperties);
                // 生成模块目录
                executionProperties.put("parentArtifactId", artifactId);
                loopGenerateModuleAndApi(artifactId, module, executionProperties);
            } else {
                getLog().error("当前目录已经存在项目【" + artifactId + "】, 如需要生成单独生成模块请进行项目【" + artifactId + "】执行命令");
            }
        } else {
            BaseModule parentModuleInfo = getParentModuleInfo();
            if (parentModuleInfo == null) {
                throw new MojoExecutionException("必须在父工程目录下创建模块");
            }
            // 如果没有参数输入直接使用默认的变量
            if (StringUtils.isBlank(artifactId)) {
                artifactId = PluginConstants.DEFAULT_ARTIFACT_ID_FOR_MODULE;
            }
            groupId = parentModuleInfo.getGroupId();
            version = parentModuleInfo.getVersion();
            if (StringUtils.isBlank(packageName)) {
                packageName = groupId;
            }
            Properties executionProperties = getExecutionProperties(groupId, artifactId, version, packageName);
            executionProperties.put("parentArtifactId", parentModuleInfo.getArtifactId());
            // 对artifactId参数生成模块
            loopGenerateModuleAndApi(null, artifactId, executionProperties);

            // 对模块参数生成模块
            loopGenerateModuleAndApi(null, module, executionProperties);
        }
    }
}
