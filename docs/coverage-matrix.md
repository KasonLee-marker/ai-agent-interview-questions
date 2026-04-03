# 知识点覆盖矩阵

> 本文件由 coverage-checker.py 自动生成和维护
> 最后更新：2026-03-30

---

## 覆盖概览

| 模块 | 状态 | 完成度 | 题目数 | 优先级 |
|------|------|--------|--------|--------|
| **paradigms** | ✅ | 80% | ~25 | P0 |
| **retrieval** | ✅ | 90% | ~23 | P0 |
| **agent** | ✅ | 70% | ~15 | P0 |
| **frameworks** | ⚠️ | 30% | ~3 | P1 |
| **java-examples** | ⚠️ | 40% | ~4 | P2 |
| **fundamentals** | ⚠️ | 57% | 13 | **P0** |
| **evaluation** | ⚠️ | 33% | 12 | **P0** |
| **deployment** | ❌ | 0% | 0 | P1 |
| **multimodal** | ❌ | 0% | 0 | P2 |
| **fine-tuning** | ❌ | 0% | 0 | P2 |

**总体完成度：约 45%**

---

## 详细覆盖矩阵

### 1. paradigms/ - Agent 开发范式 ✅

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| ReAct | react.md | ✅ | 4 | 完整 |
| Plan-and-Solve | plan-and-solve.md | ✅ | 3 | 完整 |
| Reflexion | reflexion.md | ✅ | 3 | 完整 |
| Multi-Agent | multi-agent.md | ✅ | 4 | 完整 |
| CoT/ToT | cot-tot.md | ✅ | 3 | 完整 |
| **缺口** | | | | |
| Self-Consistency | - | ❌ | 0 | 待补充 |
| Tree of Thoughts 深度 | - | ❌ | 0 | 待补充 |
| Agent 组合模式 | - | ❌ | 0 | 待补充 |

### 2. retrieval/ - RAG 检索技术 ✅

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| BM25 | bm25.md | ✅ | 2 | 完整 |
| Hybrid Retrieval | hybrid-retrieval.md | ✅ | 2 | 完整 |
| HyDE | hyde.md | ✅ | 2 | 完整 |
| Parent-Child Index | parent-child-index.md | ✅ | 2 | 完整 |
| Re-ranker | re-ranker.md | ✅ | 2 | 完整 |
| Context Engineering | context-engineering.md | ✅ | 2 | 完整 |
| Context Optimization | context-optimization.md | ✅ | 2 | 完整 |
| RAG Evaluation | rag-evaluation.md | ✅ | 3 | 完整 |
| **缺口** | | | | |
| Dense Retrieval (向量检索) | - | ❌ | 0 | **高优先级** |
| Embedding 模型选型 | - | ❌ | 0 | **高优先级** |
| Dense Retrieval | dense-retrieval.md | ✅ | 3 | **P0 - 已完成** |
| 向量数据库对比 | - | ❌ | 0 | **高优先级** |
| 多路召回 | - | ❌ | 0 | 待补充 |

### 3. agent/ - Agent 核心组件 ✅

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| Memory System | memory-system.md | ✅ | 3 | 完整 |
| Function Calling | function-calling.md | ✅ | 3 | 完整 |
| Task Planning | task-planning.md | ✅ | 3 | 完整 |
| Security | security.md | ✅ | 3 | 完整 |
| **缺口** | | | | |
| Tool Use 设计模式 | - | ❌ | 0 | 待补充 |
| Error Handling | - | ❌ | 0 | 待补充 |
| Retry & Fallback | - | ❌ | 0 | 待补充 |

### 4. frameworks/ - 开发框架 ⚠️

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| 框架对比概述 | README.md | ⚠️ | 1 | 骨架 |
| **缺口** | | | | |
| LangChain 深度解析 | - | ❌ | 0 | **高优先级** |
| LlamaIndex 深度解析 | - | ❌ | 0 | **高优先级** |
| LangChain vs LlamaIndex 对比 | - | ❌ | 0 | **高优先级** |
| AutoGPT/AutoGen 分析 | - | ❌ | 0 | 待补充 |
| 框架选型决策树 | - | ❌ | 0 | 待补充 |

### 5. java-examples/ - Java 实现示例 ⚠️

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| 示例说明 | README.md | ⚠️ | - | 骨架 |
| **缺口** | | | | |
| ReActAgent.java | - | ❌ | - | 待补充 |
| PlanAndSolveAgent.java | - | ❌ | - | 待补充 |
| MultiAgentOrchestrator.java | - | ❌ | - | 待补充 |
| ReflexionAgent.java | - | ❌ | - | 待补充 |

### 6. fundamentals/ - 模型基础 ⚠️

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| Transformer 架构 | transformer-architecture.md | ✅ | 4 | **P0 - 已完成** |
| 注意力机制（Self/Cross/Multi-head） | attention-mechanism.md | ✅ | 3 | **P0 - 已完成** |
| 位置编码 | positional-encoding.md | ✅ | 3 | **P0 - 已完成** |
| 预训练任务（MLM/CLM） | pretraining.md | ✅ | 3 | **P0 - 已完成** |
| Tokenizer 原理 | - | ❌ | 0 | P1 |
| 模型参数量与计算量 | - | ❌ | 0 | P1 |
| 大模型架构变种 | - | ❌ | 0 | P1 |

### 7. evaluation/ - 评估评测 ⚠️

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| Agent 评测指标 | agent-evaluation.md | ✅ | 3 | **P0 - 已完成** |
| LLM-as-a-Judge | llm-as-judge.md | ✅ | 3 | **P0 - 已完成** |
| 人工评估 vs 自动评估 | human-vs-auto-evaluation.md | ✅ | 4 | **P0 - 已完成** |
| LLM-as-a-Judge | - | ❌ | 0 | **P0** |
| RAG 评测指标 | - | ❌ | 0 | P1 |
| 幻觉检测 | - | ❌ | 0 | P1 |
| A/B 测试设计 | - | ❌ | 0 | P1 |

### 8. deployment/ - 部署运维 ❌

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| **全部缺口** | | | | |
| 模型推理优化 | - | ❌ | 0 | P1 |
| 量化技术（INT8/INT4/GPTQ） | - | ❌ | 0 | P1 |
| 推理引擎（vLLM/TGI/TensorRT-LLM） | - | ❌ | 0 | P1 |
| Agent 服务化部署 | - | ❌ | 0 | P1 |
| 流式输出设计 | - | ❌ | 0 | P2 |
| 负载均衡与扩缩容 | - | ❌ | 0 | P2 |

### 9. multimodal/ - 多模态 Agent ❌

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| **全部缺口** | | | | |
| 多模态理解（VLM） | - | ❌ | 0 | P2 |
| 图文检索 | - | ❌ | 0 | P2 |
| 多模态 Agent 架构 | - | ❌ | 0 | P2 |
| 工具调用中的图像处理 | - | ❌ | 0 | P2 |

### 10. fine-tuning/ - 微调与对齐 ❌

| 知识点 | 文件 | 状态 | 题目数 | 备注 |
|--------|------|------|--------|------|
| **全部缺口** | | | | |
| SFT 监督微调 | - | ❌ | 0 | P2 |
| RLHF 原理 | - | ❌ | 0 | P2 |
| LoRA/QLoRA 高效微调 | - | ❌ | 0 | P2 |
| 指令微调数据构建 | - | ❌ | 0 | P2 |

---

## 执行计划队列

### 当前进行中的任务

暂无

### 待认领任务（按优先级排序）

#### P0 - 紧急
1. [x] ~~evaluation/human-vs-auto-evaluation.md - 人工评估 vs 自动评估~~ ✅ 2026-04-03
2. [ ] fundamentals/transformer-architecture.md - Transformer 架构详解
3. [ ] fundamentals/attention-mechanism.md - 注意力机制详解
4. [ ] evaluation/agent-evaluation.md - Agent 评测指标
5. [ ] evaluation/llm-as-judge.md - LLM-as-a-Judge 方法

#### P1 - 重要
5. [ ] retrieval/dense-retrieval.md - 稠密检索/向量检索
6. [ ] retrieval/embedding-models.md - Embedding 模型选型
7. [ ] frameworks/langchain-deep-dive.md - LangChain 深度解析
8. [ ] frameworks/llamaindex-deep-dive.md - LlamaIndex 深度解析
9. [ ] deployment/model-inference-optimization.md - 模型推理优化

#### P2 - 一般
10. [ ] deployment/quantization.md - 量化技术
11. [ ] java-examples/ReActAgent.java - Java 实现示例
12. [ ] multimodal/vlm-basics.md - 多模态基础

---

## 更新记录

| 日期 | 操作 | 内容 |
|------|------|------|
| 2026-03-30 | 创建 | 初始化覆盖矩阵 |
| 2026-04-03 | 完成 | 添加 evaluation/human-vs-auto-evaluation.md |
