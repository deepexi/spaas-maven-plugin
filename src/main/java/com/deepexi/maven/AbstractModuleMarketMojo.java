package com.deepexi.maven;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import com.deepexi.maven.entity.UserInfo;
import com.deepexi.maven.tools.MetaConvert;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.codehaus.plexus.components.interactivity.Prompter;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author huangzh
 */
public abstract class AbstractModuleMarketMojo extends AbstractMojo {

    @Component
    private Prompter prompt;

    /**
     * 获取用户信息
     * @param settingPath setting的存放目录
     * @return
     * @throws MojoExecutionException
     */
    protected UserInfo getUserInfo(String settingPath) throws MojoExecutionException {
        UserInfo userInfo =  new UserInfo();
        try {
            File file = new File(settingPath, "user.xml");
            if (!file.exists()) {
                String userName;
                String password;
                String tenantId;
                do {
                    userName = prompt.prompt("请输入模块市场注册用户名");
                } while (StringUtils.isAnyBlank(userName));
                do {
                    password = prompt.promptForPassword("请输入模块市场注册用户密码");
                } while (StringUtils.isAnyBlank(password));
                do {
                    tenantId = prompt.prompt("请输入模块市场注册用户所属租户");
                } while (StringUtils.isAnyBlank(tenantId));
                userInfo.setUserName(userName);
                userInfo.setPassword(password);
                userInfo.setTenantId(tenantId);
                //生成xml信息
                String xml = MetaConvert.toXml(userInfo);
                FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8);
                fileWriter.write(xml);
            } else {
                FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
                byte[] bytes = reader.readBytes();
                userInfo = MetaConvert.parseXml(bytes);
            }
            return userInfo;
        } catch (Exception e) {
            throw new MojoExecutionException("获取用户信息失败");
        }
    }

    /**
     * 创建插件的临时目录
     * @param tmpDirName
     * @return
     */
    protected File createPluginTmpDir(String tmpDirName) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), tmpDirName);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        return tmpDir;
    }

    /**
     * 删除临时目录的文件
     * @param tmpDir
     */
    protected void cleanPluginTmpDir(File tmpDir) {
        FileUtil.del(tmpDir);
    }
}
