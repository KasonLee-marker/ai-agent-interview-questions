/**
 * Multi-Agent 协作范式 Java 实现示例
 * 
 * 多个专业 Agent 分工协作完成任务
 */
public class MultiAgentOrchestrator {
    
    private PlannerAgent planner;
    private ExecutorAgent executor;
    private ReviewerAgent reviewer;
    private Map<String, Agent> specialists;
    
    public String run(String task) {
        // 1. 规划阶段
        Plan plan = planner.createPlan(task);
        
        // 2. 执行阶段
        ExecutionContext context = new ExecutionContext();
        
        for (Phase phase : plan.getPhases()) {
            // 并行或串行执行
            if (phase.isParallel()) {
                executeParallel(phase.getSteps(), context);
            } else {
                executeSequential(phase.getSteps(), context);
            }
            
            // 审查阶段结果
            ReviewResult review = reviewer.review(phase, context);
            if (!review.isPassed()) {
                // 重新规划
                plan = planner.revisePlan(plan, review.getFeedback());
            }
        }
        
        // 3. 汇总结果
        return synthesizeResults(context);
    }
    
    private void executeSequential(List<Step> steps, ExecutionContext context) {
        for (Step step : steps) {
            Agent agent = selectAgent(step);
            String result = agent.execute(step, context);
            context.addResult(step.getId(), result);
        }
    }
    
    private void executeParallel(List<Step> steps, ExecutionContext context) {
        // 使用线程池并行执行
        List<Future<String>> futures = steps.stream()
            .map(step -> executor.submit(() -> {
                Agent agent = selectAgent(step);
                return agent.execute(step, context);
            }))
            .collect(Collectors.toList());
        
        // 收集结果
        for (int i = 0; i < steps.size(); i++) {
            try {
                String result = futures.get(i).get();
                context.addResult(steps.get(i).getId(), result);
            } catch (Exception e) {
                context.addError(steps.get(i).getId(), e);
            }
        }
    }
    
    private Agent selectAgent(Step step) {
        // 根据步骤类型选择专业 Agent
        return specialists.getOrDefault(
            step.getRequiredExpertise(), 
            executor
        );
    }
    
    private String synthesizeResults(ExecutionContext context) {
        // 汇总所有 Agent 的执行结果
        String prompt = "请综合以下各阶段结果，生成最终答案:\n" + context.getAllResults();
        return llm.complete(prompt);
    }
}

// 专业 Agent 实现
class PlannerAgent extends Agent {
    public Plan createPlan(String task) {
        String prompt = "作为规划专家，请为以下任务制定详细计划:\n" + task;
        return parsePlan(llm.complete(prompt));
    }
    
    public Plan revisePlan(Plan currentPlan, String feedback) {
        String prompt = String.format("""
            当前计划: %s
            反馈: %s
            
            请修订计划:
            """, currentPlan, feedback);
        return parsePlan(llm.complete(prompt));
    }
}

class ExecutorAgent extends Agent {
    public String execute(Step step, ExecutionContext context) {
        String prompt = String.format("""
            执行步骤: %s
            上下文: %s
            """, step.getDescription(), context);
        return llm.complete(prompt);
    }
}

class ReviewerAgent extends Agent {
    public ReviewResult review(Phase phase, ExecutionContext context) {
        String prompt = String.format("""
            审查阶段: %s
            执行结果: %s
            
            请评估是否通过，如有问题请指出:
            """, phase.getName(), context.getPhaseResults(phase));
        
        String response = llm.complete(prompt);
        return parseReview(response);
    }
}

// 辅助类
class Plan {
    private List<Phase> phases;
    // ...
}

class Phase {
    private String name;
    private List<Step> steps;
    private boolean parallel;
    // ...
}

class ReviewResult {
    private boolean passed;
    private String feedback;
    // ...
}

class ExecutionContext {
    private Map<String, String> results = new ConcurrentHashMap<>();
    private Map<String, Exception> errors = new ConcurrentHashMap<>();
    
    public void addResult(String stepId, String result) {
        results.put(stepId, result);
    }
    
    public void addError(String stepId, Exception error) {
        errors.put(stepId, error);
    }
    
    // getters...
}
