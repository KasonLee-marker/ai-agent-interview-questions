# 评估评测

> AI Agent 面试题 - 效果评估与评测方法

---

## 模块目标

本模块覆盖 AI Agent 和 LLM 的评估方法，帮助理解如何衡量 Agent 效果。

面试中常被问到：
- "如何评估一个 Agent 的效果？"
- "人工评估和自动评估各有什么优缺点？"
- "LLM-as-a-Judge 是什么？有什么局限性？"

---

## 待补充知识点

### P0 - 紧急

- [ ] Agent 评测指标
  - 任务完成率 (Task Success Rate)
  - 步骤效率 (Step Efficiency)
  - 工具使用准确率
  - 用户满意度
  - 多轮对话连贯性

- [ ] 人工评估 vs 自动评估
  - 人工评估的优缺点
  - 自动评估的优缺点
  - 如何设计人工评估流程
  - 评估者间一致性 (Inter-annotator Agreement)

- [ ] LLM-as-a-Judge
  - 原理和方法
  - 评分维度设计
  - 位置偏见问题
  - 自我增强偏见
  - 与人类判断的对齐

### P1 - 重要

- [ ] RAG 评测指标
  - 检索准确率 (Retrieval Accuracy)
  - 答案相关性 (Answer Relevance)
  - 忠实度 (Faithfulness)
  - 上下文召回率 (Context Recall)

- [ ] 幻觉检测
  - 幻觉类型（事实性幻觉、忠实性幻觉）
  - 检测方法
  - 缓解策略

- [ ] A/B 测试设计
  - 实验设计原则
  - 样本量计算
  - 统计显著性检验
  - 指标选择

### P2 - 一般

- [ ] 基准测试 (Benchmark)
  - 通用 LLM 评测基准
  - Agent 专用评测基准
  - 中文评测基准

---

## 参考资源

- [Evaluating LLM-as-a-Judge](https://arxiv.org/abs/2310.07641)
- [RAGAS: Automated Evaluation of RAG](https://arxiv.org/abs/2309.15217)
- [HELM: Holistic Evaluation of Language Models](https://arxiv.org/abs/2211.09110)

---

## 面试特点

- **工程实践**：关注实际落地中的评估难题
- **权衡思维**：理解不同评估方法的 trade-off
- **数据敏感**：评估数据的质量直接影响结论

---

> 💡 **提示**：评估是 Agent 落地的关键环节，好的评估体系是迭代优化的基础
