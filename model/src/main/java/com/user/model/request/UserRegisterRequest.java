package com.user.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ice
 * @date 2022/6/14 16:35
 */

@Data
public class UserRegisterRequest implements Serializable {
    // userAccount, password, checkPassword
    private String userAccount;
    private String password;
    private String checkPassword;
    private String planetCode;
}
