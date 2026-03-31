# 模型基础

> AI Agent 面试题 - 大模型底层原理

---

## 模块目标

本模块覆盖大语言模型的底层原理，是理解 Agent 行为的基础。

面试中常被问到：
- "Transformer 的架构是怎样的？"
- "Self-Attention 的计算复杂度是多少？"
- "为什么大模型需要位置编码？"

---

## 待补充知识点

### P0 - 紧急

- [ ] Transformer 架构详解
  - Encoder-Decoder 结构
  - Self-Attention 机制
  - Feed-Forward 网络
  - Layer Normalization
  - Residual Connection

- [ ] 注意力机制详解
  - Self-Attention
  - Cross-Attention
  - Multi-Head Attention
  - Attention 计算复杂度分析

- [ ] 位置编码
  - 绝对位置编码
  - 相对位置编码
  - RoPE (Rotary Position Embedding)
  - 为什么需要位置编码

- [ ] 预训练任务
  - MLM (Masked Language Modeling)
  - CLM (Causal Language Modeling)
  - Span Corruption
  - 预训练 vs 微调

### P1 - 重要

- [ ] Tokenizer 原理
  - BPE (Byte Pair Encoding)
  - WordPiece
  - SentencePiece
  - Token 与字符的关系

- [ ] 模型参数量与计算量
  - FLOPs 计算
  - 显存占用估算
  - 推理速度分析

- [ ] 大模型架构变种
  - GPT 系列 (Decoder-only)
  - BERT 系列 (Encoder-only)
  - T5 系列 (Encoder-Decoder)
  - 各架构的适用场景

---

## 参考资源

- [Attention Is All You Need](https://arxiv.org/abs/1706.03762) - Transformer 原始论文
- [The Illustrated Transformer](https://jalammar.github.io/illustrated-transformer/) - 图解 Transformer
- [Hugging Face NLP Course](https://huggingface.co/learn/nlp-course) - NLP 课程

---

## 面试特点

- **高频考点**：Transformer、Attention 几乎是必考
- **原理深度**：需要理解"为什么"而不仅是"是什么"
- **计算分析**：可能涉及复杂度计算和优化思路

---

> 💡 **提示**：本模块是其他所有模块的前置基础，建议优先学习
