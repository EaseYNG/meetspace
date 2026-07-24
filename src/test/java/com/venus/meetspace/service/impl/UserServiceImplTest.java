package com.venus.meetspace.service.impl;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserConvert userConvert;

    @Mock
    private ActivityService activityService;

    @Mock
    private ActivityParticipantService participantService;

    private UserServiceImpl userService;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
        ReflectionTestUtils.setField(userService, "userConvert", userConvert);
        ReflectionTestUtils.setField(userService, "activityService", activityService);
        ReflectionTestUtils.setField(userService, "participantService", participantService);
    }

    // ==================== getProfile ====================

    @Test
    void getProfile_shouldReturnProfile_whenUserExists() {
        User user = new User();
        user.setId(USER_ID);
        user.setNickname("Test");
        user.setUsername("testuser");

        UserProfileVO vo = new UserProfileVO();
        vo.setId(USER_ID);
        vo.setNickname("Test");

        when(userMapper.selectById(USER_ID)).thenReturn(user);
        when(userConvert.toProfileVO(user)).thenReturn(vo);

        UserProfileVO result = userService.getProfile(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals("Test", result.getNickname());
    }

    @Test
    void getProfile_shouldThrowException_whenUserNotFound() {
        when(userMapper.selectById(USER_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.getProfile(USER_ID));

        assertEquals(ResultCode.NOT_FOUND, ex.getCode());
        assertTrue(ex.getMessage().contains("User not found"));
    }

    // ==================== updateProfile ====================

    @Test
    void updateProfile_shouldSucceed_whenUserExists() {
        ProfileUpdateCmd cmd = new ProfileUpdateCmd();
        cmd.setAge(25);
        cmd.setGender("male");

        User user = new User();
        user.setId(USER_ID);

        when(userMapper.selectById(USER_ID)).thenReturn(user);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.updateProfile(USER_ID, cmd));

        verify(userConvert).updateProfile(user, cmd);
        verify(userMapper).updateById(user);
    }

    @Test
    void updateProfile_shouldThrowException_whenUserNotFound() {
        ProfileUpdateCmd cmd = new ProfileUpdateCmd();

        when(userMapper.selectById(USER_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.updateProfile(USER_ID, cmd));

        assertEquals(ResultCode.NOT_FOUND, ex.getCode());
    }

    // ==================== getHome ====================

    @Test
    void getHome_shouldReturnHomeVO_whenUserExists() {
        User user = new User();
        user.setId(USER_ID);
        user.setNickname("Test");

        UserProfileVO profileVO = new UserProfileVO();
        profileVO.setId(USER_ID);
        profileVO.setNickname("Test");

        ActivityVO activityVO = new ActivityVO();
        activityVO.setId(100L);

        when(userMapper.selectById(USER_ID)).thenReturn(user);
        when(userConvert.toProfileVO(user)).thenReturn(profileVO);
        when(activityService.getReadyActivities()).thenReturn(List.of(activityVO));
        when(participantService.getParticipatedActivities(USER_ID)).thenReturn(List.of(activityVO));
        when(participantService.isParticipant(anyLong(), anyLong())).thenReturn(true);

        UserHomeVO result = userService.getHome(USER_ID);

        assertNotNull(result);
        assertNotNull(result.getProfile());
        assertEquals(USER_ID, result.getProfile().getId());
        assertEquals(1, result.getRecommendedActivities().size());
        assertEquals(1, result.getOngoingActivities().size());
        assertTrue(result.getRecommendedActivities().get(0).getIsParticipant());
        assertTrue(result.getOngoingActivities().get(0).getIsParticipant());
    }

    @Test
    void getHome_shouldHandleNullRecommendedActivities() {
        User user = new User();
        user.setId(USER_ID);

        UserProfileVO profileVO = new UserProfileVO();
        profileVO.setId(USER_ID);

        ActivityVO activityVO = new ActivityVO();
        activityVO.setId(100L);

        when(userMapper.selectById(USER_ID)).thenReturn(user);
        when(userConvert.toProfileVO(user)).thenReturn(profileVO);
        when(activityService.getReadyActivities()).thenReturn(null);
        when(participantService.getParticipatedActivities(USER_ID)).thenReturn(List.of(activityVO));

        UserHomeVO result = userService.getHome(USER_ID);

        assertNotNull(result);
        assertNull(result.getRecommendedActivities());
    }
}
