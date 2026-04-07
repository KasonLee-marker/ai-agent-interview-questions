# AI Agent 面试题收集

AI Agent 开发面试题与学习资料，涵盖设计范式、检索技术、框架选型与工程实践。

## 📚 内容导航

| 模块 | 内容 | 面试热度 | 入口 |
|------|------|----------|------|
| **Agent 范式** | ReAct、Plan-and-Solve、Reflexion、Multi-Agent、CoT/ToT | ⭐⭐⭐⭐⭐ | [paradigms/](./paradigms/README.md) |
| **RAG 检索** | BM25、混合检索、HyDE、父子索引、Re-ranker、上下文工程、评测优化 | ⭐⭐⭐⭐⭐ | [retrieval/](./retrieval/README.md) |
| **Agent 核心** | 记忆系统、Function Calling、任务规划、安全防护 | ⭐⭐⭐⭐⭐ | [agent/](./agent/README.md) |
| **开发框架** | LangChain、LlamaIndex、Spring AI 对比与选型 | ⭐⭐⭐⭐ | [frameworks/](./frameworks/README.md) |
| **性能优化** | Token 优化、流式输出、Rate Limit、Prompt 压缩 | ⭐⭐⭐⭐ | [performance-optimization/](./performance-optimization/README.md) |
| **评估评测** | Agent 效果评估、LLM-as-a-Judge、RAG 评测指标 | ⭐⭐⭐ | [evaluation/](./evaluation/README.md) |
| **部署运维** | 模型推理优化、量化技术、推理引擎对比、服务化部署 | ⭐⭐⭐ | [deployment/](./deployment/README.md) |
| **Java 示例** | ReAct、Plan-and-Solve、Multi-Agent、Reflexion 伪代码 | ⭐⭐⭐⭐ | [java-examples/](./java-examples/README.md) |

## 📖 推荐阅读顺序

### 新手路线
```
ReAct → CoT/ToT → Plan-and-Solve → Multi-Agent → Retrieval → Memory → Function Calling
```

### 面试冲刺路线
按 JD 要求选读，**高频考点**：
- **必考**：ReAct、Multi-Agent、混合检索、Memory System、Function Calling
- **常考**：Plan-and-Solve、Reflexion、HyDE、Re-ranker、框架选型
- **加分**：性能优化、安全防护、部署优化

## 🔥 面试高频题速览

### Agent 范式
1. ReAct 和 Plan-and-Solve 的区别？什么时候选哪个？
2. Multi-Agent 如何设计通信机制？
3. Reflexion 的记忆如何设计？短期 vs 长期记忆？

### RAG 检索
1. 为什么单独使用 BM25 或向量检索都不够？
2. 混合检索的 RRF 融合原理是什么？
3. 父子索引解决了什么问题？
4. Re-ranker 后如何选择 Top-K？

### Agent 核心
1. 记忆系统如何设计？STM/LTM 分层策略？
2. Function Calling 工具描述怎么写？
3. 任务规划：静态 vs 动态规划的选择依据？
4. Prompt 注入攻击如何防御？

### 框架选型
1. LangChain vs LlamaIndex 核心区别？
2. Spring AI 和 LangChain4j 怎么选？
3. 如何评估一个框架是否适合生产环境？

## 🎯 学习目标

阅读本仓库后，你应该能够：

1. **解释核心概念**：清楚说明 ReAct、RAG、Function Calling 等核心机制的原理
2. **进行技术选型**：根据场景选择合适的范式、框架和优化策略
3. **设计系统方案**：能够设计 Agent 系统的记忆、检索、规划等模块
4. **解决实际问题**：针对性能、安全、效果等工程问题给出优化方案

## 📂 目录结构

```
ai-agent-interview-questions/
├── paradigms/              # Agent 开发范式
│   ├── react.md           # ReAct 推理+行动
│   ├── plan-and-solve.md  # 先规划后执行
│   ├── reflexion.md       # 自我反思学习
│   ├── multi-agent.md     # 多智能体协作
│   └── cot-tot.md         # 链式/树式推理
├── retrieval/              # RAG 检索技术
│   ├── bm25.md
│   ├── hybrid-retrieval.md
│   ├── hyde.md
│   ├── parent-child-index.md
│   ├── re-ranker.md
│   └── ...
├── agent/                  # Agent 核心机制
│   ├── memory-system.md
│   ├── function-calling.md
│   ├── task-planning.md
│   └── security.md
├── frameworks/             # 开发框架对比
├── performance-optimization/  # 性能优化
├── evaluation/             # 评估评测
├── deployment/             # 部署运维
└── java-examples/          # Java 实现示例
```

## 🛠️ 技术栈覆盖

| 领域 | 技术点 |
|------|--------|
| **LLM** | GPT-4、Claude、文心一言、通义千问 |
| **框架** | LangChain、LlamaIndex、Spring AI、LangChain4j |
| **检索** | BM25、向量检索、混合检索、Re-ranker |
| **向量库** | Milvus、Pinecone、Redis、Elasticsearch |
| **部署** | vLLM、TGI、TensorRT-LLM、量化技术 |

---

> 💡 本仓库为面试准备资料，伪代码示例请勿直接用于生产环境。
