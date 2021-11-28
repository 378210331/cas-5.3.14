package com.cas.password;

import lombok.Data;
import org.apereo.cas.authentication.AbstractCredential;

@Data
public class PasswordCredential extends AbstractCredential {

    public PasswordCredential(String username, String password,String captcha){
        super();
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String captcha;


    @Override
    public String getId() {
        return username;
    }
}
