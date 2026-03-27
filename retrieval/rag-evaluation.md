# RAG 系统评测与优化

## 一、RAG 系统如何评测？

### 1.1 评测层次

```mermaid
flowchart TB
    subgraph Pipeline["RAG 评测流水线 (Evaluation Pipeline)"]
        direction TB
        Query["Query\n(问题)"] --> Retrieve["Retrieve\n(检索)"]
        Retrieve --> Generate["Generate\n(生成)"]

        Query --> Ref["标准答案\nReference"]
        Retrieve --> Ctx["检索文档\nContexts"]
        Generate --> Ans["生成答案\nAnswer"]

        Ref --> Metrics["评测指标\nMetrics Layer"]
        Ctx --> Metrics
        Ans --> Metrics
    end
```

### 1.2 模块级 vs 端到端评测

| 评测类型 | 关注点 | 优点 | 缺点 |
|---------|--------|------|------|
| **模块级** | 各组件独立性能 | 定位问题快、可针对性优化 | 无法反映整体效果 |
| **端到端** | 最终问答质量 | 贴近真实场景、综合评估 | 问题归因困难 |

**最佳实践**：两者结合，模块级用于日常迭代优化，端到端用于发布前验证。

---

## 二、评测维度与常见指标

### 2.1 检索维度指标

```mermaid
mindmap
  root(RAG 评测维度全景图)
    检索质量 Retrieval Quality
      Recall@K 召回率
      Precision 精确率
      MRR 平均倒数排名
      NDCG 归一化折损
      Hit Rate 命中率
      MAP 平均精度均值
      Coverage 覆盖率
    生成质量 Generation Quality
      BLEU N-gram匹配
      ROUGE 召回导向
      METEOR 同义词友好
      BERTScore 语义相似度
      GPT-4评分 LLM-as-judge
      人工评估 HumanEval
      困惑度 Perplexity
    忠实度 Faithfulness
      FactScore 事实评分
      幻觉检测 Hallucination
      引用准确性 Attribution
    答案相关性 Answer Relevance
      问题-答案相关性
      上下文-答案相关性
      答案完整性 Completeness
```

#### 核心指标详解

| 指标 | 公式/定义 | 适用场景 | 理想值 |
|------|----------|---------|--------|
| **Recall@K** | 前K个结果中相关文档数 / 总相关文档数 | 确保不遗漏关键信息 | > 0.8 |
| **Precision@K** | 前K个结果中相关文档数 / K | 确保返回结果质量 | > 0.6 |
| **MRR** | 第一个相关文档排名的倒数平均值 | 关注首位结果质量 | > 0.5 |
| **NDCG@K** | 考虑文档相关度分级的归一化折损累积增益 | 需要分级评估时 | > 0.7 |
| **Hit Rate@K** | 至少有一个相关文档在前K中的比例 | 快速评估召回能力 | > 0.9 |

**指标计算公式**：

```
检索指标 (Retrieval Metrics)

Recall@K    = |{相关文档} ∩ {Top-K检索结果}| / |{相关文档}|

Precision@K = |{相关文档} ∩ {Top-K检索结果}| / K

MRR = (1/|Q|) × Σ(1/rank_i)
      其中 rank_i 是第i个查询的第一个相关文档的排名

NDCG@K = DCG@K / IDCG@K
DCG@K  = Σ(rel_i / log₂(i+1))  i从1到K
```

**Python 计算示例**：

```python
# Recall@K 计算
def recall_at_k(retrieved_docs, relevant_docs, k=5):
    """
    retrieved_docs: 检索返回的文档列表（按相关性排序）
    relevant_docs: 所有相关文档集合
    """
    retrieved_k = set(retrieved_docs[:k])
    relevant = set(relevant_docs)
    return len(retrieved_k & relevant) / len(relevant)

# 示例
retrieved = ["doc_A", "doc_B", "doc_C", "doc_D", "doc_E"]
relevant = ["doc_A", "doc_C", "doc_F"]  # 3个相关文档
recall_at_5 = 2 / 3  # doc_A 和 doc_C 被召回
```

### 2.2 生成维度指标

```
生成指标 (Generation Metrics)

BLEU = BP × exp(Σ w_n × log p_n)
       其中 p_n 是n-gram精确率，BP是简短惩罚

ROUGE-N = Σ(S∈References) Σ(gram_n∈S) Count_match(gram_n)
          ─────────────────────────────────────────────
          Σ(S∈References) Σ(gram_n∈S) Count(gram_n)

BERTScore F1 = 2 × (P × R) / (P + R)
               基于token embeddings的余弦相似度
```

#### 生成指标对比

| 指标 | 类型 | 优点 | 缺点 |
|------|------|------|------|
| **BLEU** | N-gram匹配 | 计算快、标准化 | 不考虑语义 |
| **ROUGE** | 召回导向 | 适合长文本 | 忽略流畅度 |
| **BERTScore** | 语义嵌入 | 捕捉语义相似 | 计算成本高 |
| **Faithfulness** | 事实一致性 | 检测幻觉关键 | 需要参考文本 |

**BERTScore 计算原理**：

```python
from bert_score import score

# 计算 BERTScore
P, R, F1 = score(candidates, references, lang='zh', device='cuda')
# P: Precision, R: Recall, F1: 综合得分
```

---

## 三、评测数据集内容

### 3.1 标准数据集组成

一个完整的 RAG 评测数据集通常包含：

| 组件 | 说明 | 示例 |
|------|------|------|
| **Queries** | 用户问题集合 | "RAG 系统如何评测？" |
| **Contexts** | 检索用的文档库 | 技术文档、论文、FAQ |
| **References** | 标准答案/参考答案 | 人工标注的理想回答 |
| **Relevant Docs** | 每个问题对应的相关文档 | doc_id 列表 |
| **Metadata** | 问题类型、难度、领域标签 | domain=tech, difficulty=hard |

### 3.2 构建高质量评测数据的方法

```mermaid
flowchart TB
    S1["1. 问题收集"] --> S2["2. 文档标注"]
    S2 --> S3["3. 答案生成"]
    S3 --> S4["4. 质量验证"]

    S1 --> S1a["真实用户查询日志"]
    S1 --> S1b["人工设计覆盖各场景"]
    S1 --> S1c["难度分级：简单 / 中等 / 困难"]

    S2 --> S2a["人工标注相关文档（黄金标准）"]
    S2 --> S2b["多轮交叉验证"]
    S2 --> S2c["处理边界情况（无答案/多答案）"]

    S3 --> S3a["基于标注文档生成参考答案"]
    S3 --> S3b["人工审核与修正"]
    S3 --> S3c["确保答案忠实于文档"]

    S4 --> S4a["计算标注者间一致性 Cohen's Kappa"]
    S4 --> S4b["抽样人工复核"]
    S4 --> S4c["定期更新（防止数据老化）"]
```

**关键原则**：
- **覆盖度**：涵盖各种查询类型（事实型、推理型、比较型）
- **真实性**：问题应贴近真实用户场景
- **可验证性**：答案必须能在文档中找到依据
- **难度分布**：简单:中等:困难 ≈ 3:5:2

---

## 四、RAG 优化策略

### 4.1 提升检索相关度的方法

```mermaid
flowchart TB
    Root["检索相关度优化策略"] --> QE["查询端优化\nQuery Enhancement"]
    Root --> IO["索引端优化\nIndex Optimization"]
    Root --> MO["模型端优化\nModel Optimization"]

    QE --> QE1["查询扩展：同义词、相关概念补充"]
    QE --> QE2["查询重写：口语化问题转结构化查询"]
    QE --> QE3["HyDE：生成假设文档辅助检索"]
    QE --> QE4["意图识别：事实/推理/比较查询路由"]

    IO --> IO1["分块策略：按语义/段落/固定长度切分"]
    IO --> IO2["粒度控制：Parent-Child 索引结构"]
    IO --> IO3["元数据增强：标题、摘要、关键词标签"]
    IO --> IO4["多模态索引：文本+表格+图片联合索引"]

    MO --> MO1["Embedding 微调：领域适配训练"]
    MO --> MO2["混合检索：BM25+向量检索融合"]
    MO --> MO3["Re-ranker：精排模型二次排序"]
    MO --> MO4["查询分类：不同问题类型路由不同策略"]
```

**具体优化手段**：

| 优化点 | 方法 | 效果 |
|--------|------|------|
| 查询扩展 | 使用 LLM 生成同义词、相关词 | Recall ↑ 10-15% |
| HyDE | 生成假设答案文档再检索 | 语义匹配更准确 |
| 混合检索 | BM25 权重 0.3 + 向量 0.7 | 综合效果最优 |
| Re-ranker | Cross-encoder 精排 | Precision ↑ 20%+ |
| 分块优化 | 256-512 tokens 段落 | 平衡粒度与上下文 |

### 4.2 优化回答效果的思路

**优化阶段划分**：

```mermaid
flowchart LR
    subgraph Ret["检索阶段优化 (Retrieval Phase)"]
        R1["提升召回率"]
        R2["提高精确率"]
        R3["排序质量"]
        R4["上下文筛选"]
    end
    subgraph Gen["生成阶段优化 (Generation Phase)"]
        G1["上下文压缩"]
        G2["提示词优化"]
        G3["模型微调"]
        G4["后处理修正"]
    end
    Ret --> Gen
    Note["关键原则：检索是上限，生成是体验\n优先优化检索，再优化生成"]
    Gen --> Note

    style Note fill:#fff3e0
```

**生成阶段具体优化**：

| 策略 | 方法 | 适用场景 |
|------|------|----------|
| 上下文压缩 | 使用 LLM 提取关键片段 | 检索文档过长 |
| 提示词工程 | Few-shot + 结构化指令 | 答案格式要求严格 |
| 引用生成 | 要求模型标注信息来源 | 需要可验证性 |
| 模型微调 | 领域数据 SFT | 特定领域专业回答 |
| 后处理过滤 | 事实一致性检查 | 幻觉敏感场景 |

### 4.3 验证优化有效性

```mermaid
flowchart TB
    S1["1. 基线建立\n记录当前系统各指标，确保测试集稳定不变"]
    --> S2["2. 单变量实验\n每次只改一个优化点，保持其他条件一致"]
    --> S3["3. 指标对比\n检索指标 + 生成指标 + 忠实度 + 人工评估"]
    --> S4["4. 显著性检验\nt-test / bootstrap，p-value < 0.05 认为显著"]
    --> S5["5. 线上验证\n小流量灰度发布，监控用户满意度与点击率"]
```

**关键验证原则**：
- **指标全面**：不仅看自动指标，还要人工评估
- **统计显著**：确保提升不是随机波动
- **端到端验证**：模块级提升不一定带来整体提升
- **用户反馈**：最终看真实用户满意度

---

## 五、面试题速查表

### 5.1 RAG 评测与数据集

| 问题 | 核心要点 |
|------|----------|
| RAG 系统如何评测？ | 模块级 + 端到端，检索指标 + 生成指标 |
| 评测维度有哪些？ | 检索质量、生成质量、忠实度、答案相关性 |
| 常见指标有哪些？ | Recall@K、Precision@K、MRR、NDCG、BLEU、ROUGE、BERTScore |
| 评测数据集包括什么？ | Queries、Contexts、References、Relevant Docs、Metadata |
| 如何构建高质量数据？ | 真实问题收集、人工标注、交叉验证、质量检查 |

### 5.2 RAG 优化与效果提升

| 问题 | 核心要点 |
|------|----------|
| 如何提升相关度？ | 查询扩展、HyDE、混合检索、Re-ranker、Embedding 微调 |
| 优化回答效果的思路？ | 检索阶段优化（召回/排序）+ 生成阶段优化（提示/压缩/微调） |
| 优化哪个阶段？ | 优先检索阶段（决定上限），再生成阶段（决定体验） |
| 如何验证优化有效？ | A/B 测试、指标对比、显著性检验、线上灰度 |