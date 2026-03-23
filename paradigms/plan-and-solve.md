# Plan-and-Solve 范式详解

## 一、概念与原理

### 1.1 什么是 Plan-and-Solve？

**Plan-and-Solve**（计划-执行）是一种"先规划、后执行"的 Agent 范式。与 ReAct 的"边想边做"不同，它强调**在执行前制定完整的行动计划**。

### 1.2 执行流程

```
┌─────────────────────────────────────────────────────────────┐
│                        Plan Phase                          │
│  任务输入 → 分析需求 → 制定计划 → 生成步骤列表               │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                       Solve Phase                          │
│  Step 1 → Step 2 → Step 3 → ... → 汇总结果                 │
│   │        │        │                                        │
│   ▼        ▼        ▼                                        │
│ [执行]   [执行]   [执行]                                     │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 与 ReAct 的核心差异

| 特性 | Plan-and-Solve | ReAct |
|------|----------------|-------|
| **规划时机** | 执行前一次性规划 | 执行中逐步规划 |
| **灵活性** | 较低，计划固定 | 较高，动态调整 |
| **LLM 调用次数** | 较少（2-3次） | 较多（每步1次） |
| **适用场景** | 任务明确、步骤清晰 | 探索性、不确定性高 |
| **可预测性** | 高 | 中 |

---

## 二、面试题详解

### 题目 1：什么时候应该选择 Plan-and-Solve 而不是 ReAct？

#### 考察点
- 对两种范式的理解
- 场景分析能力
- 技术选型能力

#### 详细解答

**选择 Plan-and-Solve 的场景：**

**1. 成本敏感型应用**

```
场景：批量处理 1000 个文档的分类任务

ReAct 方式：
- 每个文档需要 5-10 步推理
- 总调用次数：5000-10000 次
- 成本：$$$

Plan-and-Solve 方式：
- 先制定通用分类策略（1次调用）
- 批量执行（可用规则或小模型）
- 总调用次数：1 + 少量异常处理
- 成本：$
```

**2. 任务步骤明确且稳定**

```
场景：自动化报表生成

步骤固定：
1. 从数据库提取数据
2. 按模板计算指标
3. 生成图表
4. 导出 PDF

→ 适合 Plan-and-Solve，因为步骤不会变
```

**3. 需要提前预估资源**

```
场景：向客户报价

Plan-and-Solve 可以：
- 先列出所有步骤
- 预估每步时间和成本
- 给客户准确报价

ReAct 则难以预估（不知道要走多少步）
```

**4. 需要人工审批**

```
场景：医疗诊断辅助

Plan 阶段：
- 生成诊断计划
- 医生审批计划
- 确认后再执行

→ 安全关键场景，必须提前知道要做什么
```

**对比总结表：**

| 场景特征 | 推荐范式 |
|----------|----------|
| 预算有限，需要控制成本 | Plan-and-Solve |
| 任务步骤固定且可预测 | Plan-and-Solve |
| 需要提前知道执行路径 | Plan-and-Solve |
| 需要人工审批流程 | Plan-and-Solve |
| 探索性任务，路径不确定 | ReAct |
| 需要频繁根据反馈调整 | ReAct |
| 单轮交互，快速响应 | ReAct |

---

### 题目 2：Plan-and-Solve 如何处理执行过程中的错误？是否需要重新规划？

#### 考察点
- 错误处理机制设计
- 动态调整能力
- 工程实践经验

#### 详细解答

**错误处理的三层策略：**

```
┌─────────────────────────────────────────────────────────┐
│ Level 1: 步骤内重试 (Step Retry)                        │
│ 单个步骤失败，重试或换方式执行                            │
├─────────────────────────────────────────────────────────┤
│ Level 2: 局部重规划 (Local Replan)                      │
│ 某步骤失败，调整后续计划                                  │
├─────────────────────────────────────────────────────────┤
│ Level 3: 全局重规划 (Global Replan)                     │
│ 整体策略失败，重新制定计划                                │
└─────────────────────────────────────────────────────────┘
```

**具体实现方案：**

```java
public class RobustPlanAndSolveAgent {
    
    public String run(String task) {
        // 1. 初始规划
        Plan plan = generatePlan(task);
        ExecutionContext context = new ExecutionContext();
        
        for (int i = 0; i < plan.getSteps().size(); i++) {
            Step step = plan.getSteps().get(i);
            
            try {
                // 2. 执行步骤（带重试）
                String result = executeWithRetry(step, context);
                context.addResult(step.getId(), result);
                
            } catch (StepExecutionException e) {
                // 3. 错误处理决策
                ErrorHandlingDecision decision = decideErrorHandling(e, step, plan);
                
                switch (decision.getStrategy()) {
                    case RETRY:
                        // Level 1: 重试
                        i--; // 重新执行当前步骤
                        break;
                        
                    case SKIP:
                        // 跳过此步骤，继续
                        context.addSkippedStep(step.getId());
                        break;
                        
                    case REPLAN_LOCAL:
                        // Level 2: 局部重规划
                        List<Step> newSteps = replanFromStep(plan, i, e);
                        plan.replaceSteps(i, newSteps);
                        i--; // 重新执行
                        break;
                        
                    case REPLAN_GLOBAL:
                        // Level 3: 全局重规划
                        plan = generatePlan(task + "\n注意：之前的计划因以下原因失败：" + e.getMessage());
                        i = -1; // 从头开始
                        break;
                        
                    case FAIL:
                        // 无法恢复，返回错误
                        return "任务失败：" + e.getMessage();
                }
            }
        }
        
        return generateFinalAnswer(context);
    }
    
    private ErrorHandlingDecision decideErrorHandling(
            Exception e, Step step, Plan plan) {
        
        // 决策逻辑
        if (isTransientError(e) && step.getRetryCount() < 3) {
            return ErrorHandlingDecision.RETRY;
        }
        
        if (isOptionalStep(step)) {
            return ErrorHandlingDecision.SKIP;
        }
        
        if (canReplanLocally(step, plan)) {
            return ErrorHandlingDecision.REPLAN_LOCAL;
        }
        
        if (plan.getReplanCount() < 2) {
            return ErrorHandlingDecision.REPLAN_GLOBAL;
        }
        
        return ErrorHandlingDecision.FAIL;
    }
}
```

**何时需要重新规划？**

| 情况 | 处理方式 | 示例 |
|------|----------|------|
| 工具临时不可用 | Level 1 重试 | API 限流 |
| 可选步骤失败 | Level 2 跳过 | 装饰性查询 |
| 依赖条件变化 | Level 2 局部重规划 | 数据源切换 |
| 整体策略错误 | Level 3 全局重规划 | 理解错任务 |

---

### 题目 3：如何设计一个高质量的 Plan？Plan 应该包含哪些要素？

#### 考察点
- 计划表示设计
- 结构化思维
- 工程实现能力

#### 详细解答

**高质量 Plan 的要素：**

```java
class Plan {
    // 1. 元信息
    private String goal;              // 任务目标
    private String version;           // 计划版本（用于重规划）
    private int replanCount;          // 重规划次数
    
    // 2. 步骤列表
    private List<Step> steps;
    
    // 3. 依赖关系图
    private DependencyGraph dependencies;
    
    // 4. 资源预估
    private ResourceEstimate estimate;
    
    // 5. 回退策略
    private FallbackStrategy fallback;
}

class Step {
    private String id;
    private String description;       // 步骤描述
    private StepType type;            // 类型：LLM/Tool/Rule
    private List<String> dependencies; // 依赖的步骤ID
    private boolean optional;         // 是否可选
    private int maxRetries;           // 最大重试次数
    private String expectedOutput;    // 预期输出格式
    private ValidationRule validation; // 结果校验规则
}
```

**Plan 生成 Prompt 示例：**

```
请为以下任务制定详细执行计划：

任务：{task}

要求：
1. 将任务分解为具体可执行的步骤
2. 每个步骤明确：
   - 步骤ID（如 S1, S2）
   - 步骤描述
   - 执行类型（LLM调用/工具调用/规则处理）
   - 依赖步骤（如果有）
   - 是否可选
   - 预期输出
3. 识别可以并行执行的步骤
4. 预估每步的时间和成本

输出格式（JSON）：
{
  "goal": "任务目标",
  "steps": [
    {
      "id": "S1",
      "description": "步骤描述",
      "type": "tool|llm|rule",
      "dependencies": [],
      "optional": false,
      "expected_output": "预期结果格式"
    }
  ],
  "parallel_groups": [["S1", "S2"], ["S3"]],
  "estimate": {
    "total_steps": 5,
    "estimated_time": "10分钟",
    "estimated_cost": "$0.5"
  }
}
```

**Plan 质量检查清单：**

- [ ] 步骤是否可执行？（不模糊、不抽象）
- [ ] 依赖关系是否无环？
- [ ] 是否考虑了失败情况？
- [ ] 是否有明确的完成标准？
- [ ] 成本是否在可接受范围？

---

## 三、延伸追问

1. **"Plan-and-Solve 如何支持并行执行？"**
   - 通过依赖图分析无依赖的步骤
   - 使用线程池或异步执行
   - 注意资源竞争和结果合并

2. **"如何处理计划执行到一半用户改变需求？"**
   - 监听需求变化事件
   - 评估影响范围
   - 局部或全局重规划

3. **"Plan-and-Solve 和 WorkFlow 引擎有什么区别？"**
   - Plan-and-Solve 的计划是动态生成的
   - WorkFlow 通常是预定义的
   - 可以结合：LLM 生成 Plan，WorkFlow 引擎执行
