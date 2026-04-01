package com.venus.meetspace.service;

import com.venus.meetspace.dto.response.ActivityResponse;

import java.util.List;

public interface ActivityParticipantService {
    /**
     * 报名活动
     * 应检查活动状态为READY，报名最大人数
     * 应检查是否报名过该活动
     * @author Void
     * @param activityId 活动ID
     * @param userId 用户ID
     */
    void signup(Long activityId, Long userId);

    /**
     * 退出已报名的活动
     * 应检查活动状态为READY/CLOSED
     * 应检查是否报名过该活动
     * @author Void
     * @param activityId 活动ID
     * @param userId 用户ID
     */
    void quit(Long activityId, Long userId);

    /**
     * 获取当前用户已参加的所有活动
     * @param participantId 当前的用户ID
     * @return 活动VO列表
     */
    List<ActivityResponse> getParticipatedActivities(Long participantId);
}
