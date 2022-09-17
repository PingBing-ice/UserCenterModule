package com.user.oss.service;

import com.user.model.domain.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ice
 * @date 2022/9/17 12:48
 */

public interface OssService {
    String upload(MultipartFile file, User loginUser);
}
