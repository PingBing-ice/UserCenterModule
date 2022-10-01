package com.user.oss.controller;

import com.user.model.domain.User;
import com.user.oss.service.OssService;
import com.user.oss.util.ResponseEmail;
import com.user.util.common.B;
import com.user.util.utils.UserUtils;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/file/upload/team/{teamID}")
    public B<String> upFileByTeam(MultipartFile file, HttpServletRequest request, @PathVariable String teamID) {
        User loginUser = UserUtils.getLoginUser(request);
        String url = ossService.upFileByTeam(file,loginUser,teamID);
        return B.ok(url);
    }

    /**
     * 注册邮箱验证
     * @param email
     * @return
     */
    @PostMapping("/send")
    public B<Boolean> sendEMail(@RequestBody ResponseEmail email,HttpServletRequest request) {
        boolean is = ossService.sendEMail(email,request);
        return B.ok(is);
    }

    /**
     * 忘记密码邮箱验证
     * @param email
     * @return
     */
    @PostMapping("/sendForget")
    public B<Boolean> sendForgetEMail(@RequestBody ResponseEmail email,HttpServletRequest request) {
        boolean is = ossService.sendForgetEMail(email,request);
        return B.ok(is);
    }

    /**
     * 发送绑定邮件的验证码
     * @param email 邮件
     * @param request
     * @return
     */
    @PostMapping("/sendBinDing")
    public B<Boolean> sendBinDingEMail(@RequestBody ResponseEmail email,HttpServletRequest request) {
        boolean is = ossService.sendBinDingEMail(email,request);
        return B.ok(is);
    }
}
