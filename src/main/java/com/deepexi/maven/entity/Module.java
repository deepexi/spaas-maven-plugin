package com.deepexi.maven.entity;

import java.io.File;

/**
 *
 * @title: Module
 * @package com.deepexi.maven.entity
 * @description:
 * @author chenling
 * @date 2019/8/23 13:58
 * @since V1.0.0
 */
public class Module {

    private String userName;

    private String password;

    private String tenantId;

    private String artifactId;

    private String groupId;

    private String version;

    private File source;

    private Long resourceSize;

    public Module() {
    }

    public Module(String artifactId, String groupId, String version) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }


    public Long getResourceSize() {
        return resourceSize;
    }

    public void setResourceSize(Long resourceSize) {
        this.resourceSize = resourceSize;
    }
}
