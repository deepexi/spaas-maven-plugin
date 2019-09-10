package com.deepexi.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * 工具帮助命令
 * eg: mvn spaas:help
 * @author huangzh
 */
@Mojo(name = "help", requiresProject = false)
public class HelpMojo extends AbstractMojo {
    /**
     * 1. 创建工程或模块命令
     *    mvn spaas:init
     *    eg: 生成工程spaas-abcd-center版本号为1.0.0
     *
     * 2. 拉取模块命令
     *    mvn spaas:pull
     *    eg: 拉取模块spaas-abcd-center-action版本号为1.0.0
     *    mvn spaas:pull -Dname=spaas-abcd-center-action@1.0.0
     * 3. 推送模块命令
     *    mvn spaas:push
     *    eg: 推送模块spaas-abcd-center-action
     *    cd进入spaas-abcd-center-action目录运行mvn spaas:push
     * 4. 模块查询命令
     *    mvn spaas:search
     *    eg: 模糊查询模块信息
     *    mvn spaas:search -Dname=abcd
     * 5. 创建模块命令5. 创建模块命令
     *    mvn spaas:add
     *    eg: 增加模块名为name=spaas-abcd-center-test
     *    mvn spaas:add -Dname=spaas-abcd-center-test
     * 6. 帮助命令
     *    mvn spaas:help
     *
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        getLog().info("1. 创建工程或模块命令");
        getLog().info("   mvn spaas:init");
        getLog().info("   eg: 生成工程spaas-abcd-center版本号为1.0.0");
        getLog().info("   mvn spaas:init -Dname=spaas-abcd-center@1.0.0");
        getLog().info("");
        getLog().info("2. 拉取模块命令");
        getLog().info("   mvn spaas:pull");
        getLog().info("   eg: 拉取模块spaas-abcd-center-action版本号为1.0.0");
        getLog().info("   mvn spaas:pull -Dname=spaas-abcd-center-action@1.0.0");
        getLog().info("");
        getLog().info("3. 推送模块命令");
        getLog().info("   mvn spaas:push");
        getLog().info("   eg: 推送模块spaas-abcd-center-action");
        getLog().info("   cd进入spaas-abcd-center-action目录运行mvn spaas:push");
        getLog().info("");
        getLog().info("4. 模块查询命令");
        getLog().info("   mvn spaas:search");
        getLog().info("   eg: 模糊查询模块信息");
        getLog().info("   mvn spaas:search -Dname=abcd");
        getLog().info("");
        getLog().info("5. 创建模块命令");
        getLog().info("   mvn spaas:add");
        getLog().info("   eg: 增加模块名为name=spaas-abcd-center-test");
        getLog().info("   mvn spaas:add -Dname=spaas-abcd-center-test");
        getLog().info("");
        getLog().info("6. 帮助命令");
        getLog().info("   mvn spaas:help");
        getLog().info("");
        getLog().info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        getLog().info("");
        getLog().info("更多详细信息可查询:https://www.yuque.com/spaas/ks5iqv/vi8xha");
        getLog().info("");
        getLog().info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
