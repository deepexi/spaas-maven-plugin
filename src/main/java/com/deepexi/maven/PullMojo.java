package com.deepexi.maven;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.deepexi.maven.constant.PluginConstants;
import com.deepexi.maven.entity.UserInfo;
import com.deepexi.maven.tools.SpaasCliPropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;

/**
 * 拉取模块市场源码命令
 * eg: mvn spaas:pull -Dname=spaas-xxx-module
 * @author chenling
 * @since V1.0.0
 */
@Mojo(name = "pull", requiresProject = false)
public class PullMojo extends AbstractModuleMarketMojo {

    private static final String PULL_SAVE_TMP_DIR = "spaas-plugin-tmp";
    private static final String PULL_SAVE_MODULE_DIR_NAME = "spaas_module";

    @Parameter(property = "name")
    private String name;
    @Parameter(property = "artifactId")
    private String artifactId;
    @Parameter(property = "version")
    private String version;

    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    /**
     * pull步骤
     * 1.判断当前操作路径是模块路径还是父工程路径
     * 2.判断本地有没有配置用户信息
     * 3.下载模块市场源码文件
     * 4.解压源码文件到项目spass_module目录
     * 5.增加父模块引用新模块
     *
     * @throws MojoExecutionException 执行异常
     * @throws MojoFailureException 错误异常
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (StringUtils.isNotBlank(name)) {
            String[] parameters = name.split(PluginConstants.PARAMETER_SEPARATOR_SYMBOL);
            artifactId = parameters[0];
            if (parameters.length > 2) {
                version = parameters[1];
            }
        }
        if (StringUtils.isBlank(artifactId)) {
            throw new MojoExecutionException("模块名不能为空");
        }

        File pom = session.getRequest().getPom();
        Model model = project.getModel();
        if (pom == null || !"pom".equalsIgnoreCase(model.getPackaging())) {
            throw new MojoExecutionException("请在工程目录执行该命令");
        }

        String settingPath =  session.getRequest().getUserSettingsFile().getParent();
        try {
            UserInfo userInfo = getUserInfo(settingPath);

            File tmpDir = createPluginTmpDir(PULL_SAVE_TMP_DIR);

            // 请求模块市场下载模块原码文件
            String fileName = downloadFile(settingPath, userInfo, tmpDir);

            File moduleFile = createModuleDir();
            // 解压源码文件
            ZipUtil.unzip(new File(tmpDir, fileName), moduleFile);

            File unZipFile = new File(moduleFile, artifactId);
            // 增加父工程的module, 并且将目录复制到工程下
            addParentModule(unZipFile);

            cleanPluginTmpDir(tmpDir);

            getLog().info("----------------------------[ 模块拉取成功 ]----------------------------");
        } catch (Exception e) {
            getLog().error(e);
        }

    }

    /**
     * 拉取下来的模块统一放到spaas_module里
     * @return 统一文件
     */
    private File createModuleDir(){
        File basedir = project.getBasedir();
        File moduleFile = new File(basedir, PULL_SAVE_MODULE_DIR_NAME);
        if (!moduleFile.exists()) {
            moduleFile.mkdirs();
        }
        return moduleFile;
    }

    /**
     * 发起下载请求
     */
    private String downloadFile(String propPath, UserInfo userInfo, File tmpDir) throws IOException, MojoExecutionException {
        String prefixUrl = SpaasCliPropertiesUtils.readMarketUrlProperty(propPath);
        StringBuilder url = new StringBuilder(prefixUrl).append(PluginConstants.MARKET_URI_PULL_PROPERTY_KEY);
        url.append("?artifactId=").append(artifactId)
                .append("&userName=").append(userInfo.getUserName())
                .append("&password=******")
                .append("&tenantId=").append(userInfo.getTenantId());
        if (StringUtils.isNotBlank(version)) {
            url.append("&version=").append(version);
        }
        getLog().debug("url=" + url.toString());
        HttpResponse response = HttpRequest.get(url.toString()).execute();
        String contentType = response.header(Header.CONTENT_TYPE);
        if (contentType.startsWith("application/octet-stream")) {
            String fileName = response.header(Header.CONTENT_DISPOSITION);
            fileName = fileName.replace("\"", "");
            fileName = fileName.substring(fileName.lastIndexOf("=") + 1);
            // 进行文件下载
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(tmpDir, fileName)));
            IoUtil.copy(response.bodyStream(), bos);
            bos.close();
            return fileName;
        } else if (contentType.startsWith("application/json")){
            // 服务器异常返回
            String body = response.body();
            JSONObject payload = JSONUtil.parseObj(body);
            Object message = payload.get("message");
            throw new MojoExecutionException(message.toString());
        } else {
            throw new MojoExecutionException("请求返回的格式不支持");
        }
    }

    private boolean addParentModule(File unZipFile) throws MojoExecutionException {
        // 读取拉取下来的pom -- artifactId
        File pom = new File(unZipFile, "pom.xml");
        if (!pom.exists()) {
            getLog().warn("加载模块不是标准备的maven模块");
            return Boolean.FALSE;
        }
        // 将模块加入父模块
//        model.addModule("spaas_module/" + childArtifactId);
        try {
            File parentPom = session.getRequest().getPom();
            SAXReader reader = new SAXReader();
            org.dom4j.Document parentDoc = reader.read(parentPom);
            org.dom4j.Element parentRoot = parentDoc.getRootElement();
            org.dom4j.Element modules = parentRoot.element("modules");
//            String parentGroupId = parentRoot.elementTextTrim("groupId");
//            String parentArtifactId = parentRoot.elementTextTrim("artifactId");
//            String parentVersion = parentRoot.elementTextTrim("version");

            org.dom4j.Document childDoc = reader.read(pom);
            org.dom4j.Element childRoot = childDoc.getRootElement();
            String childArtifactId = childRoot.elementTextTrim("artifactId");
//            Element parent = childRoot.element("parent");
//            parent.element("groupId").setText(parentGroupId);
//            parent.element("artifactId").setText(parentArtifactId);
//            parent.element("version").setText(parentVersion);
//            parent.addElement("relativePath").setText("../..");

            if (modules == null) {
                modules = parentRoot.addElement("modules");
            }
            modules.addElement("module").addText(PULL_SAVE_MODULE_DIR_NAME + "/" + childArtifactId);

            try (java.io.FileWriter parentWriter = new java.io.FileWriter(parentPom)) {
                XMLWriter writer = new XMLWriter(parentWriter);
                writer.write(parentDoc);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try (java.io.FileWriter childWriter = new java.io.FileWriter(pom)) {
//                XMLWriter writer = new XMLWriter(childWriter);
//                writer.write(childDoc);
//                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.TRUE;
    }
}
