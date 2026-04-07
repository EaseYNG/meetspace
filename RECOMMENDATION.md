# MeetSpace 活动推荐功能实现建议

## 一、背景与目标

当前后端已有用户、活动、活动参与三张核心表，具备实现推荐功能的数据基础。推荐目标：**向用户展示他们感兴趣但还未参加的活动**，提升活动曝光率与用户参与度。

---

## 二、推荐策略选型

根据项目现状（数据量小、无评分系统），建议分阶段实施：

### 阶段一：规则推荐（立即可实现，无需改动 DB）

这是最快能落地的方案，纯后端逻辑实现。

```
推荐逻辑 = 排除用户已参加的活动 + 按综合得分排序
```

**综合得分计算规则：**

| 因素 | 权重 | 说明 |
|---|---|---|
| 活动状态为 READY | +100 | 报名中的优先展示 |
| 报名截止时间越近 | +50~0 | 截止前3天内 +50，越靠前越高 |
| 参与人数越多 | +0~30 | 人气热度，体现流行度 |
| 创建时间越新 | +0~20 | 新发布的活动加权 |

**后端实现思路：**

```java
// ActivityRepository 新增方法
@Query("SELECT a FROM Activity a WHERE a.status = 'READY' " +
       "AND a.id NOT IN (" +
       "  SELECT ap.activityId FROM ActivityParticipant ap " +
       "  WHERE ap.participantId = :userId" +
       ") AND a.signupDeadline > :now " +
       "ORDER BY a.signupDeadline ASC")
List<Activity> findRecommendedActivities(
    @Param("userId") Long userId,
    @Param("now") LocalDateTime now
);
```

**新增推荐接口：**

```
GET /activity/recommend
Header: Authorization: Bearer {token}
Response: List<ActivityResponse>
```

在 `ActivityController` 中：

```java
@GetMapping("/recommend")
public Result<List<ActivityResponse>> recommend(@CurrentUserId Long userId) {
    return Result.success(asi.getRecommendedActivities(userId));
}
```

---

### 阶段二：基于用户行为的协同过滤（中期，需积累数据）

**核心思想：**

> "和你参加过相同活动的用户，还参加了哪些你没参加的活动"

**SQL 实现（Item-based 协同过滤简化版）：**

```sql
-- 找出和当前用户（userId=1）有共同活动的其他用户
-- 并推荐那些用户参加了但当前用户没参加的活动
SELECT a.*, COUNT(*) AS score
FROM activity a
JOIN activity_participant ap ON a.id = ap.activity_id
WHERE ap.participant_id IN (
    -- 找出"共同参与"的其他用户
    SELECT DISTINCT ap2.participant_id
    FROM activity_participant ap1
    JOIN activity_participant ap2 ON ap1.activity_id = ap2.activity_id
    WHERE ap1.participant_id = :userId
      AND ap2.participant_id != :userId
)
AND a.id NOT IN (
    -- 排除已参加的活动
    SELECT activity_id FROM activity_participant WHERE participant_id = :userId
)
AND a.status = 'READY'
GROUP BY a.id
ORDER BY score DESC
LIMIT 10;
```

**Java 层封装（JPQL 实现或 NativeQuery）：**

```java
@Query(value = "SELECT a.* FROM activity a " +
    "JOIN activity_participant ap ON a.id = ap.activity_id " +
    "WHERE ap.participant_id IN (" +
    "  SELECT DISTINCT ap2.participant_id " +
    "  FROM activity_participant ap1 " +
    "  JOIN activity_participant ap2 ON ap1.activity_id = ap2.activity_id " +
    "  WHERE ap1.participant_id = :userId AND ap2.participant_id != :userId" +
    ") AND a.id NOT IN (" +
    "  SELECT activity_id FROM activity_participant WHERE participant_id = :userId" +
    ") AND a.status = 'READY' " +
    "GROUP BY a.id ORDER BY COUNT(*) DESC LIMIT :limit",
    nativeQuery = true)
List<Activity> findCollaborativeRecommendations(
    @Param("userId") Long userId,
    @Param("limit") int limit
);
```

---

### 阶段三：基于标签/分类的内容推荐（需扩展 Activity 实体）

**需要的改动：**

1. **Activity 实体**新增字段：
   ```java
   @Column
   private String category; // 如: 运动/技术/文艺/社交
   
   @Column
   private String tags; // JSON 字符串，如: ["户外","徒步","自然"]
   ```

2. **用户偏好表**（可选，新增）：
   ```sql
   CREATE TABLE user_preference (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     user_id BIGINT NOT NULL,
     category VARCHAR(50),       -- 偏好分类
     tag VARCHAR(50),            -- 偏好标签
     weight DOUBLE DEFAULT 1.0,  -- 偏好权重
     created_at DATETIME
   );
   ```

3. **推荐逻辑**：根据用户历史参与活动的分类/标签，计算偏好权重，推荐相同分类/标签的未参加活动。

---

## 三、Redis 缓存加速（生产环境建议）

项目已引入 Redis，推荐结果可以缓存：

```java
// Service 层
@Cacheable(value = "activity:recommend", key = "#userId", 
           cacheManager = "redisCacheManager")
public List<ActivityResponse> getRecommendedActivities(Long userId) {
    // 推荐计算逻辑
}

// 用户参与活动时，清除缓存
@CacheEvict(value = "activity:recommend", key = "#userId")
public void signup(Long activityId, Long userId) {
    // 报名逻辑
}
```

---

## 四、前端集成

在 Flutter 的**首页（HomePage）**或**活动广场（SquarePage）**中展示推荐模块：

```dart
// ApiService 新增
static Future<Map<String, dynamic>> getRecommendedActivities() async {
  final res = await http.get(
    Uri.parse('$baseUrl/activity/recommend'),
    headers: await _authHeaders(),
  );
  return _parseBody(res);
}
```

在 `HomePage` 的 `_buildBanner` 下方增加「为你推荐」区块：

```dart
_buildSectionTitle('为你推荐'),
// 水平滚动卡片列表展示推荐活动
SizedBox(
  height: 150,
  child: ListView.builder(...),
),
```

---

## 五、实现优先级建议

| 优先级 | 方案 | 难度 | 推荐理由 |
|---|---|---|---|
| ⭐⭐⭐ 首选 | 规则推荐（阶段一）| 低 | 立即可实现，覆盖90%推荐需求 |
| ⭐⭐ 中期 | 协同过滤（阶段二）| 中 | 无需改 DB，纯查询即可 |
| ⭐ 长期 | 标签/分类推荐（阶段三）| 高 | 需扩展实体，效果最佳 |

---

## 六、快速上手（阶段一具体步骤）

1. 在 `ActivityRepository` 添加 `findRecommendedActivities` 方法
2. 在 `ActivityService` 接口添加 `getRecommendedActivities(Long userId)`
3. 在 `ActivityServiceImpl` 实现该方法（调用 Repository + Mapper 转换）
4. 在 `ActivityController` 添加 `GET /activity/recommend` 接口
5. 前端 `ApiService` 添加对应调用
6. 首页增加推荐展示区块

**预计工作量：** 1-2小时即可完成阶段一的完整实现。
