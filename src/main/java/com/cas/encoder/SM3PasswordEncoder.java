package com.cas.encoder;

import com.cas.utils.SM3Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;


public class SM3PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return  SM3Util.SM3Encode((String) rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return StringUtils.equals(SM3Util.SM3Encode((String) rawPassword),encodedPassword);
    }
}
