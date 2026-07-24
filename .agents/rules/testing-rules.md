# testing-rules.md

单元测试与集成测试的编写规范。

## 测试框架

- JUnit 5 + Mockito
- Service 测试：`@ExtendWith(MockitoExtension.class)`
- Controller 测试：`@WebMvcTest` + `@MockBean`

## Service 单测模板

```java
@ExtendWith(MockitoExtension.class)
class XxxServiceImplTest {

    @Mock private XxxMapper xxxMapper;
    @Mock private XxxConvert xxxConvert;

    private XxxServiceImpl xxxService;
    private MockedStatic<SecurityUtil> securityUtilMock;

    private static final Long USER_ID = 1L;
    private static final Long RESOURCE_ID = 100L;

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

    @Test
    void methodName_shouldSucceed_whenCondition() {
        // given → when → then
    }

    @Test
    void methodName_shouldThrowException_whenCondition() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> xxxService.methodName(args));
        assertEquals(ResultCode.EXPECTED, ex.getCode());
        assertTrue(ex.getMessage().contains("expected"));
    }
}
```

## 测试命名

```
{方法名}_should{预期行为}_when{条件}

示例：
  createActivity_shouldSucceed_whenValidCmd
  updateActivity_shouldThrowException_whenNotOwner
  getActivityById_shouldThrowException_whenActivityNotFound
```

## 覆盖矩阵

每个 Service 方法至少覆盖：

| 路径 | 断言 |
|------|------|
| 正常路径 | `assertDoesNotThrow` / `assertEquals(expected, result)` |
| 资源不存在 | `BusinessException` → `ResultCode.NOT_FOUND` |
| 无权限 | `BusinessException` → `ResultCode.FORBIDDEN` |
| 参数非法 | `BusinessException` → `ResultCode.VALUE_ERROR` |
| 状态不允许 | `BusinessException` → `ResultCode.STATUS_ERROR` |
| 冲突场景 | `BusinessException` → `ResultCode.CONFLICT` |

## 关键注意事项

- ⚠️ **必须用 `ReflectionTestUtils.setField` 注入 baseMapper**，MyBatis-Plus `ServiceImpl` 不会自动注入 Mock
- ⚠️ **`MockedStatic` 必须在 `@AfterEach` 中 `close()`**，否则影响其他测试
- 如果 ServiceImpl 有额外的 Mapper（非 baseMapper 的自定义方法），也需注入：`ReflectionTestUtils.setField(service, "xxxMapper", xxxMapper)`
- `@Mock` 的 Convert 默认返回 null，需要显式 `when().thenReturn()`；`void` 方法（如 `update`）可用 `verify()` 验证或 `doNothing()` mock
