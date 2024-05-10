package com.lily.lilyojserviceclient.service;


import com.lily.lilyojcommon.common.ErrorCode;
import com.lily.lilyojcommon.exception.BusinessException;
import com.lily.lilyojmodel.model.entity.User;
import com.lily.lilyojmodel.model.enums.UserRoleEnum;
import com.lily.lilyojmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static com.lily.lilyojcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 *
* @author lily <a href="https://github.com/lilyWhenVia">come to find lily</a>
 */
@FeignClient(name = "lilyOj-user-service", path = "/api/user/inner")
public interface UserFeignClient{


    @GetMapping("/getById")
    User getById(@RequestParam("userId") Long userId);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
//    @GetMapping("/getLoginUser")
    default User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 可以考虑在这里做全局权限校验
        return currentUser;
    }


    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return 是否为管理员
     */
//    @GetMapping("/isAdmin")
    default boolean isAdmin(HttpServletRequest request){
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return 是否为管理员
     */
//    @PostMapping("/isAdmin")
    default boolean isAdmin(@RequestBody User user){
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return 脱敏的用户信息
     */
//    @PostMapping("/getUserVO")
    default UserVO getUserVO(@RequestBody User user){
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

//    /**
//     * 获取查询条件
//     *
//     * @param userQueryRequest 用户查询请求
//     * @return 查询条件
//     */
//    @PostMapping("/getQueryWrapper")
//    QueryWrapper<User> getQueryWrapper(@RequestBody UserQueryRequest userQueryRequest);

}
