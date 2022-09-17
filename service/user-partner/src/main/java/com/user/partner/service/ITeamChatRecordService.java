package com.user.partner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.domain.TeamChatRecord;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 队伍聊天记录表 服务类
 * </p>
 *
 * @author ice
 * @since 2022-09-12
 */
public interface ITeamChatRecordService extends IService<TeamChatRecord> {

    List<TeamChatRecord> getTeamChatRecordByTeamId(HttpServletRequest request, String teamId);
}
