package com.venus.meetspace.service;

import com.venus.meetspace.model.cmd.ProfileUpdateCmd;
import com.venus.meetspace.model.vo.UserHomeVO;
import com.venus.meetspace.model.vo.UserProfileVO;

public interface UserService {

    UserProfileVO getProfile(Long userId);

    void updateProfile(Long userId, ProfileUpdateCmd cmd);

    UserHomeVO getHome(Long userId);
}
