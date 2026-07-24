# architecture-rules.md

项目分层架构、包结构、依赖注入和事务管理的编码规范。

## 分层职责

| 层次 | 职责 | 禁止 |
|------|------|------|
| Controller | 参数校验（`@Valid`）、调用 Service、组装返回 `Result` | 禁止包含业务逻辑；禁止直接调用 Mapper |
| Service 接口 | 继承 `IService<Entity>`，定义业务方法签名 | 禁止包含实现代码 |
| Service Impl | 继承 `ServiceImpl<Mapper, Entity>`，实现全部业务逻辑、权限校验、状态检查 | 禁止跨模块直接注入 Controller；禁止返回 Entity 给 Controller |
| Mapper | 继承 `BaseMapper<Entity>`，自定义 SQL 写在 XML 中 | 禁止包含业务逻辑 |
| Convert | MapStruct `@Mapper(componentModel = "spring")`，负责 Entity ↔ DTO ↔ VO 转换 | 禁止包含业务逻辑 |

## 包结构约定

```
com.venus.meetspace
├── controller/        # REST 控制器
├── service/           # 服务接口
│   └── impl/          # 服务实现
├── repository/        # MyBatis Mapper 接口（命名 XxxMapper）
├── model/
│   ├── entity/        # 数据库实体（@TableName 映射）
│   ├── dto/           # 入参命令对象（命名 XxxCmd）
│   ├── vo/            # 出参视图对象（命名 XxxVO）
│   ├── query/         # 查询参数对象（命名 XxxQuery）
│   └── enums/         # 业务枚举（如 ActivityStatus）
├── convert/           # MapStruct 转换器（命名 XxxConvert）
├── common/
│   ├── enums/         # 通用枚举（如 ResultCode）
│   ├── exception/     # 异常定义与全局处理
│   └── result/        # 统一响应体（Result、PageResult）
├── config/            # Spring 配置类
├── security/          # 安全相关（认证、工具类）
└── aspect/            # AOP 切面
```

## 依赖注入

- 统一使用 `@Autowired` 字段注入（与项目现有风格保持一致）
- 日志统一使用 Lombok `@Slf4j`

## 事务管理

- 写操作（create/update/delete）必须加 `@Transactional(rollbackFor = Exception.class)`
- 只读操作不加事务注解
- 涉及多表写操作时确保事务边界覆盖所有写操作

## Service 实现模板

```java
@Service
@Slf4j
public class XxxServiceImpl extends ServiceImpl<XxxMapper, Xxx> implements XxxService {
    @Autowired private XxxConvert xxxConvert;
    @Autowired private XxxMapper xxxMapper;  // 如需自定义方法

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void someWriteMethod(XxxCmd cmd) {
        // 1. 参数校验
        // 2. 业务逻辑 + 权限检查
        // 3. 持久化
        // 4. 日志
        log.info("Xxx created: id={}", resultId);
    }

    @Override
    public XxxVO someReadMethod(Long id) {
        // 只读方法，不加 @Transactional
    }
}
```
