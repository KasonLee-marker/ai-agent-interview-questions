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

主流 Agent 开发框架对比与选型指南，帮助你从语言生态、RAG 能力、Agent 编排能力和企业落地约束四个维度做选型判断。

- [框架对比与选型](./frameworks/README.md)
- LangChain vs LlamaIndex 深度对比
- 框架选择决策树

### 三、Java 实现示例

各种 Agent 范式的 Java 伪代码实现，适合在面试准备阶段快速理解接口设计、职责划分和异常处理思路。

- [ReActAgent](./java-examples/ReActAgent.java) - ReAct 范式实现
- [PlanAndSolveAgent](./java-examples/PlanAndSolveAgent.java) - 计划执行范式
- [MultiAgentOrchestrator](./java-examples/MultiAgentOrchestrator.java) - 多 Agent 协作
- [ReflexionAgent](./java-examples/ReflexionAgent.java) - 自我反思学习

**[Java 示例说明 →](./java-examples/README.md)**

### 四、RAG 检索技术

面向知识库问答、企业搜索和 Agent 记忆增强场景的检索专题，覆盖经典稀疏检索、混合检索、查询增强和分层索引设计。

| 主题 | 重点能力 | 文档 |
|------|----------|------|
| **BM25** | 关键词相关性建模 | [查看](./retrieval/bm25.md) |
| **Hybrid Retrieval** | 稀疏+稠密结果融合 | [查看](./retrieval/hybrid-retrieval.md) |
| **HyDE** | 查询增强与召回优化 | [查看](./retrieval/hyde.md) |
| **Parent-Child Index** | 检索粒度与上下文保真 | [查看](./retrieval/parent-child-index.md) |

**[RAG 检索专题总览 →](./retrieval/README.md)**

### 五、Agent 记忆系统

Agent 记忆机制设计，包括短期/长期记忆分层、记忆提取与更新策略、记忆检索与使用。

| 主题 | 重点能力 | 文档 |
|------|----------|------|
| **记忆系统** | STM/LTM 设计、更新策略、检索使用 | [查看](./agent/memory-system.md) |

### 六、Function Calling 与任务规划

Agent 工具调用机制、任务规划策略、多工具调度与失败处理。

| 主题 | 重点能力 | 文档 |
|------|----------|------|
| **Function Calling** | 工具注册、参数校验、失败处理 | [查看](./agent/function-calling.md) |
| **任务规划** | 模型规划、规则规划、混合规划 | [查看](./agent/task-planning.md) |

### 七、Agent 安全

Prompt 注入防御、工具调用安全控制、敏感接口限制、权限管理。

| 主题 | 重点能力 | 文档 |
|------|----------|------|
| **安全防护** | Prompt 注入防御、工具安全、权限控制 | [查看](./agent/security.md) |

## 🎯 面试题特点

本仓库的面试题具有以下特点：

1. **深度解析** - 不仅给答案，还讲清原理和考察点
2. **代码示例** - 提供 Java 伪代码，便于理解
3. **对比分析** - 多个方案对比，知道何时用哪个
4. **延伸追问** - 模拟面试官的连环追问
5. **实战导向** - 结合真实工程场景
6. **检索专题补充** - 覆盖 Agent 常见的 RAG 检索与召回优化问题

## 📖 推荐阅读顺序

```
新手路线：
ReAct → CoT/ToT → Plan-and-Solve → Multi-Agent → Reflexion → Retrieval → Memory → Function Calling → Security

面试冲刺：
根据 JD 要求重点看对应范式和检索方案
一般重点：ReAct、Multi-Agent、Hybrid Retrieval、Memory System、Function Calling、Security
```

## 🤝 贡献

欢迎提交 PR 补充更多面试题和资料！

提交格式：
- 问题标题明确
- 包含考察点分析
- 提供详细解答
- 有代码示例更佳

## 📝 更新日志

- **2026-03-24** - 新增记忆系统文档（短期/长期记忆、更新策略、检索使用）
- **2026-03-24** - 新增上下文工程、上下文优化、Re-ranker 文档
- **2026-03-23** - 补充 RAG 检索技术导航，完善主 README 描述与阅读路径
- **2024-03-23** - 初始版本，包含 5 大范式详解、框架对比、Java 示例

---

> 💡 **提示**：本文档是面试准备资料，不是生产代码，请勿直接复制到生产环境。
