# Agent vs API 转发器：本质区别与设计要点

## 面试问题

**Q: 如何区分一个系统是真正的 AI Agent，还是仅仅是一个 API 转发器（API Relayer）？**

---

## 考察点分析

| 维度 | 考察重点 |
|------|----------|
| **概念理解** | 是否理解 Agent 的自主性本质 |
| **架构设计** | 能否识别伪 Agent 系统的特征 |
| **工程实践** | 如何设计一个真正的 Agent 系统 |

---

## 详细解答

### 一、两者的本质区别

#### API 转发器（API Relayer）

```
用户输入 → [意图匹配] → 固定 API 调用 → 返回结果
              ↓
         预定义规则/模板
```

**核心特征：**
- **被动响应**：等待用户触发，无自主行为
- **固定映射**：输入与 API 调用是预定义的对应关系
- **无状态/短状态**：每次请求独立，不维护长期上下文
- **确定性执行**：相同输入必然产生相同的 API 调用序列
- **无决策能力**：无法处理未预定义的场景

**典型例子：**
- 简单的 ChatGPT Plugin（仅做参数提取和 API 转发）
- 基于规则意图识别的客服机器人
- 仅做 SQL 生成和执行的 Text-to-SQL 工具

#### 真正的 AI Agent

```
用户输入 → [理解目标] → 任务规划 → 工具选择 → 执行 → 观察 → 判断 → ...
                              ↑                              ↓
                              └──────── 记忆/状态更新 ←──────┘
```

**核心特征：**
- **目标驱动**：理解用户目标而非仅匹配指令
- **自主决策**：能决定何时、使用何种工具、如何组合
- **状态维护**：维护长期记忆和当前任务状态
- **迭代执行**：根据中间结果动态调整策略
- **容错处理**：工具调用失败时能自主重试或替代

---

### 二、如何判断是真正的 Agent 还是 API 转发器

#### 判断清单

| 检验项 | API 转发器 | 真正的 Agent |
|--------|-----------|-------------|
| **多步骤任务** | 只能执行单步或预定义多步 | 能自主分解并执行多步骤任务 |
| **工具组合** | 单次调用一个固定 API | 能组合多个工具，使用上一步结果作为下一步输入 |
| **失败处理** | 直接报错或返回固定话术 | 能分析失败原因，尝试替代方案 |
| **上下文理解** | 仅提取关键词/参数 | 理解上下文，处理指代和隐含信息 |
| **主动行为** | 完全被动 | 可主动澄清、确认、或执行后台任务 |
| **学习/适应** | 无 | 能从历史交互中学习和优化 |

#### 快速测试方法

**测试 1：复杂任务分解**
```
用户：帮我订一张下周去上海的机票，要早上出发，
      到了之后帮我查一下那附近有什么好吃的，
      然后帮我预订一家评分高的酒店
```
- ❌ API 转发器：只能识别"订机票"或报错"不支持多任务"
- ✅ Agent：分解为 1)查机票→2)查美食→3)订酒店，按顺序执行

**测试 2：失败恢复**
```
用户：查一下今天的天气
[天气 API 返回 503 错误]
```
- ❌ API 转发器：返回"天气服务不可用"
- ✅ Agent：尝试备用天气源，或询问用户具体城市

**测试 3：上下文推理**
```
用户：北京今天多少度？
Agent：北京今天气温 25°C
用户：那明天呢？
```
- ❌ API 转发器：无法理解"那"指代北京
- ✅ Agent：继承上下文，查询北京明天天气

---

### 三、Agent 的核心特征详解

#### 1. 自主决策（Autonomous Decision Making）

```python
# API 转发器：固定映射
def handle_query(user_input):
    intent = classify_intent(user_input)  # 分类到预定义意图
    if intent == "weather":
        return call_weather_api(extract_city(user_input))
    elif intent == "news":
        return call_news_api(extract_topic(user_input))
    # ... 无法处理未定义意图

# Agent：动态决策
def agent_loop(user_goal):
    context = initialize_context(user_goal)
    while not context.is_complete():
        # LLM 决定下一步动作
        action = llm.decide_next_action(context)
        if action.type == "tool_call":
            result = execute_tool(action.tool, action.params)
            context.add_observation(result)
        elif action.type == "respond":
            return generate_response(context)
        elif action.type == "plan":
            context.update_plan(action.new_plan)
```

**关键差异：** Agent 的决策由 LLM 实时生成，而非预定义规则。

#### 2. 任务规划（Task Planning）

```python
# 示例：ReAct 模式的规划与执行
class Agent:
    def run(self, task):
        steps = []
        while True:
            # Thought: 基于当前状态思考下一步
            thought = self.llm.generate_thought(task, steps)
            
            # Action: 决定执行什么动作
            action = self.llm.decide_action(thought)
            
            if action.type == "finish":
                return action.answer
            
            # Observation: 执行并观察结果
            observation = self.execute(action)
            steps.append({"thought": thought, "action": action, "obs": observation})
```

**规划层次：**
- **高层规划**：将复杂目标分解为子任务
- **动态调整**：根据中间结果调整后续计划
- **依赖管理**：处理任务间的依赖关系

#### 3. 记忆系统（Memory）

```python
class AgentMemory:
    def __init__(self):
        self.short_term = []      # 当前对话上下文
        self.working_memory = {}  # 任务执行中的临时状态
        self.long_term = VectorStore()  # 历史经验、知识
    
    def retrieve_relevant(self, query, k=5):
        # 从长期记忆中检索相关信息
        return self.long_term.similarity_search(query, k)
    
    def update_stm(self, new_info):
        # 管理短期记忆长度，必要时压缩
        self.short_term.append(new_info)
        if len(self.short_term) > MAX_STM_LENGTH:
            self._compress_and_archive()
```

**记忆类型：**
- **短期记忆（STM）**：当前会话上下文
- **工作记忆**：任务执行中的中间状态
- **长期记忆（LTM）**：历史经验、用户偏好、领域知识

#### 4. 工具使用（Tool Use）

```python
# Agent 的工具使用是动态的
class ToolRegistry:
    def __init__(self):
        self.tools = {}
    
    def register(self, name, tool_func, description, parameters):
        self.tools[name] = {
            "func": tool_func,
            "description": description,
            "parameters": parameters
        }
    
    def select_and_call(self, llm, context):
        # LLM 自主选择工具
        tool_choice = llm.select_tool(context, available_tools=self.tools)
        tool = self.tools[tool_choice.name]
        
        # 参数可能是上一步的输出
        params = self.resolve_params(tool_choice.params, context)
        return tool["func"](**params)
```

**工具使用特征：**
- **动态选择**：根据上下文选择最合适的工具
- **参数推导**：参数可能来自用户输入、记忆或上一步输出
- **链式调用**：能将多个工具输出串联
- **错误处理**：工具失败时能尝试替代方案

---

### 四、面试题：如何设计一个真正的 Agent

#### 问题

**面试官：如果你要设计一个"智能旅行助手"Agent，如何确保它是一个真正的 Agent 而不是 API 转发器？**

#### 参考答案框架

**1. 架构设计**

```
┌─────────────────────────────────────────────────────────────┐
│                      Travel Agent                           │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │   Planner    │  │   Memory     │  │   Tool Executor  │  │
│  │  (任务规划)   │  │  (记忆管理)   │  │    (工具执行)     │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
│         ↑                  ↑                   ↑           │
│         └──────────────────┼───────────────────┘           │
│                            ↓                               │
│                    ┌──────────────┐                        │
│                    │  LLM Core    │                        │
│                    │  (决策中枢)   │                        │
│                    └──────────────┘                        │
└─────────────────────────────────────────────────────────────┘
```

**2. 关键设计决策**

| 组件 | 设计要点 | 避免的做法 |
|------|----------|-----------|
| **意图理解** | 使用 LLM 理解用户目标，支持复杂、模糊、多意图输入 | 不要用规则匹配意图 |
| **任务规划** | 实现 ReAct/Plan-and-Solve，动态分解任务 | 不要预定义固定流程 |
| **工具选择** | LLM 根据上下文动态选择工具组合 | 不要固定工具映射 |
| **记忆管理** | 维护用户偏好、历史行程、当前状态 | 不要无状态处理 |
| **执行循环** | 观察-思考-行动的迭代循环 | 不要一次调用返回结果 |

**3. 示例：复杂任务处理流程**

```
用户：我想带父母去北京玩三天，他们不能走太多路，
      帮我规划一下行程，要包含故宫和长城，
      然后订合适的酒店和机票

↓

Agent 思考：
1. 这是一个多步骤任务：行程规划 + 酒店预订 + 机票预订
2. 约束条件：3天、父母同行、低强度、必去故宫和长城
3. 需要先规划行程，再根据行程订酒店

↓

执行步骤：
Step 1: 查询故宫和长城的游览信息（开放时间、建议时长）
Step 2: 基于约束条件生成3天行程规划
Step 3: 根据行程区域搜索附近酒店
Step 4: 根据行程日期查询航班
Step 5: 整合结果并呈现给用户

↓

如果某步失败（如酒店 API 不可用）：
- 尝试其他酒店预订渠道
- 或调整行程区域推荐其他住宿
- 或告知用户手动预订建议
```

**4. 关键代码片段**

```python
class TravelAgent:
    def __init__(self):
        self.memory = TravelMemory()  # 用户偏好、历史行程
        self.planner = TaskPlanner()   # 任务规划器
        self.tools = ToolRegistry()    # 工具注册表
        self.llm = LLMClient()
    
    def run(self, user_request):
        # 1. 理解目标
        goal = self.llm.analyze_goal(user_request, self.memory.get_user_profile())
        
        # 2. 生成计划
        plan = self.planner.create_plan(goal, self.tools.available_tools())
        
        # 3. 执行循环
        context = ExecutionContext()
        for step in plan.steps:
            try:
                result = self.execute_step(step, context)
                context.add_result(step, result)
            except ToolError as e:
                # 自主错误恢复
                recovery = self.handle_error(e, step, context)
                if recovery:
                    context.add_result(step, recovery)
                else:
                    raise
        
        # 4. 生成回复
        return self.llm.synthesize_response(context)
    
    def execute_step(self, step, context):
        # LLM 动态选择工具
        tool_call = self.llm.select_tool(step, context, self.tools.descriptions())
        tool = self.tools.get(tool_call.tool_name)
        
        # 解析参数（可能包含上下文变量）
        params = self.resolve_params(tool_call.params, context)
        
        return tool.execute(**params)
    
    def handle_error(self, error, step, context):
        # 分析错误并决定恢复策略
        strategy = self.llm.decide_recovery_strategy(error, step, context)
        
        if strategy.type == "retry":
            return self.execute_step(step, context)
        elif strategy.type == "alternative_tool":
            return self.tools.get(strategy.alternative).execute(**strategy.params)
        elif strategy.type == "skip_and_continue":
            return {"skipped": True, "reason": str(error)}
        else:
            return None  # 无法恢复，向上抛出
```

---

### 五、常见误区与反模式

#### 误区 1：用了 LLM 就是 Agent
```python
# ❌ 这不是 Agent，只是 LLM + API 转发
class FakeAgent:
    def chat(self, user_input):
        # LLM 只是提取参数
        params = llm.extract_params(user_input)
        # 固定调用某个 API
        return call_api(params)
```

#### 误区 2：多 API 调用就是 Agent
```python
# ❌ 预定义的多步调用不是 Agent
class FakeAgent:
    def book_trip(self, request):
        # 固定流程，无自主决策
        flight = call_flight_api(request)
        hotel = call_hotel_api(request)
        return {"flight": flight, "hotel": hotel}
```

#### 误区 3：有记忆就是 Agent
```python
# ❌ 仅保存历史对话不是 Agent 记忆
class FakeAgent:
    def __init__(self):
        self.history = []  # 只是记录，不参与决策
    
    def chat(self, user_input):
        self.history.append(user_input)
        return llm.generate(self.history)
```

---

## 总结

| 维度 | API 转发器 | 真正的 Agent |
|------|-----------|-------------|
| **本质** | 输入-输出的映射器 | 目标驱动的自主系统 |
| **决策** | 预定义规则 | LLM 实时推理 |
| **执行** | 单次或固定流程 | 动态迭代循环 |
| **状态** | 无状态/短状态 | 完整记忆系统 |
| **容错** | 直接失败 | 自主恢复 |

**一句话判断：** 如果系统只是将用户输入转换为 API 调用，它是转发器；如果系统能**理解目标、自主规划、动态决策、迭代执行**，它就是 Agent。

---

## 延伸阅读

- [ReAct: Synergizing Reasoning and Acting in Language Models](https://arxiv.org/abs/2210.03629)
- [Task Planning](./task-planning.md)
- [Memory System](./memory-system.md)
- [Function Calling](./function-calling.md)