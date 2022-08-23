package com.user.partner.controller;




import com.user.model.domain.ChatRecord;
import com.user.partner.service.IChatRecordService;
import com.user.util.common.B;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 聊天记录表 前端控制器
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
@RestController
//@CrossOrigin(origins = {"http://localhost:7777"}, allowCredentials = "true")
@RequestMapping("/record")
public class ChatRecordController {

    @Autowired
    private IChatRecordService recordService;

    // 查询所有的聊天记录
    @GetMapping("/getList")
    public B<List<ChatRecord>> getAllRecordList(@RequestParam(required = false) String userId, String friendId) {
        List<ChatRecord> list =  recordService.selectAllList(userId, friendId);
        return B.ok(list);
    }
}
