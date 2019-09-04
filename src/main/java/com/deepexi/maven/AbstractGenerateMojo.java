package com.deepexi.maven;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.XmlUtil;
import com.deepexi.maven.constant.PluginConstants;
import com.deepexi.maven.entity.BaseModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeManager;
import org.apache.maven.archetype.common.Constants;
import org.apache.maven.archetype.ui.generation.ArchetypeGenerationConfigurator;
import org.apache.maven.archetype.ui.generation.ArchetypeSelector;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author huangzh
 */
public abstract class AbstractGenerateMojo extends AbstractMojo {

    private static final List<String> delFiles = Arrays.asList("remark.txt");

    @Component
    private ArchetypeManager manager;
    @Component
    private ArchetypeSelector selector;
    @Component
    private ArchetypeGenerationConfigurator configurator;
    /**
     * Local Maven repository.
     */
    @Parameter( defaultValue = "${localRepository}", readonly = true, required = true )
    private ArtifactRepository localRepository;
    /**
     * List of remote repositories used by the resolver.
     */
    @Parameter( defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true )
    private List<ArtifactRepository> remoteArtifactRepositories;
    @Parameter( defaultValue = "${basedir}", property = "outputDirectory" )
    protected File outputDirectory;
    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession session;

    /**
     * 设置生成的项目的坐标
     * @param groupId 组织标识
     * @param artifactId 项目名
     * @param version 版本
     * @param packageName 包名
     * @return
     */
    protected Properties getExecutionProperties(String groupId, String artifactId, String version, String packageName) {
        Properties executionProperties = session.getUserProperties();
        executionProperties.put(Constants.GROUP_ID, groupId);
        if (StringUtils.isBlank(artifactId)) {
            artifactId = PluginConstants.DEFAULT_ARTIFACT_ID_FOR_MODULE;
        }
        executionProperties.put(Constants.ARTIFACT_ID, artifactId);
        executionProperties.put(Constants.VERSION, version);
        if (StringUtils.isBlank(packageName)) {
            packageName = groupId;
        }
        executionProperties.put(Constants.PACKAGE, packageName);
        return executionProperties;
    }

    /**
     * 创建工程的maven请求对象
     * @return
     */
    protected ArchetypeGenerationRequest getProjectArchetypeGenerationRequest() {
        ArchetypeGenerationRequest request = getBaseArchetypeGenerationRequest();
        request.setArchetypeGroupId(PluginConstants.ARCHETYPE_GROUP_ID_FOR_PROJECT)
                .setArchetypeArtifactId(PluginConstants.ARCHETYPE_ARTIFACT_ID_FOR_PROJECT)
                .setArchetypeVersion(PluginConstants.ARCHETYPE_VERSION_FOR_PROJECT);
        return request;
    }

    /**
     * 创建模块的maven请求对象
     * @return
     */
    protected ArchetypeGenerationRequest getModuleArchetypeGenerationRequest() {
        ArchetypeGenerationRequest request = getBaseArchetypeGenerationRequest();
        request.setArchetypeGroupId(PluginConstants.ARCHETYPE_GROUP_ID_FOR_MODULE)
                .setArchetypeArtifactId(PluginConstants.ARCHETYPE_ARTIFACT_ID_FOR_MODULE)
                .setArchetypeVersion(PluginConstants.ARCHETYPE_VERSION_FOR_MODULE);
        return request;
    }

    /**
     * 创建模块API的maven请求对象
     * @return
     */
    protected ArchetypeGenerationRequest getModuleApiArchetypeGenerationRequest() {
        ArchetypeGenerationRequest request = getBaseArchetypeGenerationRequest();
        request.setArchetypeGroupId(PluginConstants.ARCHETYPE_GROUP_ID_FOR_MODULE_API)
                .setArchetypeArtifactId(PluginConstants.ARCHETYPE_ARTIFACT_ID_FOR_MODULE_API)
                .setArchetypeVersion(PluginConstants.ARCHETYPE_VERSION_FOR_MODULE_API);
        return request;
    }

    /**
     * 创建基本的maven请求对象
     * @return
     */
    protected ArchetypeGenerationRequest getBaseArchetypeGenerationRequest() {
        ArchetypeGenerationRequest request =
                new ArchetypeGenerationRequest()
                        .setOutputDirectory(outputDirectory.getAbsolutePath())
                        .setLocalRepository(localRepository)
                        .setRemoteArtifactRepositories(remoteArtifactRepositories)
//                        .setProjectBuildingRequest(session.getProjectBuildingRequest())
                ;
        return request;
    }

    /**
     * 调用maven底层生成模块工程或模块
     * @param request 请求对象
     * @param executionProperties
     * @throws MojoExecutionException
     */
    protected void generateProjectOrModuleFromArchetype(ArchetypeGenerationRequest request, Properties executionProperties) throws MojoExecutionException {
        try {
            selector.selectArchetype(request, false, "remote,local");
            if (StringUtils.isBlank(request.getArchetypeArtifactId())) {
                return;
            }
            configurator.configureArchetype(request, false, executionProperties);
            manager.generateProjectFromArchetype(request);
        } catch (Exception e) {
            throw new MojoExecutionException("生成项目或模块失败，请验证参数是否正确");
        }
    }

    protected BaseModule getParentModuleInfo() throws MojoExecutionException {
        File pom = session.getRequest().getPom();
        if (pom == null) {
            throw new MojoExecutionException("模块生成必须在父工程目录下");
        }
        if (pom.exists()) {
            try {
                Document document = XmlUtil.readXML(pom);
                Element rootElement = XmlUtil.getRootElement(document);
                String parentArtifactId = XmlUtil.elementText(rootElement, "artifactId");
                String parentGroupId = XmlUtil.elementText(rootElement, "groupId");
                String parentVersion = XmlUtil.elementText(rootElement, "version");
                return new BaseModule(parentArtifactId, parentGroupId, parentVersion);
            } catch (Exception e) {
                getLog().warn("该工程不是标准的maven工程");
            }
        }
        return null;
    }

    /**
     * 判断命令目录是否为父工程
     * @return
     */
    protected Boolean isProjectDir() {
        File pom = session.getRequest().getPom();
        if (pom == null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 生成单一的模块与模块API
     * @param executionProperties
     * @throws MojoExecutionException
     */
    protected void generateModuleAndApi(String outDir, Properties executionProperties) throws MojoExecutionException {
        String artifactId = executionProperties.getProperty(Constants.ARTIFACT_ID);
        ArchetypeGenerationRequest request = getBaseArchetypeGenerationRequest();
        // 1. 生成模块
        request.setArchetypeArtifactId(PluginConstants.ARCHETYPE_ARTIFACT_ID_FOR_MODULE)
                .setArchetypeGroupId(PluginConstants.ARCHETYPE_GROUP_ID_FOR_MODULE)
                .setArchetypeVersion(PluginConstants.ARCHETYPE_VERSION_FOR_MODULE);
        if (StringUtils.isNotBlank(outDir)) {
            request.setOutputDirectory(outputDirectory.getAbsolutePath() + File.separator + outDir);
        }
        generateProjectOrModuleFromArchetype(request, executionProperties);
        // 2. 生成模块api
        request.setArchetypeArtifactId(PluginConstants.ARCHETYPE_ARTIFACT_ID_FOR_MODULE_API)
                .setArchetypeGroupId(PluginConstants.ARCHETYPE_GROUP_ID_FOR_MODULE_API)
                .setArchetypeVersion(PluginConstants.ARCHETYPE_VERSION_FOR_MODULE_API);
        String artifactIdApi = artifactId + "-api";
        executionProperties.put(Constants.ARTIFACT_ID, artifactIdApi);
        generateProjectOrModuleFromArchetype(request, executionProperties);

        String currentPath = session.getRequest().getBaseDirectory();
        cleanSurplusFile(new File(currentPath, artifactId));
        cleanSurplusFile(new File(currentPath, artifactIdApi));
    }

    /**
     * 生成工程
     * @param executionProperties
     * @throws MojoExecutionException
     */
    protected void generateProject(Properties executionProperties) throws MojoExecutionException {
        ArchetypeGenerationRequest request = getProjectArchetypeGenerationRequest();
        generateProjectOrModuleFromArchetype(request, executionProperties);
    }

    /**
     * 循环生成模块与模块api
     *
     * @param moduleParam 模块参数，可以多个使用逗号分隔
     * @param executionProperties
     * @throws MojoExecutionException
     */
    protected void loopGenerateModuleAndApi(String outDir, String moduleParam, Properties executionProperties) throws MojoExecutionException {
        if (StringUtils.isNotBlank(moduleParam)) {
            String[] modules = moduleParam.split(PluginConstants.MULTI_PARAMETER_SEPARATOR);
            for (String moduleArtifactId : modules) {
                executionProperties.put(Constants.ARTIFACT_ID, moduleArtifactId);
                generateModuleAndApi(outDir, executionProperties);
            }
        }
    }

    /**
     * 删除多余的文件
     * @param file 需要处理的目录或文件
     */
    protected void cleanSurplusFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                cleanSurplusFile(f);
            }
        } else {
            if (delFiles.contains(file.getName())) {
                FileUtil.del(file);
            }
        }
    }
}
