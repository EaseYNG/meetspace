package com.venus.meetspace.service.impl;

import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.convert.ActivityConvert;
import com.venus.meetspace.model.dto.ActivityCreateCmd;
import com.venus.meetspace.model.dto.ActivityUpdateCmd;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.enums.ActivityStatus;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.repository.ActivityMapper;
import com.venus.meetspace.security.SecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceImplTest {

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private ActivityConvert activityConvert;

    private ActivityServiceImpl activityService;

    private MockedStatic<SecurityUtil> securityUtilMock;

    private static final Long USER_ID = 1L;
    private static final Long ACTIVITY_ID = 100L;
    private static final Long OTHER_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        activityService = new ActivityServiceImpl();
        ReflectionTestUtils.setField(activityService, "baseMapper", activityMapper);
        ReflectionTestUtils.setField(activityService, "activityConvert", activityConvert);
        ReflectionTestUtils.setField(activityService, "activityMapper", activityMapper);
        securityUtilMock = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilMock.close();
    }

    // ==================== createActivity ====================

    @Test
    void createActivity_shouldSucceed_whenValidCmd() {
        ActivityCreateCmd cmd = new ActivityCreateCmd();
        cmd.setTitle("测试活动");
        cmd.setStartTime(LocalDateTime.now().plusDays(1));
        cmd.setEndTime(LocalDateTime.now().plusDays(2));
        cmd.setSignupDeadline(LocalDateTime.now().plusHours(12));

        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);

        when(activityConvert.toEntity(cmd)).thenReturn(activity);
        when(activityMapper.insert(any(Activity.class))).thenReturn(1);

        Long result = activityService.createActivity(cmd, USER_ID);

        assertEquals(ACTIVITY_ID, result);
        assertEquals(USER_ID, activity.getOwnerId());
        verify(activityMapper).insert(activity);
    }

    @Test
    void createActivity_shouldThrowException_whenStartTimeInPast() {
        ActivityCreateCmd cmd = new ActivityCreateCmd();
        cmd.setTitle("测试活动");
        cmd.setStartTime(LocalDateTime.now().minusDays(1));
        cmd.setEndTime(LocalDateTime.now().plusDays(1));
        cmd.setSignupDeadline(LocalDateTime.now().plusHours(12));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityService.createActivity(cmd, USER_ID));

        assertEquals(ResultCode.VALUE_ERROR, ex.getCode());
        assertTrue(ex.getMessage().contains("Start time must be in the future"));
        verify(activityMapper, never()).insert(any(Activity.class));
    }

    // ==================== updateActivity ====================

    @Test
    void updateActivity_shouldSucceed_whenOwnerUpdates() {
        ActivityUpdateCmd cmd = new ActivityUpdateCmd();
        cmd.setTitle("更新标题");

        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setOwnerId(USER_ID);
        activity.setStatus(ActivityStatus.READY);

        securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

        assertDoesNotThrow(() -> activityService.updateActivity(ACTIVITY_ID, cmd));

        verify(activityConvert).update(activity, cmd);
        verify(activityMapper).updateById(activity);
    }

    @Test
    void updateActivity_shouldThrowException_whenActivityNotFound() {
        ActivityUpdateCmd cmd = new ActivityUpdateCmd();
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityService.updateActivity(ACTIVITY_ID, cmd));

        assertEquals(ResultCode.NOT_FOUND, ex.getCode());
        assertTrue(ex.getMessage().contains("Activity not found"));
    }

    @Test
    void updateActivity_shouldThrowException_whenNotOwner() {
        ActivityUpdateCmd cmd = new ActivityUpdateCmd();

        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setOwnerId(OTHER_USER_ID);
        activity.setStatus(ActivityStatus.READY);

        securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityService.updateActivity(ACTIVITY_ID, cmd));

        assertEquals(ResultCode.FORBIDDEN, ex.getCode());
        assertTrue(ex.getMessage().contains("Only the owner can edit"));
    }

    @Test
    void updateActivity_shouldThrowException_whenActivityDeleted() {
        ActivityUpdateCmd cmd = new ActivityUpdateCmd();

        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setOwnerId(USER_ID);
        activity.setStatus(ActivityStatus.DELETED);

        securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityService.updateActivity(ACTIVITY_ID, cmd));

        assertEquals(ResultCode.STATUS_ERROR, ex.getCode());
        assertTrue(ex.getMessage().contains("Activity cannot be edited"));
    }

    @Test
    void updateActivity_shouldThrowException_whenActivityOver() {
        ActivityUpdateCmd cmd = new ActivityUpdateCmd();

        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setOwnerId(USER_ID);
        activity.setStatus(ActivityStatus.OVER);

        securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityService.updateActivity(ACTIVITY_ID, cmd));

        assertEquals(ResultCode.STATUS_ERROR, ex.getCode());
    }

    // ==================== deleteActivity ====================

    @Test
    void deleteActivity_shouldSucceed_whenOwnerDeletes() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setOwnerId(USER_ID);
        activity.setStatus(ActivityStatus.READY);

        securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

        assertDoesNotThrow(() -> activityService.deleteActivity(ACTIVITY_ID));

        assertEquals(ActivityStatus.DELETED, activity.getStatus());
        verify(activityMapper).updateById(activity);
    }

    @Test
    void deleteActivity_shouldThrowException_whenActivityNotFound() {
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityService.deleteActivity(ACTIVITY_ID));

        assertEquals(ResultCode.NOT_FOUND, ex.getCode());
    }

    @Test
    void deleteActivity_shouldThrowException_whenNotOwner() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setOwnerId(OTHER_USER_ID);
        activity.setStatus(ActivityStatus.READY);

        securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityService.deleteActivity(ACTIVITY_ID));

        assertEquals(ResultCode.FORBIDDEN, ex.getCode());
        assertTrue(ex.getMessage().contains("Only the owner can delete"));
    }

    // ==================== getActivityById ====================

    @Test
    void getActivityById_shouldReturnVO_whenActivityExists() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setTitle("测试活动");

        ActivityVO vo = new ActivityVO();
        vo.setId(ACTIVITY_ID);
        vo.setTitle("测试活动");

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityConvert.toVO(activity)).thenReturn(vo);

        ActivityVO result = activityService.getActivityById(ACTIVITY_ID);

        assertNotNull(result);
        assertEquals(ACTIVITY_ID, result.getId());
    }

    @Test
    void getActivityById_shouldThrowException_whenActivityNotFound() {
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> activityService.getActivityById(ACTIVITY_ID));

        assertEquals(ResultCode.NOT_FOUND, ex.getCode());
    }

    // ==================== getActivitiesByIds ====================

    @Test
    void getActivitiesByIds_shouldReturnVOList() {
        List<Long> ids = List.of(1L, 2L);
        Activity a1 = new Activity();
        a1.setId(1L);
        Activity a2 = new Activity();
        a2.setId(2L);
        List<Activity> activities = List.of(a1, a2);

        ActivityVO vo1 = new ActivityVO();
        vo1.setId(1L);
        ActivityVO vo2 = new ActivityVO();
        vo2.setId(2L);
        List<ActivityVO> vos = List.of(vo1, vo2);

        when(activityMapper.findAllByIds(ids)).thenReturn(activities);
        when(activityConvert.toVOList(activities)).thenReturn(vos);

        List<ActivityVO> result = activityService.getActivitiesByIds(ids);

        assertEquals(2, result.size());
    }

    // ==================== getReadyActivities ====================

    @Test
    void getReadyActivities_shouldReturnReadyActivities() {
        Activity a1 = new Activity();
        a1.setStatus(ActivityStatus.READY);
        List<Activity> activities = List.of(a1);

        ActivityVO vo1 = new ActivityVO();
        vo1.setId(1L);
        List<ActivityVO> vos = List.of(vo1);

        when(activityMapper.selectList(any())).thenReturn(activities);
        when(activityConvert.toVOList(activities)).thenReturn(vos);

        List<ActivityVO> result = activityService.getReadyActivities();

        assertEquals(1, result.size());
    }
}
