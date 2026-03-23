# Multi-Agent 范式详解

## 一、概念与原理

### 1.1 什么是 Multi-Agent？

**Multi-Agent** 是指多个独立的 AI Agent **分工协作**完成复杂任务的范式。它模拟人类团队的工作方式，每个 Agent 负责特定的子任务或扮演特定角色。

### 1.2 典型架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Multi-Agent System                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                     ┌─────────────┐                        │
│                     │ Orchestrator│  (协调器/管理者)        │
│                     │   协调器     │                        │
│                     └──────┬──────┘                        │
│                            │                                │
│        ┌───────────────────┼───────────────────┐           │
│        │                   │                   │           │
│        ▼                   ▼                   ▼           │
│   ┌─────────┐        ┌─────────┐        ┌─────────┐       │
│   │ Planner │        │Executor │        │ Reviewer│       │
│   │ 规划者   │        │ 执行者   │        │ 审查者   │       │
│   └────┬────┘        └────┬────┘        └────┬────┘       │
│        │                  │                  │            │
│        │             ┌────┴────┐             │            │
│        │             │         │             │            │
│        ▼             ▼         ▼             ▼            │
│   ┌─────────┐   ┌────────┐ ┌────────┐   ┌─────────┐      │
│   │Researcher│   │Coder   │ │Tester  │   │Critic   │      │
│   │ 研究员   │   │ 编码员  │ │ 测试员  │   │ 批评者   │      │
│   └─────────┘   └────────┘ └────────┘   └─────────┘      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 为什么需要 Multi-Agent？

| 优势 | 说明 |
|------|------|
| **专业化** | 每个 Agent 专注特定领域，能力更强 |
| **可扩展** | 新增 Agent 即可扩展能力 |
| **并行化** | 独立任务可并行执行，提高效率 |
| **容错性** | 单 Agent 失败不影响整体 |
| **可解释** | 分工明确，易于追踪问题 |

---

## 二、面试题详解

### 题目 1：Multi-Agent 系统如何解决 Agent 之间的冲突？

#### 考察点
- 分布式系统协调
- 冲突解决机制
- 共识算法理解

#### 详细解答

**冲突类型与解决方案：**

```
┌─────────────────────────────────────────────────────────────┐
│                      冲突解决机制                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 资源冲突（争抢同一资源）                                 │
│     └─→ 方案：锁机制、资源调度器                             │
│                                                             │
│  2. 意见冲突（结论不一致）                                   │
│     └─→ 方案：投票、置信度加权、仲裁者                        │
│                                                             │
│  3. 依赖冲突（循环依赖）                                     │
│     └─→ 方案：依赖图检测、拓扑排序                           │
│                                                             │
│  4. 优先级冲突（执行顺序争议）                               │
│     └─→ 方案：优先级队列、协商机制                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**具体实现 - 仲裁者模式：**

```java
public class ConflictResolver {
    
    /**
     * 解决意见冲突
     */
    public Resolution resolveOpinionConflict(
            List<AgentOpinion> opinions, 
            String conflictContext) {
        
        // 1. 分析各 Agent 的置信度
        Map<String, Double> confidenceScores = opinions.stream()
            .collect(Collectors.toMap(
                AgentOpinion::getAgentId,
                AgentOpinion::getConfidence
            ));
        
        // 2. 检查是否有一致意见
        Map<String, Long> voteCount = opinions.stream()
            .collect(Collectors.groupingBy(
                AgentOpinion::getConclusion,
                Collectors.counting()
            ));
        
        String majorityOpinion = voteCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        // 3. 多数决通过
        if (voteCount.get(majorityOpinion) > opinions.size() / 2) {
            return new Resolution(ResolutionType.MAJORITY_VOTE, majorityOpinion);
        }
        
        // 4. 置信度加权投票
        String weightedOpinion = calculateWeightedVote(opinions);
        
        // 5. 仍然冲突，引入仲裁者
        if (hasSignificantDisagreement(opinions)) {
            return arbitrate(conflictContext, opinions);
        }
        
        return new Resolution(ResolutionType.WEIGHTED_VOTE, weightedOpinion);
    }
    
    /**
     * 仲裁者决策
     */
    private Resolution arbitrate(String context, List<AgentOpinion> opinions) {
        // 使用更强的模型或人工介入
        ArbitratorAgent arbitrator = new ArbitratorAgent();
        
        String prompt = String.format("""
            以下 Agent 对问题产生了分歧：
            上下文：%s
            
            各方意见：
            %s
            
            请作为仲裁者，分析各方观点并做出最终决定。
            说明你的决策理由。
            """, context, formatOpinions(opinions));
        
        String decision = arbitrator.decide(prompt);
        return new Resolution(ResolutionType.ARBITRATION, decision);
    }
}

/**
 * 资源调度器（解决资源冲突）
 */
public class ResourceScheduler {
    private Map<String, Lock> resourceLocks = new ConcurrentHashMap<>();
    private PriorityQueue<ResourceRequest> requestQueue = new PriorityQueue<>();
    
    public ResourceAllocation allocate(ResourceRequest request) {
        Lock lock = resourceLocks.computeIfAbsent(
            request.getResourceId(), 
            k -> new ReentrantLock()
        );
        
        if (lock.tryLock()) {
            return new ResourceAllocation(request, lock);
        } else {
            // 加入队列等待
            requestQueue.offer(request);
            return null;
        }
    }
}
```

**投票策略对比：**

| 策略 | 适用场景 | 优点 | 缺点 |
|------|----------|------|------|
| **简单多数** | 意见明确分歧 | 简单快速 | 可能忽略高质量少数意见 |
| **置信度加权** | Agent 能力差异大 | 重视可靠 Agent | 置信度可能不准 |
| **Borda 计数** | 多选项排序 | 考虑偏好顺序 | 计算复杂 |
| **仲裁者** | 关键决策 | 质量高 | 延迟大、成本高 |

---

### 题目 2：如何设计 Agent 之间的通信协议？

#### 考察点
- 分布式通信设计
- 消息队列理解
- 协议设计能力

#### 详细解答

**通信模式对比：**

```
┌─────────────────────────────────────────────────────────────┐
│                      通信模式                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 直接通信（Direct）                                      │
│     Agent A ─────→ Agent B                                  │
│     特点：简单、耦合度高                                     │
│                                                             │
│  2. 消息队列（Message Queue）                               │
│     Agent A ──→ [Queue] ──→ Agent B                        │
│     特点：解耦、异步、可靠                                   │
│                                                             │
│  3. 发布订阅（Pub/Sub）                                     │
│     Agent A ──→ [Topic] ──→ Agent B, C, D                  │
│     特点：一对多、动态订阅                                   │
│                                                             │
│  4. 共享状态（Shared State）                                │
│     Agent A, B, C ──→ [State Store]                        │
│     特点：状态同步、最终一致                                 │
│                                                             │
│  5. 黑板系统（Blackboard）                                  │
│     Agent A, B, C ──→ [Blackboard]                         │
│     特点：共享工作空间、协作推理                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**消息协议设计：**

```java
/**
 * Agent 间消息格式
 */
public class AgentMessage {
    // 消息元数据
    private String messageId;
    private String correlationId;  // 用于关联请求和响应
    private String senderId;
    private List<String> recipientIds;
    private MessageType type;
    private long timestamp;
    private int ttl;  // 生存时间
    
    // 消息内容
    private MessagePayload payload;
    
    // 上下文
    private ConversationContext context;
    
    public enum MessageType {
        TASK_ASSIGNMENT,    // 任务分配
        TASK_RESULT,        // 任务结果
        QUERY,              // 查询请求
        RESPONSE,           // 查询响应
        BROADCAST,          // 广播
        HEARTBEAT,          // 心跳
        ERROR               // 错误通知
    }
}

/**
 * 消息总线实现
 */
public class AgentMessageBus {
    private Map<String, Agent> agents = new ConcurrentHashMap<>();
    private EventBus eventBus;  // 可以使用 Guava EventBus 或自定义
    
    public void registerAgent(Agent agent) {
        agents.put(agent.getId(), agent);
        eventBus.register(agent);
    }
    
    public void send(AgentMessage message) {
        // 点对点发送
        for (String recipientId : message.getRecipientIds()) {
            Agent recipient = agents.get(recipientId);
            if (recipient != null) {
                eventBus.post(new MessageEvent(message, recipientId));
            }
        }
    }
    
    public void broadcast(AgentMessage message) {
        // 广播到所有订阅者
        eventBus.post(new BroadcastEvent(message));
    }
    
    public CompletableFuture<AgentMessage> sendAndWait(
            AgentMessage message, 
            long timeoutMs) {
        // 发送并等待响应
        String correlationId = message.getCorrelationId();
        CompletableFuture<AgentMessage> future = new CompletableFuture<>();
        
        pendingResponses.put(correlationId, future);
        send(message);
        
        // 超时处理
        scheduler.schedule(() -> {
            future.completeExceptionally(
                new TimeoutException("Response timeout")
            );
        }, timeoutMs, TimeUnit.MILLISECONDS);
        
        return future;
    }
}
```

**通信协议选型建议：**

| 场景 | 推荐协议 | 原因 |
|------|----------|------|
| 实时协作 | 共享状态/黑板 | 需要频繁同步 |
| 任务分发 | 消息队列 | 解耦、可靠投递 |
| 事件通知 | 发布订阅 | 一对多广播 |
| 请求响应 | 直接通信 | 简单、低延迟 |

---

### 题目 3：Multi-Agent 和单体 Agent 相比有什么优劣？什么时候应该拆分？

#### 考察点
- 架构设计能力
- 技术选型判断
- 成本效益分析

#### 详细解答

**优劣对比：**

| 维度 | Multi-Agent | 单体 Agent |
|------|-------------|------------|
| **复杂度** | 高（协调、通信） | 低（单一流程） |
| **开发成本** | 高 | 低 |
| **运行成本** | 高（多实例） | 低 |
| **扩展性** | 好（加 Agent 即可） | 差（需改核心） |
| **性能** | 可并行（整体快） | 串行（单次快） |
| **可维护** | 模块化好 | 简单直接 |
| **可靠性** | 高（单点故障不影响） | 低（单点故障） |

**拆分原则：**

```
┌─────────────────────────────────────────────────────────────┐
│                    拆分决策树                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  任务复杂度是否高？                                          │
│     ├─ No → 单体 Agent                                      │
│     └─ Yes → 继续                                           │
│                                                             │
│  是否存在明确的功能边界？                                     │
│     ├─ No → 尝试用 ReAct 增强单体                           │
│     └─ Yes → 继续                                           │
│                                                             │
│  是否需要并行处理？                                          │
│     ├─ No → 考虑 Pipeline（串行多 Agent）                   │
│     └─ Yes → Multi-Agent                                    │
│                                                             │
│  各模块是否需要不同能力/模型？                                │
│     ├─ No → 共享模型的 Multi-Agent                          │
│     └─ Yes → 专用 Agent（如 Coding 用 GPT-4，简单任务用 GPT-3.5）│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**典型拆分场景：**

**场景 1：代码生成任务**
```
单体方式：
一个 Agent 完成需求分析→设计→编码→测试→文档

Multi-Agent 拆分：
- Planner：分析需求，制定开发计划
- Architect：设计系统架构
- Coder：编写代码
- Reviewer：代码审查
- Tester：生成测试用例
- Documenter：编写文档

收益：
- 可以并行设计多个模块
- 专业 Agent 代码质量更高
- 审查 Agent 发现 Planner 遗漏
```

**场景 2：不应该拆分的情况**
```
简单问答任务：
"今天北京天气如何？"

→ 单体 Agent 直接调用天气 API 即可
→ 拆分成多个 Agent 反而增加复杂度
```

---

## 三、延伸追问

1. **"Multi-Agent 系统的调试和监控怎么做？"**
   - 分布式追踪（Trace）
   - Agent 间消息日志
   - 可视化协作流程

2. **"如何保证 Multi-Agent 系统的安全性？"**
   - Agent 身份认证
   - 消息签名验证
   - 权限控制（哪些 Agent 能访问哪些资源）

3. **"Multi-Agent 和微服务架构有什么异同？"**
   - 相似：模块化、独立部署
   - 不同：Agent 是智能体，有自主决策能力
   - 可以结合：Agent 作为微服务的客户端
