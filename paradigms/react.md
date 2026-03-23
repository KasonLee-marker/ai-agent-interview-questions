# ReAct 范式

## 概念

ReAct (Reasoning + Acting) 是目前最流行的 Agent 开发范式，核心思想是让模型交替进行"思考"和"行动"，形成推理链。

## 执行流程

```
Thought → Action → Observation → Thought → ... → Final Answer
```

## 特点

- **显式推理**: 展示模型的思考过程
- **工具调用**: 通过 Action 调用外部工具
- **可解释性强**: 每一步都有清晰的逻辑
- **适合多步决策**: 复杂任务可以分解执行

## 典型应用场景

- 需要多步查询的任务（如：先搜索再计算）
- 需要调用外部 API 的任务
- 需要验证中间结果的任务

## 面试题

1. **ReAct 和传统的 Chain-of-Thought 有什么区别？**
   - CoT 只关注推理，ReAct 增加了 Action 能力
   - ReAct 可以与外部环境交互

2. **ReAct 的优缺点是什么？**
   - 优点：可解释性强、能处理复杂多步任务
   - 缺点：每一步都需要 LLM 调用，成本较高

3. **如何设计一个好的 ReAct Prompt？**
   - 明确 Thought/Action/Observation 格式
   - 提供工具描述和示例
   - 设置终止条件
