package com.lily.lilyojmodel.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
* @author lily <a href="https://github.com/lilyWhenVia">come to find lily</a>
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -3287048942713342858L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
