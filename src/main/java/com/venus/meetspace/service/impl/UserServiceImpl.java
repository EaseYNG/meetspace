package com.venus.meetspace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.convert.UserConvert;
import com.venus.meetspace.model.dto.ProfileUpdateCmd;
import com.venus.meetspace.model.entity.User;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.model.vo.UserHomeVO;
import com.venus.meetspace.model.vo.UserProfileVO;
import com.venus.meetspace.repository.UserMapper;
import com.venus.meetspace.service.ActivityParticipantService;
import com.venus.meetspace.service.ActivityService;
import com.venus.meetspace.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserConvert userConvert;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityParticipantService participantService;

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "User not found");
        }
        return userConvert.toProfileVO(user);
    }

    @Override
    public void updateProfile(Long userId, ProfileUpdateCmd cmd) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "User not found");
        }
        userConvert.updateProfile(user, cmd);
        this.updateById(user);
        log.info("Profile updated: userId={}", userId);
    }

    @Override
    public UserHomeVO getHome(Long userId) {
        UserProfileVO profile = getProfile(userId);
        List<ActivityVO> recommended = activityService.getReadyActivities();
        List<ActivityVO> ongoing = participantService.getParticipatedActivities(userId);

        if (recommended != null) {
            recommended.forEach(vo -> vo.setIsParticipant(participantService.isParticipant(vo.getId(), userId)));
        }
        if (ongoing != null) {
            ongoing.forEach(vo -> vo.setIsParticipant(true));
        }

        UserHomeVO home = new UserHomeVO();
        home.setProfile(profile);
        home.setRecommendedActivities(recommended);
        home.setOngoingActivities(ongoing);
        return home;
    }
}
