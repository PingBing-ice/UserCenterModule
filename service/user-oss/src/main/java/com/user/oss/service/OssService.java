package com.user.oss.service;

import com.user.model.domain.User;
import com.user.oss.util.ResponseEmail;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ice
 * @date 2022/9/17 12:48
 */

public interface OssService {
    String upload(MultipartFile file, User loginUser);

    String upFileByTeam(MultipartFile file, User loginUser, String teamID);

    boolean sendEMail(ResponseEmail email, HttpServletRequest request);
}
