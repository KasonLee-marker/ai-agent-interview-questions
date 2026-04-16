# AI Agent 面试题库扫描报告

> 扫描时间：2026-04-16
> 扫描工具：开发小柴

---

## 一、重复文件（需合并）

### 1. 人工评估主题（3个重复文件）

**文件列表：**
- `evaluation/human-vs-auto-evaluation.md` ⭐ **保留此版本**（内容最完整）
- `evaluation/human-vs-automated-evaluation.md` ❌ **待删除**
- `evaluation/human-vs-automatic-evaluation.md` ❌ **待删除**

**重复原因：** 文件名命名不一致导致重复创建

**合并建议：**
- 保留 `human-vs-auto-evaluation.md`（内容最完整，4道面试题）
- 删除另外两个版本
- 在 coverage-matrix.md 中统一引用

---

### 2. RAG评测指标（2个重复文件）

**文件列表：**
- `evaluation/rag-evaluation-metrics.md` - 根目录版本
- `docs/evaluation/rag-evaluation-metrics.md` - docs目录版本

**差异分析：**
- 两个版本内容**不同**，docs版本更详细（4道面试题）
- 根目录版本较简略（3道面试题）

**合并建议：**
- 保留 `docs/evaluation/rag-evaluation-metrics.md`（内容更完整）
- 删除 `evaluation/rag-evaluation-metrics.md`
- 在根目录 evaluation/ 下创建软链接或重定向

---

### 3. Agent评测指标（2个重复文件）

**文件列表：**
- `evaluation/agent-evaluation.md` - 根目录版本
- `docs/evaluation/agent-evaluation.md` - docs目录版本

**差异分析：**
- 两个版本内容**不同**
- docs版本更详细（4道面试题，含数据集构建）
- 根目录版本较简略（3道面试题）

**合并建议：**
- 保留 `docs/evaluation/agent-evaluation.md`（内容更完整）
- 删除 `evaluation/agent-evaluation.md`
- 统一引用到 docs 版本

---

## 二、目录结构问题

### 问题描述

项目存在**双轨制**目录结构：

```
ai-agent-interview-questions/
├── evaluation/          # 根目录版本
├── agent/
├── fundamentals/
├── retrieval/
├── ...
└── docs/               # docs目录版本
    ├── evaluation/
    ├── agent/
    ├── fundamentals/
    └── ...
```

**问题：**
1. 内容重复维护，容易不一致
2. coverage-matrix.md 引用混乱
3. 新贡献者不知道应该改哪个版本

**建议：**
- 统一使用根目录结构（`evaluation/`, `agent/` 等）
- `docs/` 目录仅保留：
  - `coverage-matrix.md`
  - `quality-standards.md`
  - `architecture.md`
  - `exec-plans/`
- 将 `docs/evaluation/`, `docs/agent/` 等子目录内容合并到根目录对应位置

---

## 三、缺失内容（根据 coverage-matrix.md）

### P0 - 紧急优先级

| 缺失项 | 所属模块 | 当前状态 | 建议位置 |
|--------|----------|----------|----------|
| Retry & Fallback | agent/ | ❌ 未创建 | `agent/retry-fallback.md` |
| Embedding模型选型 | retrieval/ | ❌ 未创建 | `retrieval/embedding-models.md` |
| 向量数据库对比 | retrieval/ | ❌ 未创建 | `retrieval/vector-database-comparison.md` |

### P1 - 重要优先级

| 缺失项 | 所属模块 | 当前状态 | 建议位置 |
|--------|----------|----------|----------|
| 模型推理优化 | deployment/ | ❌ 目录为空 | `deployment/model-inference-optimization.md` |
| 量化技术 | deployment/ | ❌ 目录为空 | `deployment/quantization.md` |
| 推理引擎对比 | deployment/ | ❌ 目录为空 | `deployment/inference-engines.md` |
| Agent服务化部署 | deployment/ | ❌ 目录为空 | `deployment/agent-deployment.md` |
| 流式输出设计 | deployment/ | ❌ 目录为空 | `deployment/streaming-output.md` |
| 负载均衡与扩缩容 | deployment/ | ❌ 目录为空 | `deployment/load-balancing.md` |
| 模型参数量与计算量 | fundamentals/ | ❌ 未创建 | `fundamentals/model-parameters-computation.md` |
| 大模型架构变种 | fundamentals/ | ❌ 未创建 | `fundamentals/llm-architecture-variants.md` |
| LangChain深度解析 | frameworks/ | ⚠️ 骨架 | `frameworks/langchain-deep-dive.md` 需完善 |
| LlamaIndex深度解析 | frameworks/ | ⚠️ 骨架 | `frameworks/llamaindex-deep-dive.md` 需完善 |

### P2 - 一般优先级

| 缺失项 | 所属模块 | 当前状态 | 建议位置 |
|--------|----------|----------|----------|
| SFT监督微调 | fine-tuning/ | ❌ 目录为空 | `fine-tuning/sft.md` |
| RLHF原理 | fine-tuning/ | ❌ 目录为空 | `fine-tuning/rlhf.md` |
| LoRA/QLoRA | fine-tuning/ | ❌ 目录为空 | `fine-tuning/lora.md` |
| 指令微调数据构建 | fine-tuning/ | ❌ 目录为空 | `fine-tuning/instruction-data.md` |
| 多模态理解(VLM) | multimodal/ | ❌ 目录为空 | `multimodal/vlm.md` |
| 图文检索 | multimodal/ | ❌ 目录为空 | `multimodal/image-text-retrieval.md` |
| 多模态Agent架构 | multimodal/ | ❌ 目录为空 | `multimodal/multimodal-agent.md` |
| 图像处理工具调用 | multimodal/ | ❌ 目录为空 | `multimodal/image-tool-calling.md` |
| ReActAgent.java | java-examples/ | ❌ 未创建 | `java-examples/ReActAgent.java` |
| PlanAndSolveAgent.java | java-examples/ | ❌ 未创建 | `java-examples/PlanAndSolveAgent.java` |
| MultiAgentOrchestrator.java | java-examples/ | ❌ 未创建 | `java-examples/MultiAgentOrchestrator.java` |
| ReflexionAgent.java | java-examples/ | ❌ 未创建 | `java-examples/ReflexionAgent.java` |

---

## 四、执行计划

### Phase 1: 清理重复文件

```bash
# 1. 删除人工评估重复文件
rm evaluation/human-vs-automated-evaluation.md
rm evaluation/human-vs-automatic-evaluation.md

# 2. 合并 RAG 评测（保留docs版本）
rm evaluation/rag-evaluation-metrics.md
# 确保 docs/evaluation/rag-evaluation-metrics.md 是最新版本

# 3. 合并 Agent 评测（保留docs版本）
rm evaluation/agent-evaluation.md
# 确保 docs/evaluation/agent-evaluation.md 是最新版本
```

### Phase 2: 统一目录结构

```bash
# 将 docs/ 子目录内容合并到根目录
# 1. 对比并合并 docs/evaluation/ -> evaluation/
# 2. 对比并合并 docs/agent/ -> agent/
# 3. 对比并合并 docs/fundamentals/ -> fundamentals/
# 4. 删除 docs/ 下的子目录，只保留核心文档
```

### Phase 3: 补充缺失内容（按优先级）

**P0 任务：**
- [ ] `agent/retry-fallback.md`
- [ ] `retrieval/embedding-models.md`
- [ ] `retrieval/vector-database-comparison.md`

**P1 任务：**
- [ ] `deployment/` 目录全套文档
- [ ] `fundamentals/model-parameters-computation.md`
- [ ] `fundamentals/llm-architecture-variants.md`
- [ ] 完善 `frameworks/langchain-deep-dive.md`
- [ ] 完善 `frameworks/llamaindex-deep-dive.md`

**P2 任务：**
- [ ] `fine-tuning/` 目录全套文档
- [ ] `multimodal/` 目录全套文档
- [ ] `java-examples/` 全套代码

---

## 五、质量检查清单

- [ ] 所有重复文件已合并
- [ ] 目录结构统一
- [ ] coverage-matrix.md 引用路径更新
- [ ] README.md 导航链接检查
- [ ] AGENTS.md 状态更新

---

*报告生成时间：2026-04-16*
*下次扫描建议：解决重复文件后*
