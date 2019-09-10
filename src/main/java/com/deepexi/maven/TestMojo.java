package com.deepexi.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * 测试插件是否可用
 * eg: mvn spaas:test
 * @author huangzh
 */
@Mojo(name = "test", requiresProject = false)
public class TestMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("========[欢迎使用滴普插件]========");
    }
}
