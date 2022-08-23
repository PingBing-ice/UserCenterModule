package com.user.usercenter.openfeign;

import com.user.model.domain.User;
import com.user.usercenter.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ice
 * @date 2022/8/22 9:06
 */
@RestController
@RequestMapping("user/feign")
public class UserOpenFeignController {
    @Autowired
    private IUserService userService;
    //  List<User> users = userService.listByIds(list);
    @PostMapping("/lists")
    public List<User> getListByIds(@RequestBody List<String> ids) {
        return userService.listByIds(ids);
    }
}
