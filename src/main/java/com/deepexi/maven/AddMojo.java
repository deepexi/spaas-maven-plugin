package com.deepexi.maven;

import com.deepexi.maven.constant.PluginConstants;
import com.deepexi.maven.entity.BaseModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Properties;
/**
 * 增加模块命令
 * @author huangzh
 */
@Mojo(name = "add", requiresProject = false)
public class AddMojo extends AbstractGenerateMojo {

    @Parameter(property = "artifactId", defaultValue = PluginConstants.DEFAULT_ARTIFACT_ID_FOR_MODULE)
    private String artifactId;
    @Parameter(property = "package")
    private String packageName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        BaseModule parentModuleInfo = getParentModuleInfo();
        if (parentModuleInfo == null) {
            throw new MojoExecutionException("必须在父工程目录下创建模块");
        }
        // 设置maven坐标
        if (StringUtils.isBlank(packageName)) {
            packageName = parentModuleInfo.getGroupId();
        }
        Properties executionProperties = getExecutionProperties(parentModuleInfo.getGroupId(), artifactId, parentModuleInfo.getVersion(), packageName);
        executionProperties.put("parentArtifactId", parentModuleInfo.getArtifactId());
        loopGenerateModuleAndApi(null, artifactId, executionProperties);
    }
}
