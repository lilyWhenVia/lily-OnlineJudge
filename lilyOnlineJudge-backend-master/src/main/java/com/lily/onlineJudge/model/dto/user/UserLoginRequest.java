package com.lily.onlineJudge.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户登录请求
 *
* @author lily <a href="https://github.com/lilyWhenVia">come to find lily</a>
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -2406802201789556918L;

    private String userAccount;

    private String userPassword;
}
