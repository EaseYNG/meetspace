package com.venus.meetspace.service.impl;

import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.convert.ActivityConvert;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.entity.ActivityParticipant;
import com.venus.meetspace.model.enums.ActivityStatus;
import com.venus.meetspace.model.vo.ActivityVO;
import com.venus.meetspace.repository.ActivityMapper;
import com.venus.meetspace.repository.ActivityParticipantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityParticipantServiceImplTest {

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private ActivityParticipantMapper activityParticipantMapper;

    @Mock
    private ActivityConvert activityConvert;

    private ActivityParticipantServiceImpl participantService;

    private static final Long USER_ID = 1L;
    private static final Long ACTIVITY_ID = 100L;

    @BeforeEach
    void setUp() {
        participantService = new ActivityParticipantServiceImpl();
        ReflectionTestUtils.setField(participantService, "baseMapper", activityParticipantMapper);
        ReflectionTestUtils.setField(participantService, "activityMapper", activityMapper);
        ReflectionTestUtils.setField(participantService, "activityParticipantMapper", activityParticipantMapper);
        ReflectionTestUtils.setField(participantService, "activityConvert", activityConvert);
    }

    // ==================== participate ====================

    @Test
    void participate_shouldSucceed_whenActivityReady() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.READY);
        activity.setSignupDeadline(LocalDateTime.now().plusDays(1));
        activity.setMaxParticipants(null);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityParticipantMapper.findBy2Ids(ACTIVITY_ID, USER_ID)).thenReturn(null);
        when(activityParticipantMapper.insert(any(ActivityParticipant.class))).thenReturn(1);

        assertDoesNotThrow(() -> participantService.participate(ACTIVITY_ID, USER_ID));

        verify(activityParticipantMapper).insert(any(ActivityParticipant.class));
    }

    @Test
    void participate_shouldThrowException_whenActivityNotFound() {
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> participantService.participate(ACTIVITY_ID, USER_ID));

        assertEquals(ResultCode.NOT_FOUND, ex.getCode());
        assertTrue(ex.getMessage().contains("Activity not found"));
    }

    @Test
    void participate_shouldThrowException_whenActivityNotReady() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.CLOSED);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> participantService.participate(ACTIVITY_ID, USER_ID));

        assertEquals(ResultCode.STATUS_ERROR, ex.getCode());
        assertTrue(ex.getMessage().contains("not open for signup"));
    }

    @Test
    void participate_shouldThrowException_whenDeadlinePassed() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.READY);
        activity.setSignupDeadline(LocalDateTime.now().minusDays(1));

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> participantService.participate(ACTIVITY_ID, USER_ID));

        assertEquals(ResultCode.STATUS_ERROR, ex.getCode());
        assertTrue(ex.getMessage().contains("deadline has passed"));
    }

    @Test
    void participate_shouldThrowException_whenAlreadySignedUp() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.READY);
        activity.setSignupDeadline(LocalDateTime.now().plusDays(1));

        ActivityParticipant existing = new ActivityParticipant();
        existing.setActivityId(ACTIVITY_ID);
        existing.setParticipantId(USER_ID);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityParticipantMapper.findBy2Ids(ACTIVITY_ID, USER_ID)).thenReturn(existing);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> participantService.participate(ACTIVITY_ID, USER_ID));

        assertEquals(ResultCode.NO_SUCH_OBJECT, ex.getCode());
        assertTrue(ex.getMessage().contains("Already signed up"));
    }

    @Test
    void participate_shouldThrowException_whenMaxParticipantsReached() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.READY);
        activity.setSignupDeadline(LocalDateTime.now().plusDays(1));
        activity.setMaxParticipants(5);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityParticipantMapper.findBy2Ids(ACTIVITY_ID, USER_ID)).thenReturn(null);
        when(activityParticipantMapper.selectCount(any())).thenReturn(5L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> participantService.participate(ACTIVITY_ID, USER_ID));

        assertEquals(ResultCode.STATUS_ERROR, ex.getCode());
        assertTrue(ex.getMessage().contains("maximum number of participants"));
        verify(activityParticipantMapper, never()).insert(any(ActivityParticipant.class));
    }

    @Test
    void participate_shouldSucceed_whenBelowMaxParticipants() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.READY);
        activity.setSignupDeadline(LocalDateTime.now().plusDays(1));
        activity.setMaxParticipants(5);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityParticipantMapper.findBy2Ids(ACTIVITY_ID, USER_ID)).thenReturn(null);
        when(activityParticipantMapper.selectCount(any())).thenReturn(3L);
        when(activityParticipantMapper.insert(any(ActivityParticipant.class))).thenReturn(1);

        assertDoesNotThrow(() -> participantService.participate(ACTIVITY_ID, USER_ID));

        verify(activityParticipantMapper).insert(any(ActivityParticipant.class));
    }

    // ==================== quit ====================

    @Test
    void quit_shouldSucceed_whenActivityReady() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.READY);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityParticipantMapper.delete(any())).thenReturn(1);

        assertDoesNotThrow(() -> participantService.quit(ACTIVITY_ID, USER_ID));

        verify(activityParticipantMapper).delete(any());
    }

    @Test
    void quit_shouldSucceed_whenActivityClosed() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.CLOSED);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);
        when(activityParticipantMapper.delete(any())).thenReturn(1);

        assertDoesNotThrow(() -> participantService.quit(ACTIVITY_ID, USER_ID));
    }

    @Test
    void quit_shouldThrowException_whenActivityNotFound() {
        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> participantService.quit(ACTIVITY_ID, USER_ID));

        assertEquals(ResultCode.NOT_FOUND, ex.getCode());
    }

    @Test
    void quit_shouldThrowException_whenActivityOver() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.OVER);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> participantService.quit(ACTIVITY_ID, USER_ID));

        assertEquals(ResultCode.STATUS_ERROR, ex.getCode());
        assertTrue(ex.getMessage().contains("has ended"));
    }

    @Test
    void quit_shouldThrowException_whenActivityDeleted() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setStatus(ActivityStatus.DELETED);

        when(activityMapper.selectById(ACTIVITY_ID)).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> participantService.quit(ACTIVITY_ID, USER_ID));

        assertEquals(ResultCode.STATUS_ERROR, ex.getCode());
    }

    // ==================== isParticipant ====================

    @Test
    void isParticipant_shouldReturnTrue_whenParticipantExists() {
        when(activityParticipantMapper.selectCount(any())).thenReturn(1L);

        boolean result = participantService.isParticipant(ACTIVITY_ID, USER_ID);

        assertTrue(result);
    }

    @Test
    void isParticipant_shouldReturnFalse_whenParticipantNotExists() {
        when(activityParticipantMapper.selectCount(any())).thenReturn(0L);

        boolean result = participantService.isParticipant(ACTIVITY_ID, USER_ID);

        assertFalse(result);
    }

    // ==================== getParticipatedActivities ====================

    @Test
    void getParticipatedActivities_shouldReturnEmptyList_whenNoRecords() {
        when(activityParticipantMapper.findByParticipantId(USER_ID)).thenReturn(List.of());

        List<ActivityVO> result = participantService.getParticipatedActivities(USER_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getParticipatedActivities_shouldReturnVOList_whenRecordsExist() {
        ActivityParticipant ap = new ActivityParticipant();
        ap.setActivityId(ACTIVITY_ID);
        ap.setParticipantId(USER_ID);

        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);

        ActivityVO vo = new ActivityVO();
        vo.setId(ACTIVITY_ID);

        when(activityParticipantMapper.findByParticipantId(USER_ID)).thenReturn(List.of(ap));
        when(activityMapper.findAllByIds(List.of(ACTIVITY_ID))).thenReturn(List.of(activity));
        when(activityConvert.toVOList(List.of(activity))).thenReturn(List.of(vo));

        List<ActivityVO> result = participantService.getParticipatedActivities(USER_ID);

        assertEquals(1, result.size());
        assertEquals(ACTIVITY_ID, result.get(0).getId());
    }

    // ==================== getSignedUpActivities ====================

    @Test
    void getSignedUpActivities_shouldReturnVOList() {
        ActivityParticipant ap = new ActivityParticipant();
        ap.setActivityId(ACTIVITY_ID);

        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);

        ActivityVO vo = new ActivityVO();
        vo.setId(ACTIVITY_ID);

        when(activityParticipantMapper.findByParticipantId(USER_ID)).thenReturn(List.of(ap));
        when(activityMapper.findAllByIds(List.of(ACTIVITY_ID))).thenReturn(List.of(activity));
        when(activityConvert.toVOList(List.of(activity))).thenReturn(List.of(vo));

        List<ActivityVO> result = participantService.getSignedUpActivities(USER_ID);

        assertEquals(1, result.size());
    }

    // ==================== getCreatedActivities ====================

    @Test
    void getCreatedActivities_shouldReturnVOList() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);

        ActivityVO vo = new ActivityVO();
        vo.setId(ACTIVITY_ID);

        when(activityMapper.findByOwnerId(USER_ID)).thenReturn(List.of(activity));
        when(activityConvert.toVOList(List.of(activity))).thenReturn(List.of(vo));

        List<ActivityVO> result = participantService.getCreatedActivities(USER_ID);

        assertEquals(1, result.size());
    }
}
