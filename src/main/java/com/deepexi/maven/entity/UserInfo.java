package com.deepexi.maven.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 用户信息
 * @author chenling
 * @since V1.0.0
 */
@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInfo {

    private String userName;

    private String password;

    private String tenantId;

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
}
