package com.user.model.request;

import lombok.Data;

import java.util.List;

/**
 * @Author ice
 * @Date 2022/10/1 15:17
 * @PackageName:com.user.model.request
 * @ClassName: UserSearchTagAndTxtRequest
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class UserSearchTagAndTxtRequest {
    private List<String> tagNameList;
    private String searchTxt;
}
