package com.deepexi.maven;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.deepexi.maven.constant.PluginConstants;
import com.deepexi.maven.entity.Module;
import com.deepexi.maven.entity.UserInfo;
import com.deepexi.maven.tools.MetaConvert;
import com.deepexi.maven.tools.SpaasCliPropertiesUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.setting.Setting.DEFAULT_CHARSET;

/**
 * 推送模块命令
 * @author chenling
 * @since V1.0.0
 */
@Mojo(name = "push", requiresProject = false)
public class PushMojo extends AbstractModuleMarketMojo {



    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;


    @Parameter(defaultValue = "${basedir}", property = "outputDirectory")
    private File outputDirectory;


    /**
     * The archetype project to execute the integration tests on.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;



    @Component
    private Prompter prompt;

    /**
     * ================================》》》push 步骤《《《=====================================
     *
     * 1.判断当前操作路径是模块路径还是父工程路径
     *
     * 2.判断本地有没有配置用户信息
     *
     * 3.获取基础信息
     *
     * 4.组装文件信息
     *
     * 5.发起push HTTP 请求
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        //1.获取父级目录
        String parentPath = outputDirectory.getParent();
        //2.获取当前操作模块目录
        String currentPath =session.getRequest().getBaseDirectory();

        String artifactId = session.getCurrentProject().getArtifactId();

        String groupId = session.getCurrentProject().getGroupId();

        String version = session.getCurrentProject().getVersion();

        String settingPath =  session.getRequest().getUserSettingsFile().getPath();

        UserInfo userInfo ;

        try {
            judgePath(this.project);

            userInfo = judgeUser(settingPath);

            Module module = posttingModule(groupId, artifactId, version, userInfo);

            posttingFile(currentPath,module);

            post(module);
        }catch (Exception e){
            throw new MojoExecutionException(e.getMessage());
        }
    }


    /**
     * 判断当前操作路径是模块路径还是父工程路径
     */
    private void judgePath(MavenProject parent) throws MojoExecutionException, MojoFailureException{
        if(parent != null && StringUtils.equalsIgnoreCase(parent.getPackaging(),"pom")){
            throw new MojoExecutionException( "非法的操作路径，请在正确的模块下执行命令！" );
        }
    }

    /**
     * 判断本地有没有配置用户信息
     * @param settingPath maven的setting的路径
     * @return 用户信息
     * @throws Exception 用户信息转换异常
     */
    private  UserInfo judgeUser(String settingPath) throws MojoExecutionException, MojoFailureException {

        if (StringUtils.isAnyBlank(settingPath)) {
            throw new MojoExecutionException( "无法正确读取maven配置信息！" );
        }
        UserInfo userInfo = getUserInfo(settingPath);
//        String userXmlPath =  settingPath.replace("settings","user");
//        File file = new File(userXmlPath);
//        UserInfo userInfo =  new UserInfo();
//        if(!file.exists()){
//            String userName  ;
//            String password  ;
//            String tenantId  ;
//            try {
//                do{
//                  userName  = prompt.prompt("请输入模块市场注册用户名：");
//                }while (StringUtils.isAnyBlank(userName ));
//                do{
//                    password  = prompt.prompt("请输入模块市场注册用户密码：");
//                } while (StringUtils.isAnyBlank(password));
//                do{
//                    tenantId  = prompt.prompt("请输入模块市场注册用户所属租户：");
//                }while (StringUtils.isAnyBlank(tenantId));
//
//                userInfo.setUserName(userName);
//                userInfo.setPassword(password);
//                userInfo.setTenantId(tenantId);
//
//                //生成xml信息
//                String xml = MetaConvert.toXml(userInfo);
//                FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8);
//                fileWriter.write(xml);
//            }  catch (Exception e) {
//               throw  new MojoExecutionException("校验信息时错误，程序执行中断！");
//            }
//        }else{
//            FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
//            byte[] bytes = reader.readBytes();
//             userInfo = MetaConvert.parseXml(bytes);
//        }
        return userInfo;
    }


    /**
     *  组装模块信息
     * @param groupId 组织标识
     * @param artifactId 模块名称
     * @param version 本本号
     * @param userInfo 用户信息
     * @return 模块象象
     */
    private Module posttingModule(String  groupId ,String artifactId,String version,UserInfo userInfo){
        Module module = new Module();
        module.setGroupId(groupId);
        module.setArtifactId(artifactId);
        module.setVersion(version);
        module.setUserName(userInfo.getUserName());
        module.setPassword(userInfo.getPassword());
        module.setTenantId(userInfo.getTenantId());
        return module;

    }


    /**
     * 封装源码文件
     * @param currentPath 当前目录路径
     * @param module 模块信息
     */
    private   void  posttingFile(String currentPath,Module module) throws MojoExecutionException {

        List<String> ignoreList = ignoreList(outputDirectory.getParent());
//        List<String> ignoreList = ignoreList("D:\\code\\module\\spaas-maven-plugin");
        List<String> directoryIgnore =  new ArrayList<>();
        List<String> fileIgnore = new ArrayList<>();;
        for( String ignore: ignoreList){
            if(ignore.contains("/")){
                directoryIgnore.add(ignore);
            }else{
                fileIgnore.add(ignore);
            }
        }
        //注意，maven 插件开发不支持java8,所以使用了内部类
        List<File> files = FileUtil.loopFiles(currentPath, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String path = pathname.getPath().replace("\\","/");
                for (String dir : directoryIgnore){
                    if(path.contains(dir)){
                        return false;
                    }
                }
                for(String file :fileIgnore){
                    if(path.endsWith(file.replace("*",""))){
                        return false;
                    }
                }
                return true;
            }
        });

        File file = new File(FileUtil.getTmpDirPath() +File.separator+ module.getArtifactId()+"-"+module.getVersion()+".zip");
        if(file.exists()){
            file.delete();
        }
        File zip = zip( file ,files,module.getArtifactId(),StandardCharsets.UTF_8);
        module.setSource(zip);
        module.setResourceSize(FileUtil.size(zip));
    }


    /**
     * 获取忽略文件列表
     * @param path 路径地址
     * @return 需要忽略的地址集合
     */
    private  List<String>  ignoreList(String path){
        path = path+File.separator+".spaasignore";
        File file = new File(path);
        if(!file.exists()){
            return Lists.newArrayList();
        }
        List<String> ignoreList = FileUtil.readLines(file, "UTF-8", Lists.newArrayList());
        List<String> notEmptyIgnoreList = Lists.newArrayList();
        for (String i : ignoreList) {
            if (!i.startsWith("#") && StringUtils.isNotBlank(i)) {
                notEmptyIgnoreList.add(i);
            }
        }
        getLog().info("ignore list=" + notEmptyIgnoreList);
        return notEmptyIgnoreList;
    }


    /**
     * 远程请求
     * @param module 模块对象
     */
    private void post(Module module) throws MojoExecutionException {
        Map<String, Object> map = BeanUtil.beanToMap(module);
        String url = SpaasCliPropertiesUtils.readMarketUrlProperty(session.getRequest().getUserSettingsFile().getParent())
            + PluginConstants.MARKET_URI_PUSH_PROPERTY_KEY;
        String post = null;
        try {
             post = HttpUtil.post(url, map,5000);
        }catch (Exception e){
            throw  new  MojoExecutionException("网络请求异常！");
        }
        if(JSONUtil.isJson(post)){
            JSONObject object = JSONUtil.parseObj(post);
            Object payload = object.get("code");
            getLog().info("----------------------------[ spaas-plugin ]----------------------------");
            if(payload.toString().equals("0")){
                getLog().info(" -------------------< 模块推送成功！ >-------------------");
            }else{
                getLog().error(" -------------------< 模块推送失败！ >-------------------");
                getLog().error("详细信息：【"+object.get("message")+"】");
            }
        }else{
            getLog().error("-------------------< 非法的返回值 >-------------------");
            getLog().error("返回信息："+post);
        }
    }

    /**
     * 生成压缩文件 ，扩展 hutool工具包
     * @param zip 压缩文件
     * @param files 需要压缩文件集合
     * @param rootDirectory 压缩文件根目录
     * @param charset 压缩编码
     * @return 压缩文件
     */
    private File  zip(File zip ,List<File> files,String rootDirectory,Charset charset) throws MojoExecutionException {
        ZipOutputStream out = null;
        try {
            out = getZipOutputStream(zip, charset);
            for(File file:files) {
                String path = file.getPath().substring(file.getParent().indexOf(rootDirectory));
                addFile(file, path, out);
            }
         }finally {
            closeEntry(out);
        }
        return zip;
    }


    /**
     * 获得 {@link ZipOutputStream}
     *
     * @param zipFile 压缩文件
     * @param charset 编码
     * @return {@link ZipOutputStream}
     */
    private static ZipOutputStream getZipOutputStream(File zipFile, Charset charset) {
        return getZipOutputStream(FileUtil.getOutputStream(zipFile), charset);
    }

    /**
     * 获得 {@link ZipOutputStream}
     *
     * @param out  压缩文件输出流
     * @param charset 编码
     * @return {@link ZipOutputStream}
     */
    private static ZipOutputStream getZipOutputStream(OutputStream out, Charset charset) {
        charset = (null == charset) ? DEFAULT_CHARSET : charset;
        return new ZipOutputStream(out, charset);
    }

    /**
     * 添加文件到压缩包
     *
     * @param file 需要压缩的文件
     * @param path 在压缩文件中的路径
     * @param out 压缩文件存储对象
     * @throws UtilException IO异常
     */
    private static void addFile(File file, String path, ZipOutputStream out) throws MojoExecutionException {
        BufferedInputStream in = null;
        try {
            in = FileUtil.getInputStream(file);
            addFile(in, path, out);
        } finally {
            IoUtil.close(in);
        }
    }

    /**
     * 添加文件流到压缩包，不关闭输入流
     *
     * @param in 需要压缩的输入流
     * @param path 压缩的路径
     * @param out 压缩文件存储对象
     * @throws UtilException IO异常
     */
    private static void addFile(InputStream in, String path, ZipOutputStream out) throws MojoExecutionException {
        if (null == in) {
            return;
        }
        try {
            out.putNextEntry(new ZipEntry(path));
            long copy = IoUtil.copy(in, out);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }


    /**
     * 关闭当前Entry，继续下一个Entry
     *
     * @param out ZipOutputStream
     */
    private static void closeEntry(ZipOutputStream out) {
        try {
            if(out != null ){
                out.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }


}
