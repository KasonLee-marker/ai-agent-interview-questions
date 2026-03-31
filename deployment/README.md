# 部署运维

> AI Agent 面试题 - 生产环境部署与优化

---

## 模块目标

本模块覆盖大模型和 Agent 系统的生产部署，包括推理优化、服务化架构等。

面试中常被问到：
- "如何优化大模型推理速度？"
- "INT8 量化和 INT4 量化有什么区别？"
- "vLLM 和 TGI 各有什么特点？"

---

## 待补充知识点

### P1 - 重要

- [ ] 模型推理优化
  - KV Cache 机制
  - 连续批处理 (Continuous Batching)
  - 投机解码 (Speculative Decoding)
  - 前缀缓存 (Prefix Caching)

- [ ] 量化技术
  - INT8 量化
  - INT4/GPTQ 量化
  - AWQ 量化
  - 量化的精度损失与速度提升

- [ ] 推理引擎对比
  - vLLM (PagedAttention)
  - TGI (Text Generation Inference)
  - TensorRT-LLM
  - llama.cpp
  - 选型考量

- [ ] Agent 服务化部署
  - 架构设计（同步 vs 异步）
  - 流式输出实现
  - 会话状态管理
  - 多租户隔离

### P2 - 一般

- [ ] 流式输出设计
  - SSE (Server-Sent Events)
  - WebSocket
  - 首 token 延迟优化

- [ ] 负载均衡与扩缩容
  - 请求路由策略
  - 动态扩缩容
  - 成本优化

- [ ] 监控与可观测性
  - 关键指标（延迟、吞吐量、错误率）
  - 日志收集与分析
  - 链路追踪

---

## 参考资源

- [vLLM 论文](https://arxiv.org/abs/2309.06180) - PagedAttention
- [GPTQ 量化](https://arxiv.org/abs/2210.17323)
- [AWQ 量化](https://arxiv.org/abs/2306.00978)
- [TensorRT-LLM 文档](https://github.com/NVIDIA/TensorRT-LLM)

---

## 面试特点

- **性能敏感**：关注延迟、吞吐量、成本
- **工程权衡**：精度 vs 速度 vs 成本的平衡
- **实践经验**：有实际部署经验会是加分项

---

> 💡 **提示**：部署优化是算法工程师向工程能力延伸的关键领域
