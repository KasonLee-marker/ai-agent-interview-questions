# Chain-of-Thought / Tree-of-Thought

## Chain-of-Thought (CoT)

### 概念
通过提示让模型逐步推理，展示思考过程。

### 经典 Prompt
```
Let's think step by step.
```

### 变体
- **Zero-shot CoT**: 直接加提示词
- **Few-shot CoT**: 提供推理示例
- **Self-Consistency**: 多次采样取多数

## Tree-of-Thought (ToT)

### 概念
在 CoT 基础上探索多条推理路径，像树一样分支搜索。

### 流程
```
         Root
       /   |   \
     T1    T2   T3    (Thoughts)
    /|\   /|\   /|\
   ...  ...  ...      (继续展开)
```

### 搜索策略
- **BFS**: 广度优先，每层评估
- **DFS**: 深度优先，逐条探索
- **Beam Search**: 保留 Top-K 路径

## 适用场景

| 范式 | 适用场景 |
|------|----------|
| CoT | 数学计算、逻辑推理 |
| ToT | 需要探索多种方案的问题 |

## 面试题

1. **CoT 为什么能提升模型推理能力？**
   - 分解复杂问题
   - 提供中间检查点
   - 利用更多计算资源

2. **ToT 的评估函数如何设计？**
   - 基于规则的评分
   - 另一个 LLM 评估
   - 环境反馈

3. **CoT/ToT 和 ReAct 如何结合使用？**
   - ReAct 负责工具调用
   - CoT/ToT 负责内部推理
