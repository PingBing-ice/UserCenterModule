package com.user.oss.controller;

import com.user.model.domain.User;
import com.user.oss.service.OssService;
import com.user.util.common.B;
import com.user.util.utils.UserUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ice
 * @date 2022/9/17 12:46
 */
@RestController
@RequestMapping("/oss")
public class OssController {

    @Resource
    private OssService ossService;


    @PostMapping("/file/upload")
    public B<String> upFile(MultipartFile file, HttpServletRequest request) {
        User loginUser = UserUtils.getLoginUser(request);
        String url = ossService.upload(file,loginUser);
        return B.ok(url);
    }
}
