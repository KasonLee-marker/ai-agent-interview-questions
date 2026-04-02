# Token 优化技术详解

## 面试问题

**Q: 在 AI Agent 系统中，如何优化 Token 使用以降低成本并提升响应速度？**

---

## 考察点分析

| 维度 | 考察重点 |
|------|----------|
| **成本控制** | Token 计费模型理解、成本优化策略 |
| **技术实现** | 流式输出、Prompt 压缩、上下文管理 |
| **架构设计** | Token 预算、降级策略、缓存机制 |

---

## 详细解答

### 一、Token 流式输出优化

#### 什么是流式输出

流式输出（Streaming Output）是指 LLM 在生成响应时，逐字/逐词返回结果，而非等待完整生成后再返回。

```
非流式：用户请求 → [等待 5s] → 完整响应
流式：   用户请求 → "今天" → "天气" → "很" → "好" → ... → 完成
              ↑ 首 Token 延迟低，用户感知快
```

#### 实现原理

```python
# OpenAI API 流式输出示例
import openai

# 非流式调用（等待完整响应）
def non_streaming_chat(messages):
    response = openai.chat.completions.create(
        model="gpt-4",
        messages=messages
    )
    return response.choices[0].message.content  # 等待全部生成完成

# 流式调用（逐 Token 返回）
def streaming_chat(messages):
    stream = openai.chat.completions.create(
        model="gpt-4",
        messages=messages,
        stream=True  # 启用流式输出
    )
    
    for chunk in stream:
        if chunk.choices[0].delta.content:
            yield chunk.choices[0].delta.content  # 逐字返回
```

#### Server-Sent Events (SSE) 实现

```python
# FastAPI + SSE 实现流式输出
from fastapi import FastAPI
from fastapi.responses import StreamingResponse
import json

app = FastAPI()

async def generate_stream(messages):
    """生成 SSE 流"""
    stream = openai.chat.completions.create(
        model="gpt-4",
        messages=messages,
        stream=True
    )
    
    for chunk in stream:
        content = chunk.choices[0].delta.content
        if content:
            # SSE 格式：data: {...}\n\n
            yield f"data: {json.dumps({'content': content})}\n\n"
    
    # 结束标记
    yield "data: [DONE]\n\n"

@app.post("/chat/stream")
async def chat_stream(request: ChatRequest):
    return StreamingResponse(
        generate_stream(request.messages),
        media_type="text/event-stream"
    )
```

```javascript
// 前端接收 SSE
const eventSource = new EventSource('/chat/stream');

eventSource.onmessage = (event) => {
    if (event.data === '[DONE]') {
        eventSource.close();
        return;
    }
    const data = JSON.parse(event.data);
    appendToUI(data.content);  // 逐字渲染
};

eventSource.onerror = (error) => {
    console.error('SSE 错误:', error);
    // 实现重连逻辑
};
```

#### 流式输出的中断与重连

```python
class ResumableStream:
    """支持断点续传的流式输出"""
    
    def __init__(self):
        self.buffer = []  # 已接收的 Token 缓存
        self.checkpoint = 0  # 检查点
    
    async def stream_with_recovery(self, messages, max_retries=3):
        """带自动重连的流式输出"""
        retries = 0
        
        while retries < max_retries:
            try:
                # 如果已有缓存，从检查点继续
                if self.buffer:
                    # 将已生成内容作为上下文，要求继续生成
                    messages = self._append_context(messages, self.buffer)
                
                stream = self.llm.create_stream(messages)
                
                async for chunk in stream:
                    token = chunk.content
                    self.buffer.append(token)
                    yield token
                    
                    # 每 50 个 Token 更新检查点
                    if len(self.buffer) % 50 == 0:
                        self.checkpoint = len(self.buffer)
                
                return  # 成功完成
                
            except (ConnectionError, TimeoutError) as e:
                retries += 1
                if retries >= max_retries:
                    # 返回已生成的内容 + 错误提示
                    yield self._format_error(self.buffer, e)
                    return
                
                # 指数退避重试
                await asyncio.sleep(2 ** retries)
    
    def _append_context(self, messages, buffer):
        """将已生成内容添加到上下文，请求继续生成"""
        generated = ''.join(buffer)
        messages.append({
            "role": "assistant",
            "content": generated
        })
        messages.append({
            "role": "user",
            "content": "请继续生成剩余内容"
        })
        return messages
```

#### Agent 场景中的流式优化

```python
class StreamingAgent:
    """支持流式输出的 Agent"""
    
    async def run_streaming(self, user_input):
        """流式执行，每个 Thought/Action 都实时返回"""
        context = self.initialize_context(user_input)
        
        # 流式返回思考过程
        async for step in self._reasoning_loop(context):
            yield {
                "type": step.type,  # "thought" | "action" | "observation" | "final"
                "content": step.content
            }
    
    async def _reasoning_loop(self, context):
        """ReAct 循环的流式实现"""
        while not context.is_complete():
            # 流式生成 Thought
            thought = ""
            async for token in self.llm.stream_think(context):
                thought += token
                yield Step(type="thought", content=token, partial=True)
            
            yield Step(type="thought", content=thought, partial=False)
            
            # 流式生成 Action
            action = ""
            async for token in self.llm.stream_action(context, thought):
                action += token
                yield Step(type="action", content=token, partial=True)
            
            yield Step(type="action", content=action, partial=False)
            
            # 执行动作
            observation = await self.execute_action(action)
            yield Step(type="observation", content=observation)
            
            context.add_step(thought, action, observation)
```

---

### 二、Rate Limit 控制

#### Rate Limit 类型

| 类型 | 描述 | 典型值 |
|------|------|--------|
| **RPM** | Requests Per Minute | 60-10000 |
| **TPM** | Tokens Per Minute | 40K-2M |
| **RPD** | Requests Per Day | 10000-无限制 |
| **并发** | 同时请求数 | 1-100 |

#### Token Bucket 算法实现

```python
import time
import asyncio
from dataclasses import dataclass
from typing import Optional

@dataclass
class RateLimitConfig:
    rpm: int = 60          # 每分钟请求数
    tpm: int = 40000       # 每分钟 Token 数
    burst_size: int = 10   # 突发请求容量

class TokenBucket:
    """Token Bucket 限流器"""
    
    def __init__(self, rate: float, capacity: int):
        """
        rate: 每秒产生 Token 数
        capacity: 桶容量（最大突发）
        """
        self.rate = rate
        self.capacity = capacity
        self.tokens = capacity
        self.last_update = time.time()
        self._lock = asyncio.Lock()
    
    async def acquire(self, tokens: int = 1) -> float:
        """获取 Token，返回等待时间"""
        async with self._lock:
            now = time.time()
            elapsed = now - self.last_update
            
            # 补充 Token
            self.tokens = min(
                self.capacity,
                self.tokens + elapsed * self.rate
            )
            self.last_update = now
            
            if tokens <= self.tokens:
                self.tokens -= tokens
                return 0
            
            # 计算需要等待的时间
            wait_time = (tokens - self.tokens) / self.rate
            self.tokens = 0
            return wait_time

class LLMRateLimiter:
    """LLM API 限流器"""
    
    def __init__(self, config: RateLimitConfig):
        self.config = config
        # RPM 限流桶
        self.request_bucket = TokenBucket(
            rate=config.rpm / 60.0,
            capacity=config.burst_size
        )
        # TPM 限流桶
        self.token_bucket = TokenBucket(
            rate=config.tpm / 60.0,
            capacity=config.tpm
        )
    
    async def acquire(self, expected_tokens: int = 1000) -> None:
        """获取调用许可"""
        # 检查请求限流
        wait = await self.request_bucket.acquire(1)
        if wait > 0:
            await asyncio.sleep(wait)
        
        # 检查 Token 限流
        wait = await self.token_bucket.acquire(expected_tokens)
        if wait > 0:
            await asyncio.sleep(wait)

class AdaptiveRateLimiter:
    """自适应限流器，根据 API 响应动态调整"""
    
    def __init__(self, initial_rpm: int = 60):
        self.rpm_limit = initial_rpm
        self.request_times = []  # 记录请求时间
        self.error_count = 0
        self.success_count = 0
    
    async def call_with_limit(self, api_func, *args, **kwargs):
        """带限流的 API 调用"""
        # 检查是否需要等待
        await self._wait_if_needed()
        
        try:
            start = time.time()
            result = await api_func(*args, **kwargs)
            elapsed = time.time() - start
            
            # 记录成功
            self._record_success(elapsed)
            return result
            
        except RateLimitError as e:
            # 被限流，调整速率
            self._handle_rate_limit_error(e)
            # 重试
            await asyncio.sleep(e.retry_after)
            return await self.call_with_limit(api_func, *args, **kwargs)
        
        except Exception as e:
            self._record_error()
            raise
    
    def _handle_rate_limit_error(self, error: RateLimitError):
        """处理限流错误，降低速率"""
        self.rpm_limit = max(1, int(self.rpm_limit * 0.8))
        self.error_count += 1
        
    def _record_success(self, elapsed: float):
        """记录成功，考虑提速"""
        self.success_count += 1
        # 连续成功 10 次，尝试提速
        if self.success_count >= 10:
            self.rpm_limit = min(self.rpm_limit + 5, 1000)
            self.success_count = 0
    
    async def _wait_if_needed(self):
        """根据当前速率决定是否等待"""
        now = time.time()
        window_start = now - 60
        
        # 清理过期记录
        self.request_times = [t for t in self.request_times if t > window_start]
        
        # 检查是否超过限流
        if len(self.request_times) >= self.rpm_limit:
            # 需要等待
            oldest = self.request_times[0]
            wait_time = 60 - (now - oldest) + 0.1
            await asyncio.sleep(max(0, wait_time))
```

---

### 三、Prompt 压缩技术

#### 压缩策略对比

| 策略 | 适用场景 | 优点 | 缺点 |
|------|----------|------|------|
| **截断** | 上下文过长 | 简单快速 | 可能丢失关键信息 |
| **摘要** | 长对话历史 | 保留语义 | 需要额外 LLM 调用 |
| **向量化检索** | 大量文档 | 精准召回 | 需要预处理和存储 |
| **选择性保留** | 结构化上下文 | 保留关键字段 | 需要领域知识 |

#### 上下文截断策略

```python
from typing import List, Dict
import tiktoken

class ContextCompressor:
    """上下文压缩器"""
    
    def __init__(self, model: str = "gpt-4", max_tokens: int = 4000):
        self.encoder = tiktoken.encoding_for_model(model)
        self.max_tokens = max_tokens
        self.reserve_tokens = 1000  # 为输出预留
    
    def truncate_messages(
        self,
        messages: List[Dict[str, str]],
        strategy: str = "oldest_first"
    ) -> List[Dict[str, str]]:
        """
        截断消息列表至 Token 限制内
        
        strategies:
        - oldest_first: 移除最早的消息（保留最新上下文）
        - summary_oldest: 摘要最旧消息，保留最新
        - priority_based: 按优先级保留
        """
        available = self.max_tokens - self.reserve_tokens
        
        if strategy == "oldest_first":
            return self._truncate_oldest(messages, available)
        elif strategy == "summary_oldest":
            return self._summary_oldest(messages, available)
        elif strategy == "priority_based":
            return self._truncate_by_priority(messages, available)
        else:
            raise ValueError(f"Unknown strategy: {strategy}")
    
    def _truncate_oldest(self, messages: List[Dict], max_tokens: int) -> List[Dict]:
        """从最早的消息开始移除"""
        # 始终保留 system 消息
        system_msgs = [m for m in messages if m.get("role") == "system"]
        other_msgs = [m for m in messages if m.get("role") != "system"]
        
        system_tokens = sum(
            len(self.encoder.encode(m["content"]))
            for m in system_msgs
        )
        
        available = max_tokens - system_tokens
        result = []
        current_tokens = 0
        
        # 从最新消息开始添加
        for msg in reversed(other_msgs):
            msg_tokens = len(self.encoder.encode(msg["content"]))
            if current_tokens + msg_tokens <= available:
                result.insert(0, msg)
                current_tokens += msg_tokens
            else:
                break
        
        return system_msgs + result
    
    def _summary_oldest(self, messages: List[Dict], max_tokens: int) -> List[Dict]:
        """摘要旧消息，保留最新"""
        if len(messages) <= 5:
            return self._truncate_oldest(messages, max_tokens)
        
        # 保留最新的 5 条消息
        recent = messages[-5:]
        old = messages[:-5]
        
        # 摘要旧消息
        summary = self._generate_summary(old)
        summary_msg = {
            "role": "system",
            "content": f"[历史对话摘要] {summary}"
        }
        
        return [summary_msg] + recent
    
    def _generate_summary(self, messages: List[Dict]) -> str:
        """生成消息摘要（使用轻量级模型或规则）"""
        # 方案 1: 使用轻量级模型
        # return call_light_llm(f"请摘要以下对话: {messages}")
        
        # 方案 2: 提取关键信息
        key_points = []
        for msg in messages:
            if msg.get("role") == "user":
                # 提取用户意图
                key_points.append(f"用户询问: {msg['content'][:50]}...")
            elif msg.get("role") == "assistant":
                # 提取关键回复
                if "根据" in msg["content"] or "结果" in msg["content"]:
                    key_points.append(f"Agent 回复: {msg['content'][:50]}...")
        
        return "; ".join(key_points[-5:])  # 保留最近 5 个要点
```

#### Agent 记忆压缩

```python
class AgentMemoryCompressor:
    """Agent 记忆的智能压缩"""
    
    def __init__(self, llm_client):
        self.llm = llm_client
        self.compression_threshold = 10  # 超过 10 步触发压缩
    
    def compress_steps(self, steps: List[Step]) -> CompressedMemory:
        """压缩执行步骤为结构化记忆"""
        if len(steps) <= self.compression_threshold:
            return CompressedMemory(steps=steps)
        
        # 分段压缩
        chunks = self._chunk_steps(steps, chunk_size=5)
        compressed_chunks = []
        
        for chunk in chunks:
            compressed = self._compress_chunk(chunk)
            compressed_chunks.append(compressed)
        
        return CompressedMemory(
            summary=self._generate_overall_summary(compressed_chunks),
            key_facts=self._extract_key_facts(steps),
            recent_steps=steps[-3:]  # 保留最近 3 步详细记录
        )
    
    def _compress_chunk(self, chunk: List[Step]) -> ChunkSummary:
        """压缩一组步骤"""
        # 构建压缩 Prompt
        prompt = f"""
        请将以下 Agent 执行步骤压缩为关键信息摘要：
        
        {self._format_steps(chunk)}
        
        输出格式：
        - 完成的任务: 
        - 获得的关键信息:
        - 未解决的问题:
        """
        
        summary = self.llm.generate(prompt, max_tokens=200)
        return ChunkSummary(
            original_steps=len(chunk),
            summary=summary,
            tokens_saved=self._calculate_savings(chunk, summary)
        )
    
    def _extract_key_facts(self, steps: List[Step]) -> List[str]:
        """提取关键事实（用于快速检索）"""
        facts = []
        for step in steps:
            if step.observation:
                # 提取结构化信息
                facts.extend(self._extract_from_observation(step.observation))
        return facts
```

#### Prompt 模板优化

```python
class PromptOptimizer:
    """Prompt 模板优化器"""
    
    def __init__(self):
        self.templates = {}
    
    def optimize_template(self, template: str) -> str:
        """
        优化 Prompt 模板，减少 Token 使用
        
        优化策略：
        1. 移除冗余空白和换行
        2. 使用缩写和符号
        3. 结构化表示
        4. 移除示例中的冗余内容
        """
        optimized = template
        
        # 1. 压缩空白
        optimized = self._compress_whitespace(optimized)
        
        # 2. 使用结构化格式
        optimized = self._use_structured_format(optimized)
        
        # 3. 缩写常用短语
        optimized = self._apply_abbreviations(optimized)
        
        return optimized
    
    def _compress_whitespace(self, text: str) -> str:
        """压缩多余空白"""
        import re
        # 多个空格/换行压缩为单个
        text = re.sub(r'\n\s*\n', '\n', text)
        text = re.sub(r'[ \t]+', ' ', text)
        return text.strip()
    
    def _use_structured_format(self, text: str) -> str:
        """使用结构化格式减少 Token"""
        # JSON/YAML 格式通常比自然语言更省 Token
        # 但需权衡可读性
        return text
    
    def _apply_abbreviations(self, text: str) -> str:
        """应用缩写"""
        abbreviations = {
            "Artificial Intelligence": "AI",
            "Large Language Model": "LLM",
            "Function Calling": "FC",
            "if and only if": "iff",
            "with respect to": "w.r.t.",
            "for example": "e.g.",
            "that is": "i.e.",
        }
        for full, abbr in abbreviations.items():
            text = text.replace(full, abbr)
        return text

# 示例：优化前后对比
ORIGINAL_TEMPLATE = """
You are an AI assistant. Your task is to help users with their questions.

When responding, please follow these guidelines:
1. Be helpful and accurate
2. If you don't know something, say so
3. Use the provided tools when necessary

Here are some examples of how to respond:

Example 1:
User: What is the weather?
Assistant: I'll check the weather for you.
[uses weather tool]
The weather is sunny today.

Example 2:
...
"""

OPTIMIZED_TEMPLATE = """You are an AI assistant. Help users accurately. Admit unknowns. Use tools when needed.

Examples:
Q: Weather?
A: [weather_tool] → Sunny today.
"""
```

---

### 四、Token 预算与成本预估

#### Token 预算分配

```python
@dataclass
class TokenBudget:
    """Token 预算分配"""
    total: int = 8000
    system: int = 500      # 系统 Prompt
        history: int = 3000    # 对话历史
    context: int = 2000    # RAG/工具结果
    reasoning: int = 1000  # Agent 思考过程
    output: int = 1500     # 输出预留
    
    @property
    def available_for_input(self) -> int:
        return self.total - self.system - self.output

class BudgetManager:
    """Token 预算管理器"""
    
    def __init__(self, budget: TokenBudget):
        self.budget = budget
        self.usage = {
            "system": 0,
            "history": 0,
            "context": 0,
            "reasoning": 0,
            "output": 0
        }
    
    def allocate_for_rag(self, documents: List[Document]) -> List[Document]:
        """为 RAG 分配 Token 预算"""
        available = self.budget.context - self.usage["context"]
        
        selected = []
        total_tokens = 0
        
        for doc in documents:
            doc_tokens = doc.token_count
            if total_tokens + doc_tokens <= available:
                selected.append(doc)
                total_tokens += doc_tokens
            else:
                # 尝试截断文档
                truncated = doc.truncate_to(available - total_tokens)
                if truncated:
                    selected.append(truncated)
                break
        
        self.usage["context"] += total_tokens
        return selected
    
    def compress_if_needed(self, messages: List[Dict]) -> List[Dict]:
        """如果超出预算，压缩消息"""
        total = sum(len(m["content"].split()) for m in messages)  # 粗略估算
        
        if total > self.budget.available_for_input:
            compressor = ContextCompressor(max_tokens=self.budget.history)
            return compressor.truncate_messages(
                messages,
                strategy="summary_oldest"
            )
        
        return messages
```

#### 成本预估与告警

```python
class CostEstimator:
    """LLM 调用成本预估"""
    
    PRICING = {
        "gpt-4": {"input": 0.03, "output": 0.06},      # $/1K tokens
        "gpt-3.5": {"input": 0.0015, "output": 0.002},
        "claude-3": {"input": 0.008, "output": 0.024},
    }
    
    def __init__(self, model: str):
        self.model = model
        self.daily_budget = 10.0  # $10/天
        self.daily_usage = 0.0
    
    def estimate_cost(
        self,
        input_tokens: int,
        expected_output_tokens: int
    ) -> Dict:
        """预估单次调用成本"""
        pricing = self.PRICING.get(self.model, self.PRICING["gpt-3.5"])
        
        input_cost = (input_tokens / 1000) * pricing["input"]
        output_cost = (expected_output_tokens / 1000) * pricing["output"]
        
        return {
            "input_tokens": input_tokens,
            "output_tokens": expected_output_tokens,
            "input_cost": input_cost,
            "output_cost": output_cost,
            "total_cost": input_cost + output_cost,
            "daily_remaining": self.daily_budget - self.daily_usage
        }
    
    def should_use_cheaper_model(self, estimated_cost: float) -> bool:
        """判断是否应降级到更便宜的模型"""
        if self.daily_usage + estimated_cost > self.daily_budget * 0.9:
            return True
        return False
    
    def get_fallback_model(self) -> str:
        """获取降级模型"""
        fallback_map = {
            "gpt-4": "gpt-3.5",
            "claude-3": "claude-3-haiku"
        }
        return fallback_map.get(self.model, "gpt-3.5")
```

---

### 五、缓存策略

#### 多级缓存

```python
from functools import lru_cache
import hashlib
import json

class LLMCache:
    """LLM 调用多级缓存"""
    
    def __init__(self):
        self.memory_cache = {}  # L1: 内存缓存
        self.redis_cache = None  # L2: Redis（可选）
        self.cache_hits = 0
        self.cache_misses = 0
    
    def _get_cache_key(self, messages: List[Dict], **kwargs) -> str:
        """生成缓存 Key"""
        # 对消息和参数哈希
        content = json.dumps({"messages": messages, "params": kwargs}, sort_keys=True)
        return hashlib.sha256(content.encode()).hexdigest()
    
    async def get_or_call(
        self,
        llm_func,
        messages: List[Dict],
        use_cache: bool = True,
        cache_ttl: int = 3600,
        **kwargs
    ):
        """带缓存的 LLM 调用"""
        if not use_cache:
            return await llm_func(messages, **kwargs)
        
        key = self._get_cache_key(messages, **kwargs)
        
        # L1 检查
        if key in self.memory_cache:
            self.cache_hits += 1
            return self.memory_cache[key]
        
        # L2 检查
        if self.redis_cache:
            cached = await self.redis_cache.get(key)
            if cached:
                self.cache_hits += 1
                result = json.loads(cached)
                self.memory_cache[key] = result  # 回填 L1
                return result
        
        # 缓存未命中
        self.cache_misses += 1
        result = await llm_func(messages, **kwargs)
        
        # 写入缓存
        self.memory_cache[key] = result
        if self.redis_cache:
            await self.redis_cache.setex(key, cache_ttl, json.dumps(result))
        
        return result
    
    def get_stats(self) -> Dict:
        """获取缓存统计"""
        total = self.cache_hits + self.cache_misses
        hit_rate = self.cache_hits / total if total > 0 else 0
        return {
            "hits": self.cache_hits,
            "misses": self.cache_misses,
            "hit_rate": f"{hit_rate:.2%}",
            "memory_size": len(self.memory_cache)
        }

# 语义缓存：相似查询复用结果
class SemanticCache:
    """基于向量相似度的缓存"""
    
    def __init__(self, embedding_model, similarity_threshold: float = 0.95):
        self.embeddings = []  # 查询向量
        self.results = []     # 对应结果
        self.embedding_model = embedding_model
        self.threshold = similarity_threshold
    
    async def get_similar(self, query: str) -> Optional[str]:
        """查找相似查询的缓存结果"""
        if not self.embeddings:
            return None
        
        query_vec = await self.embedding_model.embed(query)
        
        # 计算相似度
        best_match = None
        best_score = 0
        
        for i, emb in enumerate(self.embeddings):
            similarity = self._cosine_similarity(query_vec, emb)
            if similarity > self.threshold and similarity > best_score:
                best_score = similarity
                best_match = self.results[i]
        
        return best_match
    
    def _cosine_similarity(self, a, b) -> float:
        import numpy as np
        return np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))
```

---

## 面试重点总结

| 问题类型 | 回答要点 |
|----------|----------|
| **流式输出原理** | SSE 协议、逐 Token 返回、首 Token 延迟优化 |
| **Rate Limit 实现** | Token Bucket、自适应限流、指数退避重试 |
| **Prompt 压缩** | 截断策略、摘要生成、向量化检索、结构化表示 |
| **Token 预算** | 分层预算、成本预估、降级策略 |
| **缓存设计** | 多级缓存、语义缓存、命中率优化 |

## 关键数字记忆

- **GPT-4**: 输入 $0.03/1K tokens，输出 $0.06/1K tokens
- **GPT-3.5**: 输入 $0.0015/1K tokens，输出 $0.002/1K tokens
- **典型上下文窗口**: 4K-128K tokens
- **流式首 Token 延迟**: 100-500ms（vs 非流式 2-10s）
- **Rate Limit 典型值**: RPM 60-10000，TPM 40K-2M

---

## 延伸阅读

- [OpenAI Rate Limits](https://platform.openai.com/docs/guides/rate-limits)
- [Token 计算最佳实践](https://platform.openai.com/tokenizer)
- [记忆系统优化](../agent/memory-system.md)
- [RAG 检索优化](../retrieval/README.md)