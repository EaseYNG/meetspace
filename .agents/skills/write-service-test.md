# write-service-test

为 MeetSpace 项目的 Service 实现类编写完整的单元测试。

## 触发条件

当用户说"写单元测试"、"补测试"、"XXService 的测试"时使用此 Skill。

## 测试文件模板

```java
package com.venus.meetspace.service.impl;

import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.convert.XxxConvert;
import com.venus.meetspace.model.dto.XxxCreateCmd;
import com.venus.meetspace.model.dto.XxxUpdateCmd;
import com.venus.meetspace.model.entity.Xxx;
import com.venus.meetspace.model.vo.XxxVO;
import com.venus.meetspace.repository.XxxMapper;
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
class XxxServiceImplTest {

    @Mock
    private XxxMapper xxxMapper;

    @Mock
    private XxxConvert xxxConvert;

    private XxxServiceImpl xxxService;

    private MockedStatic<SecurityUtil> securityUtilMock;

    private static final Long USER_ID = 1L;
    private static final Long RESOURCE_ID = 100L;
    private static final Long OTHER_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        xxxService = new XxxServiceImpl();
        ReflectionTestUtils.setField(xxxService, "baseMapper", xxxMapper);
        ReflectionTestUtils.setField(xxxService, "xxxConvert", xxxConvert);
        securityUtilMock = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilMock.close();
    }

    // ==================== methodName ====================

    @Test
    void methodName_shouldSucceed_whenCondition() {
        // given - 准备测试数据
        // when - 执行被测方法
        // then - 断言结果
    }

    @Test
    void methodName_shouldThrowException_whenCondition() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> xxxService.methodName(args));
        assertEquals(ResultCode.EXPECTED_CODE, ex.getCode());
        assertTrue(ex.getMessage().contains("expected message"));
    }
}
```

## Mock 速查表

| 场景 | Mock 写法 |
|------|----------|
| MyBatis-Plus getById | `when(mapper.selectById(id)).thenReturn(entity)` |
| MyBatis-Plus insert | `when(mapper.insert(any(Xxx.class))).thenReturn(1)` |
| MyBatis-Plus updateById | `when(mapper.updateById(any(Xxx.class))).thenReturn(1)` |
| MyBatis-Plus selectList | `when(mapper.selectList(any())).thenReturn(list)` |
| 自定义 Mapper 方法 | `when(mapper.customMethod(args)).thenReturn(result)` |
| MapStruct toEntity | `when(convert.toEntity(cmd)).thenReturn(entity)` |
| MapStruct toVO | `when(convert.toVO(entity)).thenReturn(vo)` |
| MapStruct toVOList | `when(convert.toVOList(entities)).thenReturn(voList)` |
| SecurityUtil.getCurrentUserId | `securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(userId)` |
| MapStruct update (void) | `doNothing().when(convert).update(entity, cmd)` 或不 mock，用 verify |

## 断言速查表

| 场景 | 断言写法 |
|------|----------|
| 不抛异常 | `assertDoesNotThrow(() -> service.method(args))` |
| 抛 BusinessException | `BusinessException ex = assertThrows(BusinessException.class, () -> ...)` |
| 验证错误码 | `assertEquals(ResultCode.NOT_FOUND, ex.getCode())` |
| 验证消息含关键字 | `assertTrue(ex.getMessage().contains("not found"))` |
| 验证方法被调用 | `verify(mapper).insert(any())` |
| 验证方法未被调用 | `verify(mapper, never()).insert(any())` |
| 验证返回值 | `assertEquals(expectedValue, result)` |
| 验证非空 | `assertNotNull(result)` |
| 验证 entity 状态变更 | `assertEquals(Status.DELETED, entity.getStatus())` |

## 测试覆盖检查清单

对每个 Service 方法，确认覆盖：
- [ ] 正常路径（所有校验通过，操作成功）
- [ ] 资源不存在 → `BusinessException(ResultCode.NOT_FOUND, ...)`
- [ ] 无权限 → `BusinessException(ResultCode.FORBIDDEN, ...)`
- [ ] 参数非法 → `BusinessException(ResultCode.VALUE_ERROR, ...)`
- [ ] 状态不允许 → `BusinessException(ResultCode.STATUS_ERROR, ...)`
- [ ] 冲突场景 → `BusinessException(ResultCode.CONFLICT, ...)`

## 注意事项

- 必须用 `ReflectionTestUtils.setField` 注入 baseMapper，MyBatis-Plus 的 `ServiceImpl` 不会自动注入 Mock
- `MockedStatic` 必须在 `@AfterEach` 中 `close()`，否则会影响其他测试
- 如果 ServiceImpl 有额外的 Mapper（非 baseMapper），也需要注入：`ReflectionTestUtils.setField(service, "xxxMapper", xxxMapper)`
- 测试方法名使用英文，遵循 `{方法名}_should{预期}_when{条件}` 格式
