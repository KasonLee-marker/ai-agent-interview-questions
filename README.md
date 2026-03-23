# AI Agent 面试题收集

本仓库用于收集和整理 AI Agent 相关的面试题和学习资料，帮助开发者系统掌握 Agent 开发知识。

## 📚 内容导航

### 一、Agent 开发范式

深入讲解 5 大主流 Agent 范式，包含原理、面试题详解和 Java 实现示例。

| 范式 | 核心思想 | 面试热度 | 文档 |
|------|----------|----------|------|
| **ReAct** | 推理+行动交替执行 | ⭐⭐⭐⭐⭐ | [查看](./paradigms/react.md) |
| **Plan-and-Solve** | 先规划后执行 | ⭐⭐⭐⭐ | [查看](./paradigms/plan-and-solve.md) |
| **Reflexion** | 自我反思学习 | ⭐⭐⭐⭐ | [查看](./paradigms/reflexion.md) |
| **Multi-Agent** | 多智能体协作 | ⭐⭐⭐⭐⭐ | [查看](./paradigms/multi-agent.md) |
| **CoT/ToT** | 链式/树式推理 | ⭐⭐⭐⭐ | [查看](./paradigms/cot-tot.md) |

**[范式总览与对比 →](./paradigms/README.md)**

### 二、开发框架

主流 Agent 开发框架对比与选型指南。

- [框架对比与选型](./frameworks/README.md)
- LangChain vs LlamaIndex 深度对比
- 框架选择决策树

### 三、Java 实现示例

各种 Agent 范式的 Java 伪代码实现，面试参考用。

- [ReActAgent](./java-examples/ReActAgent.java) - ReAct 范式实现
- [PlanAndSolveAgent](./java-examples/PlanAndSolveAgent.java) - 计划执行范式
- [MultiAgentOrchestrator](./java-examples/MultiAgentOrchestrator.java) - 多 Agent 协作
- [ReflexionAgent](./java-examples/ReflexionAgent.java) - 自我反思学习

**[Java 示例说明 →](./java-examples/README.md)**

## 🎯 面试题特点

本仓库的面试题具有以下特点：

1. **深度解析** - 不仅给答案，还讲清原理和考察点
2. **代码示例** - 提供 Java 伪代码，便于理解
3. **对比分析** - 多个方案对比，知道何时用哪个
4. **延伸追问** - 模拟面试官的连环追问
5. **实战导向** - 结合真实工程场景

## 📖 推荐阅读顺序

```
新手路线：
ReAct → CoT/ToT → Plan-and-Solve → Multi-Agent → Reflexion

面试冲刺：
根据 JD 要求重点看对应范式
一般重点：ReAct、Multi-Agent
```

## 🤝 贡献

欢迎提交 PR 补充更多面试题和资料！

提交格式：
- 问题标题明确
- 包含考察点分析
- 提供详细解答
- 有代码示例更佳

## 📝 更新日志

- **2024-03-23** - 初始版本，包含 5 大范式详解、框架对比、Java 示例

---

> 💡 **提示**：本文档是面试准备资料，不是生产代码，请勿直接复制到生产环境。
