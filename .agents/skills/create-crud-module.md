# create-crud-module

创建 MeetSpace 项目中一个新的 CRUD 模块的完整工作流。

## 触发条件

当用户说"创建 XX 模块"、"添加 XX 功能"、"实现 XX 的增删改查"时使用此 Skill。

## 工作流步骤

### 1. 确认需求

与用户确认：
- 模块名称（如 "Comment"）
- 核心字段列表
- 是否需要权限控制（owner 校验）
- 是否需要状态机
- 前端需要的 VO 字段

### 2. 创建文件清单

按以下顺序创建文件，每创建一个文件后立即验证编译：

```
1. model/entity/Xxx.java              # 数据库实体
2. model/enums/XxxStatus.java         # 业务枚举（如需）
3. model/dto/XxxCreateCmd.java        # 创建入参
4. model/dto/XxxUpdateCmd.java        # 更新入参
5. model/query/XxxSearchQuery.java    # 查询参数（如需）
6. model/vo/XxxVO.java                # 出参视图
7. repository/XxxMapper.java          # MyBatis Mapper 接口
8. resources/repository/XxxMapper.xml # Mapper XML（如需自定义SQL）
9. convert/XxxConvert.java            # MapStruct 转换器
10. service/XxxService.java           # Service 接口
11. service/impl/XxxServiceImpl.java  # Service 实现
12. controller/XxxController.java     # REST 控制器
13. test/.../XxxServiceImplTest.java  # Service 单元测试
```

### 3. 每层规范速查

**Entity**:
```java
@Data
@TableName("xxx")
public class Xxx {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 其他字段用 @TableField 映射下划线命名
}
```

**Service 接口**:
```java
public interface XxxService extends IService<Xxx> {
    // 业务方法
}
```

**Service Impl**:
```java
@Service
@Slf4j
public class XxxServiceImpl extends ServiceImpl<XxxMapper, Xxx> implements XxxService {
    @Autowired private XxxConvert xxxConvert;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void someMethod(XxxCmd cmd) {
        // 1. 参数校验
        // 2. 业务逻辑
        // 3. 持久化
        // 4. 日志
    }
}
```

**Controller**:
```java
@RestController
@RequestMapping("${meetspace.api.version}/xxx")
@Slf4j
public class XxxController {
    @Autowired private XxxService xxxService;
    
    @PostMapping("/create")
    public Result<Void> create(@Valid @RequestBody XxxCreateCmd cmd) {
        xxxService.create(cmd);
        return Result.success(null, "Xxx created");
    }
}
```

**Convert**:
```java
@Mapper(componentModel = "spring")
public interface XxxConvert {
    Xxx toEntity(XxxCreateCmd cmd);
    XxxVO toVO(Xxx entity);
    List<XxxVO> toVOList(List<Xxx> entities);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Xxx entity, XxxUpdateCmd cmd);
}
```

### 4. 测试覆盖清单

每个 Service 方法至少覆盖：
- [ ] 正常路径（happy path）
- [ ] 资源不存在 → `NOT_FOUND`
- [ ] 无权限 → `FORBIDDEN`
- [ ] 状态不允许 → `STATUS_ERROR`

### 5. 更新文档

- 在 `API-endpoints.md` 追加新端点
- 在 `CHANGELOG.md` 记录变更

## 注意事项

- 写操作必须加 `@Transactional(rollbackFor = Exception.class)`
- Controller 不包含业务逻辑，只做参数校验和结果组装
- 异常统一抛 `BusinessException(ResultCode, message)`
- 参数校验用 Jakarta Validation 注解，校验信息用中文
