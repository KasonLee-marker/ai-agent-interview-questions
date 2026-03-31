# AGENTS.md - AI Agent 面试题库导航

## 项目目标

维护一份**全面、高质量、持续更新**的 AI Agent 面试题知识库，帮助开发者系统掌握 Agent 开发知识，覆盖从理论到工程落地的完整链路。

---

## 目录结构

```
ai-agent-interview-questions/
├── AGENTS.md              # 本文件 - 项目导航地图
├── README.md              # 项目主入口 - 面向用户的介绍
│
├── docs/                  # 知识库文档
│   ├── architecture.md    # 模块架构与依赖关系
│   ├── coverage-matrix.md # 知识点覆盖矩阵（核心）
│   ├── quality-standards.md # 内容质量标准
│   └── exec-plans/        # 执行计划（待完成任务）
│       └── README.md
│
├── tools/                 # 工具链
│   └── coverage-checker.py # 覆盖率扫描工具
│
├── paradigms/             # ✅ Agent 开发范式（5大范式）
│   ├── README.md
│   ├── react.md           # ReAct - 推理+行动
│   ├── plan-and-solve.md  # Plan-and-Solve - 先规划后执行
│   ├── reflexion.md       # Reflexion - 自我反思
│   ├── multi-agent.md     # Multi-Agent - 多智能体协作
│   └── cot-tot.md         # CoT/ToT - 链式/树式推理
│
├── retrieval/             # ✅ RAG 检索技术
│   ├── README.md
│   ├── bm25.md            # BM25 稀疏检索
│   ├── hybrid-retrieval.md # 混合检索
│   ├── hyde.md            # HyDE 查询增强
│   ├── parent-child-index.md # 父子索引
│   ├── re-ranker.md       # 重排序
│   ├── context-engineering.md # 上下文工程
│   ├── context-optimization.md # 上下文优化
│   └── rag-evaluation.md  # RAG 评测
│
├── agent/                 # ✅ Agent 核心组件
│   ├── memory-system.md   # 记忆系统
│   ├── function-calling.md # 函数调用
│   ├── task-planning.md   # 任务规划
│   └── security.md        # 安全防护
│
├── frameworks/            # ⚠️ 开发框架（待完善）
│   └── README.md
│
├── java-examples/         # ⚠️ Java 实现示例（待完善）
│   └── README.md
│
├── fundamentals/          # ❌ 模型基础（待创建）
│   └── README.md
│
├── evaluation/            # ❌ 评估评测（待创建）
│   └── README.md
│
├── deployment/            # ❌ 部署运维（待创建）
│   └── README.md
│
├── multimodal/            # ❌ 多模态 Agent（待创建）
│   └── README.md
│
└── fine-tuning/           # ❌ 微调与对齐（待创建）
    └── README.md
```

**图例：**
- ✅ 已完成 - 内容较完整
- ⚠️ 待完善 - 有骨架需补充
- ❌ 待创建 - 目录待建立

---

## 内容质量标准

详见 [docs/quality-standards.md](./docs/quality-standards.md)

**核心要求：**
1. **深度解析** - 不仅给答案，还讲清原理和考察点
2. **代码示例** - 提供 Java 伪代码，便于理解
3. **对比分析** - 多个方案对比，知道何时用哪个
4. **延伸追问** - 模拟面试官的连环追问
5. **实战导向** - 结合真实工程场景

---

## 当前缺口

详见 [docs/coverage-matrix.md](./docs/coverage-matrix.md)

**高优先级缺口（P0）：**
- fundamentals/ - Transformer 架构、注意力机制
- evaluation/ - Agent 评测指标、人工 vs 自动评估

**中优先级缺口（P1）：**
- deployment/ - 模型推理优化、服务化部署
- frameworks/ - LangChain vs LlamaIndex 深度对比

---

## 贡献工作流（Harness 模式）

### 对于人类（你）

1. **查看缺口** - 运行 `python tools/coverage-checker.py` 或查看 coverage-matrix.md
2. **创建执行计划** - 在 `docs/exec-plans/` 创建任务描述
3. **触发生成** - 通知本汪（开发小柴）启动 Claude 生成内容
4. **审核合并** - 审核生成的 PR，确认后合并

### 对于智能体（Claude）

1. **读取 AGENTS.md** - 理解项目结构和标准
2. **查阅 coverage-matrix** - 确定当前缺口
3. **生成内容** - 按 quality-standards 创建面试题文档
4. **自检** - 运行 coverage-checker 验证
5. **提交** - 创建 PR 等待审核

---

## 快速开始

```bash
# 1. 查看当前覆盖情况
python tools/coverage-checker.py

# 2. 查看待完成任务
ls docs/exec-plans/

# 3. 开始新任务（人类操作）
# 编辑 docs/exec-plans/xxx.md，然后通知本汪
```

---

## 更新日志

- **2026-03-30** - 初始化 Harness Engineering 基础设施（AGENTS.md, coverage-matrix, docs/）
- **2026-03-26** - 新增上下文工程、上下文优化、Re-ranker 文档
- **2026-03-24** - 新增记忆系统文档
- **2026-03-23** - 初始版本，包含 5 大范式、RAG 检索、核心组件

---

> 💡 **提示**：本文档是智能体的工作导航，人类通过编辑 coverage-matrix 和 exec-plans 来指导内容生成方向。
