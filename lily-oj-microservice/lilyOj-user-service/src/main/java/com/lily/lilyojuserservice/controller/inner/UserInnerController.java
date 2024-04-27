package com.lily.lilyojuserservice.controller.inner;

import com.lily.lilyojmodel.model.entity.User;
import com.lily.lilyojserviceclient.service.UserFeignClient;
import com.lily.lilyojuserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by lily via on 2024/4/26 10:34
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    @Override
    @GetMapping("/getById")
    public User getById(Long userId) {
        return userService.getById(userId);
    }

    @Override
    @GetMapping("/getLoginUser")
    public User getLoginUser(HttpServletRequest request) {
        return userService.getLoginUser(request);
    }
}
