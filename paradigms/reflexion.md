# Reflexion 范式详解

## 一、概念与原理

### 1.1 什么是 Reflexion？

**Reflexion** 是一种让 Agent 具备**自我反思和从错误中学习**能力的范式。它不通过微调模型参数来学习，而是通过**语言反馈**来改进策略。

### 1.2 核心组件

```
┌─────────────────────────────────────────────────────────────┐
│                      Reflexion Agent                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌─────────┐    ┌───────────┐    ┌─────────────┐          │
│   │  Actor  │───→│ Evaluator │───→│  Reflector  │          │
│   │ 执行者  │    │  评估者   │    │  反思生成   │          │
│   └────┬────┘    └───────────┘    └──────┬──────┘          │
│        │                                  │                 │
│        │         ┌───────────┐          │                 │
│        └────────→│  Memory   │←─────────┘                 │
│                  │  记忆存储  │                            │
│                  └───────────┘                            │
│                          │                                │
│                          ▼                                │
│                   ┌─────────────┐                         │
│                   │  Next Trial │  (带着经验重试)          │
│                   └─────────────┘                         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 与传统强化学习的区别

| 特性 | Reflexion | RL (强化学习) |
|------|-----------|---------------|
| **学习信号** | 语言反馈 | 数值奖励 |
| **模型更新** | ❌ 不更新参数 | ✅ 更新参数 |
| **样本效率** | 高（零样本） | 低（需要大量样本） |
| **可解释性** | 高（自然语言） | 低（权重变化） |
| **适用模型** | 任意 LLM | 需可训练模型 |

---

## 二、面试题详解

### 题目 1：Reflexion 的记忆机制如何设计？短期记忆和长期记忆有什么区别？

#### 考察点
- 记忆系统设计
- 向量数据库理解
- 工程实现能力

#### 详细解答

**记忆分层架构：**

```
┌─────────────────────────────────────────────────────────────┐
│                      Memory System                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                 Working Memory                      │   │
│  │  工作记忆（当前任务上下文）                          │   │
│  │  - 最近几次的尝试                                    │   │
│  │  - 当前反思                                          │   │
│  │  - 临时变量                                          │   │
│  │  生命周期：单次任务                                   │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                 Short-term Memory                   │   │
│  │  短期记忆（会话级）                                  │   │
│  │  - 本次会话的所有反思                                │   │
│  │  - 用户偏好学习                                      │   │
│  │  生命周期：会话结束                                   │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                 Long-term Memory                    │   │
│  │  长期记忆（持久化）                                  │   │
│  │  - 跨任务的通用经验                                  │   │
│  │  - 按任务类型分类                                    │   │
│  │  - 向量检索                                          │   │
│  │  存储：向量数据库                                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**具体实现：**

```java
public class ReflexionMemory {
    
    // 1. 工作记忆 - 内存存储
    private Deque<Trial> workingMemory = new ArrayDeque<>();
    private static final int WORKING_MEMORY_SIZE = 5;
    
    // 2. 短期记忆 - 会话级
    private List<Reflection> sessionMemory = new ArrayList<>();
    
    // 3. 长期记忆 - 向量数据库
    private VectorDB longTermMemory;
    
    /**
     * 添加新的尝试到工作记忆
     */
    public void addTrial(Trial trial) {
        workingMemory.addLast(trial);
        if (workingMemory.size() > WORKING_MEMORY_SIZE) {
            // 溢出的转入短期记忆
            Trial old = workingMemory.removeFirst();
            sessionMemory.addAll(old.getReflections());
        }
    }
    
    /**
     * 获取当前任务相关的反思（工作记忆 + 检索长期记忆）
     */
    public List<Reflection> getRelevantReflections(String task) {
        List<Reflection> relevant = new ArrayList<>();
        
        // 1. 加入工作记忆
        workingMemory.forEach(t -> relevant.addAll(t.getReflections()));
        
        // 2. 从短期记忆筛选相关的
        relevant.addAll(sessionMemory.stream()
            .filter(r -> isRelevant(r, task))
            .collect(Collectors.toList()));
        
        // 3. 向量检索长期记忆
        List<Reflection> similar = longTermMemory.similaritySearch(
            task, 
            embeddingModel.embed(task),
            5
        );
        relevant.addAll(similar);
        
        return relevant;
    }
    
    /**
     * 任务结束时，总结并保存到长期记忆
     */
    public void persistReflections(String taskType) {
        // 提取通用经验
        String summary = summarizeReflections(sessionMemory);
        
        Reflection generalReflection = new Reflection(
            "task_type: " + taskType,
            summary,
            sessionMemory
        );
        
        // 存入向量数据库
        longTermMemory.insert(
            generalReflection,
            embeddingModel.embed(summary)
        );
    }
}

class Reflection {
    private String context;      // 产生反思的上下文
    private String content;      // 反思内容
    private List<String> lessons; // 学到的教训
    private String strategy;     // 改进策略
    private long timestamp;
    private int taskType;        // 任务类型标签
}
```

**三种记忆的对比：**

| 维度 | 工作记忆 | 短期记忆 | 长期记忆 |
|------|----------|----------|----------|
| **存储位置** | JVM 内存 | 内存/Redis | 向量数据库 |
| **生命周期** | 单次任务 | 单次会话 | 永久 |
| **检索方式** | 直接访问 | 关键词过滤 | 向量相似度 |
| **内容** | 原始尝试 | 反思记录 | 通用经验 |
| **容量** | 小（最近5次） | 中（会话级） | 大（全历史） |

---

### 题目 2：如何避免 Reflexion 陷入无限循环？

#### 考察点
- 系统鲁棒性设计
- 终止条件设计
- 防循环机制

#### 详细解答

**多层防循环机制：**

```java
public class ReflexionAgent {
    
    // 1. 硬限制
    private static final int MAX_ATTEMPTS = 5;
    
    // 2. 收敛检测
    private ConvergenceDetector convergenceDetector = new ConvergenceDetector();
    
    // 3. 重复检测
    private Set<String> attemptedStrategies = new HashSet<>();
    
    public String run(String task) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            
            // 1. 执行
            String result = execute(task);
            
            // 2. 评估
            Evaluation eval = evaluate(result);
            if (eval.isSuccess()) {
                return result;
            }
            
            // 3. 检查是否收敛
            if (convergenceDetector.isConverged(result)) {
                logger.warn("检测到收敛停滞，终止重试");
                return "无法收敛到更好结果，最佳尝试：" + convergenceDetector.getBestResult();
            }
            
            // 4. 生成反思
            Reflection reflection = generateReflection(task, result, eval);
            
            // 5. 检查策略是否重复
            String strategyHash = hashStrategy(reflection.getStrategy());
            if (attemptedStrategies.contains(strategyHash)) {
                logger.warn("策略重复，尝试新方向");
                reflection = generateAlternativeReflection(reflection);
            }
            attemptedStrategies.add(strategyHash);
            
            // 6. 调整任务
            task = adjustTask(task, reflection);
        }
        
        return "达到最大尝试