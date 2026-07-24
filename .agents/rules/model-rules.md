# model-rules.md

DTO / VO / Entity / Query 对象的命名约定与注解规范。

## 类型速查

| 类型 | 后缀 | 包路径 | 职责 | 必需注解 |
|------|------|--------|------|---------|
| 入参命令 | `Cmd` | `model.dto` | 接收前端请求体，含校验规则 | `@Data` + Jakarta Validation |
| 出参视图 | `VO` | `model.vo` | 返回给前端的展示数据，可含非数据库字段 | `@Data` |
| 查询参数 | `Query` | `model.query` | 接收搜索/筛选参数 | `@Data` |
| 数据库实体 | 无后缀 | `model.entity` | 映射数据表，与数据库列一一对应 | `@Data` + `@TableName` + `@TableId` + `@TableField` |
| 业务枚举 | `XxxStatus` | `model.enums` | 业务状态枚举，用 Integer code 持久化 | `@Getter` + 自定义 `map` 方法 |

## Entity 规范

```java
@Data
@TableName("activity")  // 蛇形命名
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("start_time")  // 数据库蛇形 ↔ Java 驼峰
    private LocalDateTime startTime;

    private ActivityStatus status;  // 枚举类型用自定义 TypeHandler 或 Convert 转换
}
```

## Cmd 规范

```java
@Data
public class ActivityCreateCmd {
    @NotBlank(message = "活动标题不能为空")       // 校验信息用中文
    private String title;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须在未来")
    private LocalDateTime startTime;

    // 可选字段不加校验注解
    private String description;
}
```

## VO 规范

```java
@Data
public class ActivityVO {
    private Long id;
    private String title;
    private String description;

    // 可包含非数据库字段（前端展示需要）
    private Boolean isParticipant;   // 当前用户是否已报名
    private String ownerName;        // 创建者昵称（关联查询）
}
```

## Query 规范

```java
@Data
public class ActivitySearchQuery {
    private String keyword;       // 搜索关键词
    private Integer status;       // 按状态筛选
    private String sortBy;        // 排序字段
}
```

## MapStruct Convert 规范

```java
@Mapper(componentModel = "spring")
public interface XxxConvert {
    Xxx toEntity(XxxCreateCmd cmd);

    XxxVO toVO(Xxx entity);

    List<XxxVO> toVOList(List<Xxx> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Xxx entity, XxxUpdateCmd cmd);  // null 值不覆盖

    // 枚举 ↔ Integer 自定义映射
    default Integer map(ActivityStatus status) {
        return status == null ? null : status.getCode();
    }

    default ActivityStatus map(Integer code) {
        if (code == null) return null;
        for (ActivityStatus s : ActivityStatus.values()) {
            if (s.getCode() == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
```
