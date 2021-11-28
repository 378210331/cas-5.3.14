package com.cas.mobile;

import lombok.Data;
import org.apereo.cas.authentication.AbstractCredential;

@Data
public class MobileCredential extends AbstractCredential {

    public MobileCredential(String phoneNumber, String validateCode){
        super();
        this.phoneNumber = phoneNumber;
        this.validateCode = validateCode;
    }

    private String phoneNumber;
    private String validateCode;


    @Override
    public String getId() {
        return phoneNumber;
    }
}
