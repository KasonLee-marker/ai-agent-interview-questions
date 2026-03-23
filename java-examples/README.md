# Java Agent 实现示例

本目录包含各种 Agent 范式的 Java 伪代码实现，用于面试参考和学习。

## 目录索引

| 文件 | 范式 | 核心概念 | 复杂度 |
|------|------|----------|--------|
| [ReActAgent.java](./ReActAgent.java) | ReAct | Thought-Action-Observation 循环 | ⭐⭐⭐ |
| [PlanAndSolveAgent.java](./PlanAndSolveAgent.java) | Plan-and-Solve | 先规划后执行 | ⭐⭐⭐ |
| [MultiAgentOrchestrator.java](./MultiAgentOrchestrator.java) | Multi-Agent | 多智能体协作 | ⭐⭐⭐⭐ |
| [ReflexionAgent.java](./ReflexionAgent.java) | Reflexion | 自我反思学习 | ⭐⭐⭐⭐ |

## 核心抽象接口

```java
/**
 * LLM 客户端接口
 * 可根据实际情况替换为 OpenAI、Claude、文心一言等实现
 */
interface LLMClient {
    String complete(String prompt);
    String complete(String prompt, double temperature);
    Stream<String> completeStream(String prompt);
}

/**
 * 工具接口
 * 代表 Agent 可以调用的外部能力
 */
interface Tool {
    String getName();
    String getDescription();
    String execute(String input);
}

/**
 * 记忆接口
 * 用于存储对话历史和上下文
 */
interface Memory {
    void add(String entry);
    String getContext();
    void clear();
    boolean isEmpty();
}

/**
 * Agent 基类
 */
abstract class Agent {
    protected LLMClient llm;
    protected List<Tool> tools = new ArrayList<>();
    protected Memory memory;
    
    public void registerTool(Tool tool) {
        tools.add(tool);
    }
    
    public abstract String run(String task);
}
```

## 设计要点说明

### 1. 依赖注入

所有实现都支持通过构造函数注入依赖，便于：
- 单元测试（Mock 依赖）
- 灵活切换实现（如更换 LLM 提供商）
- 配置化管理

### 2. 异常处理

```java
try {
    String result = agent.run(task);
} catch (AgentException e) {
    // 处理 Agent 执行异常
} catch (ToolExecutionException e) {
    // 处理工具调用异常
} catch (LLMException e) {
    // 处理 LLM 调用异常
}
```

### 3. 可观测性

建议在实际项目中添加：
- 日志记录（SLF4J）
- 性能指标（Micrometer）
- 分布式追踪（OpenTelemetry）

### 4. 线程安全

- ReAct/Plan-and-Solve：通常单线程执行
- Multi-Agent：使用线程池并行执行
- Reflexion：注意内存的并发访问

## 面试提示

1. **代码是伪代码**，重点理解设计思想，不要直接复制到生产环境
2. **关注接口设计**和**职责分离**，这是面试考察的重点
3. **能够画出架构图**，比写代码更重要
4. **准备好讨论扩展性**：如何支持新的范式？如何接入新的 LLM？
