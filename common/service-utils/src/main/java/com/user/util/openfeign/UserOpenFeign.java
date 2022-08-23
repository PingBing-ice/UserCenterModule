package com.user.util.openfeign;

import com.user.model.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author ice
 * @date 2022/8/22 9:06
 */
@FeignClient("user-center")
public interface UserOpenFeign {
    @PostMapping("/api/user/feign/lists")
    List<User> getListByIds(@RequestBody List<String> ids);
}
