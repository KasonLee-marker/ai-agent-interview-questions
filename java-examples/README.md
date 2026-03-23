# Java Agent 实现示例

本目录包含各种 Agent 范式的 Java 伪代码实现。

## 目录

- [ReAct 实现](./ReActAgent.java)
- [Plan-and-Solve 实现](./PlanAndSolveAgent.java)
- [Multi-Agent 实现](./MultiAgentOrchestrator.java)
- [Reflexion 实现](./ReflexionAgent.java)

## 核心抽象

```java
// LLM 客户端
interface LLMClient {
    String complete(String prompt);
}

// 工具接口
interface Tool {
    String getName();
    String execute(String input);
}

// 记忆接口
interface Memory {
    void add(String entry);
    String getContext();
}

// Agent 基类
abstract class Agent {
    protected LLMClient llm;
    protected List<Tool> tools;
    protected Memory memory;
    
    public abstract String run(String task);
}
```

## 设计要点

1. **接口抽象**: 便于替换实现（如切换 LLM 提供商）
2. **依赖注入**: 支持灵活的组件组合
3. **异常处理**: Agent 执行中的容错机制
4. **可观测性**: 日志和追踪支持
